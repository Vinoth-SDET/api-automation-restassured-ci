package com.vinoth.automation.tests.users;

import com.vinoth.automation.base.BaseTest;
import com.vinoth.automation.constants.HttpStatus;
import com.vinoth.automation.dataproviders.UserDataProvider;
import com.vinoth.automation.models.request.UserRequest;
import com.vinoth.automation.services.UserService;
import com.vinoth.automation.utils.ResponseValidator;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("User Management API")
@Feature("Negative — Error Handling")
public class UserNegativeTests extends BaseTest {

    private static final long EXTERNAL_API_SLA_MS = 8000L;
    private UserService userService;

    @BeforeMethod(alwaysRun = true)
    public void initService(Method method) {
        userService = new UserService(client());
    }

    @Test(groups = {"regression", "negative"})
    @Story("Non-existent user")
    @Severity(SeverityLevel.CRITICAL)
    public void getUser_nonExistentId_returns404() {
        Response response = userService.getUserById(99999);
        ResponseValidator.of(response)
                .hasStatus(HttpStatus.NOT_FOUND)
                .respondsWithin(EXTERNAL_API_SLA_MS);
    }

    @Test(groups = {"regression", "negative"},
            dataProvider = "boundaryUserIds",
            dataProviderClass = UserDataProvider.class)
    @Story("Boundary value IDs")
    @Severity(SeverityLevel.NORMAL)
    public void getUser_withBoundaryId_returns4xx(int userId, String scenario) {
        Response response = userService.getUserById(userId);
        int status = response.statusCode();
        assertThat(status)
                .as("Scenario [%s] id=%d — expected 4xx but got %d", scenario, userId, status)
                .isBetween(400, 499);
    }

    @Test(groups = {"regression", "negative"})
    @Story("Delete non-existent user")
    @Severity(SeverityLevel.NORMAL)
    public void deleteUser_nonExistentId_respondsWithinSla() {
        Response response = userService.deleteUser(99999);
        ResponseValidator.of(response).respondsWithin(EXTERNAL_API_SLA_MS);
    }

    @Test(groups = {"regression", "negative"})
    @Story("Create user — empty name boundary")
    @Severity(SeverityLevel.NORMAL)
    public void createUser_emptyName_apiRespondsWithinSla() {
        UserRequest payload = UserRequest.builder().name("").email("boundary@test.com").build();
        Response response = userService.createUser(payload);
        ResponseValidator.of(response).respondsWithin(EXTERNAL_API_SLA_MS).hasContentTypeJson();
    }

    @Test(groups = {"regression", "negative"})
    @Story("Create user — null name boundary")
    @Severity(SeverityLevel.NORMAL)
    public void createUser_nullName_apiRespondsWithinSla() {
        UserRequest payload = UserRequest.builder().email("null-name@test.com").build();
        Response response = userService.createUser(payload);
        ResponseValidator.of(response).respondsWithin(EXTERNAL_API_SLA_MS);
    }
}