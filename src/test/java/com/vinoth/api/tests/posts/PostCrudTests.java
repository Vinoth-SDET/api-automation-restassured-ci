package com.vinoth.api.tests.posts;

import com.vinoth.api.base.BaseTest;
import com.vinoth.api.constants.HttpStatus;
import com.vinoth.api.dataproviders.PostDataProvider;
import com.vinoth.api.models.request.PostRequest;
import com.vinoth.api.models.response.PostResponse;
import com.vinoth.api.services.PostService;
import com.vinoth.api.utils.ResponseValidator;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("Posts API")
@Feature("CRUD /posts")
public class PostCrudTests extends BaseTest {

    private PostService postService;

    @BeforeMethod
    public void initService() {
        postService = new PostService(api());
    }

    @Test(description = "GET /posts returns 200 with non-empty list")
    @Story("List all posts")
    @Severity(SeverityLevel.BLOCKER)
    public void getAllPosts_returns200() {
        Response response = postService.getAllPosts();

        ResponseValidator.assertThat(response)
                .statusIs(HttpStatus.OK)
                .contentTypeIsJson()
                .respondsWithin(3000)
                .listSizeGreaterThan("$", 0)
                .matchesSchema("post-list-schema.json");
    }

    @Test(description = "GET /posts/{id} returns correct post for id=1")
    @Story("Get post by ID")
    @Severity(SeverityLevel.CRITICAL)
    public void getPostById_returnsExpectedPost() {
        Response response = postService.getPostById(1);

        PostResponse post = ResponseValidator.assertThat(response)
                .statusIs(HttpStatus.OK)
                .fieldEquals("id", 1)
                .fieldNotNull("userId")
                .fieldNotNull("title")
                .fieldNotNull("body")
                .matchesSchema("post-response-schema.json")
                .as(PostResponse.class);

        assertThat(post.getId()).isEqualTo(1);
        assertThat(post.getTitle()).isNotBlank();
    }

    @Test(
            description  = "POST /posts creates a post - data-driven",
            dataProvider = "validPosts",
            dataProviderClass = PostDataProvider.class
    )
    @Story("Create post - data-driven")
    @Severity(SeverityLevel.CRITICAL)
    public void createPost_withValidData_returns201(PostRequest payload) {
        Response response = postService.createPost(payload);

        ResponseValidator.assertThat(response)
                .statusIs(HttpStatus.CREATED)
                .fieldNotNull("id")
                .fieldEquals("title", payload.getTitle())
                .fieldEquals("userId", payload.getUserId());
    }

    @Test(description = "PUT /posts/{id} full update returns 200")
    @Story("Update post")
    @Severity(SeverityLevel.CRITICAL)
    public void updatePost_returns200WithUpdatedFields() {
        PostRequest update = PostRequest.builder()
                .userId(1)
                .title("Updated Title")
                .body("Updated body content")
                .build();

        Response response = postService.updatePost(1, update);

        ResponseValidator.assertThat(response)
                .statusIs(HttpStatus.OK)
                .fieldEquals("id", 1)
                .fieldEquals("title", "Updated Title");
    }

    @Test(description = "DELETE /posts/{id} returns 200")
    @Story("Delete post")
    @Severity(SeverityLevel.NORMAL)
    public void deletePost_returns200() {
        Response response = postService.deletePost(1);

        ResponseValidator.assertThat(response)
                .statusIs(HttpStatus.OK);
    }

    @Test(description = "GET /posts?userId=1 filters posts by user")
    @Story("Filter posts by user")
    @Severity(SeverityLevel.NORMAL)
    public void getPostsByUserId_returnsOnlyUserPosts() {
        Response response = postService.getPostsByParams(java.util.Map.of("userId", 1));

        ResponseValidator.assertThat(response)
                .statusIs(HttpStatus.OK)
                .listSizeGreaterThan("$", 0)
                .fieldEquals("[0].userId", 1);
    }
}