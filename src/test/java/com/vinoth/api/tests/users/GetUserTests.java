package com.vinoth.api.tests.users;

import com.vinoth.api.base.BaseTest;
import com.vinoth.api.constants.HttpStatus;
import com.vinoth.api.dataproviders.UserDataProvider;
import com.vinoth.api.models.request.UserRequest;
import com.vinoth.api.models.response.UserResponse;
import com.vinoth.api.services.UserService;
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

@Epic("User Management API")
@Feature("GET /users")
public class GetUserTests extends BaseTest {

    private UserService userService;

    @BeforeMethod
    public void initService() {
        userService = new UserService(api());
    }

    @Test(description = "GET /users returns 200 with a non-empty JSON array")
    @Story("List all users")
    @Severity(SeverityLevel.BLOCKER)
    public void getAllUsers_returns200WithNonEmptyList() {
        Response response = userService.getAllUsers();

        ResponseValidator.assertThat(response)
                .statusIs(HttpStatus.OK)
                .contentTypeIsJson()
                .respondsWithin(3000)
                .listSizeGreaterThan("$", 0)
                .matchesSchema("user-list-schema.json");
    }

    @Test(description = "GET /users - every user has required fields")
    @Story("List all users")
    @Severity(SeverityLevel.CRITICAL)
    public void getAllUsers_eachUserHasRequiredFields() {
        Response response = userService.getAllUsers();

        ResponseValidator.assertThat(response)
                .statusIs(HttpStatus.OK)
                .fieldNotNull("[0].id")
                .fieldNotNull("[0].name")
                .fieldNotNull("[0].username")
                .fieldNotNull("[0].email");
    }

    @Test(description = "GET /users/{id} returns correct user for id=1")
    @Story("Get user by ID")
    @Severity(SeverityLevel.CRITICAL)
    public void getUserById_returnsExpectedUser() {
        Response response = userService.getUserById(1);

        ResponseValidator.assertThat(response)
                .statusIs(HttpStatus.OK)
                .contentTypeIsJson()
                .respondsWithin(2000)
                .fieldEquals("id", 1)
                .fieldNotNull("name")
                .fieldNotNull("email")
                .matchesSchema("user-response-schema.json");
    }

    @Test(description = "GET /users/{id} - response deserialises to UserResponse POJO")
    @Story("Get user by ID")
    @Severity(SeverityLevel.NORMAL)
    public void getUserById_deserialisesToPojo() {
        Response response = userService.getUserById(1);

        UserResponse user = ResponseValidator.assertThat(response)
                .statusIs(HttpStatus.OK)
                .as(UserResponse.class);

        assertThat(user.getId()).isEqualTo(1);
        assertThat(user.getName()).isNotBlank();
        assertThat(user.getEmail()).contains("@");
    }

    @Test(description = "GET /users/{id} with non-existent id returns 404")
    @Story("Error handling - not found")
    @Severity(SeverityLevel.NORMAL)
    public void getUserById_withNonExistentId_returns404() {
        Response response = userService.getUserById(99999);

        ResponseValidator.assertThat(response)
                .statusIs(HttpStatus.NOT_FOUND);
    }

    @Test(
            description = "POST /users creates a new user - data-driven across valid-users.json",
            dataProvider = "validUsers",
            dataProviderClass = UserDataProvider.class
    )
    @Story("Create user - data-driven")
    @Severity(SeverityLevel.CRITICAL)
    public void createUser_withValidData_returns201(UserRequest payload) {
        Response response = userService.createUser(payload);

        ResponseValidator.assertThat(response)
                .statusIs(HttpStatus.CREATED)
                .fieldNotNull("id")
                .fieldEquals("name", payload.getName())
                .fieldEquals("email", payload.getEmail());
    }
}