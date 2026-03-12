package com.vinoth.api.services;
 
import com.vinoth.api.client.ApiClient;
import com.vinoth.api.constants.Endpoints;
import com.vinoth.api.models.request.PostRequest;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
 
import java.util.Map;
 
public class PostService {
 
    private static final Logger log = LogManager.getLogger(PostService.class);
 
    private final ApiClient client;
 
    public PostService(ApiClient client) {
        this.client = client;
    }
 
    public Response getAllPosts() {
        log.info("Retrieving all posts");
        return client.get(Endpoints.POSTS);
    }
 
    public Response getPostById(int id) {
        log.info("Retrieving post | id={}", id);
        return client.getWithPathParams(Endpoints.POST_BY_ID, Map.of("id", id));
    }
 
    public Response getPostsByParams(Map<String, ?> queryParams) {
        log.info("Retrieving posts | params={}", queryParams);
        return client.get(Endpoints.POSTS, queryParams);
    }
 
    public Response createPost(PostRequest request) {
        log.info("Creating post | title={} | userId={}", request.getTitle(), request.getUserId());
        return client.post(Endpoints.POSTS, request);
    }
 
    public Response updatePost(int id, PostRequest request) {
        log.info("Updating post | id={}", id);
        return client.put(
                Endpoints.POST_BY_ID.replace("{id}", String.valueOf(id)), request);
    }
 
    public Response patchPost(int id, PostRequest request) {
        log.info("Patching post | id={}", id);
        return client.patch(
                Endpoints.POST_BY_ID.replace("{id}", String.valueOf(id)), request);
    }
 
    public Response deletePost(int id) {
        log.info("Deleting post | id={}", id);
        return client.delete(
                Endpoints.POST_BY_ID.replace("{id}", String.valueOf(id)));
    }
 
    public Response getCommentsByPost(int postId) {
        log.info("Retrieving comments for post | postId={}", postId);
        return client.getWithPathParams(
                Endpoints.POST_COMMENTS, Map.of("postId", postId));
    }
}
