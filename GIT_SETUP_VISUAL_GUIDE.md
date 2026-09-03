# 🎯 Git Integration Setup — Visual Quick Guide

## The 3-Step Setup (Takes ~9 Minutes)

```
┌─────────────────────────────────────────────────────────────────┐
│ STEP 1: Generate GitLab Personal Access Token (5 min)           │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  URL: https://git.epam.com/-/user_settings/personal_access_tokens
│                                                                  │
│  Click: [Add new token]                                          │
│                                                                  │
│  Fill in:                                                        │
│  Name: SDLC-Pipeline-Bot                                         │
│  Expiration: 365 days                                            │
│  Scopes: ✅ api                                                  │
│          ✅ read_api                                             │
│          ✅ read_repository                                      │
│          ✅ write_repository                                     │
│                                                                  │
│  Click: [Create personal access token]                           │
│                                                                  │
│  RESULT: glpat-XXXXXXXXXXXXXXXX (save this!)                     │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│ STEP 2: Update Configuration File (2 min)                       │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  File: sdlc-pipeline/api-configuration.properties               │
│                                                                  │
│  Line 22: Replace "YOUR_GITLAB_PERSONAL_ACCESS_TOKEN_HERE"      │
│  With:    git.personal.access.token=glpat-XXXXXXX...            │
│                                                                  │
│  Line 26: Get Project ID from repo Settings → General            │
│  Replace: YOUR_GITLAB_PROJECT_ID_HERE                           │
│  With:    git.repo.project.id=12345                             │
│                                                                  │
│  Line 46-47: Already set to your username                       │
│  (sampada_chendake)                                              │
│                                                                  │
│  ✅ SAVE FILE                                                    │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│ STEP 3: Verify Configuration (2 min)                            │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  Command:                                                        │
│  cd sdlc-pipeline                                                │
│  mvn -q exec:java \                                              │
│    "-Dexec.mainClass=com.sdlc.pipeline.config.GitConfigValidator"
│    "-Dexec.args=--validate-git-config"                          │
│                                                                  │
│  Expected Output:                                                │
│  ✅ GitLab API endpoint reachable                                │
│  ✅ Personal access token valid                                  │
│  ✅ Repository accessible                                        │
│  ✅ Project ID verified                                          │
│  ✅ All required fields configured                               │
│  ✅ READY FOR PRODUCTION                                         │
│                                                                  │
│  If all ✅: You're done! Proceed to full pipeline run            │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

---

## What Gets Filled In (3 Fields Only)

### Field #1: Your GitLab PAT
```properties
# sdlc-pipeline/api-configuration.properties, Line 22
git.personal.access.token=glpat-XXXXXXXXXXXXXXXX
```
**Where**: https://git.epam.com/-/user_settings/personal_access_tokens  
**Looks like**: `glpat-...` (starts with "glpat-")

---

### Field #2: Your Project ID
```properties
# sdlc-pipeline/api-configuration.properties, Line 26
git.repo.project.id=12345
```
**Where**: https://git.epam.com/sampada_chendake/growprogramautomation  
**Click**: Settings → General → Find "Project ID"  
**Looks like**: A plain number (e.g., `12345`)

---

### Field #3: Your Username (Already Set ✅)
```properties
# sdlc-pipeline/api-configuration.properties, Lines 46-47
git.pr.assignee.default=sampada_chendake
git.pr.reviewers=sampada_chendake
```
**Already filled in with**: `sampada_chendake`  
**No action needed** ✅

---

## What Happens Next (Phase 8 Automatic Flow)

```
SDLC Pipeline Run
       ↓
Phase 1 → Phase 2 → Phase 3 → Phase 4 → Phase 5 → Phase 6 → Phase 7
       ↓
PHASE 8: PR Coordinator Activates
       ↓
┌──────────────────────────────────────────────┐
│ 1. Create branch: pr/EDJOBSA-22-{timestamp}  │
├──────────────────────────────────────────────┤
│ 2. Copy all Phase 5 code to:                 │
│    • Java → src/main/java/com/library/...    │
│    • Tests → src/test/java/com/library/...   │
│    • SQL → src/main/resources/db/migration/  │
├──────────────────────────────────────────────┤
│ 3. Git commit                                │
│    Author: SDLC Pipeline Bot                 │
│    Message: [EDJOBSA-22] Self-Service...     │
├──────────────────────────────────────────────┤
│ 4. Git push to remote                        │
│    Destination: https://git.epam.com/...     │
│    Branch: pr/EDJOBSA-22-{timestamp}         │
├──────────────────────────────────────────────┤
│ 5. CREATE PULL REQUEST (Automatic!)          │
│    Title: [AUTO] EDJOBSA-22: Self-Service... │
│    Assigned to: You                          │
│    Labels: feature, automated, EDJOBSA-22    │
│    Description: All 8 phase artifacts        │
│    Ready for: Your review + merge            │
└──────────────────────────────────────────────┘
       ↓
✅ PR Ready in GitLab
   https://git.epam.com/sampada_chendake/growprogramautomation/-/merge_requests/123

   You can:
   • Review code online
   • Check test results
   • Read all 8 phase documents
   • Merge with one click
   • Feature branch auto-deleted
