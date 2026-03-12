package com.vinoth.api.tests.users;

import com.vinoth.api.base.BaseTest;
import com.vinoth.api.constants.HttpStatus;
import com.vinoth.api.services.UserService;
import com.vinoth.api.utils.ResponseValidator;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Epic("User Management API")
@Feature("DELETE /users")
public class DeleteUserTests extends BaseTest {

    private UserService userService;

    @BeforeMethod
    public void initService() {
        userService = new UserService(api());
    }

    @Test(description = "DELETE /users/{id} with valid id returns 200")
    @Story("Delete user — happy path")
    @Severity(SeverityLevel.CRITICAL)
    public void deleteUser_withValidId_returns200() {
        Response response = userService.deleteUser(1);

        ResponseValidator.assertThat(response)
                .statusIs(HttpStatus.OK);
    }

    @Test(description = "DELETE /users/{id} response body is empty or {}")
    @Story("Delete user — response body")
    @Severity(SeverityLevel.NORMAL)
    public void deleteUser_responseBodyIsEmpty() {
        Response response = userService.deleteUser(1);

        // JSONPlaceholder returns {} for successful deletes
        ResponseValidator.assertThat(response)
                .statusIs(HttpStatus.OK)
                .contentTypeIsJson();
    }
}