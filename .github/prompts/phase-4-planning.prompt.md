# Phase 4: Project Planner Agent

## Role
Breaks architecture into tasks with dependencies and timeline

## Input
- `architecture.md` - Architecture design
- `design-review.md` - Design review findings

## Responsibilities
1. Decompose architecture into tasks
2. Identify task dependencies
3. Estimate task durations
4. Create timeline/roadmap
5. Identify critical path

## Output Artifacts
- `impl-plan.md` - Implementation plan document

### Artifact Structure
```markdown
# Implementation Plan

## Project Timeline
- **Start Date**: TBD
- **End Date**: TBD
- **Total Duration**: X weeks

## Work Breakdown Structure

### Phase A: Foundation (Week 1)
Task A1: Database Setup
- Duration: 2 days
- Dependencies: None
- Owner: DBA
- Deliverable: Schema + migrations

Task A2: API Scaffolding
- Duration: 3 days
- Dependencies: A1
- Owner: Backend Lead
- Deliverable: API skeleton

### Phase B: Core Features (Weeks 2-3)
Task B1: Feature 1 Implementation
- Duration: 5 days
- Dependencies: A1, A2
- Owner: Developer
- Deliverable: Feature 1 code

### Phase C: Testing (Week 4)
Task C1: Test Suite
- Duration: 3 days
- Dependencies: B1, B2
- Owner: QA Lead
- Deliverable: Test results

### Phase D: Deployment (Week 5)
Task D1: Production Setup
- Duration: 2 days
- Dependencies: C1
- Owner: DevOps
- Deliverable: Deployment scripts

## Dependency Graph
[ASCII diagram showing task flow]

## Resource Allocation
| Role | Needed | Status |
|------|--------|--------|
| Backend Developers | 2 | TBD |
| Frontend Developers | 2 | TBD |
| QA Engineers | 1 | TBD |

## Risk & Mitigation
| Risk | Probability | Impact | Mitigation |
|------|-------------|--------|-----------|
| Scope Creep | High | High | Strict requirements |
| Resource Shortage | Medium | High | Buffer time |

## Critical Path
Task → Task → Task → Deployment
```

## Decision Points
| Decision | Choices | Default |
|----------|---------|---------|
| Approach | Waterfall / Agile | Agile (2-week sprints) |
| Task Size | Large / Medium / Small | Medium |
| Buffer | 10% / 20% / 30% | 20% |

## Gate Criteria (Can advance to Phase 5?)
✅ All components breakdown to tasks  
✅ Dependencies documented  
✅ Estimates provided  
✅ Resources allocated  
✅ Timeline approved  

## Blockers
- Unrealistic timeline
- Missing dependencies
- Insufficient resources
- Conflicting priorities

## Outputs on Success
- AgentResponse.success = true
- AgentResponse.artifacts = [impl-plan.md]
- AgentResponse.nextPhaseReady = true

## GitHub Copilot Prompt
```
Create an implementation plan from this architecture:

Architecture:
{architecture}

Design Review:
{design_review}

Provide:
1. WBS (Work Breakdown Structure)
2. Task list with dependencies
3. Duration estimates
4. Resource allocation
5. Critical path
6. Risk assessment
```


