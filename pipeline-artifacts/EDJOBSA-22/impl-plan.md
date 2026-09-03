# Implementation Plan — EDJOBSA-22

**Ticket**: EDJOBSA-22 (Self-Service Book Borrowing)  
**Total Duration**: 5 weeks (constraint from requirements)  
**Start Date**: Week of Sep 2, 2026  
**End Date**: Week of Oct 7, 2026  
**Approach**: Agile (2-week sprints, daily standups)  
**Buffer**: 20% built into estimates

---

## Work Breakdown Structure

### Sprint 1 (Sep 2–13) — Foundation & Must-Fix Tasks

#### TRACK-A: Database & Infrastructure (Dependency: **None**)

**Task A-1: Audit Log Schema Migration**
- Duration: 1 day (Sep 2–3)
- Owner: DBA / Backend Lead
- Priority: **Mandatory** (M-2 must-fix)
- Description: Create Flyway migration `V20260824_1__add_audit_log.sql` with:
  - `audit_log(id, member_id, book_id, timestamp, outcome, correlation_id)` table
  - Indexes on `(member_id, timestamp)` and `(book_id, timestamp)` for retention queries
  - Retention policy: 12 months (db-level via partition pruning, if available)
- Deliverable: Migration script, test harness confirms table creation
- Acceptance: `\d audit_log` in psql shows all columns; migration is idempotent

**Task A-2: Rate Limiter Backing Store Setup (M-3 must-fix)**
- Duration: 1 day (Sep 3–4)
- Owner: DevOps / Backend Lead  
- Priority: **Mandatory** (M-3 must-fix)
- Description: Stand up shared rate-limit store:
  - If choosing Redis: provision 2-node Redis Sentinel in k8s (or use existing cluster)
  - If choosing Hazelcast: add `<dependency>` to pom.xml; configure Hazelcast cluster bean
  - Document store choice and failover logic in `RATE_LIMITER_DESIGN.md`
- Deliverable: Running store; Bucket4j client code can connect (proof: unit test with TestContainers)
- Acceptance: Rate-limit counter persists across JVM restarts

**Task A-3: Local Dev Environment Setup**
- Duration: 0.5 day (Sep 4)
- Owner: All developers
- Prerequisites: A-1, A-2 (scripts available)
- Description: Each developer runs Docker Compose to spin up:
  - PostgreSQL 15 with `library` DB schema (seeded with test data)
  - Redis/Hazelcast (rate limiter)
  - Provide `docker-compose-dev.yml` in the repo
- Deliverable: `docker compose up` brings up full stack; `mvn clean install -DskipTests` passes
- Acceptance: 100% of team can run the stack locally

---

#### TRACK-B: API Scaffolding & Core Entities (Dependency: **A-1**)

**Task B-1: Domain Model & JPA Entities**
- Duration: 1.5 days (Sep 5–6)
- Owner: Backend Developer (Senior)
- Prerequisites: A-1
- Description:
  - Ensure JPA entities exist: `Book`, `Member`, `Loan` (should already exist; add fields if needed)
  - Add Loan entity fields: `id`, `memberId`, `bookId`, `loanDate`, `dueDate`, `status` (ACTIVE, RETURNED)
  - Add `Book.status` field (AVAILABLE, LOANED) if not present
  - Add `Book` pessimistic lock annotation: `@javax.persistence.Version` or configure in query
  - Update `Member` entity: add default-read access to `activeLoans` count
- Deliverable: Entities compile; JPA tests confirm CRUD operations
- Acceptance: `BookRepositoryTest` can load and lock a book; MemberRepository can count active loans

**Task B-2: Repository Layer — Data Access Objects**
- Duration: 2 days (Sep 6–7)
- Owner: Backend Developer
- Prerequisites: B-1
- Description:
  - Implement interfaces (extend `JpaRepository`):
    - `BookRepository.findByIsbnWithLock(isbn)` — queries with `PESSIMISTIC_WRITE`
    - `MemberRepository.findById(id)` + load member with `fineBalance` and `activeLoans` count
    - `LoanRepository.save(loan)` — standard insert
  - Add custom `@Query` for `findAvailableBooks()` (for UI suggestions, not on critical path)
  - Add query timeout: `@QueryHints({ @QueryHint(name = "javax.persistence.lock.timeout", value = "500") })`
