# API Automation Framework

[![CI](https://github.com/Vinoth-SDET/enterprise-api-automation-suite/actions/workflows/ci.yml/badge.svg)](https://github.com/Vinoth-SDET/enterprise-api-automation-suite/actions/workflows/ci.yml)
[![Allure Report](https://img.shields.io/badge/Allure-Report-brightgreen)](https://Vinoth-SDET.github.io/enterprise-api-automation-suite)
[![Java](https://img.shields.io/badge/Java-21-blue)](https://adoptium.net/)
[![RestAssured](https://img.shields.io/badge/RestAssured-5.4.0-orange)](https://rest-assured.io/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

Enterprise-grade REST API automation framework built with **RestAssured**, **TestNG**, **Maven**, **Log4j2**, **Allure**, and **GitHub Actions**.

> Designed to reflect the standards expected at Tier-1 financial institutions and high-growth SaaS companies. Clone it and have 30+ tests running across three environments in minutes.

---

## Architecture

```
Tests  →  Service Layer  →  ApiClient  →  RestAssured  →  API
```

The framework follows a **Ports & Adapters** pattern adapted for test automation:

| Layer | Responsibility | Key Classes |
|---|---|---|
| **Test** | Business-scenario assertions. Zero HTTP concern. | `GetUserTests`, `PostCrudTests` |
| **Service** | Reusable business-intent methods | `UserService`, `PostService` |
| **Client** | HTTP verb abstraction; auth, retry, logging | `ApiClient`, `RetryFilter` |
| **Config** | Environment resolution, secret interpolation | `ConfigManager` |
| **Model** | Typed POJO request/response objects (Lombok) | `UserRequest`, `UserResponse` |
| **Util** | Assertion library, schema validator, data loader | `ResponseValidator`, `TestDataLoader` |

---

## Tech Stack

| Tool | Version | Purpose |
|---|---|---|
| Java | 21 | Language |
| RestAssured | 5.4.0 | HTTP client DSL |
| TestNG | 7.9.0 | Test runner + parallel execution |
| Allure | 2.27.0 | Rich HTML reports with steps & attachments |
| Log4j2 | 2.23.1 | Async rolling-file logging with correlation IDs |
| Jackson | 2.17.1 | JSON serialisation / POJO mapping |
| Lombok | 1.18.32 | Boilerplate-free POJOs (`@Builder`, `@Data`) |
| AssertJ | 3.25.3 | Fluent assertions for complex object comparisons |
| GitHub Actions | — | CI/CD: matrix strategy, Allure Pages publish |
| Docker | — | Hermetic test execution |

---

## Project Structure

```
enterprise-api-automation-suite/
├── .github/
│   └── workflows/
│       ├── ci.yml                    # PR gate: compile + test + report
│       └── nightly-regression.yml   # Full suite on schedule
├── src/
│   ├── main/java/com/vinoth/api/
│   │   ├── client/
│   │   │   ├── ApiClient.java        # Core HTTP facade
│   │   │   ├── RequestBuilder.java   # Fluent request builder
│   │   │   └── RetryFilter.java      # Auto-retry on 5xx
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
│   │       ├── ResponseValidator.java  # Fluent assertion library
│   │       ├── SchemaValidator.java    # JSON Schema validation
│   │       ├── TestDataLoader.java     # JSON → POJO data loader
│   │       └── RetryAnalyzer.java      # TestNG retry + listener
│   └── test/
│       ├── java/com/vinoth/api/
│       │   ├── base/BaseTest.java      # ThreadLocal ApiClient lifecycle
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

---

## Quick Start

**Prerequisites:** Java 21, Maven 3.8+

```bash
# Clone
git clone https://github.com/Vinoth-SDET/enterprise-api-automation-suite
cd enterprise-api-automation-suite

# Run smoke suite on QA (default)
mvn test -Denv=qa -Dtestng.suite=src/test/resources/testng-suites/smoke.xml

# Run full regression with 5 parallel threads
mvn test -Denv=qa -Dtestng.suite=src/test/resources/testng-suites/parallel.xml -Dthreads=5

# View Allure report in browser
mvn allure:serve
```

**Makefile shortcuts:**

```bash
make smoke                     # quick sanity check
make regression ENV=staging    # full suite against staging
make docker-test ENV=qa        # hermetic Docker run
make report                    # open Allure locally
```

---

## Run in Docker

```bash
# Build image and run tests
make docker-test ENV=qa SUITE=regression

# Reports extracted to ./output/
```

---

## Environment Configuration

| Flag | Description | Default |
|---|---|---|
| `-Denv` | Active environment profile (`dev`/`qa`/`staging`) | `qa` |
| `-Dtestng.suite` | Path to TestNG suite XML | `regression.xml` |
| `-Dthreads` | Parallel thread count | `5` |
| `-Dlog.level` | Log level (`DEBUG`/`INFO`/`WARN`) | `DEBUG` |

Auth tokens are resolved from environment variables — **no secrets in code**:

```properties
# qa.properties
auth.token = ${QA_AUTH_TOKEN}   # expanded from CI secret at runtime
```

---

## CI/CD Pipeline

GitHub Actions runs automatically on every PR and push to `main`:

1. **Build** — compile + validate (Maven cache reduces time ~60%)
2. **Test** — matrix strategy: `[smoke, regression] × [qa]`
3. **Report** — merge Allure results and publish to GitHub Pages

**Live Allure Report:** https://Vinoth-SDET.github.io/enterprise-api-automation-suite

---

## Key Design Decisions

| Decision | Problem Solved |
|---|---|
| `ThreadLocal<ApiClient>` | Thread safety in parallel runs — each thread gets its own isolated HTTP client |
| `ConfigManager` singleton | Eliminates hardcoded URLs; env is a JVM flag, not a code change |
| Service layer | Hides RestAssured DSL from tests; tests read as business specifications |
| `RetryFilter` in `ApiClient` | Handles transient 5xx responses without leaking retry logic into tests |
| JSON Schema validation | Catches API contract drift (new fields, type changes) that status-code assertions miss |
| `@Step` on service methods | Every service call appears as a named step in Allure — no opaque HTTP noise |
| `RetryListener` not `@Test` | Retry attached via framework hook — zero boilerplate in test classes |
| `src/main` for framework code | Framework is publishable as a Maven dependency for other teams to build on |

---

## Author

**Vinoth M** — Staff SDET  
[GitHub](https://github.com/Vinoth-SDET) · [LinkedIn](https://linkedin.com/in/vinoth-m-qa)

---


*Framework built to production standards. See [Architecture Decision Records](docs/ADR.md) for design rationale.*
