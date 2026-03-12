package com.vinoth.api.services;

import com.vinoth.api.client.ApiClient;
import com.vinoth.api.constants.Endpoints;
import com.vinoth.api.models.request.PostRequest;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

/**
 * Business-intent service for Post API operations.
 */
public class PostService {

    private static final Logger log = LogManager.getLogger(PostService.class);

    private final ApiClient client;

    public PostService(ApiClient client) {
        this.client = client;
    }

    @Step("GET /posts — retrieve all posts")
    public Response getAllPosts() {
        log.info("Retrieving all posts");
        return client.get(Endpoints.POSTS);
    }

    @Step("GET /posts/{id}")
    public Response getPostById(int id) {
        log.info("Retrieving post | id={}", id);
        // IMPORTANT: must use getWithPathParams — path contains {id} placeholder
        return client.getWithPathParams(Endpoints.POST_BY_ID, Map.of("id", id));
    }

    @Step("GET /posts — filter by query params")
    public Response getPostsByParams(Map<String, ?> queryParams) {
        log.info("Retrieving posts | params={}", queryParams);
        return client.get(Endpoints.POSTS, queryParams);
    }

    @Step("POST /posts — create post: {request.title}")
    public Response createPost(PostRequest request) {
        log.info("Creating post | title={} | userId={}", request.getTitle(), request.getUserId());
        return client.post(Endpoints.POSTS, request);
    }

    @Step("PUT /posts/{id} — full update")
    public Response updatePost(int id, PostRequest request) {
        log.info("Updating post | id={}", id);
        return client.put(
                Endpoints.POST_BY_ID.replace("{id}", String.valueOf(id)), request);
    }

    @Step("PATCH /posts/{id} — partial update")
    public Response patchPost(int id, PostRequest request) {
        log.info("Patching post | id={}", id);
        return client.patch(
                Endpoints.POST_BY_ID.replace("{id}", String.valueOf(id)), request);
    }

    @Step("DELETE /posts/{id}")
    public Response deletePost(int id) {
        log.info("Deleting post | id={}", id);
        return client.delete(
                Endpoints.POST_BY_ID.replace("{id}", String.valueOf(id)));
    }

    @Step("GET /posts/{postId}/comments")
    public Response getCommentsByPost(int postId) {
        log.info("Retrieving comments for post | postId={}", postId);
        return client.getWithPathParams(
                Endpoints.POST_COMMENTS, Map.of("postId", postId));
    }
}