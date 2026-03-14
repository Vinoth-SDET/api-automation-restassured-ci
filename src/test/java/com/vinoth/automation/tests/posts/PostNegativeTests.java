package com.vinoth.automation.tests.posts;

import com.vinoth.automation.base.BaseTest;
import com.vinoth.automation.constants.HttpStatus;
import com.vinoth.automation.dataproviders.PostDataProvider;
import com.vinoth.automation.services.PostService;
import com.vinoth.automation.utils.ResponseValidator;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("Posts API")
@Feature("Negative — Error Handling")
public class PostNegativeTests extends BaseTest {

    private static final long EXTERNAL_API_SLA_MS = 8000L;
    private PostService postService;

    @BeforeMethod(alwaysRun = true)
    public void initService(Method method) {
        postService = new PostService(client());
    }

    @Test(groups = {"regression", "negative"})
    @Story("Non-existent post")
    @Severity(SeverityLevel.CRITICAL)
    public void getPost_nonExistentId_returns404() {
        Response response = postService.getPostById(99999);
        ResponseValidator.of(response)
                .hasStatus(HttpStatus.NOT_FOUND)
                .respondsWithin(EXTERNAL_API_SLA_MS);
    }

    @Test(groups = {"regression", "negative"},
            dataProvider = "boundaryPostIds",
            dataProviderClass = PostDataProvider.class)
    @Story("Boundary post IDs")
    @Severity(SeverityLevel.NORMAL)
    public void getPost_withBoundaryId_returns4xx(int postId, String scenario) {
        Response response = postService.getPostById(postId);
        int status = response.statusCode();
        assertThat(status)
                .as("Scenario [%s] id=%d — expected 4xx but got %d", scenario, postId, status)
                .isBetween(400, 499);
    }

    @Test(groups = {"regression", "negative"})
    @Story("Delete non-existent post")
    @Severity(SeverityLevel.NORMAL)
    public void deletePost_nonExistentId_respondsWithinSla() {
        Response response = postService.deletePost(99999);
        ResponseValidator.of(response).respondsWithin(EXTERNAL_API_SLA_MS);
    }

    @Test(groups = {"regression", "negative"})
    @Story("Filter posts — invalid userId")
    @Severity(SeverityLevel.NORMAL)
    public void getPostsByUserId_invalidUser_returnsEmptyOrError() {
        Response response = postService.getPostsByUserId(99999);
        int status = response.statusCode();
        assertThat(status).as("Expected 200 or 404 for unknown userId, got %d", status)
                .isIn(200, 404);
    }
}