# Contributing to Enterprise API Automation Suite

Thank you for taking the time to contribute. This guide covers everything needed to make a quality contribution.

---

## Table of contents

- [Development setup](#development-setup)
- [Branch strategy](#branch-strategy)
- [Commit conventions](#commit-conventions)
- [Adding a new API test](#adding-a-new-api-test)
- [Adding a new service](#adding-a-new-service)
- [PR checklist](#pr-checklist)
- [Code standards](#code-standards)

---

## Development setup

**Prerequisites:** Java 21, Maven 3.8+, IntelliJ IDEA (recommended)

```bash
git clone https://github.com/Vinoth-SDET/Enterprise-API-Automation-Suite
cd Enterprise-API-Automation-Suite
mvn compile
mvn test -Dtestng.suite=src/test/resources/testng-suites/smoke.xml -Denv=qa
```

Verify the smoke suite passes before making any changes.

---

## Branch strategy

| Branch pattern | Purpose |
|---|---|
| `main` | Stable — protected, CI gate enforced |
| `feature/EAS-NNN-short-description` | New test coverage or framework features |
| `fix/EAS-NNN-short-description` | Bug fixes |
| `chore/...` | Dependency updates, config changes, docs |
| `refactor/...` | Code improvements with no behaviour change |

---

## Commit conventions

Follow [Conventional Commits](https://www.conventionalcommits.org/):

```
type(scope): short imperative description (max 72 chars)

Optional body explaining why, not what.
```

**Types:** `feat` `fix` `test` `refactor` `chore` `docs` `ci` `perf`

**Examples:**
```
feat(users): add negative tests for 404 and 422 responses
fix(retry): guard retryAnalyzer against retrying passing tests
test(contract): add WireMock stubs for POST /users
chore(deps): bump restassured to 5.4.0
docs(adr): add ADR-010 for @BeforeMethod alwaysRun pattern
ci: remove main-only gate on regression job
```

---

## Adding a new API test

1. Add endpoint constants to `constants/Endpoints.java`
2. Add request/response POJOs to `models/request/` and `models/response/`
3. Add JSON schema to `src/test/resources/schemas/`
4. Add or update the service class in `services/` with `@Step` annotations
5. Create test class in `tests/` — cover positive, negative, and contract cases
6. Register tests in the relevant `testng-suites/*.xml` files
7. Add test data to `src/test/resources/testdata/` if data-driven

---

## Adding a new service

For a new microservice (e.g., `OrderService`):

```
src/main/java/com/vinoth/automation/
├── constants/
│   └── Endpoints.java          ← add ORDER_* constants
├── models/
│   ├── request/OrderRequest.java
│   └── response/OrderResponse.java
└── services/
    └── OrderService.java        ← @Step annotated methods

src/test/java/com/vinoth/automation/
├── tests/orders/
│   ├── GetOrderTests.java
│   ├── CreateOrderTests.java
│   └── OrderNegativeTests.java
└── dataproviders/
    └── OrderDataProvider.java

src/test/resources/
├── schemas/order-response-schema.json
├── testdata/orders/valid-orders.json
└── testng-suites/           ← add order tests to regression.xml
```

---

## PR checklist

Before raising a PR, verify all of these:

- [ ] Tests are in the correct package matching the API domain
- [ ] All `@Test` methods annotated with `@Epic`, `@Feature`, `@Story`, `@Severity`
- [ ] `@BeforeMethod(alwaysRun = true)` used — not `@BeforeClass` for service init
- [ ] `ResponseValidator.of()` used — no raw `assertEquals` or `assertThat` on Response
- [ ] No hardcoded URLs, credentials, or environment-specific values in source
- [ ] New POJOs use `@Builder`, `@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`
- [ ] JSON schema file added for any new response type
- [ ] Test method names follow `action_condition_expectedOutcome` pattern
- [ ] New tests added to relevant `testng-suites/*.xml`
- [ ] `target/`, `.idea/`, `*.iml` are NOT in the commit (`git status` check)
- [ ] CI passes on the feature branch before requesting review
- [ ] CHANGELOG.md updated under `[Unreleased]`

---

## Code standards

**Test method naming:**
```java
// Pattern: action_condition_expectedOutcome
getUserById_withValidId_returns200AndValidSchema()
createUser_withMissingEmail_returns422()
deleteUser_asUnauthorisedCaller_returns401()
```

**Service method structure:**
```java
@Step("Fetch user with id={userId}")
public Response getUserById(int userId) {
    return RequestBuilder.from(client)
            .withPath(Endpoints.USER_BY_ID)
            .withPathParam("id", userId)
            .get();
}
```

**Assertion chain:**
```java
ResponseValidator.of(response)
    .hasStatus(HttpStatus.OK)
    .hasContentTypeJson()
    .bodyFieldEquals("id", 1)
    .bodyMatchesSchema("user-response-schema.json")
    .respondsWithin(EXTERNAL_API_SLA_MS);
```

**SLA constants — always named, never magic numbers:**
```java
private static final long EXTERNAL_API_SLA_MS = 8000L;  // free public API
private static final long INTERNAL_API_SLA_MS = 2000L;  // internal microservice
```