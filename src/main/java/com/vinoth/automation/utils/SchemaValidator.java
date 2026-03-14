package com.vinoth.automation.utils;

import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import lombok.extern.log4j.Log4j2;

import java.io.InputStream;

/**
 * JSON Schema draft-07 contract validator.
 *
 * Schema files live in src/test/resources/schemas/.
 * A failing validation means the API has drifted from its contract —
 * field removed, type changed, or required field missing.
 *
 * Usage:
 *   SchemaValidator.validate(response, "user-response-schema.json");
 *
 * Or via ResponseValidator chain:
 *   ResponseValidator.of(response).bodyMatchesSchema("user-response-schema.json");
 */
@Log4j2
public final class SchemaValidator {

    private SchemaValidator() {}

    public static void validate(Response response, String schemaFileName) {
        String schemaPath = "schemas/" + schemaFileName;
        InputStream schema = SchemaValidator.class
                .getClassLoader()
                .getResourceAsStream(schemaPath);

        if (schema == null) {
            throw new IllegalArgumentException(
                    "Schema file not found on classpath: " + schemaPath);
        }

        log.debug("Validating response against schema: {}", schemaFileName);
        response.then().assertThat().body(JsonSchemaValidator.matchesJsonSchema(schema));
        log.debug("Schema validation passed: {}", schemaFileName);
    }
}