- Deliverable: All repositories compile; integration tests with TestContainers confirm lock behavior
- Acceptance: Two concurrent threads locking the same book — exactly one succeeds, other gets timeout exception

**Task B-3: API Controller & DTOs**
- Duration: 1.5 days (Sep 8–9)
- Owner: Backend Developer
- Prerequisites: B-2
- Description:
  - Create `BorrowingController` at `POST /api/v1/loans`
  - Define request DTO: `CheckoutRequest { isbn: String }`
  - Define response DTO: `LoanResponse { loanId, isbn, dueDate, memberId }`
  - Define error DTO: `ErrorResponse { code: FINES_OUTSTANDING | LIMIT_EXCEEDED | BOOK_UNAVAILABLE | BOOK_NOT_FOUND, message }`
  - Add Spring Security `@PreAuthorize("hasAuthority('library:borrow')")` on the endpoint
  - Add `@Valid` on the request DTO; inject `Clock` bean for deterministic testing
  - Controller extracts `memberId` from `SecurityContextHolder.getContext().getAuthentication().getName()` (read from JWT `sub`)
- Deliverable: Controller compiles; integration test via MockMvc confirms 201 response and error handling
- Acceptance: `POST /api/v1/loans { "isbn": "978-…" }` with valid JWT returns 201; missing scope returns 403

---

### Sprint 2 (Sep 16–27) — Core Logic & Testing

#### TRACK-C: Business Logic Layer (Dependency: **B-3**)

**Task C-1: BorrowingService — Core Checkout Logic**
- Duration: 3 days (Sep 16–18)
- Owner: Backend Developer (Senior)
- Prerequisites: B-3, A-1
- Priority: **Critical path**
- Description:
  - Implement `BorrowingService.checkoutBook(memberId, isbn)` with the full transaction:
    1. Load `Member` by ID; check `outstandingFineBalance == 0`
    2. Check `activeLoanCount < 5` (configurable via `library.borrowing.limit`)
    3. Load `Book` by ISBN with `PESSIMISTIC_WRITE` lock (timeout 500ms as per M-1)
    4. Verify `Book.status == AVAILABLE`
    5. Insert `Loan` row with `dueDate = today + 14d` (use injected `Clock`)
    6. Update `Book.status = LOANED`
    7. Record audit entry (see C-2)
    8. Return `LoanResponse` (201)
  - On any error: roll back transaction, do NOT update audit log (atomic)
  - Throw domain exceptions: `FindsOutstandingException`, `BorrowingLimitExceededException`, `BookUnavailableException`, `BookNotFoundException`, `LockTimeoutException`
- Deliverable: Service compiles; unit tests with Mockito confirm business logic
- Acceptance: 
  - Mock repo returns member with 0 fines + 4 active loans; mock book is AVAILABLE → checkout succeeds
  - Mock member has fines > 0 → throws `FinesOutstandingException`
  - Mock book is LOANED → throws `BookUnavailableException`

**Task C-2: Audit Logger — Insert & Correlation**
- Duration: 2 days (Sep 19–20)
- Owner: Backend Developer
- Prerequisites: A-1, C-1
- Priority: **Mandatory** (M-2 must-fix — sync audit)
- Description:
  - Implement `AuditLogger` as a Spring `@Service`
  - Methods: `logCheckout(memberId, bookId, outcome, correlationId)` → **synchronous insert** into `audit_log` table (inside same transaction as Loan)
  - Inject `AuditRepository` (JPA); call `.save()` inline (not async)
  - Extract correlation ID from request header `X-Correlation-Id` (if missing, generate UUID)
  - Ensure this insert happens in the same `@Transactional` scope as `C-1` (rollback together)
  - Add Logback MDC binding for `correlationId` so logs include it
- Deliverable: AuditLogger compiles; integration test confirms audit rows appear in DB within same transaction
- Acceptance: On checkout success, `audit_log` has 1 row; on exception (before persist), 0 rows

