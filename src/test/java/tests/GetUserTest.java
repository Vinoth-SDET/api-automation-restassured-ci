package tests;

import base.BaseTest;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.Constants;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;

@Epic("JSONPlaceholder Posts API")
@Feature("GET Post")
public class GetUserTest extends BaseTest {

    @Test(priority = 1,
            description = "Verify GET /posts/2 returns status 200")
    @Story("Retrieve a post by ID")
    @Severity(SeverityLevel.CRITICAL)
    public void testGetPostById_StatusCode_ShouldBe200() {

        given()
                .spec(requestSpec)
                .pathParam("id", Constants.DEFAULT_USER_ID)
                .when()
                .get(Constants.USER_BY_ID)
                .then()
                .statusCode(Constants.STATUS_OK)
                .log().ifValidationFails();

        System.out.println("✅ GET /posts/2 — Status 200 verified");
    }

    @Test(priority = 2,
            description = "Verify GET /posts/2 returns correct fields")
    @Story("Response body contains correct data")
    @Severity(SeverityLevel.CRITICAL)
    public void testGetPostById_ResponseBody_ShouldContainCorrectData() {

        given()
                .spec(requestSpec)
                .pathParam("id", Constants.DEFAULT_USER_ID)
                .when()
                .get(Constants.USER_BY_ID)
                .then()
                .statusCode(Constants.STATUS_OK)
                .body("id", equalTo(2))
                .body("userId", notNullValue())
                .body("title", notNullValue())
                .body("body", notNullValue())
                .log().ifValidationFails();

        System.out.println("✅ GET /posts/2 — Body fields verified");
    }

    @Test(priority = 3,
            description = "Verify GET /posts/2 matches JSON schema")
    @Story("Response matches API contract schema")
    @Severity(SeverityLevel.NORMAL)
    public void testGetPostById_JsonSchema_ShouldBeValid() {

        given()
                .spec(requestSpec)
                .pathParam("id", Constants.DEFAULT_USER_ID)
                .when()
                .get(Constants.USER_BY_ID)
                .then()
                .statusCode(Constants.STATUS_OK)
                .body(matchesJsonSchemaInClasspath(Constants.SCHEMA_GET_USER))
                .log().ifValidationFails();

        System.out.println("✅ GET /posts/2 — Schema validation passed");
    }

    @Test(priority = 4,
            description = "Verify response fields using assertions")
    @Story("Field-level assertions on response")
    @Severity(SeverityLevel.NORMAL)
    public void testGetPostById_FieldAssertions_ShouldPass() {

        Response response = given()
                .spec(requestSpec)
                .pathParam("id", Constants.DEFAULT_USER_ID)
                .when()
                .get(Constants.USER_BY_ID)
                .then()
                .statusCode(Constants.STATUS_OK)
                .extract().response();

        int id = response.jsonPath().getInt("id");
        int userId = response.jsonPath().getInt("userId");
        String title = response.jsonPath().getString("title");

        Assert.assertEquals(id, 2, "Post ID should be 2");
        Assert.assertTrue(userId > 0, "userId should be positive");
        Assert.assertNotNull(title, "title should not be null");
        Assert.assertFalse(title.isEmpty(), "title should not be empty");

        System.out.println("✅ GET /posts/2 — All assertions passed");
        System.out.println("   ID: " + id + " | userId: " + userId);
    }
}