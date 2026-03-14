package com.vinoth.automation.tests.users;

import com.vinoth.automation.base.BaseTest;
import com.vinoth.automation.constants.HttpStatus;
import com.vinoth.automation.models.response.UserResponse;
import com.vinoth.automation.services.UserService;
import com.vinoth.automation.utils.ResponseValidator;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("User Management API")
@Feature("GET /users")
public class GetUserTests extends BaseTest {

    private static final long EXTERNAL_API_SLA_MS = 8000L;

    private UserService userService;

    @BeforeMethod
    public void initService() {
        userService = new UserService(client());
    }

    @Test(groups = {"smoke", "regression"},
            description = "GET /users returns 200 with non-empty list")
    @Story("List all users")
    @Severity(SeverityLevel.BLOCKER)
    public void getAllUsers_returns200WithNonEmptyList() {
        Response response = userService.getAllUsers();
        ResponseValidator.of(response)
                .hasStatus(HttpStatus.OK)
                .hasContentTypeJson()
                .listSizeGreaterThan("$", 0)
                .respondsWithin(EXTERNAL_API_SLA_MS);
    }

    @Test(groups = {"smoke", "regression"},
            description = "GET /users/1 returns correct user with valid schema")
    @Story("Get user by ID — happy path")
    @Severity(SeverityLevel.BLOCKER)
    public void getUserById_returns200WithValidSchema() {
        Response response = userService.getUserById(1);
        UserResponse user = ResponseValidator.of(response)
                .hasStatus(HttpStatus.OK)
                .hasContentTypeJson()
                .bodyFieldEquals("id", 1)
                .bodyFieldNotNull("name")
                .bodyFieldNotNull("email")
                .bodyFieldNotNull("username")
                .bodyMatchesSchema("user-response-schema.json")
                .respondsWithin(EXTERNAL_API_SLA_MS)
                .as(UserResponse.class);
        assertThat(user.getId()).isEqualTo(1);
        assertThat(user.getName()).isNotBlank();
        assertThat(user.getEmail()).contains("@");
    }

    @Test(groups = {"regression"},
            description = "GET /users/{id} for multiple IDs returns correct data")
    @Story("Get user by ID — multiple users")
    @Severity(SeverityLevel.NORMAL)
    public void getMultipleUsers_eachReturns200() {
        int[] userIds = {1, 2, 3};
        for (int id : userIds) {
            Response response = userService.getUserById(id);
            ResponseValidator.of(response)
                    .hasStatus(HttpStatus.OK)
                    .bodyFieldEquals("id", id)
                    .bodyFieldNotNull("name");
        }
    }

    @Test(groups = {"regression", "negative"},
            description = "GET /users/99999 returns 404 for non-existent user")
    @Story("Get user by ID — not found")
    @Severity(SeverityLevel.CRITICAL)
    public void getUserById_nonExistent_returns404() {
        Response response = userService.getUserById(99999);
        ResponseValidator.of(response)
                .hasStatus(HttpStatus.NOT_FOUND);
    }
}