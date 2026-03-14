package com.vinoth.automation.constants;

/**
 * All API endpoint path constants — one place to update when paths change.
 */
public final class Endpoints {

    private Endpoints() {}

    public static final String USERS      = "/users";
    public static final String USER_BY_ID = "/users/{id}";

    public static final String POSTS          = "/posts";
    public static final String POST_BY_ID     = "/posts/{id}";
    public static final String POSTS_COMMENTS = "/posts/{id}/comments";

    public static final String COMMENTS = "/comments";

    public static final String TODOS      = "/todos";
    public static final String TODO_BY_ID = "/todos/{id}";

    // Auth endpoints — pattern for real microservice testing
    public static final String AUTH_LOGIN  = "/auth/login";
    public static final String AUTH_LOGOUT = "/auth/logout";
}