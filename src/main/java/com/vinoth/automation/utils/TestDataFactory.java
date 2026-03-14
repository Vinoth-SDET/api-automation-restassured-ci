package com.vinoth.automation.utils;

import com.github.javafaker.Faker;
import com.vinoth.automation.models.request.PostRequest;
import com.vinoth.automation.models.request.UserRequest;

/**
 * Builder-pattern test data factory using JavaFaker.
 * Generates realistic, unique test data for each test run —
 * no hardcoded strings that clash in parallel execution.
 *
 * Usage:
 *   UserRequest user  = TestDataFactory.randomUser();
 *   PostRequest post  = TestDataFactory.randomPost(1);
 *   UserRequest named = TestDataFactory.userWithName("Vinoth M");
 */
public final class TestDataFactory {

    private static final Faker faker = new Faker();

    private TestDataFactory() {}

    // ── User factories ─────────────────────────────────────────────────────────

    public static UserRequest randomUser() {
        return UserRequest.builder()
                .name(faker.name().fullName())
                .username(faker.name().username())
                .email(faker.internet().emailAddress())
                .phone(faker.phoneNumber().phoneNumber())
                .website(faker.internet().domainName())
                .build();
    }

    public static UserRequest userWithName(String name) {
        return UserRequest.builder()
                .name(name)
                .username(faker.name().username())
                .email(faker.internet().emailAddress())
                .build();
    }

    public static UserRequest minimalUser() {
        return UserRequest.builder()
                .name(faker.name().fullName())
                .build();
    }

    // ── Post factories ─────────────────────────────────────────────────────────

    public static PostRequest randomPost(int userId) {
        return PostRequest.builder()
                .userId(userId)
                .title(faker.lorem().sentence(5))
                .body(faker.lorem().paragraph(2))
                .build();
    }

    public static PostRequest postWithTitle(int userId, String title) {
        return PostRequest.builder()
                .userId(userId)
                .title(title)
                .body(faker.lorem().paragraph())
                .build();
    }
}