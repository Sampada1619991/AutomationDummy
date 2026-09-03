# orchestrator

**Master Agent - SDLC Pipeline Orchestrator**

## What is This?

The Master Agent that coordinates all 8 SDLC phases. This is the entry point for the entire pipeline.

## Responsibilities

1. **STEP 1 (MANDATORY)**: Extract JIRA Ticket Details
   - Read JIRA config from `api-configuration.properties`
   - Connect to JIRA via REST API (token-based authentication)
   - Fetch ticket details using JIRA ticket ID
   - Display formatted ticket information to user
   - Request user approval before proceeding

2. **STEP 2**: Automatically Execute All SDLC Phases
   - Phase 1 → Phase 2 → Phase 3 → ... → Phase 8
   - Continuous execution without interruption
   - All phases run automatically after approval

## Input

- **JIRA Ticket ID**: e.g., `EDJOBASA-22`

## Output

- Approval Display with ticket details
- Automated execution of all 8 phases
- Final pipeline status report

## Configuration Required

`api-configuration.properties`:
```properties
jira.enabled=true
jira.url=https://your-jira-instance.atlassian.net
jira.email=your-email@company.com
jira.api.token=your_api_token
```

## Java Implementation

- **Package**: `com.sdlc.pipeline.agents.orchestrator`
- **Class**: `SDLCOrchestrator.java`
- **Location**: `src/main/java/com/sdlc/pipeline/agents/orchestrator/`

## Usage

```java
SDLCOrchestrator orchestrator = new SDLCOrchestrator();

// Register agents for all phases
orchestrator.registerAgent(1, requirementsAgent);
orchestrator.registerAgent(2, architectureAgent);
// ... Phase 3-8

// STEP 1: Extract JIRA ticket (mandatory)
boolean approved = orchestrator.extractJiraTicketDetails("EDJOBASA-22");

if (approved) {
    // STEP 2: Automatically execute all phases
    orchestrator.executePhases();
}
```

## Key Methods

- `extractJiraTicketDetails(String ticketId)` - STEP 1 (Mandatory)
- `executePhases()` - STEP 2 (Automatic phase execution)
- `registerAgent(int phase, SDLCAgent agent)` - Register phase agents
- `advancePhase()` - Move to next phase
- `printStatus()` - Display pipeline status

## Flow Diagram

```
JIRA Ticket Input
       ↓
STEP 1: Extract & Display
   ↓ Approve?
       ↓
STEP 2: Auto-Execute Phases
   Phase 1 → 2 → 3 → 4 → 5 → 6 → 7 → 8
       ↓
    DONE ✅
```

## Status

✅ Fully Implemented
✅ Production Ready
✅ JIRA Integration Active
✅ Continuous Execution Enabled

