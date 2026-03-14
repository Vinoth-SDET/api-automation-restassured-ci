package com.vinoth.automation.tests.contract;

import com.vinoth.automation.base.WireMockBase;
import com.vinoth.automation.constants.HttpStatus;
import com.vinoth.automation.utils.ResponseValidator;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.given;

/**
 * Consumer-side contract tests backed by WireMock.
 * Run fully OFFLINE — no external network dependency.
 *
 * These tests prove the framework understands API contracts,
 * not just status codes. The schema file defines the contract —
 * if the live API drifts from it, the test catches the drift.
 */
@Epic("API Contract Testing")
@Feature("User API — Consumer Contract")
public class UserContractTests extends WireMockBase {

    @Override
    protected void registerStubs() {
        // GET /users/1 — happy path contract
        wireMock().stubFor(get(urlEqualTo("/users/1"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                    {
                      "id": 1,
                      "name": "Leanne Graham",
                      "username": "Bret",
                      "email": "Sincere@april.biz",
                      "phone": "1-770-736-8031 x56442",
                      "website": "hildegard.org"
                    }
                    """)));

        // GET /users/99999 — 404 contract
        wireMock().stubFor(get(urlEqualTo("/users/99999"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{}")));

        // POST /users — create contract
        wireMock().stubFor(post(urlEqualTo("/users"))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                    {
                      "id": 11,
                      "name": "Test User",
                      "username": "testuser",
                      "email": "test@example.com"
                    }
                    """)));

        // DELETE /users/1 — delete contract
        wireMock().stubFor(delete(urlEqualTo("/users/1"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{}")));
    }

    @Test(groups = {"contract"},
            description = "GET /users/1 response matches consumer schema contract")
    @Story("GET /users/{id} — schema contract")
    @Severity(SeverityLevel.BLOCKER)
    public void getUser_responseMatchesSchemaContract() {
        Response response = given()
                .baseUri(stubBaseUrl())
                .contentType("application/json")
                .when()
                .get("/users/1");

        ResponseValidator.of(response)
                .hasStatus(HttpStatus.OK)
                .hasContentTypeJson()
                .bodyFieldEquals("id", 1)
                .bodyFieldNotNull("name")
                .bodyFieldNotNull("email")
                .bodyMatchesSchema("user-response-schema.json");

        wireMock().verify(1, getRequestedFor(urlEqualTo("/users/1")));
    }

    @Test(groups = {"contract"},
            description = "GET /users/99999 contract returns 404")
    @Story("GET /users/{id} — 404 contract")
    @Severity(SeverityLevel.CRITICAL)
    public void getUser_nonExistent_contractReturns404() {
        Response response = given()
                .baseUri(stubBaseUrl())
                .contentType("application/json")
                .when()
                .get("/users/99999");

        ResponseValidator.of(response)
                .hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test(groups = {"contract"},
            description = "POST /users contract returns 201 with id")
    @Story("POST /users — create contract")
    @Severity(SeverityLevel.CRITICAL)
    public void createUser_contractReturns201WithId() {
        Response response = given()
                .baseUri(stubBaseUrl())
                .contentType("application/json")
                .body("{\"name\":\"Test User\",\"email\":\"test@example.com\"}")
                .when()
                .post("/users");

        ResponseValidator.of(response)
                .hasStatus(HttpStatus.CREATED)
                .bodyFieldNotNull("id");
    }

    @Test(groups = {"contract"},
            description = "DELETE /users/1 contract returns 200")
    @Story("DELETE /users/{id} — delete contract")
    @Severity(SeverityLevel.NORMAL)
    public void deleteUser_contractReturns200() {
        Response response = given()
                .baseUri(stubBaseUrl())
                .contentType("application/json")
                .when()
                .delete("/users/1");

        ResponseValidator.of(response)
                .hasStatus(HttpStatus.OK);
    }
}