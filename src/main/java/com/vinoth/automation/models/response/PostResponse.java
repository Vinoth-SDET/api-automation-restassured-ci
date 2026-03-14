package com.vinoth.automation.models.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PostResponse {
    private Integer id;
    private Integer userId;
    private String  title;
    private String  body;
}