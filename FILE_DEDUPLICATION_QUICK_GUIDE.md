# 📋 File Deduplication Quick Reference

**Purpose**: Ensure NO duplicate files during full SDLC pipeline execution (Phases 1-8)  
**Applies To**: EDJOBSA-22 and all future pipeline runs  
**Status**: ✅ Enabled by default

---

## What This Means

### Before (Without Deduplication)
```
Phase 5 (first run): Generate BorrowingService.java
  ✅ File created: sdlc-pipeline/src/main/java/com/library/borrowing/BorrowingService.java

You review code, add enhancements...

Phase 5 (re-run): Generate BorrowingService.java again
  ❌ PROBLEM: Creates BorrowingService-v2.java (DUPLICATE!)
  ❌ PROBLEM: Creates BorrowingService-enhanced.java (DUPLICATE!)
  ❌ PROBLEM: Creates BorrowingService-final.java (DUPLICATE!)
  
Result: 3 versions of same class, confusion!
```

### After (With Deduplication ✅)
```
Phase 5 (first run): Generate BorrowingService.java
  ✅ File created: sdlc-pipeline/src/main/java/com/library/borrowing/BorrowingService.java

You review code, add enhancements...

Phase 5 (re-run): Generate BorrowingService.java again
  ✅ SMART MERGE: Detects existing file
  ✅ MERGE: Combines existing + new methods intelligently
  ✅ ENHANCE: Updates documentation, improves code
  ❌ NEVER creates: BorrowingService-v2.java, _old, -draft
  
Result: Single optimized BorrowingService.java class
         All methods preserved, duplicates eliminated
         Code quality improved!
```

---

## How It Works (Simplified)

### Phase 5: Code Generation with Intelligent Merging

```
FOR EACH Java file being generated:

  ✅ STEP 1: Check if file already exists
  
  ✅ STEP 2: If EXISTS → MERGE mode
     ├─ Extract methods from existing file
     ├─ Extract methods from new generation
     ├─ Find only NEW methods (new generation - existing)
     ├─ Add new methods to existing file
     ├─ Update Javadoc with version/timestamp
     └─ LOG: "MERGED: Added 2 new methods"
  
  ✅ STEP 3: If DOESN'T EXIST → CREATE mode
     ├─ Create new file normally
     └─ LOG: "CREATED: New file"
  
  ✅ STEP 4: Special handling for tests
     ├─ Extract test methods from both versions
     ├─ Merge test cases (avoid duplicate test coverage)
     └─ Improve overall coverage %
  
  ✅ STEP 5: Special handling for SQL
     ├─ If migration identical → SKIP
     ├─ If migration differs → FLAG for manual review
     └─ NEVER create V2, V3 migrations

RESULT: No duplicates, code enhanced, everything merged!
```

---

## File Handling by Type

### Java Source Files (.java)

**Default Behavior**: `MERGE` if file exists

```
❌ DON'T create:
  • BorrowingService-v2.java
  • BorrowingService-enhanced.java
  • BorrowingService-final.java
  • BorrowingService_old.java
  • BorrowingService.backup.java

✅ DO merge into:
  • BorrowingService.java (original, enhanced)
```

**Example**:
```java
// EXISTING file (Sep 1)
public class BorrowingService {
    public LoanResponse checkoutBook(...) { ... }
    public void cancelLoan(...) { ... }
}

// NEW GENERATION (Sep 3)
public class BorrowingService {
    public LoanResponse checkoutBook(...) { ... }  // Same
    public void trackInterlibrary(...) { ... }     // NEW
}

// MERGED RESULT
public class BorrowingService {
    public LoanResponse checkoutBook(...) { ... }  // Enhanced
    public void cancelLoan(...) { ... }            // From Sep 1
    public void trackInterlibrary(...) { ... }    // NEW from Sep 3
}
// Version updated in Javadoc: v1.0 → v1.1
```

### Test Files (.java with "Test" suffix)

**Default Behavior**: `MERGE` test cases

```
❌ DON'T create:
  • BorrowingServiceTest-v2.java
  • BorrowingServiceTest-additional.java
  • BorrowingServiceTest-phase2.java

✅ DO merge into:
  • BorrowingServiceTest.java (original, enhanced with new tests)
```

**Example**:
```java
// EXISTING tests (Sep 1, 5 test methods)
@Test void testSuccessfulCheckout() { ... }
@Test void testCheckoutWithFines() { ... }
@Test void testCheckoutWithLimitExceeded() { ... }
@Test void testCheckoutBookUnavailable() { ... }
@Test void testCheckoutBookNotFound() { ... }

// NEW GENERATION (Sep 3, 8 test methods)
// ... 5 existing tests + 3 new tests

// MERGED RESULT (Sep 3, 8 unique test methods)
@Test void testSuccessfulCheckout() { ... }       // Original
@Test void testCheckoutWithFines() { ... }        // Original
@Test void testCheckoutWithLimitExceeded() { ... } // Original
@Test void testCheckoutBookUnavailable() { ... }  // Original
@Test void testCheckoutBookNotFound() { ... }    // Original
@Test void testConcurrentCheckout() { ... }       // NEW
@Test void testAuditCompleteness() { ... }        // NEW
@Test void testPerformanceMetrics() { ... }       // NEW

// Coverage: 80% → 87%
```

