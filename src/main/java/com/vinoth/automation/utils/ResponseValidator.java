package com.vinoth.automation.utils;

import io.restassured.response.Response;
import org.assertj.core.api.Assertions;

/**
 * Fluent assertion chain for RestAssured responses.
 *
 * Usage:
 *   ResponseValidator.of(response)
 *       .hasStatus(200)
 *       .hasContentTypeJson()
 *       .bodyFieldEquals("id", 1)
 *       .bodyFieldEquals("name", "Vinoth")
 *       .bodyFieldNotNull("email")
 *       .bodyMatchesSchema("user-response-schema.json")
 *       .respondsWithin(2000);
 *
 * Terminal method:
 *   .as(MyPojo.class) — deserialise response body to POJO after all assertions pass
 */
public final class ResponseValidator {

    private final Response response;

    private ResponseValidator(Response response) {
        this.response = response;
    }

    // ── Static factory ─────────────────────────────────────────────────────────

    public static ResponseValidator of(Response response) {
        return new ResponseValidator(response);
    }

    // ── Status ─────────────────────────────────────────────────────────────────

    public ResponseValidator hasStatus(int expectedStatus) {
        Assertions.assertThat(response.statusCode())
                .as("Expected HTTP %d but got HTTP %d.\nResponse body: %s",
                        expectedStatus, response.statusCode(), response.asString())
                .isEqualTo(expectedStatus);
        return this;
    }

    // ── Headers ────────────────────────────────────────────────────────────────

    public ResponseValidator hasHeader(String headerName, String expectedValue) {
        Assertions.assertThat(response.header(headerName))
                .as("Header [%s] — expected to contain [%s] but was [%s]",
                        headerName, expectedValue, response.header(headerName))
                .contains(expectedValue);
        return this;
    }

    public ResponseValidator hasContentTypeJson() {
        return hasHeader("Content-Type", "application/json");
    }

    // ── Body / JSON path ───────────────────────────────────────────────────────

    /**
     * Assert that the value at jsonPath equals expected.
     * Accepts any type: int, String, boolean, etc.
     */
    public ResponseValidator bodyFieldEquals(String jsonPath, Object expected) {
        Object actual = response.jsonPath().get(jsonPath);
        Assertions.assertThat(actual)
                .as("JSON path [%s] — expected [%s] but got [%s]\nFull body: %s",
                        jsonPath, expected, actual, response.asString())
                .isEqualTo(expected);
        return this;
    }

    public ResponseValidator bodyFieldNotNull(String jsonPath) {
        Object actual = response.jsonPath().get(jsonPath);
        Assertions.assertThat(actual)
                .as("JSON path [%s] should not be null.\nFull body: %s",
                        jsonPath, response.asString())
                .isNotNull();
        return this;
    }

    public ResponseValidator bodyFieldContains(String jsonPath, String substring) {
        String actual = response.jsonPath().getString(jsonPath);
        Assertions.assertThat(actual)
                .as("JSON path [%s] — expected to contain [%s] but was [%s]",
                        jsonPath, substring, actual)
                .contains(substring);
        return this;
    }

    public ResponseValidator bodyIsNotEmpty() {
        Assertions.assertThat(response.asString())
                .as("Response body should not be empty")
                .isNotBlank();
        return this;
    }

    // ── Schema ─────────────────────────────────────────────────────────────────

    public ResponseValidator bodyMatchesSchema(String schemaFileName) {
        SchemaValidator.validate(response, schemaFileName);
        return this;
    }

    // ── List ───────────────────────────────────────────────────────────────────

    public ResponseValidator listSizeGreaterThan(String jsonPath, int minSize) {
        int actual = response.jsonPath().getList(jsonPath).size();
        Assertions.assertThat(actual)
                .as("List at [%s] — expected size > %d but was %d", jsonPath, minSize, actual)
                .isGreaterThan(minSize);
        return this;
    }

    public ResponseValidator listSizeEquals(String jsonPath, int expectedSize) {
        int actual = response.jsonPath().getList(jsonPath).size();
        Assertions.assertThat(actual)
                .as("List at [%s] — expected size %d but was %d", jsonPath, expectedSize, actual)
                .isEqualTo(expectedSize);
        return this;
    }

    // ── Performance ────────────────────────────────────────────────────────────

    public ResponseValidator responseTimeLessThan(long thresholdMs) {
        Assertions.assertThat(response.time())
                .as("Response time %dms exceeded SLA threshold of %dms",
                        response.time(), thresholdMs)
                .isLessThan(thresholdMs);
        return this;
    }

    /**
     * Alias for responseTimeLessThan — used as .respondsWithin(3000).
     */
    public ResponseValidator respondsWithin(long thresholdMs) {
        return responseTimeLessThan(thresholdMs);
    }

    /**
     * Alias for bodyMatchesSchema — used as .matchesSchema("file.json").
     */
    public ResponseValidator matchesSchema(String schemaFileName) {
        return bodyMatchesSchema(schemaFileName);
    }

    // ── Terminal: deserialise to POJO ──────────────────────────────────────────

    /**
     * Deserialise the response body to the given POJO class.
     * Call this LAST in the chain after all assertions.
     *
     * Usage:
     *   PostResponse post = ResponseValidator.of(response)
     *       .hasStatus(200)
     *       .bodyFieldEquals("id", 1)
     *       .as(PostResponse.class);
     */
    public <T> T as(Class<T> clazz) {
        return response.as(clazz);
    }
}