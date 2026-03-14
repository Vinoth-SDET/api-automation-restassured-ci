package com.vinoth.automation.models.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

/**
 * Generic paged response wrapper for paginated API endpoints.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PagedResponse<T> {
    private Integer    page;
    private Integer    perPage;
    private Integer    total;
    private Integer    totalPages;
    private List<T>    data;
}