package com.vinoth.automation.config;

import lombok.Builder;
import lombok.Getter;

/**
 * Typed, immutable config POJO for a single environment.
 * Constructed by ConfigManager from the resolved .properties file.
 */
@Getter
@Builder
public class EnvironmentConfig {
    private final String env;
    private final String baseUrl;
    private final String authToken;
    private final int    maxRetries;
    private final long   retryDelayMs;
    private final int    threads;
}