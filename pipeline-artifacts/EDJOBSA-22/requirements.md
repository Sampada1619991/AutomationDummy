# Requirements Document

**Ticket**: EDJOBSA-22
**Project**: CCAP
**Type**: Story · **Priority**: Medium · **Status**: Open
**Reporter**: Sampada Chendake

## User Story
> Currently, library members must manually request books at the front desk, creating long wait times. We want to introduce a self-service checkout process on our web portal. Logged-in members should be able to scan or select a book, verify its availability, and check it out instantly if they do not have outstanding fines or have not exceeded their borrowing limits.

**As a** logged-in library member
**I want** to check out a book from the web portal without visiting the front desk
**So that** I can borrow books instantly and avoid queues.

## Functional Requirements
- **FR-1** Members shall authenticate before accessing the self-service checkout.
- **FR-2** The system shall accept a book identifier either via manual selection (search by title/ISBN) or by barcode scan (ISBN-13).
- **FR-3** The system shall verify the book exists and its `status = AVAILABLE` before allowing checkout.
- **FR-4** The system shall reject checkout if the member has any `outstandingFineBalance > 0`.
- **FR-5** The system shall reject checkout if the member has reached the configured borrowing limit (default: 5 active loans).
- **FR-6** On successful checkout, the system shall create a `Loan` record, mark the book `status = LOANED`, and return a due date (default: today + 14 days).
- **FR-7** The system shall return a machine-readable error code (`FINES_OUTSTANDING`, `LIMIT_EXCEEDED`, `BOOK_UNAVAILABLE`, `BOOK_NOT_FOUND`) on failure.
- **FR-8** Every checkout attempt (success or failure) shall be audit-logged with `memberId`, `bookId`, `timestamp`, `outcome`.

## Non-Functional Requirements
- **NFR-1 Performance**: P95 checkout latency < 500 ms; P99 < 2000 ms.
- **NFR-2 Throughput**: ≥ 200 checkouts/second sustained.
- **NFR-3 Availability**: 99.9% monthly.
- **NFR-4 Security**: OAuth2 + JWT bearer; all endpoints TLS 1.2+; OWASP Top 10 compliant.
- **NFR-5 Concurrency**: Two members checking out the same book simultaneously → exactly one succeeds (no double-loan).
- **NFR-6 Auditability**: 100% of checkout attempts logged; retained 12 months.
- **NFR-7 Accessibility**: Web UI complies with WCAG 2.1 AA.

## Acceptance Criteria
- [ ] Given a logged-in member with 0 active loans and no fines, when they check out an AVAILABLE book, then a Loan is created with a 14-day due date and the response is HTTP 201.
- [ ] Given a member with `outstandingFineBalance > 0`, when they attempt checkout, then the response is HTTP 409 with error code `FINES_OUTSTANDING` and no Loan is created.
- [ ] Given a member with 5 active loans, when they attempt to check out a 6th book, then the response is HTTP 409 with error code `LIMIT_EXCEEDED`.
- [ ] Given a book with `status = LOANED`, when a member attempts checkout, then the response is HTTP 409 with error code `BOOK_UNAVAILABLE`.
- [ ] Given an unknown ISBN, when a member attempts checkout, then the response is HTTP 404 with error code `BOOK_NOT_FOUND`.
- [ ] Given two concurrent requests for the same book, exactly one succeeds and the other returns `BOOK_UNAVAILABLE`.
- [ ] Every attempt appears in the audit log within 1 second of the request.

## Constraints
- Must integrate with the existing member auth service (OAuth2/JWT already provisioned).
- Must reuse the existing PostgreSQL `library` database and its `book`, `member`, `loan` tables (schema additions only, no destructive migrations).
- Frontend framework is fixed to the existing React web portal.
- 5-week delivery window.

## Assumptions
- Barcode scanning on the client is handled by an existing JS library; backend receives a plain ISBN-13 string.
- Fine calculation is out of scope — we consume the `member.outstanding_fine_balance` field as-is.
- Loan duration (14 days) and borrowing limit (5) are configurable via `library.borrowing.*` properties.
- Return / renewal flows are out of scope for this ticket.

## Success Metrics
- 70% of checkouts happen via self-service within 60 days of launch.
- Average front-desk wait time drops by ≥ 40%.
- Zero double-loan incidents.

## Gate Criteria — Phase 1
- ✅ All requirements documented
- ✅ Success metrics defined
- ✅ No blocking ambiguities
- ✅ `nextPhaseReady = true`
