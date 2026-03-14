package com.vinoth.automation.services;

import com.vinoth.automation.client.ApiClient;
import com.vinoth.automation.client.RequestBuilder;
import com.vinoth.automation.constants.Endpoints;
import com.vinoth.automation.models.request.AuthRequest;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;

/**
 * Business-intent service for auth endpoints.
 * Demonstrates the pattern for token-based API authentication flows.
 * JSONPlaceholder does not have a real auth endpoint —
 * this class documents the pattern for real microservice testing.
 */
@RequiredArgsConstructor
public class AuthService {

    private final ApiClient client;

    @Step("Authenticate: username={request.username}")
    public Response login(AuthRequest request) {
        return RequestBuilder.from(client)
                .withPath(Endpoints.AUTH_LOGIN)
                .withBody(request)
                .post();
    }

    @Step("Logout current session")
    public Response logout() {
        return RequestBuilder.from(client)
                .withPath(Endpoints.AUTH_LOGOUT)
                .post();
    }
}