### SQL Migration Files (.sql)

**Default Behavior**: `SKIP` if identical, `FLAG` if different

```
❌ DON'T create:
  • V20260824_1__Add_Audit_Log_v2.sql
  • V20260824_1__Add_Audit_Log.backup.sql
  • V20260825_...

✅ DO use:
  • V20260824_1__Add_Audit_Log.sql (original migration)
```

**Scenarios**:

```
Scenario 1: Identical migration
  Existing: V20260824_1__Add_Audit_Log.sql (from Sep 1)
  New Gen:  V20260824_1__Add_Audit_Log.sql (from Sep 3, identical)
  Action:   SKIP (migration already exists, checksum matches)
  Result:   ✅ No duplicate migration

Scenario 2: Migration changed
  Existing: V20260824_1__Add_Audit_Log.sql (from Sep 1)
  New Gen:  V20260824_1__Add_Audit_Log.sql (from Sep 3, different DDL)
  Action:   FLAG for manual review (content changed!)
  Result:   ⚠️ Ask developer to review changes
```

### Configuration Files

**Default Behavior**: `MERGE` if exists

```
❌ DON'T create:
  • BorrowingConfig-v2.java
  • BorrowingConfig-enhanced.java
  • BorrowingConfig-updated.java

✅ DO merge into:
  • BorrowingConfig.java (original, enhanced with new @Beans)
```

---

## Configuration Settings

**File**: `sdlc-pipeline/api-configuration.properties` (Lines 63–74)

```properties
# Enable deduplication
phase5.deduplication.enabled=true

# Strategy: "merge" or "overwrite"
phase5.deduplication.strategy=merge

# Merge on identical class names
phase5.merge.on.class.name.match=true

# Merge on identical method names (don't duplicate methods)
phase5.merge.on.method.name.match=true

# Consolidate test cases (avoid duplicate test coverage)
phase5.merge.test.cases=true

# Skip SQL migrations if identical
phase5.skip.identical.migrations=true

# Auto-generate merge log (deduplication-log.md)
phase5.generate.merge.log=true

# Conflict resolution: "manual-review" or "auto-merge"
phase5.merge.conflict.resolution=manual-review

# Backup existing files before merge
phase5.backup.existing.files=true

# Backup location (uses timestamp)
phase5.backup.directory=./code-backup/{timestamp}
```

---

## Deduplication Log (Auto-Generated)

**File**: `pipeline-artifacts/EDJOBSA-22/deduplication-log.md`

**Auto-generated during Phase 5** with:

```markdown
Summary
├─ Created: 12 new files
├─ Merged: 4 existing files (enhanced)
├─ Skipped: 1 SQL migration (already exists)
└─ Total: 16 files (zero duplicates)

Detailed Operations
├─ BorrowingService.java: MERGED (added 1 method)
├─ Book.java: MERGED (added 2 properties)
├─ BorrowingServiceTest.java: MERGED (added 3 test cases)
├─ V20260824_1__Add_Audit_Log.sql: SKIPPED (identical)
└─ ... (all 16 files)

Quality Metrics
├─ Code Duplication: 12% → 0% ✅
├─ Test Coverage: 80% → 87% ✅
└─ Duplicates Eliminated: 5 potential duplicates → 0
```

---

## Examples: What Gets Merged vs Created

### Scenario 1: First Run (Sep 1)

```
Phase 5 Generation:

✅ Book.java                    → CREATE (new file)
✅ Member.java                  → CREATE (new file)
✅ Loan.java                    → CREATE (new file)
✅ BorrowingService.java        → CREATE (new file)
✅ BorrowingController.java     → CREATE (new file)
✅ BorrowingServiceTest.java    → CREATE (new file)
✅ ... (10 more files)          → CREATE (all new)

Result: 16 new Java files created
        No merging needed (nothing existed)
```

### Scenario 2: Second Run (Sep 3, Re-executing same ticket)

```
Phase 5 Generation:

Target directory already has: 16 files from Sep 1

✅ Book.java                    → MERGE (existing found)
   ├─ Sep 1 version: 8 properties
   ├─ Sep 3 version: 10 properties (added createdAt, modifiedAt)
   └─ Result: 10 properties (no duplication)

✅ Member.java                  → MERGE (existing found)
   ├─ Sep 1 version: 12 methods
   ├─ Sep 3 version: 12 methods (identical)
   └─ Result: 12 methods (no new methods to add)

✅ BorrowingService.java        → MERGE (existing found)
   ├─ Sep 1 version: 5 methods
   ├─ Sep 3 version: 6 methods (added sync audit fix per M-2)
   └─ Result: 6 methods (1 new method merged)

✅ BorrowingServiceTest.java    → MERGE (existing found)
   ├─ Sep 1 version: 5 test cases (80% coverage)
   ├─ Sep 3 version: 8 test cases (87% coverage)
   └─ Result: 8 test cases (3 new tests merged, 0 duplicates)

✅ V20260824_1__Add_Audit_Log.sql → SKIP (identical)
   ├─ Sep 1 version: Migration for audit_log table
   ├─ Sep 3 version: Same migration (identical checksum)
   └─ Result: SKIP (don't re-create)

✅ ... (10 more files)          → MERGE/CREATE as needed

Result: Smart merging, no duplicates, everything enhanced!
        Deduplication log: CREATED
```

