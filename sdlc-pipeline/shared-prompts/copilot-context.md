# GitHub Copilot Integration - System Context

## Overview
This document provides the system context and instructions for GitHub Copilot to assist in code generation, review, and testing within the SDLC pipeline.

## Code Generation Context (Phase 5)

### Instructions for Copilot
When generating code for implementation:

1. **Follow Architecture**: Generate code that strictly adheres to the architecture.md specification
2. **Standards Compliance**: Use consistent naming, formatting, and project conventions
3. **Error Handling**: Include proper exception handling and error scenarios
4. **Logging**: Add appropriate logging statements for debugging
5. **Documentation**: Include docstrings and inline comments for complex logic
6. **Testing**: Consider testability - write code that is easy to test

### Prompt Template for Code Generation
```
Architecture Context:
[INSERT ARCHITECTURE.MD]

Implementation Plan:
[INSERT IMPL-PLAN.MD]

Task to Implement:
[INSERT CURRENT_TASK]

Requirements:
[INSERT REQUIREMENTS.MD]

Generate code that:
1. Follows the architecture specification
2. Implements the task as planned
3. Includes error handling
4. Is testable and maintainable
5. Includes necessary docstrings
```

## Code Review Context (Phase 6)

### Review Criteria
When reviewing generated code, use these criteria:

- **Correctness**: Does the code implement the intended functionality?
- **Security**: Are there any security vulnerabilities?
- **Performance**: Is the code efficient?
- **Maintainability**: Is the code clear and easy to maintain?
- **Testing**: Is the code adequately tested?
- **Standards**: Does it follow project conventions?
- **DRY**: Is code duplication minimized?

### Prompt Template for Code Review
```
Code to Review:
[INSERT_CODE]

Context:
Requirements: [REQUIREMENTS]
Architecture: [ARCHITECTURE]
Design Review: [DESIGN_REVIEW]

Please provide:
1. Summary assessment (Pass/Fail)
2. Critical issues
3. Non-blocking improvements
4. Security concerns
5. Performance considerations
6. Test coverage assessment
```

## Test Generation Context (Phase 7)

### Testing Strategy
Copilot should generate tests that cover:

1. **Happy Path**: Normal operation scenarios
2. **Error Cases**: Exception and error handling
3. **Edge Cases**: Boundary conditions
4. **Security**: Security-relevant scenarios
5. **Performance**: Load and stress scenarios

### Prompt Template for Test Generation
```
Code to Test:
[INSERT_CODE]

Requirements:
[INSERT_REQUIREMENTS]

Generate comprehensive tests including:
1. Unit tests for all public methods
2. Integration tests for component interactions
3. Error case tests
4. Edge case tests
5. Test coverage should be >= 80%

Use [TEST_FRAMEWORK] and follow these patterns:
[INSERT_PROJECT_TEST_PATTERNS]
```

## Integration Points

### Phase 1: Requirements (Clarification Prompts)
- Generate clarifying questions based on ambiguous user stories
- Identify missing information

### Phase 2: Architecture (Design Assistance)
- Generate ASCII diagrams for system components
- Suggest tech stack recommendations based on requirements
- Outline data flow diagrams

### Phase 3: Design Review (Risk Identification)
- Identify potential architectural issues
- Suggest security improvements
- Highlight performance concerns

### Phase 4: Implementation Planning (Task Breakdown)
- Break down components into implementation tasks
- Identify dependencies
- Generate task descriptions

### Phase 5: Implementation (Code Generation)
- Generate code scaffolds based on architecture
- Implement business logic
- Add error handling and logging

### Phase 6: Code Review (Quality Validation)
- Validate code quality against standards
- Check for security issues
- Verify test coverage
- Review for performance concerns

### Phase 7: Verification (Test Generation)
- Generate unit tests
- Generate integration tests
- Create test documentation
- Verify coverage metrics

### Phase 8: PR Coordinator (Documentation)
- Generate PR title and description
- Create comprehensive changelog
- Generate reviewer checklist

## Best Practices

### For Code Generation
1. Always include error handling
2. Use meaningful variable names
3. Write self-documenting code
4. Add docstrings for all functions
5. Consider edge cases
6. Maintain consistency with existing code

### For Code Review
1. Focus on functionality first
2. Check security early
3. Verify test coverage
4. Look for performance issues
5. Ensure maintainability

### For Test Generation
1. Aim for >80% coverage
2. Test both success and failure paths
3. Use descriptive test names
4. Group related tests
5. Include setup and teardown

## Context Variables

Replace these in prompts:
- `[REQUIREMENTS]` - Content of requirements.md
- `[ARCHITECTURE]` - Content of architecture.md
- `[DESIGN_REVIEW]` - Content of design-review.md
- `[IMPL_PLAN]` - Content of impl-plan.md
- `[INSERT_CODE]` - Code to review or test
- `[TEST_FRAMEWORK]` - pytest, jest, unittest, etc.
- `[PROJECT_CONVENTIONS]` - Style guide and standards

## Error Handling Patterns

### Python
```python
try:
    # operation
except SpecificException as e:
    logger.error(f"Operation failed: {e}")
    raise
except Exception as e:
    logger.exception("Unexpected error")
    raise
```

### JavaScript/TypeScript
```javascript
try {
    // operation
} catch (error) {
    if (error instanceof SpecificError) {
        logger.error(`Operation failed: ${error.message}`);
    } else {
        logger.error(`Unexpected error: ${error}`);
    }
    throw error;
}
```

## Security Checklist for Code Generation

- [ ] Input validation implemented
- [ ] SQL injection prevention (if applicable)
- [ ] XSS protection (if applicable)
- [ ] Authentication/Authorization checks
- [ ] Sensitive data not logged
- [ ] Dependencies verified
- [ ] OWASP Top 10 considerations addressed
- [ ] Rate limiting implemented (if applicable)

## Performance Checklist

- [ ] Algorithms chosen for efficiency
- [ ] Database queries optimized
- [ ] Caching implemented where beneficial
- [ ] Unnecessary loops eliminated
- [ ] Memory usage optimized
- [ ] Appropriate data structures used
- [ ] Lazy loading implemented (if applicable)

