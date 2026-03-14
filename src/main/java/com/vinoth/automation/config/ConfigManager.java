package com.vinoth.automation.config;

import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Env-aware singleton configuration manager.
 *
 * Resolution order:
 *   1. System property  (-Dkey=value on JVM launch)
 *   2. Environment variable  (CI secrets injected via env block)
 *   3. Properties file  (src/test/resources/config/{env}.properties)
 *
 * Secret interpolation: values matching ${ENV_VAR_NAME} are resolved
 * from system environment at runtime — no secrets ever touch source files.
 *
 * Usage:
 *   ConfigManager cfg = ConfigManager.getInstance();
 *   String url   = cfg.getBaseUrl();
 *   String token = cfg.getAuthToken();  // resolved from CI secret
 */
@Log4j2
public final class ConfigManager {

    private static final Pattern SECRET_PATTERN = Pattern.compile("\\$\\{([^}]+)\\}");
    private static volatile ConfigManager instance;

    private final Properties props = new Properties();
    private final String env;

    private ConfigManager() {
        this.env = System.getProperty("env", "qa").toLowerCase();
        String file = "config/" + env + ".properties";
        log.info("Loading config: {} (env={})", file, env);

        try (InputStream is = getClass().getClassLoader().getResourceAsStream(file)) {
            if (is == null) {
                throw new IllegalStateException(
                        "Config file not found: " + file + ". Valid envs: dev, qa, staging");
            }
            props.load(is);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load config: " + file, e);
        }
    }

    /** Thread-safe double-checked locking singleton. */
    public static ConfigManager getInstance() {
        if (instance == null) {
            synchronized (ConfigManager.class) {
                if (instance == null) {
                    instance = new ConfigManager();
                }
            }
        }
        return instance;
    }

    public String getBaseUrl()    { return resolve("base.url"); }
    public String getAuthToken()  { return resolve("auth.token"); }
    public String getEnv()        { return env; }
    public int    getMaxRetries() { return Integer.parseInt(resolve("max.retries", "3")); }
    public long   getRetryDelay() { return Long.parseLong(resolve("retry.delay.ms", "1000")); }
    public int    getThreads()    { return Integer.parseInt(System.getProperty("threads", "5")); }

    /**
     * Resolve a property value, interpolating ${ENV_VAR} references from
     * system environment. Supports chained interpolation.
     */
    private String resolve(String key) {
        String raw = System.getProperty(key, props.getProperty(key));
        if (raw == null) {
            throw new IllegalStateException(
                    "Missing required config key [" + key + "] in " + env + ".properties");
        }
        return interpolate(raw);
    }

    private String resolve(String key, String defaultValue) {
        String raw = System.getProperty(key, props.getProperty(key, defaultValue));
        return interpolate(raw);
    }

    private String interpolate(String value) {
        if (value == null || !value.contains("${")) return value;
        Matcher m = SECRET_PATTERN.matcher(value);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String envVar  = m.group(1);
            String envVal  = System.getenv(envVar);
            if (envVal == null) {
                log.warn("Secret interpolation: env var [{}] not set — using placeholder", envVar);
                envVal = "UNSET_" + envVar;
            }
            m.appendReplacement(sb, Matcher.quoteReplacement(envVal));
        }
        m.appendTail(sb);
        return sb.toString();
    }
}