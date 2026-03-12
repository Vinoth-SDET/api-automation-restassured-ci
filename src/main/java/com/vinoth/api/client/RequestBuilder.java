package com.vinoth.api.client;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

/**
 * Fluent builder for one-off requests that need headers, query params, or path
 * params beyond what {@link ApiClient} covers directly.
 *
 * <p>Example usage:
 * <pre>{@code
 *   Response response = new RequestBuilder(api().getBaseSpec())
 *       .header("X-Custom", "value")
 *       .queryParam("filter", "active")
 *       .pathParam("id", 42)
 *       .get("/users/{id}");
 * }</pre>
 */
public class RequestBuilder {

    private final RequestSpecification baseSpec;
    private final Map<String, String>  headers     = new HashMap<>();
    private final Map<String, Object>  queryParams = new HashMap<>();
    private final Map<String, Object>  pathParams  = new HashMap<>();
    private Object                     body;

    public RequestBuilder(RequestSpecification baseSpec) {
        this.baseSpec = baseSpec;
    }

    public RequestBuilder header(String name, String value) {
        headers.put(name, value);
        return this;
    }

    public RequestBuilder queryParam(String name, Object value) {
        queryParams.put(name, value);
        return this;
    }

    public RequestBuilder pathParam(String name, Object value) {
        pathParams.put(name, value);
        return this;
    }

    public RequestBuilder body(Object body) {
        this.body = body;
        return this;
    }

    // ── Terminal methods ──────────────────────────────────────────────────────

    public Response get(String path) {
        return build().when().get(path);
    }

    public Response post(String path) {
        return build().when().post(path);
    }

    public Response put(String path) {
        return build().when().put(path);
    }

    public Response patch(String path) {
        return build().when().patch(path);
    }

    public Response delete(String path) {
        return build().when().delete(path);
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private RequestSpecification build() {
        RequestSpecification spec = given(baseSpec);
        if (!headers.isEmpty())     spec = spec.headers(headers);
        if (!queryParams.isEmpty()) spec = spec.queryParams(queryParams);
        if (!pathParams.isEmpty())  spec = spec.pathParams(pathParams);
        if (body != null)           spec = spec.body(body);
        return spec;
    }
}