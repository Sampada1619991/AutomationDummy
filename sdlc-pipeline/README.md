# 🚀 Agentic SDLC Pipeline - Java Implementation

A **lightweight, minimal** multi-agent system that orchestrates the complete software development lifecycle through coordinated specialized agents.

**Tech Stack**: Java 11+ | Maven | JIRA Integration | Token-Based Auth

## System Overview

```
┌─────────────────────────────────────────────────────────────┐
│ Master Agent: SDLC Orchestrator                              │
│ (Maintains state: current_phase, artifacts[], decisions[])  │
└──────────────────────┬──────────────────────────────────────┘
                       │
       ┌───────────────┼───────────────┐
       ▼               ▼               ▼
   Phase 1         Phase 2         Phase 3
   Requirements    Architecture    Design Review
   Agent           Agent           Agent
       │               │               │
       ├─→ req.md      ├─→ arch.md     ├─→ review.md
       │               │               │
       └───────────────┴───────────────┘
                       │
       ┌───────────────┼───────────────┐
       ▼               ▼               ▼
   Phase 4         Phase 5         Phase 6
   Planning        Implementation  Code Review
   Agent           Agent           Agent
       │               │               │
       ├─→ plan.md     ├─→ src/        ├─→ review.md
       │               │               │
       └───────────────┴───────────────┘
                       │
                       ▼
                   Phase 7
                   Verification
                   Agent
                       │
                       ├─→ verification.md
                       │
                       ▼
                   PR Coordinator
                       │
                       └─→ GitHub PR
```

## Architecture Components

### Master Agent: SDLC Orchestrator
- **Role**: Pipeline coordinator and state manager
- **Responsibilities**:
  - Accept user story input
  - Route to appropriate sub-agent based on current phase
  - Track artifact versions
  - Maintain execution state & decision log
  - Coordinate PR creation at the end

### 7 Sub-Agents (One per SDLC Phase)

#### Phase 1: Requirements Specialist 🔍
- **Input**: User Story
- **Actions**:
  - Ask clarifying questions
  - Capture responses
  - Generate requirements.md
- **Output**: requirements.md

#### Phase 2: Architecture Designer 🏛️
- **Input**: requirements.md
- **Actions**:
  - Propose high-level system design
  - Recommend components, tech stack, data flow
  - Generate diagrams (ASCII or reference)
- **Output**: architecture.md

#### Phase 3: Senior Design Reviewer ✅
- **Input**: requirements.md + architecture.md
- **Actions**:
  - Identify risks, gaps, security concerns
  - Challenge assumptions
  - Document findings
- **Output**: design-review.md (+ optional updates)

#### Phase 4: Project Planner 📋
- **Input**: architecture.md + design-review.md
- **Actions**:
  - Break down architecture into tasks
  - Order by dependency
  - Identify blocked tasks
- **Output**: impl-plan.md (dependency graph)

#### Phase 5: Code Implementer 💻
- **Input**: impl-plan.md + architecture.md
- **Actions**:
  - Generate/scaffold code based on tasks
  - Use GitHub Copilot code completion
  - Commit implementation changes
- **Output**: Source code + commits

#### Phase 6: Peer Code Reviewer 🔎
- **Input**: Implementation artifacts + requirements.md
- **Actions**:
  - Evaluate correctness, security, error handling
  - Check test coverage, code clarity, DRY principle
  - Validate dependencies
- **Output**: code-review.md + approval/rejection

#### Phase 7: Verification & Testing Lead ✨
- **Input**: Code + Tests
- **Actions**:
  - Run unit + integration tests
  - Verify output quality
  - Generate test evidence
- **Output**: verification-report.md + test results

#### Bonus Phase: PR Coordinator 📤
- **Input**: All previous artifacts + verification-report.md
- **Actions**:
  - Generate PR title & description
  - Compile changelog
  - Create reviewer checklist
- **Output**: Pull Request on GitHub

## Shared Skills & Hooks

### 5 Shared Skills (Reusable across agents)

1. **artifact_versioning_skill**
   - Track versions of .md files
   - Hook: validate_version_increment

2. **user_input_collection_skill**
   - Gather clarifications & feedback
   - Hook: validate_completeness