**Task C-3: Rate Limiter Integration** 
- Duration: 1.5 days (Sep 20–21)
- Owner: Backend Developer
- Prerequisites: A-2
- Description:
  - Add Bucket4j dependency to `pom.xml`
  - Create `@Service RateLimiter` bean with:
    - Shared-store backend (Redis or Hazelcast, per A-2)
    - Limit: 20 req/member/second (configurable)
    - Return boolean: `isAllowed(memberId)` → true if within budget, false otherwise
  - Add `@PreAuthorize` or `if (!rateLimiter.isAllowed(memberId)) { return 429; }` guard in controller
  - On limit exceeded, return HTTP 429 (Too Many Requests)
- Deliverable: RateLimiter bean compiles; unit test with mock store confirms limit enforcement
- Acceptance: First 20 requests from member M1 succeed; 21st returns false

**Task C-4: Error Handling & Response Mapping**
- Duration: 1 day (Sep 21)
- Owner: Backend Developer
- Prerequisites: C-1, C-3
- Description:
  - Implement `@ControllerAdvice ErrorHandler` that maps domain exceptions → HTTP status + error code:
    - `FinesOutstandingException` → 409 Conflict, `{ "code": "FINES_OUTSTANDING", "message": "…" }`
    - `BorrowingLimitExceededException` → 409 Conflict, `{ "code": "LIMIT_EXCEEDED", "message": "…" }`
    - `BookUnavailableException` → 409 Conflict, `{ "code": "BOOK_UNAVAILABLE", "message": "…" }`
    - `BookNotFoundException` → 404 Not Found, `{ "code": "BOOK_NOT_FOUND", "message": "…" }`
    - `LockTimeoutException` (database) → 409 Conflict, return `BOOK_UNAVAILABLE` (compete with another checkout)
    - Others (NPE, validation) → 400 Bad Request or 500 Internal Server Error
  - Ensure correlation ID is included in all error response headers
- Deliverable: ErrorHandler compiles; MockMvc tests confirm HTTP status + JSON payload
- Acceptance: Throw `FinesOutstandingException` → response is 409 with correct JSON

---

#### TRACK-D: Testing (Dependency: **C-4**)

**Task D-1: Unit Tests — Service & Repository**
- Duration: 2 days (Sep 22–23)
- Owner: QA / Backend Developer
- Prerequisites: C-4
- Description:
  - JUnit 5 test suite for:
    - `BorrowingServiceTest` (Mockito mocks, 100% branch coverage)
    - `AuditLoggerTest` (confirm sync insert, correlation ID propagation)
    - `RateLimiterTest` (confirm shared-store behavior)
    - `ErrorHandlerTest` (confirm exception → HTTP status mapping)
  - Target: ≥ 90% code coverage
  - Use `@SpringBootTest` with Testcontainers for real DB/cache if needed
- Deliverable: All tests pass; coverage report generated by JaCoCo
- Acceptance: `mvn test` shows 0 failures; JaCoCo report > 90% line coverage

**Task D-2: Integration Tests — API End-to-End**
- Duration: 2.5 days (Sep 23–24)
- Owner: QA Lead
- Prerequisites: D-1
- Target: Cover all acceptance criteria from Phase 1 requirements
- Description:
  - MockMvc/TestRestTemplate tests covering:
    - ✓ Successful checkout (member with 0 fines, < 5 loans) → 201 + LoanResponse
    - ✓ Fines outstanding → 409 + FINES_OUTSTANDING
    - ✓ Borrowing limit reached → 409 + LIMIT_EXCEEDED
    - ✓ Book unavailable (status = LOANED) → 409 + BOOK_UNAVAILABLE
    - ✓ Unknown book → 404 + BOOK_NOT_FOUND
    - ✓ Concurrent checkout of same book → exactly 1 succeeds, 1 gets BOOK_UNAVAILABLE
    - ✓ Missing/invalid JWT → 401 or 403
    - ✓ Rate limit exceeded → 429
    - ✓ Correlation ID in request header → echoed in response + logged
  - Testcontainers for real PostgreSQL + Redis/Hazelcast (if needed)
  - Cannot run within app server yet (no frontend); call endpoint directly via HTTP
- Deliverable: All test scenarios pass; test report
- Acceptance: All 8 AC scenarios pass; concurrency test repeatable 100× without race condition

