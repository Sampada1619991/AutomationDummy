# System Architecture Design — EDJOBSA-22

## Overview
The Self-Service Book Borrowing capability is added as a **new REST module inside the existing Spring Boot library-service monolith**, exposing a single POST endpoint. It reuses the existing PostgreSQL database and OAuth2/JWT infrastructure. No new services or infrastructure components are required.

**Style**: Modular monolith (aligned with existing platform). A future extraction to a microservice is possible but is not justified by current traffic (< 500 rps).

## Components

```
 ┌──────────────────────┐        ┌──────────────────────────────┐
 │  React Web Portal    │  HTTPS │       API Gateway (nginx)    │
 │  (existing)          │───────▶│     - TLS termination        │
 │  BorrowingPage.jsx   │        │     - Rate-limit (100 rps/ip)│
 └──────────────────────┘        └──────────────┬───────────────┘
                                                │  JWT (Bearer)
                                                ▼
                                ┌───────────────────────────────┐
                                │   library-service (Spring)    │
                                │  ┌─────────────────────────┐  │
                                │  │ BorrowingController     │  │
                                │  │  POST /api/v1/loans     │  │
                                │  └───────────┬─────────────┘  │
                                │              ▼                │
                                │  ┌─────────────────────────┐  │
                                │  │ BorrowingService        │  │
                                │  │  - checkoutBook(...)    │  │
                                │  │  - @Transactional       │  │
                                │  └───────────┬─────────────┘  │
                                │        ┌─────┴──────┐         │
                                │        ▼            ▼         │
                                │  BookRepository  MemberRepo   │
                                │  LoanRepository  AuditLogger  │
                                └──────────────┬────────────────┘
                                               │ JDBC
                                               ▼
                                ┌───────────────────────────────┐
                                │   PostgreSQL (existing)       │
                                │   book · member · loan        │
                                │   audit_log (NEW)             │
                                └───────────────────────────────┘
```

### Component responsibilities
| Component | Responsibility |
|-----------|----------------|
| `BorrowingController` | HTTP boundary, DTO validation, error → HTTP status mapping |
| `BorrowingService` | Business rules (fines, limit, availability), transactional integrity |
| `BookRepository` | JPA access with **pessimistic write lock** on the target book row |
| `MemberRepository` | Member lookup + fine balance + active-loan count |
| `LoanRepository` | Create Loan row |
| `AuditLogger` | Async append-only audit trail |

## Data Flow
1. Client sends `POST /api/v1/loans { "isbn": "978-…" }` with `Authorization: Bearer <jwt>`.
2. Gateway terminates TLS, applies IP rate-limit, forwards to service.
3. Spring Security validates JWT, extracts `memberId` from `sub` claim.
4. `BorrowingController` binds & validates the DTO (`@Valid`).
5. `BorrowingService.checkoutBook(memberId, isbn)` runs inside a transaction:
   1. Load `Member` → verify `outstandingFineBalance == 0` and `activeLoanCount < 5`.
   2. Load `Book` **with `PESSIMISTIC_WRITE` lock** → verify `status == AVAILABLE`.
   3. Insert `Loan` row (dueDate = today + 14 days).
   4. Update `Book.status = LOANED`.
   5. Publish audit event (async, ApplicationEventPublisher).
6. Return `201 Created` with `LoanResponse { loanId, isbn, dueDate }`.

## Technology Stack
| Layer | Choice | Rationale |
|-------|--------|-----------|
| Language / Runtime | Java 17 | Matches existing library-service |
| Framework | Spring Boot 3.2.x | In use across platform |
| Data access | Spring Data JPA + Hibernate | Existing entity model |
| DB | PostgreSQL 15 (existing) | Row-level locking satisfies NFR-5 |
| Auth | OAuth2 Resource Server + JWT (existing IdP) | Already provisioned |
| Migrations | Flyway | Existing convention |
| Build | Maven | Existing tooling |
| Observability | Micrometer → Prometheus + Grafana | Existing stack |
| Logging | Logback JSON → ELK | Existing stack |
| Tests | JUnit 5, Mockito, Testcontainers (Postgres) | Existing convention |

## Security Architecture
- **AuthN**: OAuth2 Resource Server; JWT signed by corporate IdP (RS256, JWKS rotation).
- **AuthZ**: Scope check `library:borrow`; `memberId` derived **only** from JWT `sub` (never from request body) → prevents IDOR.
- **Transport**: TLS 1.2+ enforced at gateway; HSTS.
- **Input validation**: Bean Validation (`@Pattern` on ISBN-13); reject > 20 bytes.
- **Injection**: JPA parameter binding only; no dynamic SQL.
- **Rate limiting**: 100 rps/IP at gateway + 20 rps/member at service (Bucket4j).
- **Secrets**: DB creds & IdP JWKS URL from environment / Vault; never in source.
- **Audit**: Every attempt (success/failure) written to `audit_log` with correlation ID.

### OWASP Top 10 mapping
| Risk | Mitigation |
|------|-----------|
| A01 Broken Access Control | JWT scope + `memberId` from token |
| A02 Cryptographic Failures | TLS 1.2+, JWT RS256 |
| A03 Injection | JPA binding, `@Pattern` validation |
| A04 Insecure Design | Pessimistic locking design (NFR-5) |
| A05 Security Misconfiguration | Actuator restricted, CSRF disabled only for stateless API |
| A07 Auth Failures | JWT expiry ≤ 15 min, JWKS rotation |
| A08 Integrity Failures | Flyway checksummed migrations |
| A09 Logging Failures | Async audit + correlation ID |
| A10 SSRF | No outbound calls in this feature |

## Scalability
- Stateless service → horizontal scale behind existing k8s HPA (CPU 70%).
- PostgreSQL row-level lock scope is per-book → no global contention.
- Read caching **not** used for `Book` on the checkout path (must read fresh with lock).
- Expected sizing at 200 rps: 3 pods × 0.5 CPU, DB pool 10/pod, headroom ×3.

## Deployment Model
- Deployed as part of existing `library-service` container image.
- Flyway migration `V20260824_1__add_audit_log.sql` adds the new `audit_log` table.
- Feature flag `library.borrowing.self-service.enabled` (default `false`) enables rollout ring by ring.
- CI/CD: existing GitHub Actions pipeline — build → test → SAST (SonarQube) → container scan (Trivy) → deploy.

## Decision Log
| Decision | Choice | Rationale |
|----------|--------|-----------|
| Pattern | **Modular monolith** (not microservice) | Traffic doesn't justify split; reuses auth & DB |
| Concurrency | **Pessimistic row lock** on `book` | Simplest correct solution; contention only on hot books |
| Cache | **None on write path** | Correctness > latency for NFR-5 |
| Auth | **OAuth2 + JWT** (existing) | Reuse |
| DB | **PostgreSQL** (existing) | Reuse; supports row locks |

## Gate Criteria — Phase 2
- ✅ Architecture aligns with all FRs and NFRs
- ✅ Tech stack justified against constraints
- ✅ Security architecture documented (OWASP mapped)
- ✅ Scalability plan quantified
- ✅ `nextPhaseReady = true`

