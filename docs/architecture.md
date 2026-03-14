# Architecture Decision Records

This document captures the key architectural decisions made in this framework and the reasoning behind each one. These are the questions you should expect in a technical interview — every answer is here.

---

## ADR-001: Ports & Adapters (Hexagonal) Architecture

**Decision:** Tests never call RestAssured directly. All HTTP is encapsulated behind a Service → Client boundary.

**Context:** In naive frameworks, test classes are tightly coupled to HTTP verbs (`given().when().get()`). When the HTTP library changes or a common pattern (auth, retry) needs updating, every test class requires modification.

**Consequences:** Tests read as business specifications. The HTTP library is an implementation detail. The entire client stack can be swapped without touching a single test.

---

## ADR-002: ThreadLocal ApiClient

**Decision:** `ApiClient` instances are stored in a `ThreadLocal` — one per test thread.

**Context:** TestNG parallel execution runs multiple test methods simultaneously across threads. A shared `ApiClient` with mutable request state would cause race conditions. Synchronising access would serialize execution, defeating the purpose of parallelism.

**Consequences:** Zero race conditions. No synchronisation overhead. Each thread has an isolated HTTP client with its own correlation ID, auth token, and log buffer.

---

## ADR-003: Immutable RequestBuilder

**Decision:** Every `RequestBuilder` method returns a new instance rather than mutating `this`.

**Context:** A mutable builder shared across parallel calls accumulates state from multiple test threads.

**Consequences:** Builders are safe to pass around. No defensive copying needed at call sites.

---

## ADR-004: JSON Schema Validation as First-Class Assertion

**Decision:** Every response type has a corresponding JSON Schema draft-07 file. Schema validation is called via `ResponseValidator.bodyMatchesSchema()`.

**Context:** Status code assertions tell you the API responded. Schema assertions tell you the API responded *correctly* — with the right field names, types, and required fields. API drift (a field renamed or removed) is caught by schema validation, not status codes.

**Consequences:** The framework detects breaking API changes automatically. Contract tests can run against WireMock stubs, making them independent of external availability.

---

## ADR-005: Dual Reporting Strategy

**Decision:** Run Allure and ExtentReports simultaneously on every test execution.

**Context:** Allure is powerful but requires a server or GitHub Pages. ExtentReports produces a self-contained HTML file that any stakeholder can open directly. Different audiences need different views.

**Consequences:** Deep engineering debugging via Allure (step trace, HTTP payloads, history trend). Instant stakeholder communication via ExtentReports (open the file, see pass/fail).

---

## ADR-006: Framework Code in src/main

**Decision:** The client, config, services, and utils packages live in `src/main`, not `src/test`.

**Context:** `src/test` classes are not exported in a Maven build. If the framework were to be shared across test suites for different microservices, placing it in `src/main` allows publishing it as a Maven dependency.

**Consequences:** The framework is independently deployable. Other teams can depend on `com.vinoth.automation:api-automation-core:1.0.0` and write only test classes.

---

## ADR-007: WireMock for Contract Tests

**Decision:** Contract tests run against a local WireMock server, not the live API.

**Context:** Contract tests must be deterministic and fast. Running against a live external API introduces network flakiness and external dependency. WireMock stubs define the expected contract — if the live API drifts from the stub, the test catches it at schema validation time.

**Consequences:** Contract tests run in CI without any external network call. They are the fastest and most reliable tests in the suite.

---

## ADR-008: RetryListener over @Test(retryAnalyzer=)

**Decision:** Retry logic is attached via a TestNG `IRetryAnalyzer` registered as a listener in `testng.xml`, not as an annotation on each test.

**Context:** `@Test(retryAnalyzer = RetryAnalyzer.class)` requires the annotation on every test method. A TestNG listener applies retry framework-wide from a single configuration point.

**Consequences:** Zero retry boilerplate across all test methods. Retry policy changes in one place.