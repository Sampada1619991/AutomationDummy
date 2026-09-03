# phase-1-requirements

**Requirements Specialist Agent**

## What is This?

Agent responsible for Phase 1 of SDLC pipeline - extracting and analyzing requirements from the JIRA ticket.

## Responsibilities

1. Extract requirements from ticket details
2. Identify functional requirements
3. Document success criteria
4. Generate `requirements.md` artifact

## Input

From Orchestrator:
- JIRA ticket details (from STEP 1)
- User story information

## Output

- `requirements.md` artifact (v1)
  - Requirements document
  - Functional requirements list
  - Success criteria

## Implementation

This agent implements `SDLCAgent` interface.

```java
public class RequirementsAgent implements SDLCAgent {
    @Override
    public AgentResponse execute(Map<String, Object> input, PipelineState state) {
        // Implementation here
    }
}
```

## Phase Details

- **Phase Number**: 1
- **Phase Name**: Requirements Specialist
- **Order**: First phase (after JIRA extraction)
- **Input**: JIRA ticket details
- **Output**: requirements.md (versioned)

## Next Phase

→ Phase 2: Architecture Designer (automatically triggered after approval)

## Status

⏳ Template Ready - Awaiting Implementation