3. **artifact_linking_skill**
   - Reference between artifacts
   - Hook: validate_cross_references

4. **github_integration_skill**
   - Commit, push, create PR
   - Hook: validate_git_state

5. **quality_gate_skill**
   - Pass/fail criteria for phase exit
   - Hook: block_on_critical_issues

## State Management

Master Agent maintains:
```python
state = {
  current_phase: int (1-8),
  user_story: str,
  artifacts: {
    requirements_md: File,
    architecture_md: File,
    design_review_md: File,
    impl_plan_md: File,
    implementation_artifacts: List[File],
    code_review_md: File,
    verification_report_md: File,
  },
  phase_status: dict,
  decisions_log: List[Decision],
  blockers: List[Blocker],
}
```

## Phase Exit Criteria (Quality Gates)

Each phase has:
- ✅ Definition of Done (DoD)
- 🚫 Blocking conditions (prevent phase advancement)
- 📋 Approval checklist
- 🔗 Artifact cross-reference validation

## Directory Structure

```
sdlc-pipeline/
├── README.md (this file)
├── master-agent/
│   ├── orchestrator.py
│   ├── state-manager.py
│   └── config.yaml
├── sub-agents/
│   ├── phase-1-requirements/
│   │   ├── agent.py
│   │   ├── prompts.yaml
│   │   └── config.yaml
│   ├── phase-2-architecture/
│   ├── phase-3-design-review/
│   ├── phase-4-planning/
│   ├── phase-5-implementation/
│   ├── phase-6-code-review/
│   ├── phase-7-verification/
│   └── phase-8-pr-coordinator/
├── shared-skills/
│   ├── artifact-versioning.py
│   ├── user-input-collection.py
│   ├── artifact-linking.py
│   ├── github-integration.py
│   └── quality-gate.py
├── shared-prompts/
│   ├── copilot-context.md
│   ├── code-review-checklist.md
│   ├── security-validation.md
│   └── test-strategy.md
└── types/
    ├── phase.py
    ├── artifact.py
    └── decision.py
```

## Execution Flow

```python
def run_agentic_sdlc(user_story):
    state = initialize_state(user_story)
    
    # Phase 1: Requirements
    requirements = run_agent("Requirements Agent", user_story)
    
    # Phase 2: Architecture
    architecture = run_agent("Architecture Agent", requirements)
    
    # Phase 3: Design Review
    design_review = run_agent("Design Review Agent", 
                               requirements + architecture)
    if design_review.has_blocker():
        notify_user(design_review.blockers)
        return
    
    # Phase 4: Implementation Planning
    impl_plan = run_agent("Planning Agent", architecture + design_review)
    
    # Phase 5: Implementation
    code = run_agent("Implementation Agent", impl_plan + architecture)
    
    # Phase 6: Code Review
    code_review = run_agent("Code Review Agent", code + requirements)
    
    # Phase 7: Verification
    verification = run_agent("Verification Agent", code)
    
    # Create PR
    pr = run_agent("PR Coordinator", state.artifacts)
    
    return pr
```

## Key Design Decisions

| Aspect | Decision | Why |
|--------|----------|-----|
| Sequencing | Linear phases with validation gates | Ensures quality at each step |
| Feedback Loops | Phase X can request updates to Phase X-1 | Handles discovery during later phases |
| Human-in-Loop | User confirms after each phase | Maintains control & alignment |
| Artifacts | Markdown files as source of truth | Easy version control, readable, Git-friendly |
| Skills Reuse | 5 shared skills across all agents | DRY principle, easier maintenance |
| GitHub Integration | Commits + PR at end | Standard DevOps practice |

## Getting Started

1. Review phase definitions in `sub-agents/`
2. Review shared skills in `shared-skills/`
3. Review shared prompts in `shared-prompts/`
4. Initialize master agent: `python master-agent/orchestrator.py`
5. Feed user story and let the pipeline execute

## GitHub Copilot Integration

Each phase leverages GitHub Copilot via:
- System prompts in `shared-prompts/copilot-context.md`
- Code generation hooks at Phase 5
- Code review validation at Phase 6
- Test generation at Phase 7

See `shared-prompts/copilot-context.md` for integration details.


