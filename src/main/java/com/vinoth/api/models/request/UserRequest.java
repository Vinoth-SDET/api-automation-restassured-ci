package com.vinoth.api.models.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request payload model for User API operations.
 *
 * <p>{@code @JsonInclude(NON_NULL)} ensures that null fields are omitted from
 * the serialised JSON body — prevents accidental overwriting of fields in PATCH calls.
 *
 * <p>{@code @Builder} enables readable construction in test code:
 * <pre>{@code
 *   UserRequest user = UserRequest.builder()
 *       .name("Alice Johnson")
 *       .email("alice@example.com")
 *       .build();
 * }</pre>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserRequest {

    private String name;
    private String username;
    private String email;
    private String phone;
    private String website;
    private Address address;
    private Company company;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Address {
        private String street;
        private String suite;
        private String city;
        private String zipcode;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Company {
        private String name;
        private String catchPhrase;
        private String bs;
    }
}

