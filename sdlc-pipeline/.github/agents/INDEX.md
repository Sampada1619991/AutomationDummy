# SDLC Pipeline Agents Index

All agents in the SDLC Pipeline are organized in `.github/agents/` folder.

## Available Agents

### Master Agent
- **Orchestrator** - Master agent that coordinates all phases
  - Location: `.github/agents/orchestrator/`
  - Role: JIRA integration + Phase orchestration
  - Status: ✅ Fully Implemented

### Phase Agents (1-8)

| Phase | Agent Name | Location | Status |
|-------|-----------|----------|--------|
| 1 | Requirements Specialist | `.github/agents/phase-1-requirements/` | ⏳ Template Ready |
| 2 | Architecture Designer | `.github/agents/phase-2-architecture/` | ⏳ Template Ready |
| 3 | Design Reviewer | `.github/agents/phase-3-design-review/` | ⏳ Template Ready |
| 4 | Project Planner | `.github/agents/phase-4-planning/` | ⏳ Template Ready |
| 5 | Code Implementer | `.github/agents/phase-5-implementation/` | ⏳ Template Ready |
| 6 | Code Reviewer | `.github/agents/phase-6-code-review/` | ⏳ Template Ready |
| 7 | Verification Lead | `.github/agents/phase-7-verification/` | ⏳ Template Ready |
| 8 | PR Coordinator | `.github/agents/phase-8-pr-coordinator/` | ⏳ Template Ready |

## How to Use

### Browse in GitHub

1. Go to `.github/agents/` folder
2. Select any agent folder
3. Read the `README.md` for specifications

### Implement an Agent

Each agent folder has a `README.md` with:
- Agent responsibilities
- Input/Output specifications
- Implementation guidance
- Phase details

## Execution Flow

```
JIRA Ticket ID Input (e.g., EDJOBASA-22)
         ↓
Orchestrator (STEP 1: Extract & Approve)
         ↓
Phase 1: Requirements Specialist
         ↓
Phase 2: Architecture Designer
         ↓
Phase 3: Design Reviewer
         ↓
Phase 4: Project Planner
         ↓
Phase 5: Code Implementer
         ↓
Phase 6: Code Reviewer
         ↓
Phase 7: Verification Lead
         ↓
Phase 8: PR Coordinator
         ↓
DONE ✅ (GitHub PR Created)
```

## Quick Commands

### Run the Pipeline
```bash
mvn exec:java@java "-Dexec.mainClass=com.sdlc.pipeline.StartWithTicket"
```

### Compile
```bash
mvn clean compile
```

## Notes

- All agents follow the `SDLCAgent` interface
- Orchestrator coordinates automatic phase progression
- JIRA integration provides initial input
- Continuous execution without user intervention (after approval)