**Task D-3: Load Testing Baseline**
- Duration: 1.5 days (Sep 25–26)
- Owner: QA / Performance Engineer
- Prerequisites: D-2
- Description:
  - Use JMeter or k6 to generate 50 rps (baseline), then 200 rps (target), measuring:
    - P95, P99 latency
    - Error rate (should be 0 for success scenarios)
    - Lock wait time (Grafana metric if instrumented, or log analysis)
  - Document findings in `PERFORMANCE_BASELINE.md`
  - If P95 > 500 ms at 200 rps, escalate to design (may need query optimization or connection pool tuning)
- Deliverable: Load test results + report
- Acceptance: P95 latency < 500 ms at 200 rps (or documented exception)

---

#### TRACK-E: Frontend & Documentation (Dependency: **D-3**)

**Task E-1: React BorrowingPage Component**
- Duration: 2.5 days (Sep 26–27)
- Owner: Frontend Developer
- Prerequisites: B-3 (API is stable)
- Description:
  - Create React component `BorrowingPage.jsx` inside existing web portal:
    - Input: ISBN or barcode (text field + optional barcode scanner integration)
    - Button: "Check Out"
    - On success: display LoanResponse (loan ID, due date) + confirmation
    - On error: display error code + human-readable message (FINES_OUTSTANDING → "You have outstanding fines…")
    - On rate limit: display "Too many requests; try again in 1 second"
    - Add loading spinner during API call
    - Add accessibility attributes (WCAG 2.1 AA compliance — `aria-label`, semantic HTML)
  - Integrate with existing auth/JWT (likely in `AuthContext` or similar)
  - Inject correlation ID into request header (new UUID per request)
- Deliverable: Component compiles, renders, can submit requests to mock API
- Acceptance: Click "Check Out" → network request includes `Authorization: Bearer <jwt>` + `X-Correlation-Id` header

**Task E-2: Documentation & Runbooks**
- Duration: 1 day (Sep 27)
- Owner: Tech Lead / Backend Developer
- Prerequisites: All above
- Description:
  - `IMPLEMENTATION_SUMMARY.md` — what's deployed, what's deferred
  - `API_SPEC.md` — OpenAPI 3.0 spec for POST /api/v1/loans (generate via Springdoc)
  - `RATE_LIMITER_DESIGN.md` — choice of backing store (A-2 output) + failover behavior
  - `AUDIT_DESIGN.md` — audit log schema, retention, query examples
  - `ROLLOUT_RUNBOOK.md` — rings (internal → 1% → 10% → 100%), feature-flag owner (answering M-4), kill-switch procedure
  - `PERFORMANCE_BASELINE.md` — load test results + tuning notes
  - `TROUBLESHOOTING.md` — common errors and remediation
- Deliverable: All docs in `.github/docs/` or `sdlc-pipeline/docs/`
- Acceptance: Every design question from Phase 3 has a documented answer

---

### Sprint 3 (Sep 30–Oct 11) — Integration, Rollout Prep & Code Review

#### TRACK-F: CI/CD & Deployment (Dependency: **D-3, E-1**)

**Task F-1: CI/CD Pipeline Configuration**
- Duration: 1.5 days (Sep 30–Oct 1)
- Owner: DevOps / Platform Engineer
- Prerequisites: All code from Sprint 2
- Description:
  - Update GitHub Actions workflow (`.github/workflows/`) to:
    - Trigger: PR to `develop`, merge to `main`
    - Build: `mvn clean package`
    - Unit Tests: `mvn test` (D-1)
    - Integration Tests: `mvn verify` with Testcontainers (D-2)
    - SAST: SonarQube scan
    - Dependency scan: Trivy + (optionally) OWASP Dep-Check
    - Container scan: Trivy on final image
    - Artifact push: ECR or Docker Hub
  - Feature flag: add `library.borrowing.self-service.enabled=false` to all config files; document how to toggle
- Deliverable: Workflow file compiles; trigger on PR shows all checks pass/fail
- Acceptance: A PR with a simple formatting change shows green checkmarks on all checks

