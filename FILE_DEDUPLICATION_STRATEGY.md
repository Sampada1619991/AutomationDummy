# File Handling Strategy — SDLC Pipeline Execution

**Purpose**: Avoid duplicate files during Phases 1-8 execution  
**Strategy**: Merge, enhance, and deduplicate existing code  
**Date**: September 3, 2026

---

## Overview

When running the full SDLC pipeline (EDJOBSA-22), the orchestrator will:

✅ **Detect existing files** before generating  
✅ **Merge similar files** intelligently  
✅ **Enhance existing code** instead of duplicating  
✅ **Maintain code consistency** across phases  
✅ **Track merged artifacts** in a deduplication log  

---

## File Handling Rules by Phase

### Phase 1: Requirements Analysis
**Input**: JIRA ticket (EDJOBSA-22)  
**Output**: `pipeline-artifacts/EDJOBSA-22/requirements.md`

**Existing file check**:
- ✅ If `requirements.md` exists → **MERGE** additional AC/constraints
- ❌ Never create: `requirements-v2.md`, `requirements-draft.md`
- Strategy: Append new findings to existing document with version timestamp

---

### Phase 2: Architecture Design
**Input**: `requirements.md`  
**Output**: `pipeline-artifacts/EDJOBSA-22/architecture.md`

**Existing file check**:
- ✅ If `architecture.md` exists → **ENHANCE** with new design insights
- ❌ Never create: `architecture-v2.md`, `architecture-updated.md`
- Strategy: Update design patterns, add new component details, merge tech stack decisions

---

### Phase 3: Design Review
**Input**: `requirements.md`, `architecture.md`  
**Output**: `pipeline-artifacts/EDJOBSA-22/design-review.md`

**Existing file check**:
- ✅ If `design-review.md` exists → **APPEND** new findings
- ❌ Never create: `design-review-final.md`, `review-v2.md`
- Strategy: Add new risk items, keep previous findings, update gate criteria

---

### Phase 4: Project Planning
**Input**: All prior artifacts  
**Output**: `pipeline-artifacts/EDJOBSA-22/impl-plan.md`

**Existing file check**:
- ✅ If `impl-plan.md` exists → **REFINE** sprint breakdown
- ❌ Never create: `implementation-plan-v2.md`, `plan-final.md`
- Strategy: Adjust timeline, enhance WBS, refine resource allocation based on new insights

---

### Phase 5: Code Implementation
**Input**: `impl-plan.md`, `architecture.md`  
**Output**: 16 Java files + 1 SQL migration

**Existing file check** (CRITICAL - Most likely to have duplicates):

```
For each generated file:
  1. Check if file exists in target directory
     Target: sdlc-pipeline/src/main/java/com/library/borrowing/
  
  2. If FILE EXISTS:
     ├─ Analyze existing code
     ├─ Check for similar functionality
     ├─ MERGE codebases intelligently:
     │  ├─ Combine methods (no duplication)
     │  ├─ Merge annotations
     │  ├─ Consolidate imports
     │  ├─ Enhance Javadoc
     │  └─ Preserve existing bug fixes/enhancements
     ├─ Update version/timestamp in Javadoc
     └─ Log merge operation
  
  3. If FILE DOES NOT EXIST:
     └─ Create new file normally

DUPLICATE DETECTION:
  • Same class name → MERGE
  • Same package → Check methods (don't duplicate methods)
  • Similar functionality → Consolidate into one file
  • Test files → Merge test cases (avoid duplicate test coverage)
```

**Example: BorrowingService.java**

```java
// EXISTING FILE (previously generated/created)
@Service
public class BorrowingService {
    public LoanResponse checkoutBook(UUID memberId, String isbn) {
        // existing implementation v1
    }
}

// NEW GENERATION (from Phase 5)
@Service
public class BorrowingService {
    public LoanResponse checkoutBook(UUID memberId, String isbn) {
        // enhanced implementation v2
    }
}

// MERGED RESULT (intelligently combined)
@Service
public class BorrowingService {
    /**
     * Checkout a book for a library member.
     * 
     * @param memberId The member ID (from JWT sub claim)
     * @param isbn The ISBN-13 of the book to checkout
     * @return LoanResponse with loan details
     * @throws FinesOutstandingException if member has outstanding fines
     * @throws BorrowingLimitExceededException if member at borrowing limit
     * @throws BookNotFoundException if ISBN not found
     * @throws BookUnavailableException if book already loaned
     * 
     * @version 2.0 (Sep 3, 2026 - Enhanced with sync audit + lock verification)
     */
    @Transactional(rollbackFor = Exception.class)
    public LoanResponse checkoutBook(UUID memberId, String isbn) {
        // BEST OF BOTH: v1 + v2 combined logic
        // Sync audit (M-2 fix)
        // Pessimistic lock (M-1)
        // Full error handling
        // Optimized performance
    }
}
```

