package com.vinoth.automation.config;

import lombok.extern.log4j.Log4j2;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Resolves ${ENV_VAR} placeholders in config values from system environment.
 * Used by ConfigManager to interpolate secrets at runtime.
 *
 * Example:
 *   auth.token = ${QA_AUTH_TOKEN}
 *   → resolved to the value of System.getenv("QA_AUTH_TOKEN")
 *
 * If the env var is not set, warns and returns a safe placeholder
 * so the framework does not crash on startup — tests that need auth will fail
 * with a clear 401, not an NPE.
 */
@Log4j2
public final class SecretResolver {

    private static final Pattern PLACEHOLDER = Pattern.compile("\\$\\{([^}]+)\\}");

    private SecretResolver() {}

    public static String resolve(String value) {
        if (value == null || !value.contains("${")) return value;

        Matcher m = PLACEHOLDER.matcher(value);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String varName = m.group(1);
            String envVal  = System.getenv(varName);
            if (envVal == null || envVal.isBlank()) {
                log.warn("SecretResolver: env var [{}] is not set — using empty string", varName);
                envVal = "";
            }
            m.appendReplacement(sb, Matcher.quoteReplacement(envVal));
        }
        m.appendTail(sb);
        return sb.toString();
    }
}