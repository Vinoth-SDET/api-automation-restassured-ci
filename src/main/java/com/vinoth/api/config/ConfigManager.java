package com.vinoth.api.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Singleton environment-aware configuration manager.
 *
 * <p>Resolves the active profile via the {@code -Denv} JVM flag (default: {@code qa}).
 * Secrets are interpolated from {@code ${ENV_VAR}} placeholders expanded at startup —
 * no plaintext secrets ever committed to source control.
 *
 * <p>Usage: {@code ConfigManager.get().baseUrl()}
 */
public final class ConfigManager {

    private static final Logger log = LogManager.getLogger(ConfigManager.class);

    // Double-checked locking singleton — thread-safe without synchronising every read
    private static volatile ConfigManager INSTANCE;

    private final Properties props = new Properties();
    private final String activeEnv;

    private ConfigManager() {
        this.activeEnv = System.getProperty("env", "qa");
        String path = "/config/" + activeEnv + ".properties";

        try (InputStream in = getClass().getResourceAsStream(path)) {
            if (in == null) {
                throw new RuntimeException("Config file not found on classpath: " + path);
            }
            props.load(in);
            resolveEnvVarPlaceholders();
            log.info("Configuration loaded | env={} | baseUrl={}", activeEnv, props.getProperty("base.url"));
        } catch (IOException e) {
            throw new ExceptionInInitializerError("Failed to load config [" + path + "]: " + e.getMessage());
        }
    }

    /**
     * Returns the singleton instance, initialising it on first call.
     */
    public static ConfigManager get() {
        if (INSTANCE == null) {
            synchronized (ConfigManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ConfigManager();
                }
            }
        }
        return INSTANCE;
    }

    // ── Typed property accessors ──────────────────────────────────────────────

    public String baseUrl()   { return require("base.url"); }
    public int    timeout()   { return Integer.parseInt(require("api.timeout.ms")); }
    public int    retries()   { return Integer.parseInt(require("retry.count")); }
    public String authToken() { return require("auth.token"); }
    public int    threads()   { return Integer.parseInt(require("parallel.threads")); }
    public String logLevel()  { return props.getProperty("log.level", "INFO"); }
    public String activeEnv() { return activeEnv; }

    /**
     * Returns a raw property value, or {@code defaultValue} if absent.
     */
    public String getProperty(String key, String defaultValue) {
        return props.getProperty(key, defaultValue);
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private String require(String key) {
        String value = props.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(
                    "Required config key '" + key + "' is missing from [" + activeEnv + ".properties]");
        }
        return value.trim();
    }

    /**
     * Expands {@code ${SOME_ENV_VAR}} tokens using OS environment variables.
     * Placeholders that have no matching env var are left as-is.
     */
    private void resolveEnvVarPlaceholders() {
        props.replaceAll((key, value) -> {
            String s = (String) value;
            if (s.startsWith("${") && s.endsWith("}")) {
                String envKey = s.substring(2, s.length() - 1);
                String envVal = System.getenv(envKey);
                if (envVal != null) {
                    log.debug("Resolved placeholder ${{{}}}", envKey);
                    return envVal;
                }
                log.warn("Env var '{}' not set — keeping placeholder", envKey);
            }
            return s;
        });
    }

    // ── For testing: allow resetting the singleton ────────────────────────────
    static void reset() {
        INSTANCE = null;
    }
}