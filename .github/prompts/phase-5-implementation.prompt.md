# Phase 5: Code Implementer Agent

## Role
Generates code implementation based on plan and architecture

## Input
- `impl-plan.md` - Implementation plan
- `architecture.md` - Architecture design

## Responsibilities
1. Generate code scaffolds for components
2. Implement business logic
3. Add error handling & logging
4. Write initial unit tests
5. Create code documentation

## Output Artifacts
- `code/` - Source code files
- `tests/` - Unit test files
- `implementation.md` - Implementation notes

### Artifact Structure
```markdown
# Implementation Summary

## Code Generated
- `services/UserService.java` - User management
- `controllers/UserController.java` - API endpoints
- `models/User.java` - Data model
- `tests/UserServiceTest.java` - Unit tests

## Build Status
- Compilation: ✅ PASS
- Unit Tests: ✅ 45/45 PASS
- Code Coverage: 85%
- Code Quality: A (SonarQube)

## Implementation Notes
- Used Spring Boot framework as planned
- Implemented JWT authentication
- Added error handling for all endpoints
- Database migrations included

## Dependencies Added
- spring-boot 2.7.0
- postgresql driver 42.4.0
- junit 4.13.2

## Manual Testing Checklist
- [ ] API endpoints respond correctly
- [ ] Authentication works
- [ ] Database queries execute properly
- [ ] Error handling triggered appropriately
- [ ] Logging captured correctly
```

## Decision Points
| Decision | Choices | Default |
|----------|---------|---------|
| Code Generation | Full / Partial / Scaffold | Full |
| Testing | TDD / Post-code / Both | Both |
| Documentation | Inline / Separate / Both | Inline |

## Gate Criteria (Can advance to Phase 6?)
✅ Code compiles successfully  
✅ Unit tests pass  
✅ Code coverage > 80%  
✅ Error handling implemented  
✅ Documentation complete  

## Blockers
- Code won't compile
- Unresolved dependencies
- Circular dependencies
- Security violations in code

## Outputs on Success
- AgentResponse.success = true
- AgentResponse.artifacts = [code files, tests, implementation.md]
- AgentResponse.nextPhaseReady = true

## GitHub Copilot Prompt
```
Generate implementation code for this architecture:

Architecture:
{architecture}

Implementation Plan:
{impl_plan}

Generate:
1. Complete service implementations
2. API endpoint controllers
3. Data models
4. Unit tests (80%+ coverage)
5. Error handling & logging
6. Docstrings for all classes

Use best practices:
- Clean code principles
- SOLID design patterns
- Proper error handling
- Comprehensive logging
```


