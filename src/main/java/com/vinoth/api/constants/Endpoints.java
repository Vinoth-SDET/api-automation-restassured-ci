package com.vinoth.api.constants;

/**
 * Centralised registry of all API endpoint paths.
 *
 * <p>Using constants here means a URL change is a single-line fix, not a
 * grep-and-replace across 80 test files — a critical maintainability pattern.
 *
 * <p>Path parameter templates use RestAssured's {@code {param}} syntax so they
 * can be passed directly to {@code given().pathParam("id", value)}.
 */
public final class Endpoints {

    private Endpoints() {
        // utility class — no instantiation
    }

    // ── Users ─────────────────────────────────────────────────────────────────
    public static final String USERS      = "/users";
    public static final String USER_BY_ID = "/users/{id}";

    // ── Posts ─────────────────────────────────────────────────────────────────
    public static final String POSTS      = "/posts";
    public static final String POST_BY_ID = "/posts/{id}";
    public static final String USER_POSTS = "/users/{userId}/posts";

    // ── Comments ──────────────────────────────────────────────────────────────
    public static final String COMMENTS          = "/comments";
    public static final String COMMENT_BY_ID     = "/comments/{id}";
    public static final String POST_COMMENTS     = "/posts/{postId}/comments";

    // ── Albums / Photos / Todos ───────────────────────────────────────────────
    public static final String ALBUMS     = "/albums";
    public static final String ALBUM_BY_ID = "/albums/{id}";
    public static final String TODOS      = "/todos";
    public static final String TODO_BY_ID = "/todos/{id}";
}