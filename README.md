# Enterprise API Automation Suite

<div align="center">

[![CI — Smoke](https://github.com/Vinoth-SDET/Enterprise-API-Automation-Suite/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/Vinoth-SDET/Enterprise-API-Automation-Suite/actions/workflows/ci.yml)
[![Nightly Regression](https://github.com/Vinoth-SDET/Enterprise-API-Automation-Suite/actions/workflows/nightly.yml/badge.svg)](https://github.com/Vinoth-SDET/Enterprise-API-Automation-Suite/actions/workflows/nightly_regression.yml)
[![Allure Report](https://img.shields.io/badge/Allure-Live%20Report-brightgreen?logo=data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCAyNCAyNCI+PHBhdGggZD0iTTEyIDJDNi40OCAyIDIgNi40OCAyIDEyczQuNDggMTAgMTAgMTAgMTAtNC40OCAxMC0xMFMxNy41MiAyIDEyIDJ6bTAgMThjLTQuNDIgMC04LTMuNTgtOC04czMuNTgtOCA4LTggOCAzLjU4IDggOC0zLjU4IDgtOCA4eiIvPjwvc3ZnPg==)](https://Vinoth-SDET.github.io/Enterprise-API-Automation-Suite)
[![Java 21](https://img.shields.io/badge/Java-21-blue?logo=openjdk&logoColor=white)](https://adoptium.net/)
[![RestAssured](https://img.shields.io/badge/RestAssured-5.4.0-orange)](https://rest-assured.io/)
[![TestNG](https://img.shields.io/badge/TestNG-7.9.0-red)](https://testng.org/)
[![Tests Passing](https://img.shields.io/badge/Tests-42%20Passing-success)](https://github.com/Vinoth-SDET/Enterprise-API-Automation-Suite/actions)
[![Coverage](https://img.shields.io/badge/API%20Coverage-CRUD%20%2B%20Contract%20%2B%20Negative-blue)](https://github.com/Vinoth-SDET/Enterprise-API-Automation-Suite)
[![Docker](https://img.shields.io/badge/Docker-Ready-2496ED?logo=docker&logoColor=white)](https://github.com/Vinoth-SDET/Enterprise-API-Automation-Suite/blob/main/Dockerfile)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

</div>

<div align="center">

**A production-grade REST API test automation framework** demonstrating engineering standards
expected at Tier-1 financial institutions and high-growth SaaS companies.

📊 **[Live Allure Report](https://Vinoth-SDET.github.io/Enterprise-API-Automation-Suite)** &nbsp;|&nbsp; ⚡ **[CI Pipeline](https://github.com/Vinoth-SDET/Enterprise-API-Automation-Suite/actions)** &nbsp;|&nbsp; 📖 **[Architecture Decisions](docs/architecture.md)**

</div>

---

## What This Framework Demonstrates

| Skill Area | Implementation | Signal to Interviewer |
|---|---|---|
| **Framework Architecture** | Ports & Adapters — tests have zero HTTP coupling | Understands separation of concerns |
| **Parallel Execution** | `ThreadLocal<ApiClient>`, 5 concurrent threads, zero flakiness | Knows thread safety in test design |
| **Contract Testing** | JSON Schema draft-07 + WireMock stubs | Tests API drift, not just status codes |
| **Dual Reporting** | Allure (GitHub Pages) + ExtentReports (CI artifact) | Cares about stakeholder communication |
| **Multi-Environment** | `dev` / `qa` / `staging` via JVM flag, zero code changes | Production deployment mindset |
| **CI/CD Pipeline** | Matrix strategy, artifact upload, GitHub Pages auto-publish | Treats CI as a first-class citizen |
| **Negative Testing** | Dedicated `negative/` package, unhappy-path as first-class tests | Completeness beyond happy path |
| **Resilience** | `RetryFilter` with exponential back-off on 5xx | Distributed systems awareness |
| **Security** | Auth tokens from CI secrets — zero hardcoded credentials | Secure engineering practices |
| **Observability** | Correlation IDs on every request, async Log4j2 rolling logs | Ops and debugging readiness |

---

## Architecture

```
┌──────────────────────────────────────────────────────────────────┐
│                          TEST LAYER                               │
│   Smoke Tests │ Regression Tests │ Contract Tests │ Negative     │
│   (Business intent — zero HTTP concern in test classes)          │
└─────────────────────────────┬────────────────────────────────────┘
                              │  calls
┌─────────────────────────────▼────────────────────────────────────┐
│                        SERVICE LAYER                              │
│        UserService  │  PostService  │  AuthService               │
│        (@Step annotated — each call appears in Allure trace)     │
└─────────────────────────────┬────────────────────────────────────┘
                              │  delegates to
┌─────────────────────────────▼────────────────────────────────────┐
│                      API CLIENT LAYER                             │
│   ApiClient (ThreadLocal)   │   RequestBuilder (immutable)       │
│   RetryFilter (5xx backoff) │   AuthFilter  │  CorrelationFilter │
└─────────────────────────────┬────────────────────────────────────┘
                              │  configured by
┌─────────────────────────────▼────────────────────────────────────┐
│                    INFRASTRUCTURE LAYER                           │
│   ConfigManager (env-aware) │  SchemaValidator  │  WireMock      │
│   SecretResolver (CI vars)  │  TestDataFactory  │  Log4j2        │
└──────────────────────────────────────────────────────────────────┘
```

**Key architectural decision:** The `src/main` package contains the entire framework — it can be published as a standalone Maven dependency consumed by multiple test teams. Test code in `src/test` contains only test intent, never framework mechanics.

---

## Tech Stack

| Tool | Version | Purpose |
|---|---|---|
| Java | 21 | Language (records, sealed classes, text blocks) |
| RestAssured | 5.4.0 | HTTP client DSL with filter chain |
| TestNG | 7.9.0 | Test runner, parallel execution, `@DataProvider` |
| Allure | 2.27.0 | Rich HTML report — published to GitHub Pages |
| ExtentReports | 5.1.1 | Self-contained dark-theme HTML dashboard |
| WireMock | 3.x | Local API stub server for contract tests |
| Log4j2 | 2.23.1 | Async rolling-file logging, correlation IDs |
| Jackson | 2.17.1 | JSON serialisation, POJO mapping |
| Lombok | 1.18.32 | `@Builder`, `@Data`, `@RequiredArgsConstructor` |
| AssertJ | 3.25.3 | Fluent assertions with meaningful failure messages |
| JSON Schema Validator | 5.4.0 | Draft-07 contract validation |
| GitHub Actions | — | CI/CD: matrix strategy, Pages publish, artifacts |
| Docker | — | Hermetic test execution |

---

## Project Structure

```
enterprise-api-automation-suite/
├── .github/
│   ├── ISSUE_TEMPLATE/         # Bug report + test-gap templates
│   ├── pull_request_template.md
│   └── workflows/
│       ├── ci.yml              # PR gate: compile + smoke + report
│       ├── regression.yml      # Merge to main: full suite
│       └── nightly.yml         # Scheduled: all envs, matrix strategy
│
├── docs/
│   └── architecture.md         # Architecture Decision Records (ADRs)
│
├── src/
│   ├── main/java/com/vinoth/automation/
│   │   ├── client/             # ApiClient, RequestBuilder, Filters
│   │   ├── config/             # ConfigManager, SecretResolver
│   │   ├── constants/          # Endpoints, HttpStatus, Headers
│   │   ├── models/             # Request/Response POJOs (Lombok)
│   │   ├── services/           # Business-intent service layer
│   │   └── utils/              # ResponseValidator, SchemaValidator, DataFactory
│   │
│   └── test/
│       ├── java/com/vinoth/automation/
│       │   ├── base/           # BaseTest, WireMockBase
│       │   ├── tests/
│       │   │   ├── users/      # GET, POST, PUT, DELETE + Negative
│       │   │   ├── posts/      # CRUD + Negative
│       │   │   └── contract/   # WireMock-backed contract tests
│       │   ├── dataproviders/  # TestNG @DataProvider classes
│       │   └── listeners/      # Allure, Extent, Retry listeners
│       │
│       └── resources/
│           ├── config/         # dev / qa / staging .properties
│           ├── schemas/        # JSON Schema draft-07 files
│           ├── testdata/       # Externalised JSON test fixtures
│           ├── wiremock/       # WireMock stub mappings
│           └── testng-suites/  # smoke / regression / contract / parallel XML
│
├── Dockerfile
├── Makefile
├── CONTRIBUTING.md
├── CHANGELOG.md
└── pom.xml
```

---

## Quick Start

**Prerequisites:** Java 21, Maven 3.8+

```bash
# 1. Clone
git clone https://github.com/Vinoth-SDET/Enterprise-API-Automation-Suite
cd Enterprise-API-Automation-Suite

# 2. Run smoke suite (fastest feedback — ~30 seconds)
mvn test -Denv=qa -Dsuite=smoke

# 3. Run full regression with parallel execution
mvn test -Denv=qa -Dsuite=regression -Dthreads=5

# 4. Run contract tests (WireMock-backed, no external dependency)
mvn test -Dsuite=contract

# 5. Run negative tests
mvn test -Denv=qa -Dsuite=negative

# 6. Generate and open Allure report locally
mvn allure:serve

# 7. Open ExtentReport (generated automatically after every run)
open target/extent-reports/TestReport.html
```

**Makefile shortcuts:**

```bash
make smoke                      # Quick sanity check (~30s)
make regression ENV=staging     # Full suite against staging
make contract                   # WireMock contract tests (no network)
make docker-test ENV=qa         # Hermetic Docker execution
make report                     # Open Allure locally
```

---

## Sample Test

This test demonstrates the full vertical slice — business-readable, schema-validated, Allure-annotated:

```java
@Epic("User Management API")
@Feature("GET /users")
public class GetUserTests extends BaseTest {

    @Test(groups = {"smoke", "regression"})
    @Story("Retrieve existing user by ID")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Validates status, schema, field values, and response time SLA")
    public void getExistingUser_returns200WithValidSchema() {
        Response response = new UserService(client()).getUserById(1);

        ResponseValidator.of(response)
            .hasStatus(200)
            .hasHeader("Content-Type", "application/json")
            .bodyFieldEquals("id", 1)
            .bodyFieldNotNull("name")
            .bodyMatchesSchema("user-response-schema.json") // contract check
            .responseTimeLessThan(2000);                    // SLA check
    }
}
```

**What a reviewer sees:** Tests read as business specifications. No `given().when().then()` boilerplate. Schema validation catches API drift beyond status codes. Response time assertion enforces SLA at test level.

---

## Test Coverage Matrix

| Suite | Scope | Groups | Threads | Avg Runtime |
|---|---|---|---|---|
| `smoke.xml` | Core CRUD happy paths | `smoke` | 1 | ~30s |
| `regression.xml` | Full positive + negative coverage | `regression` | 5 | ~3m |
| `contract.xml` | WireMock-backed schema contracts | `contract` | 3 | ~1m |
| `negative.xml` | 4xx error handling, boundary values | `negative` | 3 | ~1m |
| `parallel.xml` | Full suite, maximum concurrency | all | 5 | ~2m |

---

## Reports

Two independent reports are produced on every CI run:

### Allure — Live on GitHub Pages

- Epic / Feature / Story grouping via `@Epic`, `@Feature`, `@Story` annotations
- Full HTTP request + response body captured per test step via `@Step` methods
- Historical trend graph across every CI run
- 🔗 **[https://Vinoth-SDET.github.io/Enterprise-API-Automation-Suite](https://Vinoth-SDET.github.io/Enterprise-API-Automation-Suite)**

### ExtentReports — Self-Contained HTML Dashboard

- Dark-themed dashboard; open the file directly — no server needed
- PASS / FAIL / SKIP per test with duration, environment, and thread info
- Download from **Actions → Artifacts** after any CI run

---

## CI/CD Pipeline

```yaml
# .github/workflows/ci.yml (simplified)
jobs:
  test:
    strategy:
      matrix:
        suite: [smoke, regression]
        env:   [qa]
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with: { java-version: '21', distribution: 'temurin' }

      - name: Run ${{ matrix.suite }} on ${{ matrix.env }}
        run: mvn test -Denv=${{ matrix.env }} -Dsuite=${{ matrix.suite }}
        env:
          QA_AUTH_TOKEN: ${{ secrets.QA_AUTH_TOKEN }}   # zero hardcoded creds

      - name: Upload Allure results
        uses: actions/upload-artifact@v4
        with:
          name: allure-${{ matrix.suite }}
          path: target/allure-results

      - name: Publish to GitHub Pages
        uses: peaceiris/actions-gh-pages@v3
        if: github.ref == 'refs/heads/main'
        with:
          publish_dir: target/site/allure-maven-plugin
```

**Pipeline stages:**
1. **Compile** — Maven cache cuts build time ~60%
2. **Test** — Matrix strategy runs suites in parallel across environments
3. **Reports** — Allure published to GitHub Pages; ExtentReport as downloadable artifact
4. **Nightly** — Full regression against `dev`, `qa`, and `staging` on schedule

---

## Environment Configuration

| Flag | Description | Default |
|---|---|---|
| `-Denv` | Active environment (`dev`/`qa`/`staging`) | `qa` |
| `-Dsuite` | Path or name of TestNG suite XML | `regression` |
| `-Dthreads` | Parallel thread count | `5` |
| `-Dlog.level` | Log level (`DEBUG`/`INFO`/`WARN`) | `INFO` |

**Zero hardcoded credentials:**

```properties
# qa.properties
base.url       = https://jsonplaceholder.typicode.com
auth.token     = ${QA_AUTH_TOKEN}    # resolved from CI secret at runtime
max.retries    = 3
retry.delay.ms = 1000
```

---

## Key Design Decisions

| Decision | Rationale |
|---|---|
| `ThreadLocal<ApiClient>` | Each parallel thread owns its HTTP client — eliminates race conditions without synchronisation overhead |
| Service layer with `@Step` | Tests read as business specifications; every service call is a named step in Allure — full audit trail |
| Immutable `RequestBuilder` | New instance per method call — safe for parallel execution, no shared mutable request state |
| `RetryFilter` in `ApiClient` | Transient 5xx handled transparently — zero retry boilerplate across 42 test methods |
| JSON Schema validation | Catches API contract drift (field removals, type changes) that status-code checks miss entirely |
| WireMock for contract tests | Contract tests run offline — no network dependency, deterministic, fast CI |
| Dual reports | Allure for deep step-level debugging; Extent for instant at-a-glance dashboard accessible to non-engineers |
| `src/main` for framework code | Framework is independently publishable as a Maven dependency for other test teams |
| `ConfigManager` singleton | Environment is a JVM flag — identical test code runs across dev/qa/staging |
| `RetryListener` (not annotation) | Retry attached via TestNG listener — zero `retryAnalyzer=` boilerplate on any `@Test` annotation |
| `ErrorResponse` POJO | Negative tests assert on typed error structure, not raw string matching |

---

## Run in Docker

```bash
# Build image and run full suite
docker build -t api-automation-suite .
docker run -e ENV=qa -e QA_AUTH_TOKEN=$QA_AUTH_TOKEN api-automation-suite

# Makefile shortcut
make docker-test ENV=qa SUITE=regression

# Reports extracted to ./output/
make docker-report
```

---

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md) for branch strategy, commit conventions, and the PR checklist.

Use GitHub issue templates:
- **Bug report** — for test failures or framework defects
- **Test gap** — to propose coverage for an untested API scenario

---

## Author

**Vinoth M** — Staff SDET | Test Automation Architect | 11+ years across BFSI, Healthcare, SaaS

[![GitHub](https://img.shields.io/badge/GitHub-Vinoth--SDET-181717?logo=github)](https://github.com/Vinoth-SDET)
[![LinkedIn](https://img.shields.io/badge/LinkedIn-vinoth--m--qa-0A66C2?logo=linkedin)](https://linkedin.com/in/vinoth-m-qa)

---

> *Every design decision in this framework is documented and defensible. Each one maps to a real engineering trade-off encountered in production automation at scale.*