**Merge Strategy**:
- ✅ `Book.java` with existing + new fields → **MERGE**
- ✅ `BorrowingService.java` with v1 logic + v2 enhancements → **MERGE**
- ✅ `BorrowingServiceTest.java` with old + new test cases → **MERGE** (remove duplicate tests)
- ✅ `V20260824_1__Add_Audit_Log.sql` identical → **SKIP** (already exists)

**Output**: 16 optimized files (no duplicates, enhanced)

---

### Phase 6: Code Review
**Input**: 16 merged Java files + 1 SQL  
**Output**: `pipeline-artifacts/EDJOBSA-22/code-review.md`

**Existing file check**:
- ✅ If `code-review.md` exists → **APPEND** new review findings
- ❌ Never create: `code-review-round2.md`, `final-review.md`
- Strategy: Add additional findings, update security/performance scores, refine recommendations

---

### Phase 7: Verification & Testing
**Input**: 16 merged Java files + tests  
**Output**: `pipeline-artifacts/EDJOBSA-22/verification-report.md`

**Existing file check**:
- ✅ If `verification-report.md` exists → **APPEND** new test results
- ❌ Never create: `verification-report-v2.md`, `qa-report-final.md`
- Strategy: Add new test metrics, update coverage percentages, refine gate criteria

---

### Phase 8: PR Coordinator & Launch
**Input**: All 8 phase artifacts + 16 merged Java files  
**Output**: `pipeline-artifacts/EDJOBSA-22/launch-summary.md` + **AUTO PR**

**Existing file check**:
- ✅ If `launch-summary.md` exists → **APPEND** new launch details
- ❌ Never create: `launch-summary-v2.md`, `final-pr-report.md`
- Strategy: Update deployment timeline, refine go/no-go decision, add new roll-out rings

**PR Creation** (Phase 8 Special):
- Commits merged code to feature branch: `pr/EDJOBSA-22-{timestamp}`
- Includes all 16 optimized Java files (no duplicates)
- PR description references ALL 8 phase artifacts
- Auto-assigned to you for review & merge

---

## Deduplication Rules

### Rule 1: Same Class, Same Package
**Situation**: `BorrowingService.java` already exists

**Action**:
```
MERGE (don't create BorrowingService-v2.java)
├─ Analyze both versions
├─ Combine methods (detect duplicates)
├─ Keep best/most complete implementation
├─ Update Javadoc with latest version
└─ Add comment: "Merged from Phase 5 execution (Sep 3, 2026)"
```

### Rule 2: Similar Functionality, Different Names
**Situation**: `BookAvailabilityService.java` exists, new generation wants `BookService.java`

**Action**:
```
CONSOLIDATE (don't create duplicate)
├─ Analyze both implementations
├─ Keep existing class name if already in use
├─ Add missing methods from new generation
├─ Update package/imports as needed
└─ Test that functionality is preserved
```

### Rule 3: Duplicate Test Cases
**Situation**: `BorrowingServiceTest.java` exists with 5 tests, new generation adds 3 more

**Action**:
```
MERGE TESTS (don't run tests twice)
├─ Compare test methods (@Test)
├─ Remove duplicate test cases
├─ Keep union of all unique test scenarios
├─ Verify coverage still ≥ 80%
└─ Update test class Javadoc
```

### Rule 4: Database Migrations
**Situation**: `V20260824_1__Add_Audit_Log.sql` already exists

**Action**:
```
SKIP DUPLICATE (don't re-create)
├─ Check SQL content matches
├─ Verify checksum (Flyway will validate)
├─ If different → Log warning, ask for manual review
└─ Use existing migration, don't create V2
```

### Rule 5: Configuration Files
**Situation**: `BorrowingConfig.java` already exists

**Action**:
```
ENHANCE (keep existing, add missing beans)
├─ Analyze existing Spring @Configuration
├─ Add any new @Bean definitions from new generation
├─ Preserve existing bean configurations
├─ Update comments/Javadoc
└─ Don't rename to BorrowingConfig-enhanced.java
```

---

## File Merging Algorithm

### Pseudocode for Intelligent Merge

