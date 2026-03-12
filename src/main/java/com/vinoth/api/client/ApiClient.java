package com.vinoth.api.client;

import com.vinoth.api.config.ConfigManager;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;

/**
 * Core HTTP facade — centralises auth, headers, retry, logging and timeout.
 * Tests never contain RestAssured DSL; they call api().get(Endpoints.USERS).
 */
public class ApiClient {

    private static final Logger log = LogManager.getLogger(ApiClient.class);

    private final RequestSpecification baseSpec;
    private final String correlationId;

    public ApiClient() {
        ConfigManager cfg = ConfigManager.get();
        this.correlationId = UUID.randomUUID().toString();

        this.baseSpec = new RequestSpecBuilder()
                .setBaseUri(cfg.baseUrl())
                .setContentType(ContentType.JSON)
                .addHeader("Accept", "application/json")
                .addHeader("X-Correlation-Id", correlationId)
                .setConfig(RestAssuredConfig.config()
                        .httpClient(HttpClientConfig.httpClientConfig()
                                .setParam("http.connection.timeout", cfg.timeout())
                                .setParam("http.socket.timeout",     cfg.timeout())))
                .addFilter(new AllureRestAssured())
                .addFilter(new RetryFilter(cfg.retries()))
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();

        log.debug("ApiClient initialised | correlationId={} | baseUrl={}",
                correlationId, cfg.baseUrl());
    }

    // ── GET ───────────────────────────────────────────────────────────────────

    /** GET with no parameters. */
    public Response get(String path) {
        log.debug("GET {} | correlationId={}", path, correlationId);
        return given(baseSpec)
                .when().get(path);
    }

    /**
     * GET with query parameters — e.g. /posts?userId=1
     * Use this when the path has NO {placeholders}.
     */
    public Response get(String path, Map<String, ?> queryParams) {
        log.debug("GET {} | queryParams={} | correlationId={}", path, queryParams, correlationId);
        return given(baseSpec)
                .queryParams(queryParams)
                .when().get(path);
    }

    /**
     * GET with path parameters — e.g. /users/{id} with Map.of("id", 1)
     * Use this when the path CONTAINS {placeholders}.
     */
    public Response getWithPathParams(String path, Map<String, ?> pathParams) {
        log.debug("GET {} | pathParams={} | correlationId={}", path, pathParams, correlationId);
        return given(baseSpec)
                .pathParams(pathParams)
                .when().get(path);
    }

    // ── POST ──────────────────────────────────────────────────────────────────

    public <T> Response post(String path, T body) {
        log.debug("POST {} | correlationId={}", path, correlationId);
        return given(baseSpec)
                .body(body)
                .when().post(path);
    }

    // ── PUT ───────────────────────────────────────────────────────────────────

    public <T> Response put(String path, T body) {
        log.debug("PUT {} | correlationId={}", path, correlationId);
        return given(baseSpec)
                .body(body)
                .when().put(path);
    }

    // ── PATCH ─────────────────────────────────────────────────────────────────

    public <T> Response patch(String path, T body) {
        log.debug("PATCH {} | correlationId={}", path, correlationId);
        return given(baseSpec)
                .body(body)
                .when().patch(path);
    }

    // ── DELETE ────────────────────────────────────────────────────────────────

    public Response delete(String path) {
        log.debug("DELETE {} | correlationId={}", path, correlationId);
        return given(baseSpec)
                .when().delete(path);
    }

    // ── Accessors ─────────────────────────────────────────────────────────────

    public String getCorrelationId() {
        return correlationId;
    }

    public RequestSpecification getBaseSpec() {
        return baseSpec;
    }
}