```

---

## Repository Structure After Phase 8

```
growprogramautomation/ (Your GitLab Repo)
│
├── main (default branch)
│   └── All code from previous work
│
└── pr/EDJOBSA-22-20260903-144203/ (New feature branch)
    │
    ├── sdlc-pipeline/src/main/java/com/library/borrowing/
    │   ├── Book.java
    │   ├── Member.java
    │   ├── Loan.java
    │   ├── AuditLog.java
    │   ├── BookRepository.java
    │   ├── LoanRepository.java
    │   ├── MemberRepository.java
    │   ├── AuditRepository.java
    │   ├── BorrowingService.java
    │   ├── AuditLogger.java
    │   ├── RateLimitService.java
    │   ├── BorrowingController.java
    │   ├── BorrowingExceptionHandler.java
    │   ├── BorrowingConfig.java
    │   ├── CheckoutRequest.java
    │   ├── LoanResponse.java
    │   └── ErrorResponse.java + 5 exception classes
    │
    ├── sdlc-pipeline/src/test/java/com/library/borrowing/
    │   ├── BorrowingServiceTest.java
    │   └── BorrowingControllerTest.java
    │
    └── sdlc-pipeline/src/main/resources/db/migration/
        └── V20260824_1__Add_Audit_Log.sql
```

---

## Configuration Fields Reference

### Must Fill (3 fields)

| Line | Field | Example | From Where |
|---|---|---|---|
| 22 | `git.personal.access.token` | `glpat-xxxxx` | https://git.epam.com/-/user_settings/personal_access_tokens |
| 26 | `git.repo.project.id` | `12345` | Repo Settings → General |
| 46-47 | `git.pr.assignee.default` + `reviewers` | `sampada_chendake` | Already set ✅ |

### Already Configured (No changes needed)

| Line | Field | Value |
|---|---|---|
| 15-19 | Git connection | `git.epam.com`, GitLab, API v4 |
| 23-25 | Repo URL | Your repo URL |
| 28-30 | Author info | SDLC Pipeline Bot |
| 32-36 | Branch names | Feature + PR prefixes |
| 38-49 | PR settings | Auto-create, title, labels, merge strategy |
| 52-55 | **Code paths** ✅ | Exact directories for Java, tests, SQL |
| 57-61 | Git workflow | Push retries, clone depth |

---

## Before Running Full Pipeline

### ✅ Checklist

- [ ] Fill `git.personal.access.token` (Line 22)
- [ ] Fill `git.repo.project.id` (Line 26)
- [ ] Save `api-configuration.properties`
- [ ] Run validation command (Step 3)
- [ ] All ✅ signs appear in validation output
- [ ] Ready to run full SDLC pipeline!

---

## Run Full Pipeline Command

```bash
cd C:\Users\sampada_chendake\IdeaProjects\CapstoneCopilotProject

# Run with ticket ID
mvn exec:java \
  -Dexec.mainClass=com.sdlc.pipeline.StartWithTicket \
  -Dexec.args=EDJOBSA-22

# Or just run directly
java -cp sdlc-pipeline/target/classes \
  com.sdlc.pipeline.StartWithTicket EDJOBSA-22
```

**What you'll see**:
```
🚀 Starting Phase 1: Requirements Analysis
✅ Phase 1 Gate: All criteria met · nextPhaseReady = true
🚀 Starting Phase 2: Architecture Design
✅ Phase 2 Gate: All criteria met · nextPhaseReady = true
... (Phases 3-7) ...
🚀 Starting Phase 8: PR Coordinator
   Copying code to sdlc-pipeline/src/main/java/com/library/borrowing/
   Creating feature branch: pr/EDJOBSA-22-20260903-144203
   Committing code...
   Pushing to remote...
   Creating Pull Request...
✅ PHASE 8 COMPLETE - PR CREATED!

🎉 Pull Request Created Successfully!
   URL: https://git.epam.com/sampada_chendake/growprogramautomation/-/merge_requests/123
   Branch: pr/EDJOBSA-22-20260903-144203
   Status: READY FOR REVIEW
   Assignee: sampada_chendake
```

---

## Documents Created For You

| File | Purpose | Read Time |
|---|---|---|
| **GIT_INTEGRATION_SETUP.md** | Complete guide with all fields explained, security notes, troubleshooting | 20 min |
| **GIT_CONFIG_QUICK_REFERENCE.md** | Quick reference card with just 3 fields | 2 min |
| **GIT_INTEGRATION_SUMMARY.md** | This document + workflow explanation | 5 min |
| **This file** | Visual quick guide with diagrams | 3 min |

**Start with**: This file (you're reading it!)  
**Then read**: `GIT_CONFIG_QUICK_REFERENCE.md`  
**If questions**: See `GIT_INTEGRATION_SETUP.md`

---

## TL;DR (Too Long; Didn't Read)

1. **Get token**: https://git.epam.com/-/user_settings/personal_access_tokens → Create PAT
2. **Update line 22**: `git.personal.access.token=glpat-XXXXX`
3. **Get project ID**: Repo Settings → General → Copy ID
4. **Update line 26**: `git.repo.project.id=12345`
5. **Verify**: Run validation command (all ✅?)
6. **Run pipeline**: `mvn exec:java -Dexec.mainClass=com.sdlc.pipeline.StartWithTicket -Dexec.args=EDJOBSA-22`
7. **PR created**: Check GitLab for automatic PR in your repo ✅

**Time required**: ~9 minutes setup + pipeline run time

---

## Ready? 🚀

Once you complete Step 3 (verification), you're fully configured!

Proceed to run the full SDLC pipeline and watch Phase 8 automatically create your PR.

**Questions?** See `GIT_INTEGRATION_SETUP.md` § Troubleshooting

