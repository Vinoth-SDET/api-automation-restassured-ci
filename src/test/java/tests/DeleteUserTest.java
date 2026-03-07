package tests;

import base.BaseTest;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.Constants;

import static io.restassured.RestAssured.given;

@Epic("JSONPlaceholder Posts API")
@Feature("DELETE Post")
public class DeleteUserTest extends BaseTest {

    @Test(priority = 1,
            description = "Verify DELETE /posts/2 returns 200")
    @Story("Delete a post by ID")
    @Severity(SeverityLevel.CRITICAL)
    public void testDeletePost_StatusCode_ShouldBe200() {

        given()
                .spec(requestSpec)
                .pathParam("id", Constants.DEFAULT_USER_ID)
                .when()
                .delete(Constants.USER_BY_ID)
                .then()
                .statusCode(200)
                .log().ifValidationFails();

        System.out.println("✅ DELETE /posts/2 — Status 200 verified");
    }

    @Test(priority = 2,
            description = "Verify DELETE /posts/2 returns empty body")
    @Story("Delete response body should be empty")
    @Severity(SeverityLevel.NORMAL)
    public void testDeletePost_ResponseBody_ShouldBeEmpty() {

        Response response = given()
                .spec(requestSpec)
                .pathParam("id", Constants.DEFAULT_USER_ID)
                .when()
                .delete(Constants.USER_BY_ID)
                .then()
                .statusCode(200)
                .extract().response();

        String body = response.getBody().asString().trim();
        Assert.assertTrue(
                body.equals("{}") || body.isEmpty(),
                "DELETE body should be empty or {}. Got: " + body
        );

        System.out.println("✅ DELETE /posts/2 — Empty body verified");
    }

    @Test(priority = 3,
            description = "Verify DELETE response time under 3 seconds")
    @Story("Delete API should respond quickly")
    @Severity(SeverityLevel.MINOR)
    public void testDeletePost_ResponseTime_ShouldBeUnder3Seconds() {

        Response response = given()
                .spec(requestSpec)
                .pathParam("id", Constants.DEFAULT_USER_ID)
                .when()
                .delete(Constants.USER_BY_ID)
                .then()
                .statusCode(200)
                .extract().response();

        long time = response.getTime();
        Assert.assertTrue(time < 3000,
                "Response time should be under 3000ms. Got: " + time + "ms");

        System.out.println("✅ DELETE /posts/2 — Response time: "
                + time + "ms");
    }
}