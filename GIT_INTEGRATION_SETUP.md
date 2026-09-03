# Git Integration Setup Guide for SDLC Pipeline

**Project**: EDJOBSA-22 (Self-Service Book Borrowing)  
**Git Repository**: https://git.epam.com/sampada_chendake/growprogramautomation  
**Date**: September 3, 2026

---

## Overview

Your SDLC Pipeline has been updated to **automatically create Pull Requests** at the final stage (Phase 8). This guide explains each configuration needed in `api-configuration.properties`.

---

## Step 1: Get Your GitLab Personal Access Token

### How to Generate PAT

1. **Go to GitLab Profile**:
   - Log in to `https://git.epam.com`
   - Click your avatar (top-right) → **Preferences**

2. **Navigate to Access Tokens**:
   - Left sidebar → **Access Tokens**
   - Or direct URL: `https://git.epam.com/-/user_settings/personal_access_tokens`

3. **Create New Token**:
   - Click **Add new token**
   - **Token name**: `SDLC-Pipeline-Bot`
   - **Expiration date**: 365 days (adjust as needed)
   - **Scopes** (select all):
     - ✅ `api` — Full API access
     - ✅ `read_api` — Read API
     - ✅ `read_repository` — Read repository
     - ✅ `write_repository` — Write repository (for commits)
   - Click **Create personal access token**

4. **Copy the Token**:
   - ⚠️ **IMPORTANT**: Copy immediately and store securely
   - This is the only time you'll see it
   - Format: `glpat-XXXXXXXXXXXXXXXX`

5. **Store in Config**:
   ```properties
   git.personal.access.token=glpat-XXXXXXXXXXXXXXXX
   ```

---

## Step 2: Find Your GitLab Project ID

### Method A: Via GitLab UI
1. Go to your repo: `https://git.epam.com/sampada_chendake/growprogramautomation`
2. Click **Settings** → **General**
3. Look for **Project ID** (top section, under project name)
4. Example: `12345`

### Method B: Via GitLab API
```bash
curl -H "PRIVATE-TOKEN: YOUR_TOKEN" \
  "https://git.epam.com/api/v4/projects?search=growprogramautomation"
```

### Store in Config
```properties
git.repo.project.id=12345
```

---

## Step 3: Configure All Required Fields

Update `sdlc-pipeline/api-configuration.properties` with your actual values:

### Required (Must Fill)

| Field | Your Value | Notes |
|---|---|---|
| `git.personal.access.token` | `glpat-XXXXXX...` | GitLab PAT (from Step 1) |
| `git.repo.project.id` | `12345` | Project ID (from Step 2) |
| `git.pr.assignee.default` | `sampada_chendake` | Your EPAM username |
| `git.pr.reviewers` | `sampada_chendake` | Comma-separated; at least yourself |

### Optional (Pre-filled, Adjust if Needed)

| Field | Current Value | Change If... |
|---|---|---|
| `git.base.url` | `https://git.epam.com` | Using different GitLab instance |
| `git.branch.main` | `main` | Your repo uses `master` instead |
| `git.user.email` | `robot.sdlc@epam.com` | Different automation email |
| `git.pr.merge.strategy` | `squash` | Prefer `merge` or `rebase` |

---

## Complete Configuration Template

Replace placeholders with your actual values:

```properties
# ============================================
# GitLab Integration (EPAM Git - Primary)
# ============================================
git.enabled=true
git.provider=gitlab
git.platform=epam
git.base.url=https://git.epam.com
git.api.url=https://git.epam.com/api/v4

# GitLab Credentials & Repository
git.personal.access.token=glpat-XXXXXXXXXXXXXXXX                    ← YOUR PAT here
git.repo.url=https://git.epam.com/sampada_chendake/growprogramautomation.git
git.repo.group=sampada_chendake
git.repo.name=growprogramautomation
git.repo.project.id=12345                                           ← YOUR PROJECT ID here

# Git User Configuration (for commits)
git.user.name=SDLC Pipeline Bot
git.user.email=robot.sdlc@epam.com

# Branch Configuration
git.branch.main=main
git.branch.develop=develop
git.branch.feature.prefix=feature/EDJOBSA-22-
git.pull.request.branch.prefix=pr/EDJOBSA-22-

# PR Configuration
git.pr.enabled=true
git.pr.auto.create=true
git.pr.title.template=[AUTO] EDJOBSA-22: Self-Service Book Borrowing - Phase 8 Submission
git.pr.description.include.artifacts=true
git.pr.description.include.tests=true
git.pr.description.include.qa.sign.off=true
git.pr.labels=feature,automated,sdlc-pipeline,edjobsa-22
git.pr.assignee.default=sampada_chendake                            ← YOUR USERNAME here
git.pr.reviewers=sampada_chendake                                   ← YOUR USERNAME here
git.pr.merge.strategy=squash
git.pr.delete.branch.after.merge=true

# Code Deployment Path
code.output.directory=sdlc-pipeline/src/main/java/com/library/borrowing
code.test.directory=sdlc-pipeline/src/test/java/com/library/borrowing
code.db.migration.directory=sdlc-pipeline/src/main/resources/db/migration
code.overwrite.existing=true

# Git Workflow
git.workflow.clone.depth=1
git.workflow.commit.sign=false
git.workflow.push.retries=3
git.workflow.push.retry.delay.ms=2000
```

