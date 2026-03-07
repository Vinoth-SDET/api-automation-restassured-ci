package helpers;

import utils.Constants;

import java.util.HashMap;
import java.util.Map;

/**
 * UserPayloadHelper — Builds request payloads for User API tests
 * <p>
 * Why: Test classes should focus on ASSERTIONS, not on building
 * data. This separation makes tests cleaner and payloads reusable.
 */
public class UserPayloadHelper {

    /**
     * Creates a standard user creation payload
     * Used by: PostUserTest
     */
    public static Map<String, String> createUserPayload() {
        Map<String, String> payload = new HashMap<>();
        payload.put(Constants.FIELD_NAME, Constants.TEST_USER_NAME);
        payload.put(Constants.FIELD_JOB, Constants.TEST_USER_JOB);
        return payload;
    }

    /**
     * Creates a custom user payload with any name/job
     * Used by: parameterized tests
     */
    public static Map<String, String> createUserPayload(
            String name, String job) {
        Map<String, String> payload = new HashMap<>();
        payload.put(Constants.FIELD_NAME, name);
        payload.put(Constants.FIELD_JOB, job);
        return payload;
    }

    /**
     * Creates an update payload (for PUT/PATCH tests later)
     */
    public static Map<String, String> updateUserPayload(
            String name, String job) {
        Map<String, String> payload = new HashMap<>();
        payload.put(Constants.FIELD_NAME, name);
        payload.put(Constants.FIELD_JOB, job);
        return payload;
    }
}