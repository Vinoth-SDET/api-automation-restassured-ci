package com.vinoth.api.services;

import com.vinoth.api.client.ApiClient;
import com.vinoth.api.constants.Endpoints;
import com.vinoth.api.models.request.UserRequest;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

/**
 * Business-intent service for User API operations.
 * Each method is annotated with @Step so Allure reports show human-readable step names.
 */
public class UserService {

    private static final Logger log = LogManager.getLogger(UserService.class);

    private final ApiClient client;

    public UserService(ApiClient client) {
        this.client = client;
    }

    @Step("GET /users — retrieve all users")
    public Response getAllUsers() {
        log.info("Retrieving all users");
        return client.get(Endpoints.USERS);
    }

    @Step("GET /users/{id}")
    public Response getUserById(int id) {
        log.info("Retrieving user | id={}", id);
        // IMPORTANT: must use getWithPathParams — path contains {id} placeholder
        return client.getWithPathParams(Endpoints.USER_BY_ID, Map.of("id", id));
    }

    @Step("GET /users — filter by query params")
    public Response getUsersByParams(Map<String, ?> queryParams) {
        log.info("Retrieving users | params={}", queryParams);
        return client.get(Endpoints.USERS, queryParams);
    }

    @Step("GET /users/{userId}/posts")
    public Response getPostsByUser(int userId) {
        log.info("Retrieving posts for user | userId={}", userId);
        return client.getWithPathParams(Endpoints.USER_POSTS, Map.of("userId", userId));
    }

    @Step("POST /users — create user: {request.name}")
    public Response createUser(UserRequest request) {
        log.info("Creating user | name={} | email={}", request.getName(), request.getEmail());
        return client.post(Endpoints.USERS, request);
    }

    @Step("PUT /users/{id} — full update")
    public Response updateUser(int id, UserRequest request) {
        log.info("Updating user | id={}", id);
        return client.put(
                Endpoints.USER_BY_ID.replace("{id}", String.valueOf(id)), request);
    }

    @Step("PATCH /users/{id} — partial update")
    public Response patchUser(int id, UserRequest request) {
        log.info("Patching user | id={}", id);
        return client.patch(
                Endpoints.USER_BY_ID.replace("{id}", String.valueOf(id)), request);
    }

    @Step("DELETE /users/{id}")
    public Response deleteUser(int id) {
        log.info("Deleting user | id={}", id);
        return client.delete(
                Endpoints.USER_BY_ID.replace("{id}", String.valueOf(id)));
    }
}