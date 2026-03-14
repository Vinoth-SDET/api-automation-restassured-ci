package com.vinoth.automation.services;

import com.vinoth.automation.client.ApiClient;
import com.vinoth.automation.client.RequestBuilder;
import com.vinoth.automation.constants.Endpoints;
import com.vinoth.automation.models.request.PostRequest;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;

import java.util.Map;

/**
 * Business-intent service for the /posts API.
 *
 * All methods use RequestBuilder — zero raw RestAssured calls in this class.
 * Every method is @Step annotated so it appears as a named step in Allure reports.
 *
 * Tests call: postService.getAllPosts()  — not given().when().get("/posts")
 */
@RequiredArgsConstructor
public class PostService {

    private final ApiClient client;

    @Step("Fetch all posts")
    public Response getAllPosts() {
        return RequestBuilder.from(client)
                .withPath(Endpoints.POSTS)
                .get();
    }

    @Step("Fetch post with id={postId}")
    public Response getPostById(int postId) {
        return RequestBuilder.from(client)
                .withPath(Endpoints.POST_BY_ID)
                .withPathParam("id", postId)
                .get();
    }

    @Step("Fetch all posts for userId={userId}")
    public Response getPostsByUserId(int userId) {
        return RequestBuilder.from(client)
                .withPath(Endpoints.POSTS)
                .withQueryParam("userId", userId)
                .get();
    }

    /**
     * Legacy bridge — keeps PostCrudTests compiling without changes.
     * Extracts query params from map and applies them as query parameters.
     *
     * @deprecated Use getPostsByUserId(int) in new code.
     */
    @Deprecated(since = "2.0", forRemoval = true)
    @Step("Fetch posts by params")
    public Response getPostsByParams(Map<String, Integer> params) {
        RequestBuilder builder = RequestBuilder.from(client)
                .withPath(Endpoints.POSTS);
        for (Map.Entry<String, Integer> entry : params.entrySet()) {
            builder = builder.withQueryParam(entry.getKey(), entry.getValue());
        }
        return builder.get();
    }

    @Step("Create post: title={request.title}")
    public Response createPost(PostRequest request) {
        return RequestBuilder.from(client)
                .withPath(Endpoints.POSTS)
                .withBody(request)
                .post();
    }

    @Step("Update post with id={postId}")
    public Response updatePost(int postId, PostRequest request) {
        return RequestBuilder.from(client)
                .withPath(Endpoints.POST_BY_ID)
                .withPathParam("id", postId)
                .withBody(request)
                .put();
    }

    @Step("Partially update post with id={postId}")
    public Response patchPost(int postId, PostRequest request) {
        return RequestBuilder.from(client)
                .withPath(Endpoints.POST_BY_ID)
                .withPathParam("id", postId)
                .withBody(request)
                .patch();
    }

    @Step("Delete post with id={postId}")
    public Response deletePost(int postId) {
        return RequestBuilder.from(client)
                .withPath(Endpoints.POST_BY_ID)
                .withPathParam("id", postId)
                .delete();
    }

    @Step("Fetch comments for postId={postId}")
    public Response getCommentsByPostId(int postId) {
        return RequestBuilder.from(client)
                .withPath(Endpoints.POSTS_COMMENTS)
                .withPathParam("id", postId)
                .get();
    }
}