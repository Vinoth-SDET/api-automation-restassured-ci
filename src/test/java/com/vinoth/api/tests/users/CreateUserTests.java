package com.vinoth.api.tests.users;

import com.vinoth.api.base.BaseTest;
import com.vinoth.api.constants.HttpStatus;
import com.vinoth.api.models.request.UserRequest;
import com.vinoth.api.services.UserService;
import com.vinoth.api.utils.ResponseValidator;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Epic("User Management API")
@Feature("POST /users")
public class CreateUserTests extends BaseTest {

    private UserService userService;

    @BeforeMethod
    public void initService() {
        userService = new UserService(api());
    }

    @Test(description = "POST /users with full payload returns 201 with id assigned")
    @Story("Create user — happy path")
    @Severity(SeverityLevel.BLOCKER)
    public void createUser_withFullPayload_returns201AndAssignsId() {
        UserRequest payload = UserRequest.builder()
                .name("Vinoth Murugan")
                .username("vinothm")
                .email("vinoth@example.com")
                .phone("555-0199")
                .website("vinoth.dev")
                .build();

        Response response = userService.createUser(payload);

        ResponseValidator.assertThat(response)
                .statusIs(HttpStatus.CREATED)
                .contentTypeIsJson()
                .fieldNotNull("id")
                .fieldEquals("name", "Vinoth Murugan")
                .fieldEquals("username", "vinothm")
                .fieldEquals("email", "vinoth@example.com");
    }

    @Test(description = "POST /users with minimal payload (name only) returns 201")
    @Story("Create user — minimal payload")
    @Severity(SeverityLevel.NORMAL)
    public void createUser_withMinimalPayload_returns201() {
        UserRequest payload = UserRequest.builder()
                .name("Minimal User")
                .build();

        Response response = userService.createUser(payload);

        ResponseValidator.assertThat(response)
                .statusIs(HttpStatus.CREATED)
                .fieldNotNull("id")
                .fieldEquals("name", "Minimal User");
    }

    @Test(description = "PUT /users/{id} full update returns 200 with updated fields")
    @Story("Update user")
    @Severity(SeverityLevel.CRITICAL)
    public void updateUser_withValidPayload_returns200() {
        UserRequest payload = UserRequest.builder()
                .name("Updated Name")
                .email("updated@example.com")
                .build();

        Response response = userService.updateUser(1, payload);

        ResponseValidator.assertThat(response)
                .statusIs(HttpStatus.OK)
                .fieldEquals("id", 1)
                .fieldEquals("name", "Updated Name");
    }
}