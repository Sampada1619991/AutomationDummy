# JIRA Integration Guide

## Overview

Simple JIRA integration using:
- ✅ **Single properties file**: `api-configuration.properties`
- ✅ **Token-based authentication**: No need for passwords
- ✅ **Minimal Java classes**: Only 2 classes needed
- ✅ **Easy to configure**: Just set environment variables or edit properties file

---

## Setup Steps

### Step 1: Generate JIRA API Token

1. Go to JIRA: **Profile > Settings > Security > API Tokens**
2. Click **Create API token**
3. Copy the token (you'll need it)

### Step 2: Update Configuration File

Edit `api-configuration.properties`:

```properties
# Enable JIRA Integration
jira.enabled=true

# Your JIRA URL
jira.url=https://your-jira-instance.atlassian.net

# Your email (same as JIRA login)
jira.email=your-email@company.com

# API token (from Step 1)
jira.api.token=your_api_token_here
```

### Step 3: (Alternative) Use Environment Variables

Instead of properties file, set environment variables:

**Windows (PowerShell)**:
```powershell
$env:JIRA_ENABLED="true"
$env:JIRA_URL="https://your-jira-instance.atlassian.net"
$env:JIRA_EMAIL="your-email@company.com"
$env:JIRA_API_TOKEN="your_api_token_here"
```

**Linux/Mac**:
```bash
export JIRA_ENABLED=true
export JIRA_URL=https://your-jira-instance.atlassian.net
export JIRA_EMAIL=your-email@company.com
export JIRA_API_TOKEN=your_api_token_here
```

### Step 4: Test Connection

```bash
cd sdlc-pipeline
mvn clean compile
mvn exec:java@java "-Dexec.mainClass=com.sdlc.pipeline.demo.PipelineDemoWithJira"
```

When prompted:
```
📌 Enter JIRA issue key (e.g., PROJ-123): YOUR-123
```

---

## How It Works

### Workflow

```
User provides JIRA issue key
          ↓
JiraIntegration fetches issue
          ↓
Issue displayed for approval
          ↓
User confirms (yes/no)
          ↓
If YES → Pipeline starts with issue data
If NO  → Use manual user story
```

### Configuration Loading

```java
// Step 1: Load config from properties file
ConfigLoader config = ConfigLoader.getInstance();

// Step 2: Check if JIRA enabled
if (config.isJiraEnabled()) {
    // Step 3: Fetch issue
    JiraIntegration jira = new JiraIntegration();
    JiraIntegration.JiraIssueData issue = jira.fetchIssue("PROJ-123");
    
    // Step 4: Display for approval
    System.out.println(issue.formattedData);
}
```

---

## File Structure

```
api-configuration.properties        ← All settings in one file

src/main/java/.../
├── config/
│   └── ConfigLoader.java          ← Reads properties file
├── jira/
│   └── JiraIntegration.java       ← JIRA API calls
└── demo/
    └── PipelineDemoWithJira.java  ← Example usage
```

---

## Total Java Classes: 3

| Class | Purpose | Lines |
|-------|---------|-------|
| `ConfigLoader.java` | Read properties file | ~100 |
| `JiraIntegration.java` | JIRA API client | ~150 |
| `PipelineDemoWithJira.java` | Demo with JIRA | ~150 |

**No complex JIRA parsing or multiple configuration classes!**

---

## Example: Fetching from JIRA

```java
// Create JIRA client
JiraIntegration jira = new JiraIntegration();

// Test connection first
boolean connected = jira.testConnection();  // ✅ or ❌

// Fetch issue
JiraIntegration.JiraIssueData data = jira.fetchIssue("PROJ-123");

if (data.success) {
    System.out.println(data.formattedData);  // Display for approval
    // User approves → use formattedData as user story
} else {
    System.out.println(data.message);  // Error message
}
```

---

## Configuration Options

### api-configuration.properties

```properties
# JIRA Settings
jira.enabled=true
jira.url=https://your-company.atlassian.net
jira.api.token=your_token_here
jira.email=your-email@company.com

# Pipeline Settings
pipeline.auto.advance=false
pipeline.artifacts.persist=true
pipeline.artifacts.dir=./pipeline-artifacts

# GitHub Settings (Optional)
github.enabled=false
github.token=your_github_token_here
github.repo.owner=your-org
github.repo.name=your-repo

# Logging Settings
logging.level=INFO
logging.file=./logs/pipeline.log
```

---

## Troubleshooting

### ❌ "JIRA not enabled"
- Check `jira.enabled=true` in properties file
- Or set environment variable: `JIRA_ENABLED=true`

### ❌ "Authentication failed (401)"
- Verify email is correct
- Verify API token is correct
- Check JIRA URL ends with `.atlassian.net`

### ❌ "Issue not found"
- Verify issue key (e.g., PROJ-123, not proj-123)
- Verify user has access to that issue

### ❌ "Cannot connect to JIRA"
- Verify JIRA URL is accessible
- Check network/firewall
- Try testing with curl: `curl -H "Authorization: Basic <base64>" <jira-url>/rest/api/3/myself`

---

## Common Use Cases

### Use Case 1: Fetch Issue and Approve

```java
JiraIntegration jira = new JiraIntegration();
JiraIntegration.JiraIssueData issue = jira.fetchIssue("SPRINT-45");

// Display formatted issue
System.out.println(issue.formattedData);

// Get user approval
if (userApprovesIssue) {
    // Use issue data to start pipeline
    orchestrator.runPipeline(issue.formattedData, false);
}
```

### Use Case 2: Multiple Issues

```java
String[] issueKeys = {"PROJ-1", "PROJ-2", "PROJ-3"};

for (String key : issueKeys) {
    JiraIntegration.JiraIssueData issue = jira.fetchIssue(key);
    if (issue.success) {
        processIssue(issue.formattedData);
    }
}
```

### Use Case 3: Batch Processing

```java
// Fetch from JIRA JQL query
String jql = "project=PROJ AND status=Ready";
// List<JiraIssueData> issues = jira.fetchIssuesByJQL(jql);
// for each issue: orchestrator.runPipeline(...)
```

---

## Next Steps

1. ✅ Configure `api-configuration.properties` with your JIRA details
2. ✅ Test connection: Run `PipelineDemoWithJira`
3. ✅ Enter a JIRA issue key
4. ✅ Approve issue for pipeline
5. ✅ Watch pipeline execute with JIRA data

---

## Support

- **Properties file not found?** Create `api-configuration.properties` in project root
- **Can't generate API token?** See https://support.atlassian.com/atlassian-account/docs/manage-api-tokens-for-your-atlassian-account/
- **Need more fields?** Edit `api-configuration.properties` to add custom fields
- **Need JSON parsing?** Add gson or jackson dependency to pom.xml

---

**That's it! Simple, token-based, configuration-driven JIRA integration!** 🎉