### Scenario 3: Code Added by Developer

```
You manually add methods to existing file:
  BorrowingService.java (Sep 2, developer added: processRefund())

Phase 5 Re-executes (Sep 3):
  New generation: checkoutBook(), trackInterlibrary()

Merge Processing:
  ├─ Sep 1 version (generated):    checkoutBook(), cancelLoan()
  ├─ Sep 2 version (dev updated):  checkoutBook(), cancelLoan(), processRefund()
  ├─ Sep 3 version (new gen):      checkoutBook(), trackInterlibrary()
  └─ MERGED RESULT:                checkoutBook(), cancelLoan(), processRefund(), trackInterlibrary()

Result: ✅ All code preserved (generated + manual + new generation)
        ✅ No conflicts
        ✅ Developer's processRefund() not deleted
```

---

## When Deduplication Happens

| Phase | Action | File Handling |
|---|---|---|
| Phase 1 | Generate requirements.md | Merge (append to existing) |
| Phase 2 | Generate architecture.md | Merge (enhance existing) |
| Phase 3 | Generate design-review.md | Merge (append findings) |
| Phase 4 | Generate impl-plan.md | Merge (refine existing) |
| **Phase 5** | **Generate 16 Java files** | **MERGE/CREATE intelligently** ✅ |
| Phase 6 | Generate code-review.md | Merge (append review) |
| Phase 7 | Generate verification-report.md | Merge (append tests) |
| Phase 8 | Generate launch-summary.md | Merge (append launch plan) |

---

## Benefits

✅ **No Duplicates**: BorrowingService.java, not BorrowingService-v2.java  
✅ **Enhanced Code**: Each run improves/adds to existing code  
✅ **Preserved Changes**: Developer customizations not lost  
✅ **Better Coverage**: Test cases are merged, not duplicated  
✅ **Clean History**: Only one version of each file  
✅ **Automatic Backup**: Previous versions backed up before merge  
✅ **Audit Trail**: Deduplication log tracks all merges  

---

## What You'll See During Pipeline Run

```
🚀 Phase 5: Code Implementation

Deduplication Check...
├─ Scanning target directory: sdlc-pipeline/src/main/java/com/library/borrowing/
├─ Found existing 16 files (from Sep 1)
├─ Deduplication mode: ENABLED (merge strategy)
├─ Processing 16 files...
│
├─ ✅ Book.java: MERGED (added 2 new properties)
├─ ✅ Member.java: MERGED (no new methods)
├─ ✅ Loan.java: CREATED (new file)
├─ ✅ BorrowingService.java: MERGED (added sync audit fix per M-2)
├─ ✅ BorrowingServiceTest.java: MERGED (added 3 new test cases, 87% coverage)
├─ ✅ V20260824_1__Add_Audit_Log.sql: SKIPPED (identical, checksum match)
├─ ✅ ... (10 more files)
│
├─ Backup created: ./code-backup/20260903-144203/
├─ Deduplication log created: pipeline-artifacts/EDJOBSA-22/deduplication-log.md
│
└─ SUMMARY:
   ✅ Created: 0 new files
   ✅ Merged: 15 existing files (enhanced)
   ✅ Skipped: 1 migration (identical)
   ✅ Duplicates eliminated: 5 potential → 0 actual
   ✅ Code coverage improved: 80% → 87%

Phase 5 Complete ✅
```

---

## Quick Checklist

- [x] Deduplication enabled? **YES** (default)
- [x] Merge strategy? **YES** (class names matched)
- [x] Backup before merge? **YES** (./code-backup/)
- [x] Test case merging? **YES** (no duplicate tests)
- [x] SQL migration handling? **YES** (skip if identical)
- [x] Deduplication log? **YES** (auto-generated)
- [x] Ready for pipeline? **YES** ✅

---

## Summary

| Aspect | Behavior |
|---|---|
| **Duplicate Prevention** | ✅ Automatic, enabled by default |
| **Merge Strategy** | ✅ Smart merging by class/method name |
| **Conflict Resolution** | ✅ Manual review if migration differs |
| **Backup** | ✅ Automatic before any merge |
| **Audit Trail** | ✅ Deduplication log generated |
| **File Organization** | ✅ Single optimized version of each file |
| **Code Quality** | ✅ Improved through merging |

**Result**: No duplicate files during full pipeline execution! 🚀

