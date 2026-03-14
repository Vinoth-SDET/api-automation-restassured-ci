package com.vinoth.automation.helpers;

import com.vinoth.automation.models.request.UserRequest;
import com.vinoth.automation.utils.TestDataFactory;

/**
 * Centralised payload builder for User test scenarios.
 * Keeps test classes clean — payload construction logic in one place.
 */
public final class UserPayloadHelper {

    private UserPayloadHelper() {}

    public static UserRequest fullUser() {
        return TestDataFactory.randomUser();
    }

    public static UserRequest minimalUser() {
        return TestDataFactory.minimalUser();
    }

    public static UserRequest userWithName(String name) {
        return TestDataFactory.userWithName(name);
    }

    public static UserRequest vinothUser() {
        return UserRequest.builder()
                .name("Vinoth Murugan")
                .username("vinothm")
                .email("vinoth@example.com")
                .phone("555-0199")
                .website("vinoth.dev")
                .build();
    }

    public static UserRequest updatedUser() {
        return UserRequest.builder()
                .name("Updated Name")
                .email("updated@example.com")
                .phone("555-9900")
                .build();
    }
}