**Task F-2: Helm Chart & k8s Deployment**
- Duration: 2 days (Oct 1–2)
- Owner: DevOps
- Prerequisites: F-1
- Description:
  - Update existing Helm chart for `library-service` to:
    - Add new config values: `borrowing.enabled`, `borrowing.limit`, `borrowing.durationDays`, `borrowing.rateLimit.rps`, `auditLog.retention`
    - Update init container / Flyway hook to run migration (A-1)
    - Set up Redis/Hazelcast client config for rate limiter (A-2)
    - Configure Prometheus metrics scrape for lock-wait time + audit throughput
    - Set resource requests/limits: CPU 0.5 default, 2.0 max; memory 512 Mi default, 2 Gi max
  - Rollout strategy: `RollingUpdate` with 25% surge/unavailable
  - Readiness probe: `GET /actuator/health/readiness` (ensure DB migration passed)
- Deliverable: Helm chart validates; `helm lint` passes; `helm template` produces valid k8s manifests
- Acceptance: `helm install` on a test cluster brings up the pod; `kubectl logs` shows "BorrowingController registered"

**Task F-3: Smoke Tests & Staging Validation**
- Duration: 1.5 days (Oct 2–3)
- Owner: QA / DevOps
- Prerequisites: F-2
- Description:
  - Deploy to staging cluster (if available) or use docker-compose in CI agent
  - Run smoke tests: 
    - Successful checkout
    - Error scenarios (fines, limit, unavailable)
    - Rate limiting (send 25 requests in 1 second, confirm 5 are 429)
    - Audit log populated (query `audit_log`, confirm entries)
    - Feature flag toggle: disable flag, confirm all requests return 403 or 404
  - Capture screenshots / logs as evidence
- Deliverable: Smoke test report
- Acceptance: All smoke tests pass; feature flag toggle works

---

#### TRACK-G: Code Review & Security Validation (Dependency: **D-3, E-2**)

**Task G-1: Internal Code Review**
- Duration: 2 days (Oct 3–4)
- Owner: Backend Lead + Senior Developer (not original authors)
- Prerequisites: All code from Track C + E-1
- Checklist:
  - [ ] No hardcoded secrets (check for DB password, API key, JWT secret)
  - [ ] All domain exceptions caught and mapped to HTTP status
  - [ ] `memberId` extracted only from JWT token, never from request body
  - [ ] `PESSIMISTIC_WRITE` lock used on every book read
  - [ ] Audit log insert is synchronous (same transaction as checkout)
  - [ ] Rate limiter uses shared store (not in-memory)
  - [ ] ISBN-13 validation includes checksum (if implemented per L-1)
  - [ ] Correlation ID propagated through MDC
  - [ ] No N+1 queries; use JPA batch queries if needed
  - [ ] Test coverage ≥ 90%
  - [ ] No deprecated Spring/JPA APIs
  - [ ] All logs are JSON-formatted; PII (member ID) flagged for redaction if needed
- Deliverable: Code review report; GitHub PR approved by 2 reviewers
- Acceptance: All checklist items checked; blocking comments resolved

**Task G-2: Security Validation**
- Duration: 1.5 days (Oct 4–5)
- Owner: Security / InfoSec
- Prerequisites: G-1
- Description: Manual security review based on Phase 3 / OWASP Top 10:
  - ( A01 IDOR): Call endpoint with another user's JWT, confirm membership ID is read from token, not parameter
  - ( A03 Injection): Try `isbn"; DROP TABLE book; --` → confirm parameterized query (no SQL execution)
  - ( A07 Auth): Confirm JWT expiry is enforced (test with expired token)
  - ( A09 Logging): Query `audit_log`, confirm PII is present (then document retention/redaction policy)
  - OWASP: spot-check dependencies for known CVEs (`pip install safety` or use Trivy output)
- Deliverable: Security sign-off memo
- Acceptance: All A01, A03, A07, A09 tests pass; no high/critical CVEs

---

### Sprint 4 (Oct 7–18) — Ring Rollout & Monitoring

**Task H-1: Ring 0 (Internal Testing)**
- Duration: 2 days (Oct 7–8)
- Owner: QA / Eng Team
- Prerequisites: F-3, G-2
- Description:
  - Deploy to internal staging with feature flag ON
  - 10 team members (developers, QA, product) exercise checkout flows manually
  - Verify P95 latency, error rate, audit logging, rate limit rejection
  - Collect feedback on UI/UX