---

## Step 4: Verify Configuration

Run this verification command:

```bash
cd sdlc-pipeline
mvn -q exec:java \
  "-Dexec.mainClass=com.sdlc.pipeline.config.GitConfigValidator" \
  "-Dexec.args=--validate-git-config"
```

**Expected output**:
```
✅ GitLab API endpoint reachable
✅ Personal access token valid
✅ Repository accessible
✅ Project ID verified: sampada_chendake/growprogramautomation (ID: 12345)
✅ All required fields configured
```

---

## Step 5: Phase 8 Automatic PR Creation Flow

When you run the full SDLC pipeline (Phases 1–8) with `EDJOBSA-22`:

### What Happens Automatically:

```
Phase 8: PR Coordinator
↓
1. Code from Phase 5 → Copied to:
   - sdlc-pipeline/src/main/java/com/library/borrowing/
   - sdlc-pipeline/src/test/java/com/library/borrowing/
   - sdlc-pipeline/src/main/resources/db/migration/

2. Create feature branch:
   - Git clone repo (shallow)
   - Branch name: pr/EDJOBSA-22-{timestamp}
   - Base branch: main

3. Commit code:
   - Add all Java + SQL files
   - Commit message: "[EDJOBSA-22] Self-Service Borrowing - Phase 5 Artifacts"
   - Committer: "SDLC Pipeline Bot <robot.sdlc@epam.com>"

4. Push to remote:
   - Push feature branch to git.epam.com
   - Retry 3× on network failure

5. Create Pull Request (Automated):
   - Title: "[AUTO] EDJOBSA-22: Self-Service Book Borrowing - Phase 8 Submission"
   - Description: (includes all 8 phase artifacts + test results)
   - Labels: feature, automated, sdlc-pipeline, edjobsa-22
   - Assignee: sampada_chendake (you)
   - Reviewers: sampada_chendake (you)
   - Merge strategy: Squash
   - Auto-delete branch after merge: Yes

6. Output Summary:
   ✅ PR URL: https://git.epam.com/sampada_chendake/growprogramautomation/-/merge_requests/123
   ✅ All artifacts linked in PR description
   ✅ Ready for review & merge
```

---

## Field Explanation

### Git Connection Fields

```properties
git.provider=gitlab                    # GitLab (vs GitHub, Bitbucket, etc.)
git.platform=epam                      # Instance name (EPAM git)
git.base.url=https://git.epam.com      # GitLab web URL
git.api.url=https://git.epam.com/api/v4  # GitLab API v4 endpoint
git.personal.access.token=glpat-...    # Your authentication token
```

### Repository Fields

```properties
git.repo.url=...                       # Full clone URL
git.repo.group=sampada_chendake        # Your namespace/group
git.repo.name=growprogramautomation    # Repository name
git.repo.project.id=12345              # Numeric project ID (required for API)
```

### User Fields (For Commits)

```properties
git.user.name=SDLC Pipeline Bot        # Author name on commits
git.user.email=robot.sdlc@epam.com     # Author email on commits
```

### Branch Fields

```properties
git.branch.main=main                   # Primary branch (default base for PR)
git.branch.develop=develop             # Development branch (unused for this PR)
git.branch.feature.prefix=feature/...  # Prefix for feature branches
git.pull.request.branch.prefix=pr/...  # Prefix for PR branches
```

### PR Customization Fields

```properties
git.pr.enabled=true                    # Enable PR creation
git.pr.auto.create=true                # Automatically create PR
git.pr.title.template=...              # PR title format
git.pr.description.include.*=true      # Include phase artifacts in description
git.pr.labels=...                      # Labels to assign
git.pr.assignee.default=sampada_...    # Assign to this user
git.pr.reviewers=sampada_...           # Request review from (comma-separated)
git.pr.merge.strategy=squash            # squash | merge | rebase
git.pr.delete.branch.after.merge=true  # Clean up feature branch after merge
```

### Code Deployment Paths

```properties
code.output.directory=...              # Where Java source files are placed
code.test.directory=...                # Where test files are placed
code.db.migration.directory=...        # Where Flyway SQL migrations go
code.overwrite.existing=true           # Overwrite existing files
```

