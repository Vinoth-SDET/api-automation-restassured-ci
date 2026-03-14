# Enterprise API Automation Suite

<div align="center">

[![CI — Build & Test](https://github.com/Vinoth-SDET/Enterprise-API-Automation-Suite/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/Vinoth-SDET/Enterprise-API-Automation-Suite/actions/workflows/ci.yml)
[![Nightly Regression](https://github.com/Vinoth-SDET/Enterprise-API-Automation-Suite/actions/workflows/nightly.yml/badge.svg)](https://github.com/Vinoth-SDET/Enterprise-API-Automation-Suite/actions/workflows/nightly.yml)
[![Allure Report](https://img.shields.io/badge/Allure-Live%20Report-brightgreen?logo=buffer&logoColor=white)](https://Vinoth-SDET.github.io/Enterprise-API-Automation-Suite)
[![Java 21](https://img.shields.io/badge/Java-21-blue?logo=openjdk&logoColor=white)](https://adoptium.net/)
[![RestAssured](https://img.shields.io/badge/RestAssured-5.4.0-orange?logo=java&logoColor=white)](https://rest-assured.io/)
[![TestNG](https://img.shields.io/badge/TestNG-7.9.0-red)](https://testng.org/)
[![WireMock](https://img.shields.io/badge/WireMock-3.x-blueviolet)](https://wiremock.org/)
[![Tests](https://img.shields.io/badge/Tests-30%20Passing-success?logo=checkmarx)](https://github.com/Vinoth-SDET/Enterprise-API-Automation-Suite/actions)
[![Coverage](https://img.shields.io/badge/Coverage-CRUD%20%7C%20Contract%20%7C%20Negative-informational)](https://github.com/Vinoth-SDET/Enterprise-API-Automation-Suite)
[![Docker](https://img.shields.io/badge/Docker-Ready-2496ED?logo=docker&logoColor=white)](Dockerfile)
[![Dependabot](https://img.shields.io/badge/Dependabot-Enabled-025E8C?logo=dependabot&logoColor=white)](.github/Dependabot.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

<br/>

**A production-grade REST API test automation framework** built to enterprise engineering standards —
Ports & Adapters architecture, parallel execution, dual reporting, multi-environment CI/CD,
offline contract testing, and zero test-layer coupling to HTTP concerns.

<br/>

📊 **[Live Allure Report](https://Vinoth-SDET.github.io/Enterprise-API-Automation-Suite)** &nbsp;·&nbsp;
⚡ **[CI Pipeline](https://github.com/Vinoth-SDET/Enterprise-API-Automation-Suite/actions)** &nbsp;·&nbsp;
📖 **[Architecture Decisions](docs/architecture.md)** &nbsp;·&nbsp;
📋 **[Changelog](CHANGELOG.md)**

</div>

---

## What this framework demonstrates

| Skill area | Implementation | What it signals to an interviewer |
|---|---|---|
| **Framework architecture** | Ports & Adapters — tests have zero HTTP coupling | Separation of concerns at every layer |
| **Thread safety** | `ThreadLocal<ApiClient>`, 5 concurrent threads, zero flakiness | Parallel execution design |
| **Contract testing** | JSON Schema draft-07 + WireMock offline stubs | Tests API drift, not just status codes |
| **Dual reporting** | Allure (GitHub Pages) + ExtentReports (CI artifact) | Stakeholder-aware engineering |
| **Multi-environment** | `dev` / `qa` / `staging` via JVM flag, zero code changes | Production deployment mindset |
| **CI/CD pipeline** | Matrix strategy, nightly cross-env schedule, Pages auto-publish | CI as a first-class engineering concern |
| **Negative testing** | Dedicated `negative/` package, boundary-value `@DataProvider` | Completeness beyond happy path |
| **Resilience** | `RetryFilter` (5xx + backoff) + `RetryAnalyzer` (failure-only) | Distributed systems awareness |
| **Security** | Auth tokens from CI secrets — zero hardcoded credentials | Secure engineering practices |
| **Observability** | Correlation IDs on every request, async Log4j2 rolling logs | Ops and debugging readiness |
| **Test data** | `TestDataFactory` (JavaFaker) + externalised JSON fixtures | Realistic, collision-free data strategy |
| **Framework packaging** | Framework in `src/main` — publishable as Maven dependency | Organisational reuse mindset |
| **ADR documentation** | 10 Architecture Decision Records in `docs/architecture.md` | Thinks about why, not just what |

---

## Architecture

```
┌──────────────────────────────────────────────────────────────────────┐
│                           TEST LAYER                                 │
│  GetUserTests · CreateUserTests · UpdateUserTests · DeleteUserTests  │
│  UserNegativeTests · PostCrudTests · PostNegativeTests               │
│  UserContractTests · PostContractTests                               │
│  Business intent only — zero HTTP concern in any test class          │
└────────────────────────────┬─────────────────────────────────────────┘
                             │  calls
┌────────────────────────────▼─────────────────────────────────────────┐
│                        SERVICE LAYER                                 │
│        UserService  ·  PostService  ·  AuthService                   │
│        @Step annotated — every call is a named step in Allure        │
└────────────────────────────┬─────────────────────────────────────────┘
                             │  delegates to
┌────────────────────────────▼─────────────────────────────────────────┐
│                      API CLIENT LAYER                                │
│  ApiClient (ThreadLocal)      RequestBuilder (immutable)             │
│  RetryFilter (5xx backoff)    AuthFilter (Bearer injection)          │
│  LoggingFilter (structured)   CorrelationIdProvider (tracing)        │
└────────────────────────────┬─────────────────────────────────────────┘
                             │  configured by
┌────────────────────────────▼─────────────────────────────────────────┐
│                    INFRASTRUCTURE LAYER                              │
│  ConfigManager (env-aware singleton)   SecretResolver (CI vars)      │
│  SchemaValidator (draft-07)            TestDataFactory (JavaFaker)   │
│  WireMock (contract stubs)             Log4j2 (async rolling logs)   │
└──────────────────────────────────────────────────────────────────────┘
```

The `src/main` package contains the entire framework — independently publishable as a Maven dependency
for multiple test teams. `src/test` contains only test intent, never framework mechanics.

---

## Tech stack

| Tool | Version | Purpose |
|---|---|---|
| Java | 21 | Language — records, text blocks, sealed classes |
| RestAssured | 5.4.0 | HTTP client DSL with composable filter chain |
| TestNG | 7.9.0 | Test runner, parallel execution, `@DataProvider` |
| Allure | 2.27.0 | Rich HTML report — published to GitHub Pages |
| ExtentReports | 5.1.1 | Self-contained dark-theme HTML dashboard |
| WireMock | 3.x | Offline stub server for contract tests |
| Log4j2 | 2.23.1 | Async rolling-file logging with correlation IDs |
| Jackson | 2.17.1 | JSON serialisation and POJO mapping |
| Lombok | 1.18.32 | `@Builder`, `@Data`, `@RequiredArgsConstructor` |
| AssertJ | 3.25.3 | Fluent assertions with descriptive failure messages |
| JavaFaker | 1.0.2 | Realistic randomised test data generation |
| JSON Schema Validator | 5.4.0 | Draft-07 contract validation |
| AspectJ | 1.9.21 | Allure `@Step` weaving agent |
| GitHub Actions | — | CI/CD: matrix strategy, Pages publish, nightly schedule |
| Docker | — | Multi-stage hermetic test execution |
| Dependabot | — | Automated weekly dependency updates |

---

## Project structure

```
enterprise-api-automation-suite/
├── .github/
│   ├── ISSUE_TEMPLATE/
│   │   ├── bug_report.md           # Structured bug report template
│   │   └── test_gap.md             # Coverage gap proposal template
│   ├── dependabot.yml              # Weekly automated dependency updates
│   ├── pull_request_template.md    # PR checklist enforced on every merge
│   └── workflows/
│       ├── ci.yml                  # Every PR + push: smoke → matrix → Pages
│       └── nightly.yml             # 05:30 AM IST: 9 jobs (3 envs × 3 suites)
│
├── docs/
│   └── architecture.md             # 10 ADRs + Mermaid architecture diagrams
│
├── src/
│   ├── main/java/com/vinoth/automation/
│   │   ├── client/
│   │   │   ├── ApiClient.java          # ThreadLocal HTTP facade
│   │   │   ├── RequestBuilder.java     # Immutable fluent builder
│   │   │   ├── RetryFilter.java        # 5xx + exponential backoff
│   │   │   ├── AuthFilter.java         # Bearer token injection
│   │   │   ├── LoggingFilter.java      # Structured request logging
│   │   │   └── CorrelationIdProvider.java  # UUID per request
│   │   ├── config/
│   │   │   ├── ConfigManager.java      # Env-aware singleton (3-tier resolution)
│   │   │   ├── EnvironmentConfig.java  # Typed config POJO
│   │   │   └── SecretResolver.java     # ${ENV_VAR} interpolation
│   │   ├── constants/
│   │   │   ├── Endpoints.java          # All API path constants
│   │   │   ├── HttpStatus.java         # Named status codes
│   │   │   └── Headers.java            # Standard header names
│   │   ├── models/
│   │   │   ├── request/                # UserRequest, PostRequest, AuthRequest
│   │   │   └── response/               # UserResponse, PostResponse,
│   │   │                               # ErrorResponse, PagedResponse
│   │   ├── services/
│   │   │   ├── UserService.java        # @Step annotated User API methods
│   │   │   ├── PostService.java        # @Step annotated Post API methods
│   │   │   └── AuthService.java        # Auth endpoint pattern
│   │   └── utils/
│   │       ├── ResponseValidator.java  # Fluent assertion chain
│   │       ├── SchemaValidator.java    # JSON Schema draft-07
│   │       ├── RetryAnalyzer.java      # FAILURE-only retry (not passing tests)
│   │       ├── ExtentManager.java      # Thread-safe Extent singleton
│   │       ├── AllureAttachmentUtil.java  # Response body → Allure attachment
│   │       ├── TestDataFactory.java    # JavaFaker-powered data generation
│   │       └── TestDataLoader.java     # JSON → typed POJO loader
│   │
│   └── test/
│       ├── java/com/vinoth/automation/
│       │   ├── base/
│       │   │   ├── BaseTest.java       # ThreadLocal lifecycle + @BeforeMethod fix
│       │   │   └── WireMockBase.java   # Abstract base for contract tests
│       │   ├── dataproviders/
│       │   │   ├── UserDataProvider.java   # validUsers, invalidUsers, boundaryIds
│       │   │   └── PostDataProvider.java   # validPosts, boundaryPostIds
│       │   ├── helpers/
│       │   │   └── UserPayloadHelper.java  # Centralised payload construction
│       │   ├── listeners/
│       │   │   ├── AllureListener.java     # Env label + failure attachment
│       │   │   ├── ExtentListener.java     # Extent lifecycle hook
│       │   │   └── RetryListener.java      # IAnnotationTransformer retry wiring
│       │   └── tests/
│       │       ├── users/
│       │       │   ├── GetUserTests.java       # 4 tests — list, id, multi, 404
│       │       │   ├── CreateUserTests.java    # 3 tests — full, minimal, PUT
│       │       │   ├── UpdateUserTests.java    # 3 tests — full, random, field
│       │       │   ├── DeleteUserTests.java    # 2 tests — happy path, content-type
│       │       │   └── UserNegativeTests.java  # 5 tests — boundary, SLA, null
│       │       ├── posts/
│       │       │   ├── PostCrudTests.java      # 6 tests — CRUD + filter
│       │       │   └── PostNegativeTests.java  # 4 tests — boundary, SLA, filter
│       │       └── contract/
│       │           ├── UserContractTests.java  # 4 WireMock contract tests
│       │           └── PostContractTests.java  # 4 WireMock contract tests
│       │
│       └── resources/
│           ├── config/
│           │   ├── dev.properties
│           │   ├── qa.properties       # base.url + ${QA_AUTH_TOKEN}
│           │   └── staging.properties
│           ├── schemas/                # 5 JSON Schema draft-07 files
│           ├── testdata/               # Externalised JSON test fixtures
│           ├── wiremock/               # WireMock stub mapping definitions
│           ├── log4j2.xml              # Async rolling-file logger config
│           └── testng-suites/
│               ├── smoke.xml           # Core CRUD happy paths (~30s)
│               ├── regression.xml      # Full positive + negative (~3m)
│               ├── contract.xml        # WireMock offline contracts (~30s)
│               ├── negative.xml        # Boundary + 4xx tests (~1m)
│               └── parallel.xml        # Full suite, 5 threads (~2m)
│
├── .gitignore                          # target/, .idea/, *.iml, logs, secrets
├── CHANGELOG.md                        # Semver history
├── CONTRIBUTING.md                     # Dev setup, branch strategy, PR checklist
├── SECURITY.md                         # Vulnerability reporting policy
├── Dockerfile                          # Multi-stage hermetic test image
├── Makefile                            # Full target coverage with help text
├── LICENSE
├── pom.xml
└── README.md
```

---

## Quick start

**Prerequisites:** Java 21, Maven 3.8+

```bash
# Clone
git clone https://github.com/Vinoth-SDET/Enterprise-API-Automation-Suite
cd Enterprise-API-Automation-Suite

# Smoke suite — fastest feedback (~30s)
mvn test -Denv=qa -Dtestng.suite=src/test/resources/testng-suites/smoke.xml

# Full regression with parallel execution
mvn test -Denv=qa -Dtestng.suite=src/test/resources/testng-suites/regression.xml -Dthreads=5

# Contract tests — fully offline, no network needed
mvn test -Dtestng.suite=src/test/resources/testng-suites/contract.xml

# Negative / boundary tests
mvn test -Denv=qa -Dtestng.suite=src/test/resources/testng-suites/negative.xml

# Maximum concurrency — all tests, 5 parallel threads
mvn test -Denv=qa -Dtestng.suite=src/test/resources/testng-suites/parallel.xml -Dthreads=5

# Generate and open Allure report locally
mvn allure:serve
```

**Makefile shortcuts:**

```bash
make help                        # List all available targets
make smoke                       # Quick sanity check
make regression ENV=staging      # Full suite against staging
make contract                    # Offline WireMock contract tests
make negative                    # Boundary + 4xx tests
make parallel                    # Full suite, 5 threads
make report                      # Open Allure locally
make docker-test ENV=qa          # Hermetic Docker execution
make check-deps                  # OWASP dependency vulnerability scan
```

---

## Test coverage

| Suite | What it covers | Groups | Threads | ~Runtime |
|---|---|---|---|---|
| `smoke.xml` | Core CRUD happy paths | `smoke` | 1 | 30s |
| `regression.xml` | Full positive + negative | `regression` | 5 | 3m |
| `contract.xml` | WireMock offline schema contracts | `contract` | 1 | 30s |
| `negative.xml` | 4xx handling, boundary values | `negative` | 3 | 1m |
| `parallel.xml` | Full suite, maximum concurrency | all | 5 | 2m |

### 30 tests across 9 classes

| Class | Suites | Count | Covers |
|---|---|---|---|
| `GetUserTests` | smoke, regression | 4 | List all, get by id, multi-id, 404 |
| `CreateUserTests` | smoke, regression | 3 | Full payload, minimal payload, PUT update |
| `UpdateUserTests` | regression | 3 | Full update, random data, email field |
| `DeleteUserTests` | smoke, regression | 2 | Happy path, content-type validation |
| `UserNegativeTests` | regression, negative | 5 | 404, boundary ids, SLA, empty/null fields |
| `PostCrudTests` | smoke, regression | 6 | List, get, create (data-driven), update, delete, filter |
| `PostNegativeTests` | regression, negative | 4 | 404, boundary ids, SLA, invalid filter |
| `UserContractTests` | contract | 4 | GET / POST / DELETE schema contracts (WireMock) |
| `PostContractTests` | contract | 4 | GET / POST / DELETE schema contracts (WireMock) |

---

## Sample test

Full vertical slice — business-readable, schema-validated, Allure-annotated, POJO-deserialised:

```java
@Epic("User Management API")
@Feature("GET /users")
public class GetUserTests extends BaseTest {

    private static final long EXTERNAL_API_SLA_MS = 8000L;
    private UserService userService;

    @BeforeMethod(alwaysRun = true)
    public void initService(Method method) {
        userService = new UserService(client()); // fresh ApiClient per invocation
    }

    @Test(groups = {"smoke", "regression"})
    @Story("Get user by ID — happy path")
    @Severity(SeverityLevel.BLOCKER)
    public void getUserById_returns200WithValidSchema() {
        Response response = userService.getUserById(1);

        UserResponse user = ResponseValidator.of(response)
            .hasStatus(HttpStatus.OK)            // named constant, not magic 200
            .hasContentTypeJson()
            .bodyFieldEquals("id", 1)
            .bodyFieldNotNull("name")
            .bodyMatchesSchema("user-response-schema.json")  // contract check
            .respondsWithin(EXTERNAL_API_SLA_MS)              // named SLA
            .as(UserResponse.class);                          // typed deserialisation

        assertThat(user.getName()).isNotBlank();
        assertThat(user.getEmail()).contains("@");
    }
}
```

---

## Key design decisions

| Decision | Why it matters |
|---|---|
| `ThreadLocal<ApiClient>` | Each parallel thread owns its HTTP client — eliminates race conditions without synchronisation overhead |
| Service layer + `@Step` | Tests read as business specifications; every call is a named Allure step — full audit trail |
| Immutable `RequestBuilder` | New instance per method call — parallel-safe, no shared mutable state |
| `RetryFilter` in `ApiClient` | Transient 5xx handled once at framework level — zero retry boilerplate in test classes |
| `RetryAnalyzer` (FAILURE-only) | Guards `status != FAILURE` — prevents passing tests being retried and NPE on retry path |
| `@BeforeMethod(alwaysRun = true)` | Service objects reinitialised on every invocation including retries — no null service on retry |
| JSON Schema validation | Catches field removals and type changes that status-code assertions miss entirely |
| WireMock contract tests | Contracts run fully offline — deterministic, fast CI, no external network dependency |
| `src/main` for framework code | Framework is independently publishable as a Maven dependency for other test teams |
| `ConfigManager` singleton | 3-tier resolution: system property → env var → `.properties` file |
| `SecretResolver` interpolation | `${QA_AUTH_TOKEN}` resolved from CI env at runtime — secrets never touch source |
| `EXTERNAL_API_SLA_MS` constant | SLA thresholds named and documented — not magic numbers scattered across 30 test methods |
| `RetryListener` not annotation | Retry policy in `testng.xml` once — zero `retryAnalyzer=` on any `@Test` |
| `TestDataFactory` (JavaFaker) | Unique realistic data per run — prevents parallel data collision and stale fixture drift |
| ADR documentation | 10 decisions documented with context and consequences in `docs/architecture.md` |

Full rationale for each decision: [`docs/architecture.md`](docs/architecture.md)

---

## Reports

Two independent reports produced on every CI run.

### Allure — live on GitHub Pages

- Epic / Feature / Story grouping via `@Epic`, `@Feature`, `@Story`
- Full HTTP request + response captured per `@Step` method
- Historical trend graph across every CI run
- 🔗 **[https://Vinoth-SDET.github.io/Enterprise-API-Automation-Suite](https://Vinoth-SDET.github.io/Enterprise-API-Automation-Suite)**

### ExtentReports — self-contained HTML

- Dark-themed dashboard — open the HTML file directly, no server needed
- PASS / FAIL / SKIP with duration, environment, and thread info
- Download from **Actions → Artifacts** after any CI run

---

## CI/CD pipeline

### ci.yml — every PR and push to `main`

```
Push / PR to main
    │
    ├─ Compile + Smoke (qa)               ~46s  ← fast gate
    │       │
    │       ├─ Regression suite (qa)      ~3m
    │       ├─ Contract suite (offline)   ~30s
    │       └─ Negative suite (qa)        ~1m
    │               │
    │               └─ Publish Allure ──► GitHub Pages
```

### nightly.yml — 05:30 AM IST (23:30 UTC) every day

```
9-job matrix: 3 environments × 3 suites
    ├─ dev    × regression
    ├─ dev    × contract
    ├─ dev    × negative
    ├─ qa     × regression
    ├─ qa     × contract
    ├─ qa     × negative
    ├─ staging × regression
    ├─ staging × contract
    └─ staging × negative  ──► Publish combined Allure report
```

`fail-fast: false` — all 9 jobs complete even if one fails.
Manual trigger available via `workflow_dispatch` from the Actions tab.

---

## Environment configuration

```properties
# src/test/resources/config/qa.properties
base.url       = https://jsonplaceholder.typicode.com
auth.token     = ${QA_AUTH_TOKEN}    # resolved from CI secret at runtime
max.retries    = 2
retry.delay.ms = 1000
```

| JVM flag | Description | Default |
|---|---|---|
| `-Denv` | Active environment (`dev` / `qa` / `staging`) | `qa` |
| `-Dtestng.suite` | Path to TestNG suite XML | `regression.xml` |
| `-Dthreads` | Parallel thread count | `5` |

---

## Run in Docker

```bash
# Build multi-stage image
docker build -t api-automation-suite .

# Run tests
docker run -e ENV=qa -e QA_AUTH_TOKEN=$QA_AUTH_TOKEN api-automation-suite

# Makefile shortcut
make docker-test ENV=qa SUITE=regression
```

---

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md) for development setup, branch strategy, commit conventions, and the full PR checklist.

Use GitHub issue templates:
- **[Bug report](.github/ISSUE_TEMPLATE/bug_report.md)** — test failures or framework defects
- **[Test gap](.github/ISSUE_TEMPLATE/test_gap.md)** — propose coverage for an untested scenario

---

## Security

See [SECURITY.md](SECURITY.md) for the vulnerability reporting policy and security practices in this framework.

---

## Changelog

See [CHANGELOG.md](CHANGELOG.md) for a full version history with all fixes and additions.

---

## Author

**Vinoth M** — Staff SDET | Test Automation Architect | 11+ years across BFSI, Healthcare, SaaS

[![GitHub](https://img.shields.io/badge/GitHub-Vinoth--SDET-181717?logo=github&logoColor=white)](https://github.com/Vinoth-SDET)
[![LinkedIn](https://img.shields.io/badge/LinkedIn-vinoth--m--qa-0A66C2?logo=linkedin&logoColor=white)](https://linkedin.com/in/vinoth-m-qa)

---

<div align="center">

*Every design decision in this framework is documented in [`docs/architecture.md`](docs/architecture.md)
and defensible in a technical interview.
Each one maps to a real engineering trade-off encountered in production automation at scale.*

</div>