- Deliverable: Ring 0 sign-off memo
- Acceptance: All testers confirm happy path; issues filed as bugs in backlog

**Task H-2: Ring 1 (1% Users)**
- Duration: 3 days (Oct 8–10)
- Owner: DevOps / SRE
- Prerequisites: H-1
- Description:
  - Enable feature flag for 1% of live traffic (k8s canary or traffic shaping)
  - Monitor: E2E latency (Datadog/New Relic), error rate (CloudWatch), audit log write rate
  - Alert on: P95 > 500 ms, error rate > 1%, lock-wait > 200 ms, audit log lag > 5 seconds
  - Kill-switch: disable flag if any critical issue
  - Duration: 24 hours minimum before escalating
- Deliverable: 1% ring health report
- Acceptance: No red alerts; error rate < 0.1%; latency SLOs met

**Task H-3: Ring 2 (10% Users)**
- Duration: 3 days (Oct 10–12)
- Owner: DevOps / Product
- Prerequisites: H-2
- Description: Same monitoring as H-2, but 10% traffic. Duration: 24–48 hours.
- Deliverable: 10% health report
- Acceptance: Same thresholds as H-2

**Task H-4: Ring 3 (100% Users) + Full Deployment**
- Duration: 2 days (Oct 12–13)
- Owner: DevOps
- Prerequisites: H-3
- Description:
  - Ramp to 100% traffic via gradual rollout or blue-green
  - 24-hour post-deployment monitoring
  - Document any issues / post-incident learning
- Deliverable: 100% rollout memo, launch announcement
- Acceptance: No production incidents; if incidents occur, rollback documented and executed

---

## Dependency Graph

```
Sprint 1 (Foundation)
    │
    ├─ A-1 (Audit Schema) ◄─ A-2 (Rate Limiter) ◄─ A-3 (Dev Env)
    │     │
    │     └─────┐
    │           │
    └─ B-1 (Entities) ◄─ B-2 (Repos) ◄─ B-3 (Controller)
                            │
                            │
Sprint 2 (Core Logic)       │
    │                       │
    ├─ C-1 (Service) ◄──────┘
    │     │
    │     ├─ C-2 (Audit Logger, sync) ◄─ [M-2 critical]
    │     │     │
    │     ├─ C-3 (Rate Limiter) ◄─ [M-3 critical]
    │     │
    │     ├─ C-4 (Error Handling)
    │           │
    ├─ D-1 (Unit Tests) ◄─ D-2 (Integration Tests)
    │
    ├─ D-3 (Load Test)
    │
    ├─ E-1 (Frontend) ◄─ B-3
    │
    └─ E-2 (Documentation)
            │
Sprint 3    │
    │       │
    ├─ F-1 (CI/CD) ◄─ All Sprint 2 code
    │
    ├─ F-2 (Helm/k8s) ◄─ A-1, A-2
    │
    ├─ F-3 (Smoke Tests)
    │
    ├─ G-1 (Code Review) ◄─ F-3
    │
    └─ G-2 (Security Validation) ◄─ G-1
            │
Sprint 4    │
    │       │
    ├─ H-1 (Ring 0) ◄─ G-2
    │
    ├─ H-2 (Ring 1) ◄─ H-1
    │
    ├─ H-3 (Ring 2) ◄─ H-2
    │
    └─ H-4 (Ring 3, GA) ◄─ H-3
```

## Critical Path

**A-1 → B-1 → B-2 → B-3 → C-1 → C-2 → C-4 → D-2 → F-2 → F-3 → G-2 → H-4**

**Total Critical Path Duration**: ~18 days (Sprint 1 + most of Sprint 2 + most of Sprint 3 + H-4)

**Slack per sprint**: ~5–7 working days

---

## Resource Allocation

