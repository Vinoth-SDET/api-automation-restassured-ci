package com.vinoth.automation.client;

import java.util.UUID;

/**
 * Generates unique correlation IDs for request tracing.
 * Each ID is attached as X-Correlation-ID header by ApiClient,
 * making every request traceable across distributed logs.
 */
public final class CorrelationIdProvider {

    private CorrelationIdProvider() {}

    public static String generate() {
        return UUID.randomUUID().toString();
    }
}