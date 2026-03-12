package com.vinoth.api.utils;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

/**
 * Fluent assertion library wrapping RestAssured's {@link ValidatableResponse}.
 *
 * <p>Centralising assertions here means a single-line change fixes a status code
 * expectation across the entire suite — no grep-and-replace hunt through test files.
 *
 * <p>Usage:
 * <pre>{@code
 *   ResponseValidator.assertThat(response)
 *       .statusIs(HttpStatus.OK)
 *       .contentTypeIsJson()
 *       .respondsWithin(2000)
 *       .fieldEquals("id", 1)
 *       .fieldNotNull("name")
 *       .matchesSchema("user-response-schema.json");
 * }</pre>
 */
public final class ResponseValidator {

    private final ValidatableResponse vr;

    private ResponseValidator(Response response) {
        this.vr = response.then();
    }

    /**
     * Entry point — wraps the given {@link Response} for assertion chaining.
     */
    public static ResponseValidator assertThat(Response response) {
        return new ResponseValidator(response);
    }

    // ── Status & content-type ─────────────────────────────────────────────────

    public ResponseValidator statusIs(int expected) {
        vr.statusCode(expected);
        return this;
    }

    public ResponseValidator statusIsOneOf(int... expectedCodes) {
        vr.statusCode(Matchers.oneOf(
                java.util.Arrays.stream(expectedCodes).boxed().toArray(Integer[]::new)));
        return this;
    }

    public ResponseValidator contentTypeIsJson() {
        vr.contentType(ContentType.JSON);
        return this;
    }

    public ResponseValidator contentTypeIs(ContentType type) {
        vr.contentType(type);
        return this;
    }

    // ── Response time ─────────────────────────────────────────────────────────

    /**
     * Asserts that the response time is less than {@code maxMs} milliseconds.
     */
    public ResponseValidator respondsWithin(long maxMs) {
        vr.time(Matchers.lessThan(maxMs));
        return this;
    }

    // ── JSON field assertions ─────────────────────────────────────────────────

    public ResponseValidator fieldEquals(String jsonPath, Object expected) {
        vr.body(jsonPath, Matchers.equalTo(expected));
        return this;
    }

    public ResponseValidator fieldNotNull(String jsonPath) {
        vr.body(jsonPath, Matchers.notNullValue());
        return this;
    }

    public ResponseValidator fieldIsNull(String jsonPath) {
        vr.body(jsonPath, Matchers.nullValue());
        return this;
    }

    public ResponseValidator fieldNotEmpty(String jsonPath) {
        vr.body(jsonPath, Matchers.not(Matchers.emptyOrNullString()));
        return this;
    }

    public ResponseValidator listSizeGreaterThan(String jsonPath, int minSize) {
        vr.body(jsonPath, Matchers.hasSize(Matchers.greaterThan(minSize)));
        return this;
    }

    public ResponseValidator listSize(String jsonPath, int expectedSize) {
        vr.body(jsonPath, Matchers.hasSize(expectedSize));
        return this;
    }

    public <T> ResponseValidator fieldMatches(String jsonPath, Matcher<T> matcher) {
        vr.body(jsonPath, matcher);
        return this;
    }

    public ResponseValidator bodyContains(String substring) {
        vr.body(Matchers.containsString(substring));
        return this;
    }

    // ── Header assertions ─────────────────────────────────────────────────────

    public ResponseValidator headerPresent(String headerName) {
        vr.header(headerName, Matchers.notNullValue());
        return this;
    }

    public ResponseValidator headerEquals(String headerName, String expectedValue) {
        vr.header(headerName, expectedValue);
        return this;
    }

    // ── Schema validation ─────────────────────────────────────────────────────

    /**
     * Validates the response body against a JSON Schema file on the classpath.
     * Schema files live in {@code src/test/resources/schemas/}.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public ResponseValidator matchesSchema(String schemaFile) {
        vr.body(SchemaValidator.matchesSchemaFile(schemaFile));
        return this;
    }

    // ── Extraction ────────────────────────────────────────────────────────────

    /**
     * Deserialises the response body into the given class.
     * Call this at the end of an assertion chain when you need the POJO.
     */
    public <T> T as(Class<T> clazz) {
        return vr.extract().response().as(clazz);
    }

    public String extractString(String jsonPath) {
        return vr.extract().path(jsonPath);
    }

    public Integer extractInt(String jsonPath) {
        return vr.extract().path(jsonPath);
    }

    public Response extractResponse() {
        return vr.extract().response();
    }
}