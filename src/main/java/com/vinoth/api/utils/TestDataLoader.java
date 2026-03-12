package com.vinoth.api.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * Utility for loading test data from JSON files on the classpath.
 *
 * <p>All test data lives in {@code src/test/resources/testdata/} as JSON files.
 * Keeping data external means coverage expansion is a <em>JSON edit, not a code change</em> —
 * a fundamental separation-of-concerns pattern.
 *
 * <p>Usage:
 * <pre>{@code
 *   // Load a list of UserRequest objects
 *   List<UserRequest> users = TestDataLoader.loadList(
 *       "testdata/users/valid-users.json", UserRequest.class);
 *
 *   // Load a single object (first element)
 *   UserRequest user = TestDataLoader.loadSingle(
 *       "testdata/users/valid-users.json", UserRequest.class);
 * }</pre>
 */
public final class TestDataLoader {

    private static final Logger log = LogManager.getLogger(TestDataLoader.class);

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    private TestDataLoader() {}

    /**
     * Loads a JSON array from the classpath and deserialises it into a typed list.
     *
     * @param relativePath classpath-relative path, e.g. {@code "testdata/users/valid-users.json"}
     * @param type         POJO class to deserialise each element into
     * @param <T>          element type
     * @return immutable list of deserialised objects
     * @throws IllegalArgumentException if the file is not found on the classpath
     * @throws RuntimeException         if JSON deserialisation fails
     */
    public static <T> List<T> loadList(String relativePath, Class<T> type) {
        URL resource = TestDataLoader.class.getClassLoader().getResource(relativePath);
        if (resource == null) {
            throw new IllegalArgumentException(
                    "Test data file not found on classpath: [" + relativePath + "]. " +
                            "Expected location: src/test/resources/" + relativePath);
        }
        try {
            List<T> data = MAPPER.readValue(resource,
                    MAPPER.getTypeFactory().constructCollectionType(List.class, type));
            log.debug("Loaded {} records from [{}]", data.size(), relativePath);
            return data;
        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to deserialise test data from [" + relativePath + "]: " + e.getMessage(), e);
        }
    }

    /**
     * Loads a JSON array and returns the <em>first</em> element.
     * Convenience method for tests that need a single representative payload.
     *
     * @throws IllegalStateException if the file contains an empty array
     */
    public static <T> T loadSingle(String relativePath, Class<T> type) {
        List<T> list = loadList(relativePath, type);
        if (list.isEmpty()) {
            throw new IllegalStateException(
                    "Test data file is empty: [" + relativePath + "]");
        }
        return list.get(0);
    }

    /**
     * Loads a JSON array and returns the element at the given zero-based index.
     */
    public static <T> T loadAt(String relativePath, Class<T> type, int index) {
        List<T> list = loadList(relativePath, type);
        if (index >= list.size()) {
            throw new IndexOutOfBoundsException(
                    "Index " + index + " out of bounds for test data file [" + relativePath +
                            "] which has " + list.size() + " elements");
        }
        return list.get(index);
    }
}