# Design Review Report — EDJOBSA-22

## Executive Summary
The proposed architecture (modular monolith inside the existing `library-service`, Spring Boot 3.2 + PostgreSQL, OAuth2/JWT, pessimistic row lock on `book`) is **fit for purpose** for the stated scale (≤ 200 rps) and correctly targets the hardest requirement (NFR-5, no double-loan). Reuse of existing auth, DB, and CI/CD infrastructure is the right call for a 5-week window.

Review depth: **Comprehensive**. Overall assessment: **CONDITIONAL APPROVAL** — 4 medium items should be resolved during implementation; no critical blockers.

## Risk Assessment

### High Priority
_None._ All previously identifiable high-risk items (concurrency correctness, IDOR via body-supplied `memberId`, secret handling) are already mitigated in the architecture.

### Medium Priority
- **M-1 · Hot-book lock contention.** `PESSIMISTIC_WRITE` on a single popular book (e.g., new release) can serialize all concurrent checkout attempts and eat into the P95 < 500 ms budget under a thundering herd.
  - **Impact**: NFR-1 miss on launch-day spikes.
  - **Mitigation**: Set a short lock timeout (`javax.persistence.lock.timeout = 500ms`); on timeout return `BOOK_UNAVAILABLE` fast. Add a Grafana panel for `book_lock_wait_seconds` and alert > 200 ms P95.
- **M-2 · Async audit ≠ guaranteed audit.** `ApplicationEventPublisher` async publication can drop events if the JVM dies between commit and event dispatch, violating NFR-6 ("100% of attempts logged").
  - **Impact**: Audit gaps; compliance risk.
  - **Mitigation**: Write the `audit_log` row **inside** the same transaction as the loan (synchronous, same DB). If perf is a concern later, move to transactional outbox — not to fire-and-forget async.
- **M-3 · Rate-limit bypass for authenticated abuse.** Gateway limit is per-IP (100 rps); a single member behind CGNAT or a script from one IP could saturate. The 20 rps/member Bucket4j limit is service-side but the doc doesn't state its storage.
  - **Impact**: NFR-2 degradation; potential DoS of a hot book.
  - **Mitigation**: Confirm Bucket4j uses a shared store (Redis/Hazelcast) so limits hold across pods; otherwise per-pod limits multiply by replica count.
- **M-4 · Feature-flag default and rollout ordering.** `library.borrowing.self-service.enabled=false` is good, but the artifact doesn't specify who owns the flag flip, ring definition, or kill-switch runbook.
  - **Impact**: Slow incident response; ambiguous rollback.
  - **Mitigation**: Document rings (internal → 1% → 10% → 100%), owner, and kill-switch procedure in the Phase 4 plan.

### Low Priority
- **L-1 · ISBN validation.** `@Pattern` on ISBN-13 is planned but the regex isn't stated; ensure it also validates the check digit (Modulus-10), not just the format.
- **L-2 · Clock source for `dueDate`.** Use `Clock` bean (injectable) rather than `LocalDate.now()` directly to make time-based tests deterministic.
- **L-3 · JWT `sub` claim shape.** Confirm the IdP's `sub` is the `memberId` used by the DB (UUID vs. numeric); if not, map via a `memberId` custom claim to avoid a lookup on every request.
- **L-4 · Correlation-ID propagation.** Ensure the gateway injects `X-Correlation-Id` and Logback MDC picks it up — currently implied, not specified.
- **L-5 · DB connection pool sizing.** 10/pod × 3 pods = 30 connections; verify against PostgreSQL `max_connections` budget shared with other tenants of `library` DB.

## Security Review
| Area | Status | Notes |
|---|---|---|
| Authentication | ✅ Adequate | OAuth2 RS + JWT (RS256), JWKS rotation. Enforce `exp` ≤ 15 min as stated. |
| Authorization | ✅ Adequate | `library:borrow` scope + `memberId` from `sub` (not body) — correctly closes IDOR. |
| Encryption in transit | ✅ Adequate | TLS 1.2+ at gateway, HSTS. Recommend TLS 1.3 preferred. |
| Encryption at rest | ⚠️ Not stated | Confirm existing PostgreSQL uses disk encryption; audit_log contains PII (memberId + timestamps). |
| Data protection / PII | ⚠️ Verify | `audit_log` retention 12 months — confirm it matches the org's data-retention policy and GDPR right-to-erasure workflow. |
| Input validation | ✅ Adequate | Bean Validation + length cap; add checksum validation (L-1). |
| Injection | ✅ Adequate | JPA parameter binding only. |
| Secrets | ✅ Adequate | Env / Vault; not in source. |
| Logging | ✅ with M-2 fix | Move audit to synchronous / transactional. |
| Rate limiting | ⚠️ M-3 | Confirm shared Bucket4j store. |
| Dependency scanning | ✅ Adequate | Trivy in CI stated. Add SCA (OWASP Dep-Check or Snyk) if not already present. |

