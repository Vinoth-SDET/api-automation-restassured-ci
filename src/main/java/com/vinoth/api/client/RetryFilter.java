package com.vinoth.api.client;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Set;

/**
 * RestAssured {@link Filter} that transparently retries requests on transient server errors.
 *
 * <p>Implements an exponential back-off strategy (500ms × attempt number).
 * Only 5xx status codes that indicate server-side transient failures are retried;
 * 4xx client errors are passed through immediately.
 *
 * <p>Configured once in {@link ApiClient} — zero retry boilerplate in test classes.
 */
public class RetryFilter implements Filter {

    private static final Logger log = LogManager.getLogger(RetryFilter.class);

    /** HTTP status codes that warrant a retry (server-side transient failures only). */
    private static final Set<Integer> RETRYABLE_STATUS_CODES = Set.of(500, 502, 503, 504);

    /** Back-off base in milliseconds; multiplied by the attempt number. */
    private static final long BACKOFF_BASE_MS = 500L;

    private final int maxAttempts;

    public RetryFilter(int maxAttempts) {
        if (maxAttempts < 1) {
            throw new IllegalArgumentException("maxAttempts must be >= 1, got: " + maxAttempts);
        }
        this.maxAttempts = maxAttempts;
    }

    @Override
    public Response filter(FilterableRequestSpecification requestSpec,
                           FilterableResponseSpecification responseSpec,
                           FilterContext context) {

        Response response = null;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            response = context.next(requestSpec, responseSpec);

            if (!RETRYABLE_STATUS_CODES.contains(response.statusCode())) {
                if (attempt > 1) {
                    log.info("Request succeeded on attempt {}/{} | status={} | uri={}",
                            attempt, maxAttempts, response.statusCode(), requestSpec.getURI());
                }
                return response;
            }

            log.warn("Retryable status {} on attempt {}/{} | uri={}",
                    response.statusCode(), attempt, maxAttempts, requestSpec.getURI());

            if (attempt < maxAttempts) {
                long backoffMs = BACKOFF_BASE_MS * attempt;
                log.debug("Back-off {}ms before attempt {}/{}", backoffMs, attempt + 1, maxAttempts);
                sleep(backoffMs);
            }
        }

        log.error("All {} attempts exhausted | final status={} | uri={}",
                maxAttempts, response != null ? response.statusCode() : "N/A", requestSpec.getURI());
        return response;
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Retry sleep interrupted");
        }
    }
}

