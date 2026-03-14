package com.vinoth.automation.models.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserResponse {
    private Integer id;
    private String  name;
    private String  username;
    private String  email;
    private String  phone;
    private String  website;
}