### OWASP Top 10 Validation
| Risk | Coverage | Verdict |
|---|---|---|
| A01 Broken Access Control | JWT scope + `sub`-derived `memberId` | ✅ Pass |
| A02 Cryptographic Failures | TLS 1.2+, RS256; **at-rest unstated** | ⚠️ Verify |
| A03 Injection | JPA binding, `@Pattern` | ✅ Pass |
| A04 Insecure Design | Pessimistic locking for NFR-5 | ✅ Pass (see M-1 tuning) |
| A05 Security Misconfiguration | Actuator restricted, stateless CSRF | ✅ Pass |
| A06 Vulnerable Components | Trivy stated; add SCA | ⚠️ Enhance |
| A07 Auth Failures | Short JWT expiry, JWKS rotation | ✅ Pass |
| A08 Integrity Failures | Flyway checksums | ✅ Pass |
| A09 Logging Failures | Async audit — **fix to sync** (M-2) | ⚠️ Fix |
| A10 SSRF | No outbound calls | ✅ N/A |

## Performance Analysis
- **Expected response time**: With warm connection pool and non-contended book row, checkout is ~2 SELECTs + 1 INSERT + 1 UPDATE + 1 INSERT (audit, per M-2) = ~5–8 ms of DB work; P95 < 500 ms is comfortable (~50× headroom for GC/network).
- **Scalability limits**:
  - Per-book lock throughput is `1 / (txn_hold_time)` ≈ 100–200 checkouts/s for the same title. This is not a real-world constraint (nobody borrows one title 200×/s), but a load-test scenario would show it.
  - PostgreSQL connection budget (M-L5) is the practical horizontal-scale ceiling before adding PgBouncer.
- **Bottlenecks identified**:
  1. Hot-book lock queue (M-1).
  2. Shared DB connection budget (L-5).
  3. Bucket4j backing store, if in-memory (M-3).
- **Caching**: Correctly avoided on the write path. `Member.borrowingLimit` config value can be cached in-memory with 60 s TTL — negligible risk.

## Questions for Architect
1. What is the Bucket4j backing store? (per-pod in-memory vs. Redis)
2. Is PostgreSQL disk encryption in place on the `library` cluster?
3. Confirm the JWT `sub` claim contents (UUID? numeric?) and whether a `memberId` custom claim exists.
4. What is the `PESSIMISTIC_WRITE` lock timeout you propose, and is `book_lock_wait_seconds` already exported by the current Micrometer setup?
5. Who owns the `library.borrowing.self-service.enabled` flag lifecycle (config change process, ring cadence, kill-switch owner)?
6. Any tenant sharing the `library` DB whose connection budget we need to respect?
7. Is `outstanding_fine_balance` eventually consistent (external fines service) — if so, how stale can it be at decision time?

## Recommendations

### Must fix before implementation (block PR merge on these)
1. **M-2**: Move `audit_log` insert into the same transaction as the loan (or transactional outbox). No fire-and-forget async on the audit path.
2. **M-3**: Confirm Bucket4j shared store; if not, add Redis-backed limiter or move throttling entirely to the gateway.

### Should fix during implementation
3. **M-1**: Configure `javax.persistence.lock.timeout=500` on the pessimistic query; return `BOOK_UNAVAILABLE` on timeout; add lock-wait metric + alert.
4. **M-4**: Add rollout/kill-switch runbook to Phase 4 plan.
5. **L-1**: ISBN-13 checksum validation in the DTO (custom `@ValidIsbn13`).
6. **L-2**: Inject `Clock` bean; forbid `LocalDate.now()` in service code (Checkstyle rule).
7. **L-4**: Formalize `X-Correlation-Id` propagation from gateway → MDC → response header.

### Nice to have
8. Add SCA (OWASP Dependency-Check or Snyk) alongside Trivy.
9. Consider TLS 1.3 preferred at the gateway.
10. Cache configuration values (`borrowing.limit`, `borrowing.durationDays`) with 60 s TTL.
11. Emit a domain event `LoanCreated` for future consumers (return flow, recommendations) — cheap to add now.

## Approval Status
- [ ] APPROVED
- [x] **CONDITIONAL** — approved to proceed to Phase 4 planning provided M-2 and M-3 are captured as mandatory tasks in the sprint plan and closed before merge.
- [ ] REJECTED

## Gate Criteria — Phase 3
- ✅ No critical risks remain (2 medium items promoted to must-fix; tracked in Phase 4)
- ✅ Security gaps addressed or explicit follow-up questions raised
- ✅ Architect findings acknowledged (questions §)
- ✅ Mitigation plans documented for every medium/low item
- ✅ `nextPhaseReady = true`

