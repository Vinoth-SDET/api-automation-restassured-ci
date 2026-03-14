package com.vinoth.automation.client;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import lombok.extern.log4j.Log4j2;

/**
 * Structured request/response logging filter.
 * Logs method, URI, status, and response time on every call.
 * Attached once in ApiClient — zero logging boilerplate in tests.
 */
@Log4j2
public class LoggingFilter implements Filter {

    @Override
    public Response filter(FilterableRequestSpecification req,
                           FilterableResponseSpecification resp,
                           FilterContext ctx) {
        long start = System.currentTimeMillis();
        Response response = ctx.next(req, resp);
        long elapsed = System.currentTimeMillis() - start;

        log.info("[{}] {} {} → HTTP {} ({}ms)",
                Thread.currentThread().getId(),
                req.getMethod(),
                req.getURI(),
                response.statusCode(),
                elapsed);

        if (response.statusCode() >= 400) {
            log.warn("Non-2xx response body: {}", response.asString());
        }
        return response;
    }
}