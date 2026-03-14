package com.vinoth.automation.tests.users;

import com.vinoth.automation.base.BaseTest;
import com.vinoth.automation.constants.HttpStatus;
import com.vinoth.automation.services.UserService;
import com.vinoth.automation.utils.ResponseValidator;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Epic("User Management API")
@Feature("DELETE /users")
public class DeleteUserTests extends BaseTest {

    private UserService userService;

    @BeforeMethod
    public void initService() {
        userService = new UserService(client());
    }

    @Test(description = "DELETE /users/{id} with valid id returns 200")
    @Story("Delete user - happy path")
    @Severity(SeverityLevel.CRITICAL)
    public void deleteUser_withValidId_returns200() {
        Response response = userService.deleteUser(1);

        ResponseValidator.of(response)
                .hasStatus(HttpStatus.OK);
    }

    @Test(description = "DELETE /users/{id} response body is empty or {}")
    @Story("Delete user - response body")
    @Severity(SeverityLevel.NORMAL)
    public void deleteUser_responseBodyIsEmpty() {
        Response response = userService.deleteUser(1);

        ResponseValidator.of(response)
                .hasStatus(HttpStatus.OK)
                .hasContentTypeJson();
    }
}