package com.vinoth.automation.tests.contract;

import com.vinoth.automation.base.WireMockBase;
import com.vinoth.automation.constants.HttpStatus;
import com.vinoth.automation.utils.ResponseValidator;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.given;

@Epic("API Contract Testing")
@Feature("Post API — Consumer Contract")
public class PostContractTests extends WireMockBase {

    @Override
    protected void registerStubs() {
        // GET /posts/1
        wireMock().stubFor(get(urlEqualTo("/posts/1"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                    {
                      "userId": 1,
                      "id": 1,
                      "title": "sunt aut facere repellat provident",
                      "body": "quia et suscipit suscipit recusandae"
                    }
                    """)));

        // GET /posts
        wireMock().stubFor(get(urlEqualTo("/posts"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                    [
                      {"userId":1,"id":1,"title":"Post One","body":"Body one"},
                      {"userId":1,"id":2,"title":"Post Two","body":"Body two"}
                    ]
                    """)));

        // POST /posts
        wireMock().stubFor(post(urlEqualTo("/posts"))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                    {"id": 101, "userId": 1, "title": "New Post", "body": "New body"}
                    """)));

        // DELETE /posts/1
        wireMock().stubFor(delete(urlEqualTo("/posts/1"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{}")));

        // GET /posts/99999 — not found
        wireMock().stubFor(get(urlEqualTo("/posts/99999"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{}")));
    }

    @Test(groups = {"contract"},
            description = "GET /posts/1 matches post schema contract")
    @Story("GET /posts/{id} — schema contract")
    @Severity(SeverityLevel.BLOCKER)
    public void getPost_responseMatchesSchemaContract() {
        Response response = given()
                .baseUri(stubBaseUrl())
                .contentType("application/json")
                .when()
                .get("/posts/1");

        ResponseValidator.of(response)
                .hasStatus(HttpStatus.OK)
                .hasContentTypeJson()
                .bodyFieldEquals("id", 1)
                .bodyFieldNotNull("title")
                .bodyFieldNotNull("body")
                .bodyMatchesSchema("post-response-schema.json");

        wireMock().verify(1, getRequestedFor(urlEqualTo("/posts/1")));
    }

    @Test(groups = {"contract"},
            description = "GET /posts returns list contract")
    @Story("GET /posts — list contract")
    @Severity(SeverityLevel.CRITICAL)
    public void getAllPosts_contractReturnsList() {
        Response response = given()
                .baseUri(stubBaseUrl())
                .contentType("application/json")
                .when()
                .get("/posts");

        ResponseValidator.of(response)
                .hasStatus(HttpStatus.OK)
                .listSizeGreaterThan("$", 0);
    }

    @Test(groups = {"contract"},
            description = "POST /posts contract returns 201 with id")
    @Story("POST /posts — create contract")
    @Severity(SeverityLevel.CRITICAL)
    public void createPost_contractReturns201() {
        Response response = given()
                .baseUri(stubBaseUrl())
                .contentType("application/json")
                .body("{\"userId\":1,\"title\":\"New Post\",\"body\":\"New body\"}")
                .when()
                .post("/posts");

        ResponseValidator.of(response)
                .hasStatus(HttpStatus.CREATED)
                .bodyFieldNotNull("id");
    }

    @Test(groups = {"contract"},
            description = "GET /posts/99999 contract returns 404")
    @Story("GET /posts/{id} — 404 contract")
    @Severity(SeverityLevel.NORMAL)
    public void getPost_nonExistent_contractReturns404() {
        Response response = given()
                .baseUri(stubBaseUrl())
                .contentType("application/json")
                .when()
                .get("/posts/99999");

        ResponseValidator.of(response)
                .hasStatus(HttpStatus.NOT_FOUND);
    }
}