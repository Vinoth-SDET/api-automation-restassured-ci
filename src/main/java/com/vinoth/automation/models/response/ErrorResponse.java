package com.vinoth.automation.models.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Typed error response POJO.
 * Negative tests assert on this structure instead of raw string matching.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ErrorResponse {
    private Integer status;
    private String  error;
    private String  message;
    private String  path;
}