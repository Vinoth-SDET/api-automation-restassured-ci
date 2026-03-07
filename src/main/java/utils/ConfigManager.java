package utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * ConfigManager — Loads configuration from config.properties
 * Why: Centralizes all environment config. To switch from staging
 * to production, you change ONE file, not 50 test classes.
 */
public class ConfigManager {

    private static final Properties properties = new Properties();
    private static ConfigManager instance;

    // Private constructor — Singleton pattern
    // Only ONE instance of config is ever loaded
    private ConfigManager() {
        loadProperties();
    }

    // Thread-safe singleton accessor
    public static ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }

    private void loadProperties() {
        try (InputStream input = getClass()
                .getClassLoader()
                .getResourceAsStream("config.properties")) {

            if (input == null) {
                throw new RuntimeException(
                        "config.properties not found in resources folder!"
                );
            }
            properties.load(input);
            System.out.println("✅ Config loaded successfully");

        } catch (IOException e) {
            throw new RuntimeException("Failed to load config.properties", e);
        }
    }

    // Get any property by key
    public String get(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            throw new RuntimeException(
                    "Property '" + key + "' not found in config.properties"
            );
        }
        return value.trim();
    }

    // Convenience getters for common config values
    public String getBaseUrl() {
        return get("base.url");
    }

    public String getBasePath() {
        String path = properties.getProperty("base.path", "");
        return path == null ? "" : path.trim();
    }
}