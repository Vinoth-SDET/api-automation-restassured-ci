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
import static org.apache.http.params.CoreConnectionPNames.CONNECTION_TIMEOUT;
import static org.apache.http.params.CoreConnectionPNames.SO_TIMEOUT;

/**
 * Central HTTP facade.
 *
 * <p>Wraps RestAssured so tests never contain raw DSL. All auth headers,
 * correlation IDs, timeout config, retry logic, and reporting filters
 * live here — zero duplication across the test layer.
 *
 * <p>Reporting filters attached:
 * <ul>
 *   <li>{@link AllureRestAssured} — attaches full request/response to Allure report</li>
 *   <li>{@link RequestLoggingFilter} / {@link ResponseLoggingFilter} — writes to Log4j2</li>
 *   <li>{@link RetryFilter} — auto-retries on 5xx responses</li>
 * </ul>
 */
public class ApiClient {

    private static final Logger log = LogManager.getLogger(ApiClient.class);
    private final RequestSpecification baseSpec;

    public ApiClient() {
        ConfigManager cfg = ConfigManager.get();

        this.baseSpec = new RequestSpecBuilder()
                .setBaseUri(cfg.baseUrl())
                .setContentType(ContentType.JSON)
                .addHeader("Accept", "application/json")
                .addHeader("X-Correlation-Id", UUID.randomUUID().toString())
                .addHeader("Authorization", "Bearer " + cfg.authToken())
                .setConfig(RestAssuredConfig.config()
                        .httpClient(HttpClientConfig.httpClientConfig()
                                .setParam(CONNECTION_TIMEOUT, cfg.timeout())
                                .setParam(SO_TIMEOUT, cfg.timeout())))
                // Allure filter: attaches HTTP traffic to Allure report steps
                .addFilter(new AllureRestAssured())
                // Retry filter: handles transient 5xx without leaking into tests
                .addFilter(new RetryFilter(cfg.retries()))
                // Log filters: writes request/response to Log4j2 rolling file
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();

        log.debug("ApiClient initialised | baseUri={}", cfg.baseUrl());
    }

    // -----------------------------------------------------------------------
    // HTTP verb methods
    // -----------------------------------------------------------------------

    public Response get(String path) {
        return given(baseSpec).when().get(path);
    }

    public Response get(String path, Map<String, ?> queryParams) {
        return given(baseSpec).queryParams(queryParams).when().get(path);
    }

    public Response getWithPathParams(String path, Map<String, ?> pathParams) {
        return given(baseSpec).pathParams(pathParams).when().get(path);
    }

    public <T> Response post(String path, T body) {
        return given(baseSpec).body(body).when().post(path);
    }

    public <T> Response put(String path, T body) {
        return given(baseSpec).body(body).when().put(path);
    }

    public <T> Response patch(String path, T body) {
        return given(baseSpec).body(body).when().patch(path);
    }

    public Response delete(String path) {
        return given(baseSpec).when().delete(path);
    }
}