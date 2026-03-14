package com.vinoth.automation.client;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * RestAssured filter that injects the Bearer auth token into every request.
 * Token is resolved from ConfigManager (which reads from CI secrets / env vars).
 * Zero hardcoded credentials — the token value never appears in source code.
 *
 * If auth.token resolves to blank/null (e.g. public API), the header is skipped.
 */
@Log4j2
@RequiredArgsConstructor
public class AuthFilter implements Filter {

    private final String authToken;

    @Override
    public Response filter(FilterableRequestSpecification requestSpec,
                           FilterableResponseSpecification responseSpec,
                           FilterContext ctx) {
        if (authToken != null && !authToken.isBlank() && !authToken.startsWith("UNSET_")) {
            requestSpec.header("Authorization", "Bearer " + authToken);
            log.debug("AuthFilter: injected Bearer token");
        } else {
            log.debug("AuthFilter: no token configured — skipping Authorization header");
        }
        return ctx.next(requestSpec, responseSpec);
    }
}