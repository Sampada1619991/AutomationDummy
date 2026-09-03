# Phase 6: Code Review Agent

## Role
Reviews code for quality, security, and standards compliance

## Input
- Implementation code from Phase 5
- `requirements.md` - Requirements
- `architecture.md` - Design

## Responsibilities
1. Evaluate code correctness
2. Check security vulnerabilities
3. Assess code quality
4. Verify test coverage
5. Review error handling

## Output Artifacts
- `code-review.md` - Review findings

### Artifact Structure
```markdown
# Code Review Report

## Summary
**Decision**: ✅ APPROVED / ⚠️ CONDITIONAL / ❌ REJECTED

| Metric | Score | Status |
|--------|-------|--------|
| Functionality | 95% | ✅ |
| Security | 90% | ✅ |
| Performance | 92% | ✅ |
| Test Coverage | 85% | ✅ |
| Code Quality | 88% | ✅ |

## Issues Found

### Critical (0)
None

### High Priority (2)
1. Missing input validation on email field
   - File: UserController.java:45
   - Impact: SQL injection vulnerability
   - Fix: Add @Valid annotation

2. Hardcoded database URL
   - File: Config.java:12
   - Impact: Security risk
   - Fix: Move to environment variables

### Medium Priority (3)
1. Insufficient error logging
2. Missing timeout on external API calls
3. No retry logic for failed operations

### Low Priority (2)
1. Code duplication in validation logic
2. Missing javadoc comments

## Security Review
- [x] SQL Injection prevention
- [x] XSS protection
- [x] Authentication enforced
- [ ] Rate limiting not implemented
- [x] Secrets not in code

## Performance Review
- Response times acceptable
- Database queries optimized
- Memory usage reasonable
- Consider caching for frequently accessed data

## Code Quality Metrics
- Cyclomatic Complexity: 5.2 (Good)
- Duplication: 2% (Good)
- Coverage: 85% (Acceptable)
- Style: Consistent with standards

## Approval Decision
**APPROVED WITH CHANGES**

Must fix:
- [ ] Add input validation (1-2 hours)
- [ ] Move secrets to environment (30 mins)
- [ ] Add API timeout (1 hour)

Should fix:
- [ ] Improve logging (2 hours)
- [ ] Add retry logic (2 hours)

Nice to have:
- [ ] Reduce duplication (3 hours)
- [ ] Add javadoc (2 hours)
```

## Decision Points
| Decision | Choices | Default |
|----------|---------|---------|
| Severity | Critical / High / Medium / Low | Based on risk |
| Blocking | Must fix / Should fix / Nice to have | Critical blocks |
| Approval | Approve / Conditional / Reject | Based on findings |

## Gate Criteria (Can advance to Phase 7?)
✅ No critical issues  
✅ Security review passed  
✅ Code coverage >= 80%  
✅ Performance acceptable  
✅ Reviewer approved  

## Blockers
- Critical security vulnerabilities
- Code won't run
- Test coverage < 70%
- Performance unacceptable

## Outputs on Success
- AgentResponse.success = true
- AgentResponse.artifacts = [code-review.md]
- AgentResponse.nextPhaseReady = true

## GitHub Copilot Prompt
```
Review this implementation code against requirements:

Code:
{code}

Requirements:
{requirements}

Architecture:
{architecture}

Perform:
1. Functionality review - does it meet requirements?
2. Security review - OWASP Top 10 check
3. Performance review - bottleneck analysis
4. Code quality - style, duplication, complexity
5. Test coverage - adequate coverage achieved?
6. Error handling - comprehensive?

Provide:
1. Summary assessment
2. List of issues (Critical/High/Medium/Low)
3. Specific code locations needing fixes
4. Approval decision
```


