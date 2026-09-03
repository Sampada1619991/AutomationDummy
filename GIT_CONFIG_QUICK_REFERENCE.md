# 🚀 QUICK CONFIG CHECKLIST — Git Integration for EDJOBSA-22

**File to Update**: `sdlc-pipeline/api-configuration.properties`

---

## 3 Required Fields (You MUST Fill These)

### 1️⃣ GitLab Personal Access Token

**Where to get it**:
- Go to: https://git.epam.com/-/user_settings/personal_access_tokens
- Click: **Add new token**
- Name: `SDLC-Pipeline-Bot`
- Scopes: ✅ api, ✅ read_api, ✅ read_repository, ✅ write_repository
- Click: **Create personal access token**
- Copy the token (looks like: `glpat-XXXXXXXXXXXXXXXX`)

**Add to config**:
```properties
git.personal.access.token=glpat-XXXXXXXXXXXXXXXX
```

---

### 2️⃣ GitLab Project ID

**Where to get it**:
- Go to: https://git.epam.com/sampada_chendake/growprogramautomation
- Click: **Settings** → **General**
- Find: **Project ID** (under project name)
- Example value: `12345`

**Add to config**:
```properties
git.repo.project.id=12345
```

---

### 3️⃣ Your EPAM Username (2 places)

**Add to config**:
```properties
git.pr.assignee.default=sampada_chendake
git.pr.reviewers=sampada_chendake
```

---

## Configuration Summary

```properties
# ============================================
# GitLab Integration (REQUIRED)
# ============================================
git.enabled=true
git.personal.access.token=glpat-YOUR_TOKEN_HERE          ← Fill this #1
git.repo.project.id=YOUR_PROJECT_ID_HERE                 ← Fill this #2
git.pr.assignee.default=YOUR_USERNAME                    ← Fill this #3
git.pr.reviewers=YOUR_USERNAME                           ← Fill this #3

# All other fields (Optional - Pre-configured for your repo)
git.provider=gitlab
git.base.url=https://git.epam.com
git.api.url=https://git.epam.com/api/v4
git.repo.url=https://git.epam.com/sampada_chendake/growprogramautomation.git
git.repo.group=sampada_chendake
git.repo.name=growprogramautomation
git.user.name=SDLC Pipeline Bot
git.user.email=robot.sdlc@epam.com
git.branch.main=main
git.pr.auto.create=true
git.pr.title.template=[AUTO] EDJOBSA-22: Self-Service Book Borrowing - Phase 8 Submission
git.pr.labels=feature,automated,sdlc-pipeline,edjobsa-22
code.output.directory=sdlc-pipeline/src/main/java/com/library/borrowing
code.test.directory=sdlc-pipeline/src/test/java/com/library/borrowing
code.db.migration.directory=sdlc-pipeline/src/main/resources/db/migration
code.overwrite.existing=true
```

---

## What Happens at Phase 8

When you run the SDLC pipeline with `EDJOBSA-22` and reach **Phase 8**:

✅ All 16 Java files → Placed in `sdlc-pipeline/src/main/java/com/library/borrowing/`  
✅ Test files (2) → Placed in `sdlc-pipeline/src/test/java/com/library/borrowing/`  
✅ SQL migrations → Placed in `sdlc-pipeline/src/main/resources/db/migration/`  
✅ Feature branch created → `pr/EDJOBSA-22-{timestamp}`  
✅ Code committed → "SDLC Pipeline Bot" as author  
✅ Push to GitLab → Branch pushed to remote  
✅ **PR automatically created** → Assigned to you, labeled, ready for review  

**Output**: PR URL in your console
```
🎉 Pull Request Created Successfully!
   URL: https://git.epam.com/sampada_chendake/growprogramautomation/-/merge_requests/123
   Branch: pr/EDJOBSA-22-20260903-144203
   Assignee: sampada_chendake
   Status: READY FOR REVIEW
```

---

## Verify Configuration Works

After filling in the 3 required fields, run:

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
✅ READY FOR PRODUCTION
```

---

## Security Note ⚠️

**DO NOT commit `api-configuration.properties` with your PAT to git!**

Use environment variable instead:

```bash
# Linux/Mac
export GIT_PERSONAL_ACCESS_TOKEN=glpat-XXXXXXX

# Windows PowerShell
$env:GIT_PERSONAL_ACCESS_TOKEN = "glpat-XXXXXXX"

# Then reference in config
git.personal.access.token=${env.GIT_PERSONAL_ACCESS_TOKEN}
```

Or use Maven property:
```bash
mvn clean package -Dgit.token=glpat-XXXXXXX
```

---

## Done! ✅

Once you fill in 3 fields (PAT, Project ID, Username) your automatic PR workflow is ready to go!

Next: Run full SDLC pipeline → Phase 8 → PR created automatically 🚀

