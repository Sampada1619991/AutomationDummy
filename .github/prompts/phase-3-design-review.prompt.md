# Phase 3: Design Review Agent

## Role
Reviews architecture for risks, gaps, and improvements

## Input
- `requirements.md` - Requirements document
- `architecture.md` - Architecture design

## Responsibilities
1. Identify architectural risks
2. Find security gaps
3. Challenge design assumptions
4. Suggest improvements
5. Document concerns & recommendations

## Output Artifacts
- `design-review.md` - Review findings document

### Artifact Structure
```markdown
# Design Review Report

## Executive Summary
Overall assessment and key findings

## Risk Assessment
### High Priority
- Risk 1: Description, impact, mitigation
- Risk 2: Description, impact, mitigation

### Medium Priority
- Risk 1: Description, impact, mitigation

### Low Priority
- Risk 1: Description, impact, mitigation

## Security Review
- Authentication: Status
- Authorization: Status
- Encryption: Status
- Data Protection: Status
- OWASP Top 10 validation

## Performance Analysis
- Expected response times
- Scalability limits
- Bottleneck identification

## Questions for Architect
1. Question 1
2. Question 2

## Recommendations
- Must fix before implementation
- Should fix during implementation
- Nice to have enhancements

## Approval Status
- [ ] APPROVED
- [ ] CONDITIONAL (fix issues)
- [ ] REJECTED (rework needed)
```

## Decision Points
| Decision | Choices | Default |
|----------|---------|---------|
| Risk Level | Accept / Mitigate / Reject | Mitigate |
| Blocker Severity | Info / Warning / Critical | Critical only blocks |
| Review Depth | Quick / Comprehensive | Comprehensive |

## Gate Criteria (Can advance to Phase 4?)
✅ No critical risks remain  
✅ Security gaps addressed  
✅ Architect approved/acknowledged findings  
✅ Mitigation plans documented  

## Blockers
- Critical security vulnerabilities
- Unresolvable architectural conflicts
- Unachievable performance targets
- Missing critical components

## Outputs on Success
- AgentResponse.success = true
- AgentResponse.artifacts = [design-review.md]
- AgentResponse.blockers = [if issues found]
- AgentResponse.nextPhaseReady = [true if no blockers]

## GitHub Copilot Prompt
```
Review this system architecture against requirements:

Requirements:
{requirements}

Architecture:
{architecture}

Security Review:
1. Identify security vulnerabilities
2. Check OWASP Top 10 coverage
3. Validate authentication/authorization

Performance Review:
1. Identify bottlenecks
2. Check scalability approach
3. Validate caching strategy

Architectural Review:
1. Identify risks
2. Challenge assumptions
3. Suggest improvements
```


