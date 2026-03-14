# Changelog

All notable changes to the Enterprise API Automation Suite are documented here.

Format follows [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).
 
---

## [2.0.0] — 2025-03-01

### Added
- WireMock-backed contract test suite (`contract.xml`)
- `UserNegativeTests` and `PostNegativeTests` — unhappy-path coverage as first-class tests
- `UserContractTests` and `PostContractTests` — offline consumer-side contract validation
- `SecretResolver` — env-var interpolation in `.properties` files
- `CorrelationFilter` — X-Correlation-ID header on every outbound request
- `AuthFilter` — Bearer token injection from `ConfigManager`
- `ErrorResponse` POJO for typed negative test assertions
- `CONTRIBUTING.md` with branch strategy, commit conventions, PR checklist
- `docs/architecture.md` — Architecture Decision Records (ADRs) for all 8 key decisions
- GitHub issue templates: `bug_report.md` and `test_gap.md`
- `negative.xml` TestNG suite for isolated negative test execution
- `testng-suites/contract.xml` for WireMock-backed tests

### Changed
- `RequestBuilder` refactored to immutable pattern (each method returns a new instance)
- `BaseTest` now removes `ThreadLocal<ApiClient>` in `@AfterMethod` to prevent memory leaks
- `ConfigManager` resolution order documented: system property → env var → properties file
- README completely rewritten with architecture diagram, ADR table, and test coverage matrix
- `.gitignore` updated: `target/`, `.idea/`, `*.iml` now correctly excluded

### Fixed
- GitHub Actions badge URL corrected (case-sensitive repo name)
- `.idea/` and `*.iml` files removed from source control
- Removed `target/` directory from version control (was causing 51% HTML in language stats)

---

## [1.0.0] — 2024-11-15

### Added
- Initial framework: ApiClient, RequestBuilder, RetryFilter
- UserService, PostService with @Step annotations
- Allure + ExtentReports dual reporting
- GitHub Actions CI with auto-publish to GitHub Pages
- Dockerfile and Makefile
- JSON Schema validation for User and Post responses
- Multi-environment config: dev, qa, staging
- TestNG parallel execution with ThreadLocal isolation