# Code Review Checklist

Standard code review criteria for Phase 6 (Peer Code Reviewer)

## Functionality & Correctness
- [ ] Code implements the required functionality
- [ ] Logic is correct and handles edge cases
- [ ] No obvious bugs or logical errors
- [ ] Handles error conditions gracefully
- [ ] Follows the architecture and design
- [ ] Meets requirements specification

## Code Quality & Style
- [ ] Follows project coding standards
- [ ] Consistent naming conventions
- [ ] Proper indentation and formatting
- [ ] Uses appropriate design patterns
- [ ] Code is DRY (Don't Repeat Yourself)
- [ ] Functions/methods are reasonably sized
- [ ] Cyclomatic complexity is acceptable

## Error Handling & Logging
- [ ] All possible exceptions are handled
- [ ] Meaningful error messages provided
- [ ] No silent failures
- [ ] Logging is appropriate (not too verbose)
- [ ] Errors are logged with context
- [ ] Recovery mechanisms in place

## Security
- [ ] No hardcoded secrets or credentials
- [ ] Input validation implemented
- [ ] SQL injection prevention (if applicable)
- [ ] XSS protection (if applicable)
- [ ] Authentication/authorization enforced
- [ ] Sensitive data properly encrypted
- [ ] No security warnings ignored
- [ ] Dependencies are up-to-date

## Performance
- [ ] No obvious performance issues
- [ ] Algorithms efficient for expected data sizes
- [ ] Database queries optimized
- [ ] Caching used appropriately
- [ ] Memory usage is reasonable
- [ ] No unnecessary loops or operations

## Testing
- [ ] Unit tests provided
- [ ] Tests cover happy path
- [ ] Tests cover error cases
- [ ] Tests are meaningful (not just coverage)
- [ ] Test coverage >= 80%
- [ ] Integration tests where appropriate
- [ ] No test code duplication

## Documentation
- [ ] Code is self-documenting
- [ ] Complex logic has comments
- [ ] Functions have docstrings
- [ ] API documentation is clear
- [ ] README updated if needed
- [ ] Configuration options documented

## Maintainability
- [ ] Code is easy to understand
- [ ] Variable names are clear
- [ ] Function purposes are obvious
- [ ] Dependencies are explicit
- [ ] No dead code
- [ ] Comments explain "why" not "what"

## Backward Compatibility
- [ ] No breaking changes
- [ ] API changes documented
- [ ] Deprecation warnings if needed
- [ ] Migration path provided
- [ ] Version bumped appropriately

## Review Decision
- [ ] ✅ APPROVED - Ready to merge
- [ ] 🔄 CHANGES REQUESTED - Make updates and re-request review
- [ ] ❌ REJECTED - Needs significant rework

## Comments
(Add specific review comments here)

---

### Quick Reference: Common Issues

**Must Fix (Blocking)**:
- Security vulnerabilities
- Critical bugs
- Breaking changes without migration
- Missing error handling for critical paths
- Test coverage below 70%

**Should Fix (High Priority)**:
- Performance issues
- Code duplication
- Missing documentation
- Unclear variable names
- Overly complex logic

**Nice to Have (Low Priority)**:
- Minor style improvements
- Non-critical optimizations
- Refactoring suggestions
- Comment improvements

