package com.vinoth.api.utils;

import io.restassured.module.jsv.JsonSchemaValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hamcrest.Matcher;

import java.io.InputStream;

/**
 * Utility wrapper around RestAssured's JsonSchemaValidator.
 *
 * Loads schema files using the Thread context classloader so they are found
 * correctly regardless of whether they sit in src/main/resources or
 * src/test/resources — no classpath configuration needed.
 */
public final class SchemaValidator {

    private static final Logger log = LogManager.getLogger(SchemaValidator.class);

    private SchemaValidator() {}

    /**
     * Returns a Hamcrest matcher that validates the response body against
     * the JSON Schema file at {@code schemas/<filename>} on the classpath.
     *
     * @param filename e.g. "user-response-schema.json"
     */
    @SuppressWarnings("rawtypes")
    public static Matcher matchesSchemaFile(String filename) {
        String path = "schemas/" + filename;

        // Use Thread context classloader — finds files in BOTH
        // src/main/resources AND src/test/resources
        InputStream stream = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(path);

        if (stream == null) {
            // Fallback: try the class's own classloader
            stream = SchemaValidator.class.getClassLoader()
                    .getResourceAsStream(path);
        }

        if (stream == null) {
            throw new IllegalStateException(
                    "Schema file not found on classpath: [" + path + "]\n" +
                            "Expected location: src/test/resources/schemas/" + filename + "\n" +
                            "Check the file exists and Maven has been reloaded."
            );
        }

        log.debug("Loading schema from classpath: {}", path);
        return JsonSchemaValidator.matchesJsonSchema(stream);
    }
}