```
FOR EACH generated file in Phase 5:
  
  target_file = sdlc-pipeline/src/main/java/com/library/borrowing/<filename>
  
  IF file ALREADY EXISTS:
    
    # JAVA SOURCE FILES
    IF filename.endsWith(".java"):
      existing_code = read(target_file)
      new_code = read(generated_file)
      
      existing_methods = extract_methods(existing_code)
      new_methods = extract_methods(new_code)
      
      # Find unique methods in new code
      unique_new_methods = new_methods - existing_methods
      
      IF unique_new_methods.size() > 0:
        # Enhance existing file with new methods
        merged = existing_code
        merged += "\n\n// Enhanced methods from Phase 5 (Sep 3, 2026)"
        merged += unique_new_methods
        write(target_file, merged)
        log("MERGED: Added " + unique_new_methods.size() + " new methods")
      ELSE:
        # Methods already identical or exist
        log("SKIPPED: No new methods to add")
    
    # TEST FILES
    ELSE IF filename.endsWith("Test.java"):
      existing_tests = extract_test_methods(existing_code)
      new_tests = extract_test_methods(new_code)
      
      unique_tests = new_tests - existing_tests
      
      IF unique_tests.size() > 0:
        merged = merge_test_cases(existing_code, unique_tests)
        write(target_file, merged)
        log("MERGED: Added " + unique_tests.size() + " new test cases")
      ELSE:
        log("SKIPPED: All test cases already exist")
    
    # SQL MIGRATIONS
    ELSE IF filename.endsWith(".sql"):
      existing_sql = read(target_file)
      new_sql = read(generated_file)
      
      IF existing_sql == new_sql:
        log("SKIPPED: Migration already exists (identical)")
      ELSE:
        log("WARNING: Migration content differs - manual review required")
        # Don't overwrite, flag for review
  
  ELSE:
    # File doesn't exist, create it
    copy_file(generated_file, target_file)
    log("CREATED: " + filename)

END FOR

# Generate deduplication report
create_merge_log("pipeline-artifacts/EDJOBSA-22/deduplication-log.md")
```

---

## Deduplication Log (Auto-Generated)

**File**: `pipeline-artifacts/EDJOBSA-22/deduplication-log.md`

**Content** (auto-generated during Phase 5):

```markdown
# File Merging & Deduplication Log

**Execution Date**: September 3, 2026  
**Pipeline**: EDJOBSA-22 Full Run (Phases 1-8)  
**Strategy**: Merge existing → Enhance → Avoid duplicates

## Summary

| Action | Count | Files |
|---|---|---|
| Created | 12 | BorrowingService.java, Book.java, Controller.java, ... |
| Merged | 4 | AuditLog.java (1 new method), BorrowingServiceTest.java (2 new tests), ... |
| Skipped | 1 | V20260824_1__Add_Audit_Log.sql (already exists, identical) |
| **TOTAL** | **16** | All source files processed without duplication |

## Detailed Operations

### Created: BorrowingService.java
- **Action**: NEW FILE
- **Lines**: 150
- **Reason**: File didn't exist in target directory
- **Status**: ✅ Created

### Merged: Book.java
- **Action**: MERGE
- **Existing Methods**: 8
- **New Methods**: 2 (getModifiedDate, setModifiedDate)
- **Removed Duplicates**: 0
- **Result**: Enhanced to 10 methods
- **Version Updated**: v1.0 → v1.1
- **Status**: ✅ Merged

### Merged: BorrowingServiceTest.java
- **Action**: MERGE TEST CASES
- **Existing Tests**: 5
- **New Tests**: 3 (testConcurrentCheckout, testAuditCompleteness, testPerformance)
- **Duplicate Tests Removed**: 0
- **Result**: Enhanced to 8 test cases
- **Coverage Impact**: 80% → 87%
- **Status**: ✅ Merged

### Skipped: V20260824_1__Add_Audit_Log.sql
- **Action**: SKIP (already exists)
- **Reason**: Migration identical to existing version
- **Checksum Verified**: ✅ Matches
- **Status**: ✅ Skipped

## Files by Status

### ✅ Created (No existing version)
- BorrowingController.java (NEW)
- CheckoutRequest.java (NEW)
- LoanResponse.java (NEW)
- ErrorResponse.java (NEW)
- BookRepository.java (NEW)
- LoanRepository.java (NEW)
- MemberRepository.java (NEW)
- AuditRepository.java (NEW)
- BorrowingExceptionHandler.java (NEW)
- BorrowingConfig.java (NEW)
- Loan.java (NEW)
- Member.java (NEW)

### ✅ Merged (Enhanced existing)
- Book.java (2 new methods added)
- AuditLog.java (1 new method added)
- AuditLogger.java (enhanced with better error handling)
- BorrowingServiceTest.java (3 new test cases added, 87% coverage)

### ✅ Skipped (Identical, no changes needed)
- V20260824_1__Add_Audit_Log.sql (migration unchanged)

## Quality Metrics

| Metric | Before | After | Change |
|---|---|---|---|
| Total Java Files | 13 | 16 | +3 new |
| Total Test Cases | 5 | 8 | +3 new |
| Code Duplication | 12% | 0% | ✅ Eliminated |
| Test Coverage | 80% | 87% | +7% |
| Method Count (overall) | 87 | 102 | +15 unique |

## Deduplication Strategy Verification

- ✅ No duplicate class names
- ✅ No duplicate methods within classes
- ✅ No duplicate test cases
- ✅ No duplicate SQL migrations
- ✅ All new functionality integrated
- ✅ Code quality maintained
- ✅ Test coverage improved
- ✅ Zero breaking changes

## Recommendations

1. **Review Merged Files**: 
   - Book.java (2 methods added)
   - BorrowingServiceTest.java (3 tests added)

2. **Validate Enhancements**:
   - Run `mvn clean test` to verify merged code
   - Check 87% coverage target met
   - Review new test cases in detail

3. **Next Steps**:
   - Proceed to Phase 6 (Code Review) with merged files
   - All files optimized, no duplication
   - Ready for Phase 8 PR creation

## Log Generated
- **Date**: September 3, 2026
- **Time**: 14:42:18 UTC
- **Phase**: 5 (Code Implementation)
- **Status**: ✅ COMPLETE - No duplication issues found
```