### Git Workflow Fields

```properties
git.workflow.clone.depth=1             # Shallow clone (faster)
git.workflow.commit.sign=false         # GPG sign commits (set true if org requires)
git.workflow.push.retries=3            # Retry count on push failure
git.workflow.push.retry.delay.ms=2000  # Delay between retries
```

---

## Security Best Practices

### ⚠️ DO NOT

- ❌ Commit the PAT to git
- ❌ Share the token in Slack/email
- ❌ Use in user-level environment variables
- ❌ Store in plain-text config files without encryption

### ✅ DO

- ✅ Use environment variable override at runtime:
  ```bash
  export GIT_PERSONAL_ACCESS_TOKEN=glpat-XXXXXXX
  mvn clean package
  ```
  Then in `api-configuration.properties`:
  ```properties
  git.personal.access.token=${env.GIT_PERSONAL_ACCESS_TOKEN}
  ```

- ✅ Or use Maven secrets plugin:
  ```bash
  mvn -Dgit.token=glpat-XXXXXXX
  ```

- ✅ Or use CI/CD secrets (GitHub Actions, GitLab CI):
  ```yaml
  env:
    GIT_PERSONAL_ACCESS_TOKEN: ${{ secrets.EPAM_GIT_PAT }}
  ```

---

## Troubleshooting

### Issue 1: "401 Unauthorized"
**Cause**: Invalid or expired PAT
**Fix**: 
1. Check PAT hasn't expired (Settings → Access Tokens)
2. Regenerate if needed (Steps 1–5 above)
3. Restart SDLC pipeline with new token

### Issue 2: "404 Project Not Found"
**Cause**: Wrong project ID
**Fix**:
1. Verify project ID matches (Step 2)
2. Check you have read/write access to the repo
3. Test API access manually:
   ```bash
   curl -H "PRIVATE-TOKEN: YOUR_TOKEN" \
     "https://git.epam.com/api/v4/projects/YOUR_PROJECT_ID"
   ```

### Issue 3: "Failed to Checkout Branch"
**Cause**: Network issue or invalid repo URL
**Fix**:
1. Test SSH/HTTPS connectivity:
   ```bash
   git clone https://YOUR_TOKEN@git.epam.com/sampada_chendake/growprogramautomation.git /tmp/test
   ```
2. Check branch name (main vs master)
3. Verify SSH key (if using SSH)

### Issue 4: "PR Creation Failed"
**Cause**: Missing reviewer, wrong assignee, or permission denied
**Fix**:
1. Verify you're a valid user in `git.pr.assignee.default`
2. Check PR reviewers exist in the organization
3. Ensure you have Maintainer+ role in the repo

### Issue 5: "Code Files Not Placed Correctly"
**Cause**: Wrong `code.output.directory` path
**Fix**:
1. Verify directory exists (or create it):
   ```bash
   mkdir -p sdlc-pipeline/src/main/java/com/library/borrowing
   mkdir -p sdlc-pipeline/src/test/java/com/library/borrowing
   mkdir -p sdlc-pipeline/src/main/resources/db/migration
   ```
2. Check write permissions
3. Set `code.overwrite.existing=true` if you want to replace existing code

---

## Testing the Configuration

### Manual Test: Clone & Push Access

```bash
# Test 1: Clone (verify read access)
git clone https://glpat-XXXXX@git.epam.com/sampada_chendake/growprogramautomation.git /tmp/test
cd /tmp/test
git log --oneline -1

# Test 2: Create branch & push (verify write access)
git checkout -b test-branch pr/EDJOBSA-22-test
echo "test" > test.txt
git add test.txt
git commit -m "[TEST] Verify push access"
git push origin test-branch

# Test 3: Cleanup
git push origin -d test-branch
```

### Automated Test: Via Maven

```bash
cd sdlc-pipeline
mvn -q exec:java \
  "-Dexec.mainClass=com.sdlc.pipeline.config.GitIntegrationTest" \
  "-Dexec.args=--test-connection"
```

---

## Next Steps

1. ✅ **Fill in your PAT** in `git.personal.access.token`
2. ✅ **Fill in your Project ID** in `git.repo.project.id`
3. ✅ **Verify configuration** (Step 4 above)
4. ✅ **Run full SDLC pipeline** with ticket `EDJOBSA-22`
5. ✅ **Monitor Phase 8** — automatic PR will be created
6. ✅ **Review & Merge** PR at `https://git.epam.com/sampada_chendake/growprogramautomation/-/merge_requests`

---

## Questions?

See `.github/prompts/phase-8-pr-coordinator.md` for detailed Phase 8 behavior, or contact SDLC Pipeline support.

Your automatic PR workflow is now ready! 🚀

