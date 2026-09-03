# phase-2-architecture

**Architecture Designer Agent**

## What is This?

Agent responsible for Phase 2 of SDLC pipeline - designing system architecture based on requirements.

## Responsibilities

1. Analyze requirements from Phase 1
2. Design high-level system architecture
3. Recommend components and tech stack
4. Generate `architecture.md` artifact

## Input

From Phase 1:
- `requirements.md` artifact

## Output

- `architecture.md` artifact (v1)
  - System design
  - Component diagram
  - Technology stack recommendations

## Implementation

Implements `SDLCAgent` interface.

## Phase Details

- **Phase Number**: 2
- **Phase Name**: Architecture Designer
- **Order**: Second phase
- **Input**: requirements.md
- **Output**: architecture.md (versioned)

## Next Phase

→ Phase 3: Design Review Agent

## Status

⏳ Template Ready - Awaiting Implementation

