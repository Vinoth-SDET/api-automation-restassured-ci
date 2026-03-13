# Enterprise API Automation Suite

[![CI](https://github.com/Vinoth-SDET/enterprise-api-automation-suite/actions/workflows/ci.yml/badge.svg)](https://github.com/Vinoth-SDET/enterprise-api-automation-suite/actions/workflows/ci.yml)
[![Allure Report](https://img.shields.io/badge/Allure-Live%20Report-brightgreen?logo=data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCAyNCAyNCI+PHBhdGggZmlsbD0id2hpdGUiIGQ9Ik0xMiAyQzYuNDggMiAyIDYuNDggMiAxMnM0LjQ4IDEwIDEwIDEwIDEwLTQuNDggMTAtMTBTMTcuNTIgMiAxMiAyem0tMiAxNWwtNS01IDEuNDEtMS40MUwxMCAxNC4xN2w3LjU5LTcuNTlMMTkgOGwtOSA5eiIvPjwvc3ZnPg==)](https://Vinoth-SDET.github.io/enterprise-api-automation-suite)
[![Extent Report](https://img.shields.io/badge/Extent-Report-blueviolet)](https://github.com/Vinoth-SDET/enterprise-api-automation-suite/actions)
[![Java](https://img.shields.io/badge/Java-21-blue?logo=openjdk)](https://adoptium.net/)
[![RestAssured](https://img.shields.io/badge/RestAssured-5.4.0-orange)](https://rest-assured.io/)
[![Tests](https://img.shields.io/badge/Tests-21%20Passing-success)](https://Vinoth-SDET.github.io/enterprise-api-automation-suite)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

-----

> **A production-grade REST API test automation framework** demonstrating the engineering standards expected at Tier-1 financial institutions and high-growth SaaS companies — parallel execution, dual reporting, multi-environment CI/CD, JSON schema contract validation, and zero test-layer coupling to HTTP concerns.

📊 **[View Live Allure Report](https://Vinoth-SDET.github.io/enterprise-api-automation-suite)**  |  ⚡ **[View CI Pipeline](https://github.com/Vinoth-SDET/enterprise-api-automation-suite/actions)**

-----

## What This Framework Demonstrates

|Skill Area            |Implementation                                                            |
|----------------------|--------------------------------------------------------------------------|
|**Framework Design**  |Ports & Adapters pattern — tests have zero HTTP coupling                  |
|**Parallel Execution**|ThreadLocal ApiClient; 5 concurrent threads, zero flakiness               |
|**Dual Reporting**    |Allure (GitHub Pages) + ExtentReports (CI artifact) side by side          |
|**Contract Testing**  |JSON Schema draft-07 validation catches API drift beyond status codes     |
|**Multi-Environment** |`dev` / `qa` / `staging` via JVM flag — no code changes needed            |
|**CI/CD**             |GitHub Actions matrix strategy; auto-publish to GitHub Pages on every push|
|**Security**          |Auth tokens resolved from CI secrets — zero hardcoded credentials         |
|**Resilience**        |Auto-retry on 5xx with exponential back-off via RestAssured Filter        |
|**Data-Driven**       |Externalised JSON test data + TestNG `@DataProvider` with parallel support|
|**Observability**     |Correlation IDs on every request; async Log4j2 rolling-file logging       |

-----

## Architecture

```
Tests  →  Service Layer  →  ApiClient  →  RestAssured  →  API
```

The framework follows a **Ports & Adapters** pattern adapted for test automation. Each layer has a single responsibility — tests read as business specifications, not HTTP scripts.

|Layer      |Responsibility                                  |Key Classes                          |
|-----------|------------------------------------------------|-------------------------------------|
|**Test**   |Business-scenario assertions. Zero HTTP concern.|`GetUserTests`, `PostCrudTests`      |
|**Service**|Reusable business-intent methods                |`UserService`, `PostService`         |
|**Client** |HTTP verb abstraction; auth, retry, logging     |`ApiClient`, `RetryFilter`           |
|**Config** |Environment resolution, secret interpolation    |`ConfigManager`                      |
|**Model**  |Typed POJO request/response objects (Lombok)    |`UserRequest`, `UserResponse`        |
|**Util**   |Assertion library, schema validator, data loader|`ResponseValidator`, `TestDataLoader`|

-----

## Tech Stack

|Tool          |Version|Purpose                                                  |
|--------------|-------|---------------------------------------------------------|
|Java          |21     |Language                                                 |
|RestAssured   |5.4.0  |HTTP client DSL                                          |
|TestNG        |7.9.0  |Test runner + parallel execution                         |
|Allure        |2.27.0 |Rich HTML report — live on GitHub Pages                  |
|ExtentReports |5.1.1  |Self-contained dark-theme HTML report                    |
|Log4j2        |2.23.1 |Async rolling-file logging with correlation IDs          |
|Jackson       |2.17.1 |JSON serialisation / POJO mapping                        |
|Lombok        |1.18.32|Boilerplate-free POJOs (`@Builder`, `@Data`)             |
|AssertJ       |3.25.3 |Fluent assertions for complex object comparisons         |
|GitHub Actions|—      |CI/CD: matrix strategy, dual report upload, Pages publish|
|Docker        |—      |Hermetic test execution                                  |

-----

## Project Structure

```
enterprise-api-automation-suite/
├── .github/
│   └── workflows/
│       ├── ci.yml                    # PR gate: compile + test + both reports
│       └── nightly-regression.yml   # Full suite on schedule
├── src/
│   ├── main/java/com/vinoth/api/
│   │   ├── client/
│   │   │   ├── ApiClient.java        # Core HTTP facade
│   │   │   ├── RequestBuilder.java   # Fluent request builder
│   │   │   └── RetryFilter.java      # Auto-retry on 5xx with back-off
│   │   ├── config/
│   │   │   └── ConfigManager.java    # Env-aware singleton config
│   │   ├── constants/
│   │   │   ├── Endpoints.java        # All API path constants
│   │   │   └── HttpStatus.java       # Named status codes
│   │   ├── models/
│   │   │   ├── request/              # UserRequest, PostRequest
│   │   │   └── response/             # UserResponse, PostResponse
│   │   ├── services/
│   │   │   ├── UserService.java      # Business-intent User API methods
│   │   │   └── PostService.java      # Business-intent Post API methods
│   │   └── utils/
│   │       ├── ExtentManager.java      # Singleton ExtentReports manager
│   │       ├── ResponseValidator.java  # Fluent assertion chaining library
│   │       ├── SchemaValidator.java    # JSON Schema validation
│   │       ├── TestDataLoader.java     # JSON → POJO data loader
│   │       └── RetryAnalyzer.java      # TestNG retry + listener
│   └── test/
│       ├── java/com/vinoth/api/
│       │   ├── base/BaseTest.java      # ThreadLocal ApiClient + dual report wiring
│       │   ├── tests/
│       │   │   ├── users/              # GetUserTests, CreateUserTests, DeleteUserTests
│       │   │   └── posts/              # PostCrudTests
│       │   └── dataproviders/          # UserDataProvider, PostDataProvider
│       └── resources/
│           ├── config/                 # dev / qa / staging .properties
│           ├── schemas/                # JSON Schema draft-07 files
│           ├── testdata/               # Externalised JSON test data
│           ├── log4j2.xml
│           └── testng-suites/          # smoke / regression / parallel XML
├── Dockerfile
├── Makefile
├── pom.xml
└── README.md
```

-----

## Quick Start

**Prerequisites:** Java 21, Maven 3.8+

```bash
# Clone
git clone https://github.com/Vinoth-SDET/enterprise-api-automation-suite
cd enterprise-api-automation-suite

# Run smoke suite (fastest feedback)
mvn test -Denv=qa -Dtestng.suite=src/test/resources/testng-suites/smoke.xml

# Run full regression with 5 parallel threads
mvn test -Denv=qa -Dtestng.suite=src/test/resources/testng-suites/parallel.xml -Dthreads=5

# Generate and open Allure report locally
mvn allure:serve

# Open ExtentReports (generated automatically after every run)
open target/extent-reports/TestReport.html
```

**Makefile shortcuts:**

```bash
make smoke                     # quick sanity check
make regression ENV=staging    # full suite against staging
make docker-test ENV=qa        # hermetic Docker run
make report                    # open Allure locally
```

-----

## Reports

This framework produces **two independent reports** on every CI run — both available as downloadable artifacts from the Actions tab.

### Allure Report — Live on GitHub Pages

- Epic / Feature / Story grouping via annotations
- Full HTTP request + response captured per test step
- Historical trend across every CI run
- 🔗 **https://Vinoth-SDET.github.io/enterprise-api-automation-suite**

### ExtentReports — Self-Contained HTML

- Dark-themed dashboard; no server needed — open the file directly
- PASS / FAIL / SKIP per test with duration and environment info
- Downloaded from the **Actions → Artifacts** section after any CI run

-----

## Run in Docker

```bash
# Build image and run tests
make docker-test ENV=qa SUITE=regression

# Reports extracted to ./output/
```

-----

## Environment Configuration

|Flag            |Description                                      |Default         |
|----------------|-------------------------------------------------|----------------|
|`-Denv`         |Active environment profile (`dev`/`qa`/`staging`)|`qa`            |
|`-Dtestng.suite`|Path to TestNG suite XML                         |`regression.xml`|
|`-Dthreads`     |Parallel thread count                            |`5`             |
|`-Dlog.level`   |Log level (`DEBUG`/`INFO`/`WARN`)                |`DEBUG`         |

Auth tokens resolved from environment variables — **no secrets in code:**

```properties
# qa.properties
auth.token = ${QA_AUTH_TOKEN}   # injected from CI secret at runtime
```

-----

## CI/CD Pipeline

GitHub Actions runs automatically on every PR and push to `main`:

1. **Build** — compile + validate (Maven cache cuts build time ~60%)
1. **Test** — matrix strategy across `[smoke, regression] × [qa]` in parallel
1. **Reports** — Allure published to GitHub Pages; ExtentReport uploaded as artifact
1. **Nightly** — full regression suite runs on schedule against all environments

-----

## Key Design Decisions

|Decision                                   |Why It Matters                                                                                 |
|-------------------------------------------|-----------------------------------------------------------------------------------------------|
|`ThreadLocal<ApiClient>`                   |Each parallel thread gets its own isolated HTTP client — eliminates race conditions            |
|Service layer                              |Tests read as business specs (`createUser`, `deletePost`) not HTTP noise (`given().body()...`) |
|`RetryFilter` in `ApiClient`               |Transient 5xx handled by the framework — zero retry boilerplate in test classes                |
|JSON Schema validation                     |Catches API contract drift (field removals, type changes) that status-code checks miss entirely|
|Dual reports (Allure + Extent)             |Allure for deep step-level debugging; Extent for instant at-a-glance dashboard                 |
|`ConfigManager` singleton                  |Environment is a JVM flag — same test code runs across dev/qa/staging with no changes          |
|`src/main` for framework code              |Framework is independently publishable as a Maven dependency for other teams                   |
|`@Step` on service methods                 |Every service call appears as a named step in Allure — clear audit trail per test              |
|`RetryListener` not `@Test(retryAnalyzer=)`|Retry attached via framework hook — zero annotation boilerplate in 21 test methods             |

-----

## Author

**Vinoth M** — Staff SDET with 11+ years in test automation across financial services and SaaS

[GitHub](https://github.com/Vinoth-SDET)  ·  [LinkedIn](https://linkedin.com/in/vinoth-m-qa)

-----

*Framework built to production standards. Every design decision is documented in the Key Design Decisions table above — ask me about any of them in an interview.*