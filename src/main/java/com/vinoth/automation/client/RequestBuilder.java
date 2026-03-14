package com.vinoth.automation.client;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.HashMap;
import java.util.Map;

/**
 * Immutable fluent HTTP request builder.
 * Each method returns a NEW instance — safe for parallel execution.
 *
 * Usage:
 *   Response r = RequestBuilder.from(client)
 *       .withPath("/users/{id}")
 *       .withPathParam("id", 1)
 *       .withQueryParam("format", "json")
 *       .get();
 */
public final class RequestBuilder {

    private final RequestSpecification spec;
    private final String               path;
    private final Map<String, Object>  pathParams;
    private final Map<String, Object>  queryParams;
    private final Object               body;

    private RequestBuilder(RequestSpecification spec, String path,
                           Map<String, Object> pathParams,
                           Map<String, Object> queryParams,
                           Object body) {
        this.spec        = spec;
        this.path        = path;
        this.pathParams  = pathParams;
        this.queryParams = queryParams;
        this.body        = body;
    }

    public static RequestBuilder from(ApiClient client) {
        return new RequestBuilder(client.spec(), "", Map.of(), Map.of(), null);
    }

    public RequestBuilder withPath(String path) {
        return new RequestBuilder(spec, path, pathParams, queryParams, body);
    }

    public RequestBuilder withPathParam(String key, Object value) {
        Map<String, Object> updated = new HashMap<>(pathParams);
        updated.put(key, value);
        return new RequestBuilder(spec, path, Map.copyOf(updated), queryParams, body);
    }

    public RequestBuilder withQueryParam(String key, Object value) {
        Map<String, Object> updated = new HashMap<>(queryParams);
        updated.put(key, value);
        return new RequestBuilder(spec, path, pathParams, Map.copyOf(updated), body);
    }

    public RequestBuilder withBody(Object body) {
        return new RequestBuilder(spec, path, pathParams, queryParams, body);
    }

    public Response get()    { return build().when().get(path); }
    public Response post()   { return build().when().post(path); }
    public Response put()    { return build().when().put(path); }
    public Response patch()  { return build().when().patch(path); }
    public Response delete() { return build().when().delete(path); }

    private RequestSpecification build() {
        RequestSpecification s = spec
                .pathParams(pathParams)
                .queryParams(queryParams);
        if (body != null) s = s.body(body);
        return s;
    }
}