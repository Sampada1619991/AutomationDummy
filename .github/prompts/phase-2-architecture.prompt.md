# Phase 2: Architecture Designer Agent

## Role
Designs high-level system architecture and tech stack

## Input
- `requirements.md` - Requirements document from Phase 1

## Responsibilities
1. Analyze requirements
2. Design system components
3. Define data flow
4. Recommend technology stack
5. Document security architecture

## Output Artifacts
- `architecture.md` - System design document

### Artifact Structure
```markdown
# System Architecture Design

## Components
1. Frontend Layer - User interface
2. API Gateway - Request routing & auth
3. Business Logic - Core services
4. Data Access - Database layer
5. External Services - Third-party integrations

## Technology Stack
- Backend Framework: [Choice & rationale]
- Database: [Choice & rationale]
- Cache: [Choice & rationale]
- Message Queue: [Choice & rationale]

## Data Flow Diagram
[ASCII or reference diagram]

## Security Architecture
- Authentication strategy
- Authorization model
- Encryption approach
- Network security

## Scalability Approach
- Load balancing strategy
- Database scaling
- Caching strategy
- Auto-scaling triggers

## Deployment Model
- Containerization (Docker)
- Orchestration (Kubernetes/Cloud)
- CI/CD Pipeline
```

## Decision Points
| Decision | Choices | Default |
|----------|---------|---------|
| Pattern | Monolith / Microservices | Microservices |
| Database | SQL / NoSQL | PostgreSQL |
| Cache | Redis / Memcached | Redis |
| Auth | OAuth2 / JWT / Sessions | OAuth2 + JWT |

## Gate Criteria (Can advance to Phase 3?)
✅ Architecture aligns with requirements  
✅ Technology stack justified  
✅ Security considerations documented  
✅ Scalability planned  

## Blockers
- Technology constraints
- Team skill gaps
- Cost concerns
- Incompatible requirements

## Outputs on Success
- AgentResponse.success = true
- AgentResponse.artifacts = [architecture.md]
- AgentResponse.nextPhaseReady = true

## GitHub Copilot Prompt
```
Based on these requirements, design a system architecture:

Requirements:
{requirements}

Provide:
1. System components
2. Data flow diagram
3. Technology stack with rationale
4. Security architecture
5. Scalability approach
```


