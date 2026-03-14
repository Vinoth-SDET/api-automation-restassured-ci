package com.vinoth.automation.tests.posts;

import com.vinoth.automation.base.BaseTest;
import com.vinoth.automation.constants.HttpStatus;
import com.vinoth.automation.dataproviders.PostDataProvider;
import com.vinoth.automation.models.request.PostRequest;
import com.vinoth.automation.models.response.PostResponse;
import com.vinoth.automation.services.PostService;
import com.vinoth.automation.utils.ResponseValidator;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("Posts API")
@Feature("CRUD /posts")
public class PostCrudTests extends BaseTest {

    private static final long EXTERNAL_API_SLA_MS = 8000L;

    private PostService postService;

    @BeforeMethod(alwaysRun = true)
    public void initService(Method method) {
        postService = new PostService(client());
    }

    @Test(groups = {"smoke", "regression"},
            description = "GET /posts returns 200 with non-empty list")
    @Story("List all posts")
    @Severity(SeverityLevel.BLOCKER)
    public void getAllPosts_returns200() {
        Response response = postService.getAllPosts();
        ResponseValidator.of(response)
                .hasStatus(HttpStatus.OK)
                .hasContentTypeJson()
                .respondsWithin(EXTERNAL_API_SLA_MS)
                .listSizeGreaterThan("$", 0)
                .matchesSchema("post-list-schema.json");
    }

    @Test(groups = {"smoke", "regression"},
            description = "GET /posts/{id} returns correct post for id=1")
    @Story("Get post by ID")
    @Severity(SeverityLevel.CRITICAL)
    public void getPostById_returnsExpectedPost() {
        Response response = postService.getPostById(1);
        PostResponse post = ResponseValidator.of(response)
                .hasStatus(HttpStatus.OK)
                .bodyFieldEquals("id", 1)
                .bodyFieldNotNull("userId")
                .bodyFieldNotNull("title")
                .bodyFieldNotNull("body")
                .matchesSchema("post-response-schema.json")
                .as(PostResponse.class);
        assertThat(post.getId()).isEqualTo(1);
        assertThat(post.getTitle()).isNotBlank();
    }

    @Test(groups = {"regression"},
            description = "POST /posts creates a post — data-driven",
            dataProvider = "validPosts",
            dataProviderClass = PostDataProvider.class)
    @Story("Create post — data-driven")
    @Severity(SeverityLevel.CRITICAL)
    public void createPost_withValidData_returns201(PostRequest payload) {
        Response response = postService.createPost(payload);
        ResponseValidator.of(response)
                .hasStatus(HttpStatus.CREATED)
                .bodyFieldNotNull("id")
                .bodyFieldEquals("title",  payload.getTitle())
                .bodyFieldEquals("userId", payload.getUserId());
    }

    @Test(groups = {"regression"},
            description = "PUT /posts/{id} full update returns 200")
    @Story("Update post")
    @Severity(SeverityLevel.CRITICAL)
    public void updatePost_returns200WithUpdatedFields() {
        PostRequest update = PostRequest.builder()
                .userId(1)
                .title("Updated Title")
                .body("Updated body content")
                .build();
        Response response = postService.updatePost(1, update);
        ResponseValidator.of(response)
                .hasStatus(HttpStatus.OK)
                .bodyFieldEquals("id",    1)
                .bodyFieldEquals("title", "Updated Title");
    }

    @Test(groups = {"regression"},
            description = "DELETE /posts/{id} returns 200")
    @Story("Delete post")
    @Severity(SeverityLevel.NORMAL)
    public void deletePost_returns200() {
        Response response = postService.deletePost(1);
        ResponseValidator.of(response)
                .hasStatus(HttpStatus.OK);
    }

    @Test(groups = {"regression"},
            description = "GET /posts?userId=1 filters posts by user")
    @Story("Filter posts by user")
    @Severity(SeverityLevel.NORMAL)
    public void getPostsByUserId_returnsOnlyUserPosts() {
        Response response = postService.getPostsByUserId(1);
        ResponseValidator.of(response)
                .hasStatus(HttpStatus.OK)
                .listSizeGreaterThan("$", 0)
                .bodyFieldEquals("[0].userId", 1);
    }
}