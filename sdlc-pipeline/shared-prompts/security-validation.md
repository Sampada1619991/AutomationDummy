# Security Validation Framework

Security validation checklist for code and dependencies

## Input Validation & Data Sanitization

### General Rules
- [ ] All user inputs are validated
- [ ] Type checking enforced
- [ ] Length limits enforced
- [ ] Format validation performed
- [ ] Range validation for numeric inputs
- [ ] Whitelist approach used (reject by default)
- [ ] Error messages don't leak sensitive info

### Examples
```python
# GOOD: Validate and sanitize
user_id = request.args.get('id', type=int)
if not user_id or user_id < 0:
    return error("Invalid user ID")

# BAD: No validation
user_data = request.args.get('data')
execute_query(f"SELECT * FROM users WHERE id = {user_data}")
```

## SQL Injection Prevention

- [ ] Parameterized queries used
- [ ] ORM framework used correctly
- [ ] No string concatenation in queries
- [ ] Prepared statements implemented
- [ ] Input binding done correctly

### Examples
```python
# GOOD: Parameterized
cursor.execute("SELECT * FROM users WHERE id = ?", (user_id,))

# BAD: Injection vulnerable
cursor.execute(f"SELECT * FROM users WHERE id = {user_id}")
```

## Cross-Site Scripting (XSS) Prevention

- [ ] User input escaped in HTML context
- [ ] HTML encoding applied
- [ ] Content Security Policy implemented
- [ ] No innerHTML with user data
- [ ] Vue/React templates safe by default
- [ ] Third-party content sandboxed

### Examples
```javascript
// GOOD: Safe rendering
<div>{userInput}</div>  // React escapes by default

// BAD: Injection vulnerable
<div dangerouslySetInnerHTML={{__html: userInput}} />
```

## Cross-Site Request Forgery (CSRF) Prevention

- [ ] CSRF tokens implemented
- [ ] SameSite cookie attribute set
- [ ] State-changing requests use POST/PUT/DELETE
- [ ] Tokens validated on server side
- [ ] Double-submit cookie pattern if needed

## Authentication & Authorization

- [ ] Strong password requirements
- [ ] Password hashed (bcrypt, scrypt, Argon2)
- [ ] MFA available for sensitive operations
- [ ] Session management secure
- [ ] OAuth2/OIDC for third-party auth
- [ ] Role-based access control (RBAC)
- [ ] Permission checks on every protected resource
- [ ] No privilege escalation possible

### Authentication Checklist
- [ ] Authentication required for protected resources
- [ ] Authorization checked after authentication
- [ ] Roles/permissions clearly defined
- [ ] Least privilege principle applied
- [ ] Service accounts rotated regularly

## Sensitive Data Protection

- [ ] PII (Personally Identifiable Information) identified
- [ ] Encryption at rest for sensitive data
- [ ] Encryption in transit (TLS/HTTPS)
- [ ] Sensitive data not logged
- [ ] Database credentials in environment variables
- [ ] API keys not in version control
- [ ] Database backups encrypted
- [ ] Data retention policy implemented

### Logging Security
```python
# GOOD: Don't log sensitive data
logger.info(f"User {user_id} logged in")

# BAD: Logging password
logger.info(f"User {email} logged in with password {password}")
```

## Dependency Security

- [ ] Dependency versions pinned
- [ ] Known vulnerabilities checked (OWASP, CVE)
- [ ] Outdated dependencies updated
- [ ] Dependency scan tools run
- [ ] Supply chain risks assessed
- [ ] Only trusted package sources used

### Tools
- npm: `npm audit`
- Python: `safety check`, `pip-audit`
- Maven: `dependency-check`
- General: `snyk`, `OWASP Dependency-Check`

## API Security

- [ ] Only HTTPS/TLS used
- [ ] Rate limiting implemented
- [ ] Input validation on all endpoints
- [ ] Authentication required for sensitive endpoints
- [ ] Authorization checked appropriately
- [ ] Error messages don't leak information
- [ ] API versioning implemented
- [ ] Deprecation warning provided

### API Response Security
```json
// GOOD: No sensitive data in error
{"error": "Invalid request"}

// BAD: Revealing internal details
{"error": "Invalid request: SQL syntax error near 'users'"}
```

## OWASP Top 10

### A01:2021 - Broken Access Control
- [ ] Access control policies enforced
- [ ] Principle of least privilege applied
- [ ] Horizontal access control (user can't see other users' data)
- [ ] Vertical access control (user can't access higher privilege functions)

### A02:2021 - Cryptographic Failures
- [ ] Data classified by sensitivity
- [ ] Encryption implemented for sensitive data
- [ ] Strong encryption algorithms used
- [ ] Key management secure
- [ ] TLS/HTTPS used for data in transit

### A03:2021 - Injection
- [ ] Input validation everywhere
- [ ] Parameterized queries used
- [ ] Dangerous functions avoided
- [ ] Escaping applied appropriately

### A04:2021 - Insecure Design
- [ ] Threat modeling done
- [ ] Secure design patterns used
- [ ] Security controls integrated
- [ ] Risk assessment completed

### A05:2021 - Security Misconfiguration
- [ ] Default credentials changed
- [ ] Unnecessary features disabled
- [ ] Security headers configured
- [ ] Error handling configured securely
- [ ] Dependencies updated
- [ ] Environment isolation

### A06:2021 - Vulnerable Components
- [ ] Dependencies audited
- [ ] Known vulnerabilities resolved
- [ ] Update strategy implemented
- [ ] Compatibility verified

### A07:2021 - Authentication Failures
- [ ] Strong password policies
- [ ] Secure session management
- [ ] MFA where appropriate
- [ ] Password reset secure
- [ ] Brute force protection

### A08:2021 - Data Integrity Failures
- [ ] Input validation implemented
- [ ] Serialization/deserialization safe
- [ ] CI/CD pipeline secure
- [ ] Software updates verified

### A09:2021 - Logging & Monitoring
- [ ] Security events logged
- [ ] Logs protected from unauthorized access
- [ ] Alerts configured for suspicious activity
- [ ] Incident response plan exists

### A10:2021 - SSRF (Server-Side Request Forgery)
- [ ] External URLs validated
- [ ] Network access restricted
- [ ] DNS validation implemented
- [ ] Deny list for internal addresses

## Security Review Decision

- [ ] ✅ PASS - No critical security issues
- [ ] ⚠️  CONDITIONAL - Fix required issues before approval
- [ ] ❌ FAIL - Critical security issues found, significant rework needed

## Security Issues Found

(List any security issues discovered)

---

## References
- OWASP Top 10: https://owasp.org/Top10/
- OWASP Cheat Sheets: https://cheatsheetseries.owasp.org/
- CWE Top 25: https://cwe.mitre.org/top25/

