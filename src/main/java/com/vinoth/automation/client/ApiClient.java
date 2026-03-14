package com.vinoth.automation.client;

import com.vinoth.automation.config.ConfigManager;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import lombok.extern.log4j.Log4j2;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static io.restassured.RestAssured.given;

/**
 * Thread-safe HTTP facade.
 * Each test thread gets its own ApiClient instance via ThreadLocal in BaseTest.
 * Builds a RequestSpecification with all filters pre-wired:
 *   AuthFilter, RetryFilter, CorrelationFilter, LoggingFilter.
 */
@Log4j2
public class ApiClient {

    private final RequestSpecification baseSpec;

    public ApiClient() {
        ConfigManager cfg = ConfigManager.getInstance();

        ByteArrayOutputStream reqLog  = new ByteArrayOutputStream();
        ByteArrayOutputStream respLog = new ByteArrayOutputStream();

        this.baseSpec = new RequestSpecBuilder()
                .setBaseUri(cfg.getBaseUrl())
                .setContentType("application/json")
                .addHeader("X-Correlation-ID", CorrelationIdProvider.generate())
                .addFilter(new RetryFilter(cfg.getMaxRetries(), cfg.getRetryDelay()))
                .addFilter(new AuthFilter(cfg.getAuthToken()))
                .addFilter(new LoggingFilter())
                .addFilter(RequestLoggingFilter.logRequestTo(new PrintStream(reqLog)))
                .addFilter(ResponseLoggingFilter.logResponseTo(new PrintStream(respLog)))
                .build();

        log.debug("ApiClient initialised | env={} | baseUrl={}", cfg.getEnv(), cfg.getBaseUrl());
    }

    public RequestSpecification spec() {
        return given().spec(baseSpec);
    }
}