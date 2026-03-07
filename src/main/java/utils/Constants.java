package utils;

/**
 * Constants — All static values used across the framework
 * Why: Eliminates magic strings/numbers. If reqres.in changes
 * an endpoint, you fix it in ONE place.
 */
public final class Constants {

    // Prevent instantiation — this is a utility class
    private Constants() {
    }

    // ─── API Endpoints ───────────────────────────────────
    public static final String USERS_ENDPOINT    = "/posts";
    public static final String USER_BY_ID        = "/posts/{id}";

    // ─── HTTP Status Codes ───────────────────────────────
    public static final int STATUS_OK            = 200;
    public static final int STATUS_CREATED       = 201;
    public static final int STATUS_NO_CONTENT    = 200; // JSONPlaceholder returns 200 for DELETE
    public static final int STATUS_NOT_FOUND     = 404;

    // ─── Test Data ───────────────────────────────────────
    public static final int    DEFAULT_USER_ID   = 2;
    public static final String TEST_USER_NAME    = "Vinoth";
    public static final String TEST_USER_JOB     = "Senior SDET";

    // ─── JSON Field Names ────────────────────────────────
    public static final String FIELD_DATA        = "data";
    public static final String FIELD_ID          = "id";
    public static final String FIELD_TITLE       = "title";
    public static final String FIELD_BODY        = "body";
    public static final String FIELD_USERID      = "userId";
    public static final String FIELD_NAME        = "name";
    public static final String FIELD_JOB         = "job";
    public static final String FIELD_CREATED_AT  = "createdAt";

    // ─── Schema File Paths ───────────────────────────────
    public static final String SCHEMA_GET_USER   = "schemas/get_post_schema.json";
    public static final String SCHEMA_POST_USER  = "schemas/create_post_schema.json";
}