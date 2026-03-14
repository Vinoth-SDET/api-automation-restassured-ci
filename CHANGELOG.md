# Changelog

All notable changes follow [Keep a Changelog](https://keepachangelog.com/en/1.0.0/) and [Semantic Versioning](https://semver.org/).

---

## [2.1.0] — 2026-03-15

### Added
- `WireMockBase` abstract class — all contract tests extend it, start/stop lifecycle managed automatically
- `PostContractTests` — 4 offline contract tests for Posts API
- `UserContractTests` — 4 offline contract tests for Users API using WireMock stubs
- `UserPayloadHelper` — centralised payload builder, separates data construction from test logic
- `AllureAttachmentUtil` — attaches response bodies and JSON to Allure report steps
- `TestDataFactory` — JavaFaker-powered randomised test data, prevents parallel data collision
- `EnvironmentConfig` — typed config POJO built from `ConfigManager`
- `SecretResolver` — `${ENV_VAR}` interpolation in `.properties` files at runtime
- `AuthFilter`, `LoggingFilter`, `CorrelationIdProvider` — filter chain for every HTTP request
- `AllureListener`, `ExtentListener`, `RetryListener` — TestNG listeners registered via suite XML
- `ErrorResponse`, `PagedResponse` — typed response POJOs for negative and paginated scenarios
- `UserDataProvider`, `PostDataProvider` — `@DataProvider` classes with boundary value sets
- `SECURITY.md` — vulnerability reporting policy
- `.github/dependabot.yml` — weekly automated dependency updates
- `docs/architecture.md` — 10 Architecture Decision Records with Mermaid diagrams
- All 5 TestNG suite XMLs committed — `smoke`, `regression`, `contract`, `negative`, `parallel`
- Nightly workflow scheduled at 05:30 AM IST across 3 envs × 3 suites (9 matrix jobs)

### Fixed
- `RetryAnalyzer` now guards `result.getStatus() != FAILURE` — passing tests no longer retried
- All test classes use `@BeforeMethod(alwaysRun = true, Method method)` — no NPE on retry path
- `AllureAttachmentUtil` uses `ByteArrayInputStream` — compatible with Allure 2.27 API
- `Endpoints.java` — `AUTH_LOGIN` and `AUTH_LOGOUT` constants added
- `pom.xml` — removed orphan `<plugin>` block outside `<build>` that caused Malformed POM error
- `SLA thresholds` — replaced hardcoded `2000ms` with `EXTERNAL_API_SLA_MS = 8000L` constant
- CI badge URLs — corrected to match exact workflow filename and repo casing
- `nightly.yml` renamed from `nightly_regression.yml` to match README badge reference

### Changed
- `@DataProvider(parallel = true)` removed from external DataProvider classes — fixes TestNG 7.9 "DataProvider not found" error
- `ci.yml` — removed `if: github.ref == 'refs/heads/main'` gate on regression job — full matrix runs on every PR
- `nightly.yml` cron updated to `30 23 * * *` (23:30 UTC = 05:30 AM IST)
- `.gitignore` — comprehensive rewrite excludes `.idea/`, `target/`, `*.iml`, report output, logs, secrets

---

## [2.0.0] — 2024-11-15

### Added
- Initial enterprise framework: `ApiClient`, `RequestBuilder`, `RetryFilter`
- `UserService`, `PostService` with `@Step` Allure annotations
- Dual reporting: Allure (GitHub Pages) + ExtentReports
- GitHub Actions CI with smoke + regression matrix
- `Dockerfile` and `Makefile`
- JSON Schema validation for User and Post responses
- Multi-environment config: `dev`, `qa`, `staging`
- TestNG parallel execution with `ThreadLocal<ApiClient>` isolation