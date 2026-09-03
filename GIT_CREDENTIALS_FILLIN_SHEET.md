# 📋 Git Configuration Values — Fill-In Sheet

**File to edit**: `sdlc-pipeline/api-configuration.properties`

---

## Your Git Credentials (Fill In These 2 Values)

### Value #1: GitLab Personal Access Token

**What it is**: Authentication token for your GitLab account  
**Where to get it**: https://git.epam.com/-/user_settings/personal_access_tokens

```
Go to: https://git.epam.com/-/user_settings/personal_access_tokens
   ↓
Click: [Add new token]
   ↓
Fill in:
  Token name: SDLC-Pipeline-Bot
  Expiration date: 365 days
  Scopes: ✅ api
          ✅ read_api
          ✅ read_repository
          ✅ write_repository
   ↓
Click: [Create personal access token]
   ↓
You'll see: glpat-XXXXXXXXXXXXXXXX
   ↓
COPY THIS!
```

**Add to config file** (Line 22):

```ini
# BEFORE:
git.personal.access.token=YOUR_GITLAB_PERSONAL_ACCESS_TOKEN_HERE

# AFTER:
git.personal.access.token=glpat-XXXXXXXXXXXXXXXX
```

**Format**: `glpat-` followed by random characters  
**Example**: `glpat-1a2b3c4d5e6f7g8h9i0j`  
**⚠️ KEEP SECRET**: Don't share in Slack, email, or commit to git!

---

### Value #2: GitLab Project ID

**What it is**: Numeric ID of your growprogramautomation repository  
**Where to get it**: https://git.epam.com/sampada_chendake/growprogramautomation

```
Go to: https://git.epam.com/sampada_chendake/growprogramautomation
   ↓
Click: Settings (gear icon on left sidebar)
   ↓
Click: General
   ↓
Look for: "Project ID" (displayed at the top, under project name)
   ↓
It's a number: 12345
   ↓
COPY THIS!
```

**Add to config file** (Line 26):

```ini
# BEFORE:
git.repo.project.id=YOUR_GITLAB_PROJECT_ID_HERE

# AFTER:
git.repo.project.id=12345
```

**Format**: Plain number (e.g., `12345`, `98765`)  
**Example values**: `12345`, `54321`, `99999`  
**Tip**: If you're unsure, go to repo Settings → General and look for this ID

---

## Summary Table

| What You Need | Example | Where to Get It |
|---|---|---|
| GitLab PAT | `glpat-1a2b3c4d5e6f7g8h9i0j` | https://git.epam.com/-/user_settings/personal_access_tokens |
| Project ID | `12345` | Repo Settings → General |

---

## Step-by-Step Fill-In Process

### Step 1: Open Configuration File

**File**: `sdlc-pipeline/api-configuration.properties`

**Using any text editor**:
- VS Code
- Notepad
- JetBrains IDE (recommended)

---

### Step 2: Go to Line 22 (GitLab Token)

**Find this line**:
```
git.personal.access.token=YOUR_GITLAB_PERSONAL_ACCESS_TOKEN_HERE
```

**Replace with your token**:
```
git.personal.access.token=glpat-1a2b3c4d5e6f7g8h9i0j
```

**Save the file** (`Ctrl+S`)

---

### Step 3: Go to Line 26 (Project ID)

**Find this line**:
```
git.repo.project.id=YOUR_GITLAB_PROJECT_ID_HERE
```

**Replace with your project ID**:
```
git.repo.project.id=12345
```

**Save the file** (`Ctrl+S`)

---

### Step 4: Verify Lines 46-47 (Already Filled ✅)

**These are pre-filled with your username**:
```
git.pr.assignee.default=sampada_chendake
git.pr.reviewers=sampada_chendake
```

**No changes needed!** ✅

---

## Full Configuration Template

**Copy & fill in only these 2 lines**:

```properties
# Line 22 - Replace with your GitLab PAT
git.personal.access.token=glpat-XXXXXXXXXXXXXXXX

# Line 26 - Replace with your Project ID  
git.repo.project.id=12345
```

**Everything else is pre-configured and ready to go!**

---

## Verification Checklist

After filling in the 2 values:

- [ ] Line 22: `git.personal.access.token=glpat-...` ✅
- [ ] Line 26: `git.repo.project.id=12345` (or your actual ID) ✅
- [ ] File saved ✅
- [ ] No other changes needed ✅
- [ ] Ready to test! ✅

---

## Test Your Configuration

**Run this command**:

```bash
cd sdlc-pipeline

mvn -q exec:java \
  "-Dexec.mainClass=com.sdlc.pipeline.config.GitConfigValidator" \
  "-Dexec.args=--validate-git-config"
```

**If everything is correct**, you'll see:

```
✅ GitLab API endpoint reachable
✅ Personal access token valid
✅ Repository accessible  
✅ Project ID verified: sampada_chendake/growprogramautomation (ID: 12345)
✅ All required fields configured
✅ READY FOR PRODUCTION
```

**If you get an error**, check:
1. PAT is correct (no extra spaces)
2. Project ID is numeric (no quotes)
3. Both lines are uncommented (no # prefix)
4. File is saved

---

## What Each Value Does

### git.personal.access.token
- Used to authenticate with GitLab
- Allows pushing code to your repository
- Allows creating Pull Requests automatically
- **Requires scopes**: `api`, `read_api`, `read_repository`, `write_repository`

### git.repo.project.id
- Tells the system which repository to use
- Used for PR creation, branch management
- Must be numeric (not a name/path)
- Can be found at: Repo Settings → General

---

## Security Important ⚠️

### DO NOT:
- ❌ Share your PAT in Slack/Teams/Email
- ❌ Commit `api-configuration.properties` to git with your PAT
- ❌ Post your token in bug reports
- ❌ Use shared machines with this value

### DO:
- ✅ Keep token in local file only (don't commit)
- ✅ Add `api-configuration.properties` to `.gitignore`
- ✅ Use environment variables for CI/CD
- ✅ Rotate token every 90 days

---

## Troubleshooting

### "401 Unauthorized"
- Token is wrong or expired
- Fix: Regenerate PAT from https://git.epam.com/-/user_settings/personal_access_tokens

### "404 Not Found"
- Project ID is wrong
- Fix: Verify at Repo Settings → General

### "Unrecognized token type"
- PAT doesn't start with `glpat-`
- Fix: Make sure you created a **Personal Access Token**, not SSH key

### "Connection refused"
- Network/proxy issue
- Fix: Verify you can access https://git.epam.com in browser

---

## That's It! 🎉

Once you fill in these 2 values and verify, your git integration is ready.

**Next**: Run full SDLC pipeline → Phase 8 → PR created automatically! 🚀

