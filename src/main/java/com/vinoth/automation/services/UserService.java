package com.vinoth.automation.services;

import com.vinoth.automation.client.ApiClient;
import com.vinoth.automation.client.RequestBuilder;
import com.vinoth.automation.constants.Endpoints;
import com.vinoth.automation.models.request.UserRequest;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;

/**
 * Business-intent service for the /users API.
 * Tests call getUserById(1) — never given().when().get("/users/1").
 * Every method is @Step annotated for Allure drill-down visibility.
 */
@RequiredArgsConstructor
public class UserService {

    private final ApiClient client;

    @Step("Fetch all users")
    public Response getAllUsers() {
        return RequestBuilder.from(client)
                .withPath(Endpoints.USERS)
                .get();
    }

    @Step("Fetch user with id={userId}")
    public Response getUserById(int userId) {
        return RequestBuilder.from(client)
                .withPath(Endpoints.USER_BY_ID)
                .withPathParam("id", userId)
                .get();
    }

    @Step("Create user: name={request.name}")
    public Response createUser(UserRequest request) {
        return RequestBuilder.from(client)
                .withPath(Endpoints.USERS)
                .withBody(request)
                .post();
    }

    @Step("Update user with id={userId}")
    public Response updateUser(int userId, UserRequest request) {
        return RequestBuilder.from(client)
                .withPath(Endpoints.USER_BY_ID)
                .withPathParam("id", userId)
                .withBody(request)
                .put();
    }

    @Step("Delete user with id={userId}")
    public Response deleteUser(int userId) {
        return RequestBuilder.from(client)
                .withPath(Endpoints.USER_BY_ID)
                .withPathParam("id", userId)
                .delete();
    }
}