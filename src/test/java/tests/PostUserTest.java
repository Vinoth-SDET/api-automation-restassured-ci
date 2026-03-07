package tests;

import base.BaseTest;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.Constants;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;

@Epic("JSONPlaceholder Posts API")
@Feature("POST Create Post")
public class PostUserTest extends BaseTest {

    private Map<String, Object> buildPayload() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("title", "API Automation Test Post");
        payload.put("body", "Created by RestAssured framework");
        payload.put("userId", 1);
        return payload;
    }

    @Test(priority = 1,
            description = "Verify POST /posts returns 201 Created")
    @Story("Create a new post")
    @Severity(SeverityLevel.CRITICAL)
    public void testCreatePost_StatusCode_ShouldBe201() {

        given()
                .spec(requestSpec)
                .body(buildPayload())
                .when()
                .post(Constants.USERS_ENDPOINT)
                .then()
                .statusCode(Constants.STATUS_CREATED)
                .log().ifValidationFails();

        System.out.println("✅ POST /posts — Status 201 verified");
    }

    @Test(priority = 2,
            description = "Verify POST /posts echoes payload in response")
    @Story("Response echoes submitted data")
    @Severity(SeverityLevel.CRITICAL)
    public void testCreatePost_ResponseBody_ShouldEchoPayload() {

        given()
                .spec(requestSpec)
                .body(buildPayload())
                .when()
                .post(Constants.USERS_ENDPOINT)
                .then()
                .statusCode(Constants.STATUS_CREATED)
                .body("title", equalTo("API Automation Test Post"))
                .body("body", equalTo("Created by RestAssured framework"))
                .body("userId", equalTo(1))
                .body("id", notNullValue())
                .log().ifValidationFails();

        System.out.println("✅ POST /posts — Payload echo verified");
    }

    @Test(priority = 3,
            description = "Verify POST /posts generates an ID")
    @Story("Server generates ID for new post")
    @Severity(SeverityLevel.NORMAL)
    public void testCreatePost_ServerGeneratesId() {

        Response response = given()
                .spec(requestSpec)
                .body(buildPayload())
                .when()
                .post(Constants.USERS_ENDPOINT)
                .then()
                .statusCode(Constants.STATUS_CREATED)
                .extract().response();

        int generatedId = response.jsonPath().getInt("id");
        Assert.assertTrue(generatedId > 0,
                "Server should generate a positive ID");

        System.out.println("✅ POST /posts — Generated ID: " + generatedId);
    }

    @Test(priority = 4,
            description = "Verify POST /posts response matches JSON schema")
    @Story("POST response matches API contract")
    @Severity(SeverityLevel.NORMAL)
    public void testCreatePost_JsonSchema_ShouldBeValid() {

        given()
                .spec(requestSpec)
                .body(buildPayload())
                .when()
                .post(Constants.USERS_ENDPOINT)
                .then()
                .statusCode(Constants.STATUS_CREATED)
                .body(matchesJsonSchemaInClasspath(Constants.SCHEMA_POST_USER))
                .log().ifValidationFails();

        System.out.println("✅ POST /posts — Schema validation passed");
    }
}