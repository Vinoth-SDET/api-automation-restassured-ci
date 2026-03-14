package com.vinoth.automation.client;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * RestAssured filter that retries on 5xx responses with exponential back-off.
 *
 * Attached once at ApiClient construction — zero retry boilerplate in any test class.
 * Retry count and initial delay are driven by ConfigManager (env-specific config).
 *
 * Back-off formula: delay = initialDelayMs * 2^attempt
 *   Attempt 0 → 1000ms
 *   Attempt 1 → 2000ms
 *   Attempt 2 → 4000ms
 *
 * Constructor accepts (int maxRetries, long initialDelayMs) to match ApiClient.
 */
@Log4j2
@RequiredArgsConstructor
public class RetryFilter implements Filter {

    private final int  maxRetries;
    private final long initialDelayMs;

    /**
     * Convenience constructor for callers that only supply retry count.
     * Uses a default initial delay of 1000ms.
     */
    public RetryFilter(int maxRetries) {
        this(maxRetries, 1000L);
    }

    @Override
    public Response filter(FilterableRequestSpecification requestSpec,
                           FilterableResponseSpecification responseSpec,
                           FilterContext ctx) {

        Response response = ctx.next(requestSpec, responseSpec);
        int attempts = 0;

        while (response.statusCode() >= 500 && attempts < maxRetries) {
            long delay = initialDelayMs * (long) Math.pow(2, attempts);
            log.warn("5xx received [{}] on attempt {}/{}. Retrying after {}ms",
                    response.statusCode(), attempts + 1, maxRetries, delay);
            sleep(delay);
            response = ctx.next(requestSpec, responseSpec);
            attempts++;
        }

        if (attempts > 0 && response.statusCode() < 500) {
            log.info("Request recovered after {} retry attempt(s)", attempts);
        }

        return response;
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("RetryFilter sleep interrupted");
        }
    }
}