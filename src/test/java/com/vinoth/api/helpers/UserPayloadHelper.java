package com.vinoth.api.helpers;

import com.github.javafaker.Faker;
import com.vinoth.api.models.request.UserRequest;

/**
 * Helper that builds UserRequest payloads for test use.
 * Uses JavaFaker to generate realistic, unique test data.
 */
public final class UserPayloadHelper {

    private static final Faker FAKER = new Faker();

    private UserPayloadHelper() {}

    /** Creates a standard user payload with random realistic data. */
    public static UserRequest createUserPayload() {
        return UserRequest.builder()
                .name(FAKER.name().fullName())
                .username(FAKER.name().username())
                .email(FAKER.internet().emailAddress())
                .phone(FAKER.phoneNumber().phoneNumber())
                .website(FAKER.internet().domainName())
                .build();
    }

    /** Creates a user payload with specific name and email. */
    public static UserRequest createUserPayload(String name, String email) {
        return UserRequest.builder()
                .name(name)
                .username(name.toLowerCase().replace(" ", "_"))
                .email(email)
                .phone(FAKER.phoneNumber().phoneNumber())
                .build();
    }

    /** Creates a minimal user payload with name only. */
    public static UserRequest createMinimalPayload(String name) {
        return UserRequest.builder()
                .name(name)
                .build();
    }
}