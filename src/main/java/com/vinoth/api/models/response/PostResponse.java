package com.vinoth.api.models.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response model for Post API endpoints.
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PostResponse {

    private Integer id;
    private Integer userId;
    private String  title;
    private String  body;
}