package com.vinoth.automation.tests.users;

import com.vinoth.automation.base.BaseTest;
import com.vinoth.automation.constants.HttpStatus;
import com.vinoth.automation.services.UserService;
import com.vinoth.automation.utils.ResponseValidator;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Method;

@Epic("User Management API")
@Feature("DELETE /users")
public class DeleteUserTests extends BaseTest {

    private UserService userService;

    @BeforeMethod(alwaysRun = true)
    public void initService(Method method) {
        userService = new UserService(client());
    }

    @Test(groups = {"smoke", "regression"},
            description = "DELETE /users/{id} with valid id returns 200")
    @Story("Delete user — happy path")
    @Severity(SeverityLevel.CRITICAL)
    public void deleteUser_withValidId_returns200() {
        Response response = userService.deleteUser(1);
        ResponseValidator.of(response)
                .hasStatus(HttpStatus.OK);
    }

    @Test(groups = {"regression"},
            description = "DELETE /users/{id} response has json content type")
    @Story("Delete user — response body")
    @Severity(SeverityLevel.NORMAL)
    public void deleteUser_responseHasJsonContentType() {
        Response response = userService.deleteUser(1);
        ResponseValidator.of(response)
                .hasStatus(HttpStatus.OK)
                .hasContentTypeJson();
    }
}