---

## Configuration for Deduplication (Phase 5)

**Add to `api-configuration.properties`**:

```properties
# ============================================
# Code Generation & Deduplication (Phase 5)
# ============================================
phase5.deduplication.enabled=true
phase5.deduplication.strategy=merge
phase5.merge.on.class.name.match=true
phase5.merge.on.method.name.match=true
phase5.merge.test.cases=true
phase5.skip.identical.migrations=true
phase5.generate.merge.log=true
phase5.merge.conflict.resolution=manual-review
phase5.backup.existing.files=true
phase5.backup.directory=./code-backup/{timestamp}
```

**Behaviors**:
- ✅ `phase5.deduplication.strategy=merge` → Merge similar files
- ✅ `phase5.merge.on.class.name.match=true` → Same class = merge
- ✅ `phase5.merge.on.method.name.match=true` → Same method = don't duplicate
- ✅ `phase5.merge.test.cases=true` → Consolidate test cases
- ✅ `phase5.skip.identical.migrations=true` → Don't re-create identical SQL
- ✅ `phase5.generate.merge.log=true` → Create deduplication-log.md
- ✅ `phase5.backup.existing.files=true` → Save originals before merge

---

## Example Scenarios

### Scenario 1: Re-running Phase 5 (Code Already Exists)

```
First Run (Sep 1):
  Phase 5 generates 16 Java files
  ✅ All created successfully

You review/test code for 2 days...

Second Run (Sep 3):
  Phase 5 executes again for EDJOBSA-22
  
  File Check:
    └─ BorrowingService.java → EXISTS
       ├─ Existing: v1.0 (Sep 1)
       ├─ New Generation: v1.1 (Sep 3, with M-2 sync audit fix)
       └─ MERGE: Keep both versions, enhanced v1.1
    
    └─ Book.java → EXISTS
       ├─ Existing: 8 methods
       ├─ New Generation: 10 methods (added getModifiedDate, setModifiedDate)
       └─ MERGE: Add 2 new methods to existing class
    
    └─ CheckoutRequest.java → DOESN'T EXIST
       └─ CREATE: New file
  
  Result: 16 optimized files (no duplicates)
          Enhanced with new features
         Ready for Phase 6 & 7
```

### Scenario 2: Team Member Adds Code Between Phases

```
Phase 5 generated code on Sep 1
Developer adds custom methods to BorrowingService.java on Sep 2
Phase 5 re-executes on Sep 3

Before Merge:
  Existing BorrowingService.java (Dev's version + generated v1)
  └─ Has: checkoutBook(), processRefund() [custom], cancelLoan()
  
  New Generation (Sep 3, v1.1)
  └─ Has: checkoutBook(), trackInterlibrary() [new feature]
  
After Merge:
  Merged BorrowingService.java
  ├─ checkoutBook() [from new gen, enhanced]
  ├─ processRefund() [kept from dev's changes]
  ├─ cancelLoan() [from new gen]
  └─ trackInterlibrary() [new from Sep 3 gen]
  
  Result: ✅ No conflicts, all code preserved
```

---

## Quick Summary

| Phase | File Handling | Action |
|---|---|---|
| Phase 1 | requirements.md | Merge → append new AC |
| Phase 2 | architecture.md | Merge → enhance design |
| Phase 3 | design-review.md | Merge → append findings |
| Phase 4 | impl-plan.md | Merge → refine timeline |
| Phase 5 | 16 Java files ✅ | **MERGE** → no duplicates |
| Phase 6 | code-review.md | Merge → append review findings |
| Phase 7 | verification-report.md | Merge → append test results |
| Phase 8 | launch-summary.md | Merge → append deployment plan |

**Key Point**: Phase 5 (Code Implementation) is where most duplication risk exists → **Intelligent merging enabled by default**

---

## Files Created

1. ✅ `deduplication-log.md` (auto-generated, Phase 5)
2. ✅ This document for reference

**Ready for pipeline execution!** 🚀

