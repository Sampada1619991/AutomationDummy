# Phase 7: Verification & Testing Lead Agent

## Role
Runs tests and verifies quality meets production standards

## Input
- Implementation code from Phase 5
- Test files from Phase 5

## Responsibilities
1. Execute unit tests
2. Run integration tests
3. Perform security testing
4. Run performance testing
5. Verify code coverage

## Output Artifacts
- `verification-report.md` - Test results and approval

### Artifact Structure
```markdown
# Verification & Test Report

**Overall Status**: ✅ PASSED - READY FOR DEPLOYMENT

## Test Execution Summary

| Test Type | Tests Run | Passed | Failed | Coverage |
|-----------|-----------|--------|--------|----------|
| Unit Tests | 45 | 45 | 0 | 85% |
| Integration Tests | 12 | 12 | 0 | 72% |
| E2E Tests | 8 | 8 | 0 | 90% |
| Performance | 5 | 5 | 0 | - |
| Security | 10 | 10 | 0 | - |
| **TOTAL** | **80** | **80** | **0** | **85%** |

## Unit Tests: ✅ 45/45 PASSED
Duration: 12.3 seconds

Test Suites:
- User Service Tests (15 tests) ✅
- Authentication Tests (12 tests) ✅
- Database Tests (10 tests) ✅
- API Endpoint Tests (8 tests) ✅

## Integration Tests: ✅ 12/12 PASSED
Duration: 34.7 seconds

Workflows Tested:
- User Registration Flow ✅
- Login & Authentication ✅
- Feature Operations ✅
- Database Integration ✅

## Performance Tests: ✅ 5/5 PASSED
Duration: 1 hour

| Metric | Measured | Target | Status |
|--------|----------|--------|--------|
| P95 Response Time | 287ms | <500ms | ✅ |
| P99 Response Time | 412ms | <2000ms | ✅ |
| Throughput | 2,847 req/s | >1,000 req/s | ✅ |
| Error Rate | 0.12% | <1% | ✅ |
| Memory Leak | None | None | ✅ |

## Security Tests: ✅ 10/10 PASSED

OWASP Top 10 Validation:
- A01 Broken Access Control ✅
- A02 Cryptographic Failures ✅
- A03 Injection ✅
- ...All checks passed

## Code Coverage Analysis
```
Total Coverage: 85%
- Line Coverage: 85%
- Branch Coverage: 82%
- Function Coverage: 88%
```

## Quality Metrics Summary
```
Code Quality Score:    92/100 (Excellent)
Security Score:        95/100 (Excellent)
Performance Score:     94/100 (Excellent)
Test Coverage Score:   85/100 (Good)
_________________________________________________
OVERALL QUALITY SCORE: 91/100 (EXCELLENT)
```

## Deployment Readiness Checklist
- [x] All tests passing
- [x] Code coverage >= 80%
- [x] Performance acceptable
- [x] Security review passed
- [x] Documentation complete
- [x] Monitoring configured

## Sign-Off
**Status**: ✅ **READY FOR PRODUCTION**
**Approved By**: Testing Lead
**Approval Date**: 2024-01-15
```

## Decision Points
| Decision | Choices | Default |
|----------|---------|---------|
| Coverage Threshold | 70% / 80% / 90% | 80% |
| Performance Threshold | Response time < ? | <500ms |
| Error Rate Threshold | < 0.1% or < 1% | <0.1% |

## Gate Criteria (Can advance to Phase 8?)
✅ All tests pass (0 failures)  
✅ Code coverage >= 80%  
✅ Performance meets targets  
✅ Security testing passed  
✅ No blocking issues  

## Blockers
- Tests failing
- Coverage below 80%
- Performance unacceptable
- Security vulnerabilities found

## Outputs on Success
- AgentResponse.success = true
- AgentResponse.artifacts = [verification-report.md]
- AgentResponse.nextPhaseReady = true

## GitHub Copilot Prompt
```
Generate a comprehensive test suite and verify:

Code:
{code}

Requirements:
{requirements}

Test Strategy:
1. Unit tests for all public methods (target: 80%+ coverage)
2. Integration tests for component interactions
3. E2E tests for critical user journeys
4. Performance tests (P95 < 500ms)
5. Security tests (OWASP Top 10)

Generate:
1. Test implementations
2. Performance testing scripts
3. Security test cases
4. Coverage report
5. Go/No-Go decision for deployment
```


