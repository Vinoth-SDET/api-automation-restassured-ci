# Security Policy

## Supported versions

| Version | Supported |
|---|---|
| 2.x | Yes |
| 1.x | No — upgrade to 2.x |

## Reporting a vulnerability

If you discover a security vulnerability in this framework, please do **not** open a public GitHub issue.

Email: vinoth.sdet.security@gmail.com

Please include:
- Description of the vulnerability
- Steps to reproduce
- Potential impact
- Suggested fix (optional)

You will receive a response within 48 hours. If confirmed, a patch will be released within 7 days.

## Security practices in this framework

- Auth tokens resolved from CI secrets — never hardcoded in source
- `SecretResolver` interpolates `${ENV_VAR}` at runtime only
- No credentials in `.properties` files committed to source control
- OWASP dependency check available via `make check-deps`
- Dependabot configured for weekly dependency updates