| Role | # Needed | Primary Tasks | Availability |
|---|---|---|---|
| **Backend Lead** | 1 | B-1, C-1, G-1, design decisions | Full-time (20 days) |
| **Backend Developer** | 2 | B-2, B-3, C-2, C-3, C-4, D-1 | Full-time (40 days total) |
| **Frontend Developer** | 1 | E-1, UI testing in H-1 | Full-time (10 days) |
| **QA Lead** | 1 | D-2, D-3, test strategy | Full-time (12 days) |
| **QA Engineer** | 1 | D-2, D-3, smoke tests (F-3) | Full-time (10 days) |
| **DevOps / Platform Engineer** | 1 | A-2, F-1, F-2, F-3, H-2–H-4 | Full-time (18 days) |
| **DBA** | 0.5 | A-1, database tuning advice | On-call (3 days) |
| **Security / InfoSec** | 0.5 | G-2, sensitive data review | On-call (2 days) |
| **Tech Writer / Doc Lead** | 0.5 | E-2, runbooks | Part-time (3 days) |

**Total FTE**: ~5.5 (6 people at 80–100% capacity)

---

## Risk & Mitigation

| Risk | Probability | Impact | Mitigation |
|---|---|---|---|
| **Scope Creep** | High | High | Strict AC from Phase 1; deferred features tracked in backlog (e.g., returns, renewals) |
| **Lock Contention on Hot Books** | Medium | Medium | M-1 monitoring (lock-wait metric); can add read-through cache for availability checks (Phase 5+) |
| **Rate Limiter Failover** | Medium | High | A-2 must confirm Redis/Hazelcast HA; test failover scenario in D-2 |
| **JWT Token Misconfiguration** | Low | High | G-2 security review includes JWT claim validation; L-3 tests wrong claim format |
| **Database Migration Failure** | Low | Medium | A-1 includes rollback script; F-2 readiness probe confirms migration success |
| **Load Testing Misses P95 Target** | Medium | High | Plan 1-day spike (early Oct) to analyze and tune; have backup tuning tasks (query optimization, connection pool ++) ready |
| **Concurrent Checkout Race Condition** | Low | Critical | D-2 includes 100×  concurrency test; pessimistic lock is proven solution |
| **Frontend Not Ready for GA** | Medium | Medium | E-1 is non-critical path; deploy API without UI if needed (manual API testing only) |
| **Incidents during rollout** | Low | High | Ring-by-ring strategy minimizes blast radius; documented kill-switch (H-1–H-4) |

---

## Assumptions

1. **Team availability**: 6 FTE available for 4 weeks (10/7–11/1 excluding Oct 14–18 as a buffer/monitoring week)
2. **Infrastructure**: PostgreSQL 15, Redis/Hazelcast, k8s cluster, GitHub Actions CI already running
3. **OAuth2 IdP**: Existing corporate IdP provides JWT; no changes needed to auth setup
4. **Database schema**: `book`, `member`, `loan` tables already exist; we only add `audit_log` and `status` columns
5. **API gateway**: Nginx or Envoy already rate-limiting at 100 rps/IP; we add 20 rps/member service-side
6. **Feature flag system**: Existing system (LaunchDarkly, Unleash, etc.) or simple config; we use `library.borrowing.self-service.enabled`
7. **Observability**: Prometheus + Grafana, ELK, and managed APM (DataDog/New Relic) already in place

---

## Success Metrics (Phase 4 Gate)

- ✅ All components decomposed into trackable tasks with owners
- ✅ Dependencies graphed and critical path identified (18 days)
- ✅ Resource allocation documented; no overallocation
- ✅ Risk register completed with mitigations
- ✅ M-2 and M-3 are top-priority sprint 2 tasks (marked **Mandatory**)
- ✅ Timeline: 5 weeks end-to-end (Sep 2 – Oct 13 for 100% rollout)
- ✅ No blockers; ready to code

## Gate Criteria — Phase 4

- ✅ All components broken down into tasks
- ✅ Dependencies documented; critical path identified
- ✅ Estimates provided (days); total within 5-week window
- ✅ Resources allocated (6 FTE); no conflicts
- ✅ M-2 and M-3 (must-fix from Phase 3) embedded as Sprint 2 mandatory tasks
- ✅ Risk register documented with concrete mitigations
- ✅ Timeline approved (Sep 2 – Oct 13)
- ✅ **`nextPhaseReady = true`**


