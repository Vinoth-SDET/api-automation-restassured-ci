package com.vinoth.automation.tests.users;

import com.vinoth.automation.base.BaseTest;
import com.vinoth.automation.constants.HttpStatus;
import com.vinoth.automation.helpers.UserPayloadHelper;
import com.vinoth.automation.models.request.UserRequest;
import com.vinoth.automation.services.UserService;
import com.vinoth.automation.utils.ResponseValidator;
import com.vinoth.automation.utils.TestDataFactory;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Method;

@Epic("User Management API")
@Feature("PUT /users")
public class UpdateUserTests extends BaseTest {

    private static final long EXTERNAL_API_SLA_MS = 8000L;
    private UserService userService;

    @BeforeMethod(alwaysRun = true)
    public void initService(Method method) {
        userService = new UserService(client());
    }

    @Test(groups = {"regression"},
            description = "PUT /users/{id} full update returns 200 with updated fields")
    @Story("Full update — happy path")
    @Severity(SeverityLevel.CRITICAL)
    public void updateUser_withFullPayload_returns200() {
        UserRequest payload = UserPayloadHelper.updatedUser();
        Response response = userService.updateUser(1, payload);
        ResponseValidator.of(response)
                .hasStatus(HttpStatus.OK)
                .hasContentTypeJson()
                .bodyFieldEquals("id", 1)
                .bodyFieldEquals("name", payload.getName())
                .respondsWithin(EXTERNAL_API_SLA_MS);
    }

    @Test(groups = {"regression"},
            description = "PUT /users/{id} with random data returns 200")
    @Story("Full update — random data")
    @Severity(SeverityLevel.NORMAL)
    public void updateUser_withRandomPayload_returns200() {
        UserRequest payload = TestDataFactory.randomUser();
        Response response = userService.updateUser(1, payload);
        ResponseValidator.of(response)
                .hasStatus(HttpStatus.OK)
                .bodyFieldNotNull("id")
                .bodyFieldEquals("name", payload.getName());
    }

    @Test(groups = {"regression"},
            description = "PUT /users/{id} updates email correctly")
    @Story("Full update — email field")
    @Severity(SeverityLevel.NORMAL)
    public void updateUser_emailField_isUpdated() {
        UserRequest payload = UserRequest.builder()
                .name("Email Test User")
                .email("newemail@test.com")
                .build();
        Response response = userService.updateUser(1, payload);
        ResponseValidator.of(response)
                .hasStatus(HttpStatus.OK)
                .bodyFieldEquals("email", "newemail@test.com");
    }
}