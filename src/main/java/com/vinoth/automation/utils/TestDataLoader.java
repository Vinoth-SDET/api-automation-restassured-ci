package com.vinoth.automation.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Loads JSON test data files from src/test/resources/testdata/
 * and deserialises them to typed POJOs.
 *
 * Usage:
 *   List<UserRequest> users = TestDataLoader.loadList(
 *       "testdata/users/valid-users.json", UserRequest.class);
 *
 *   UserRequest user = TestDataLoader.loadSingle(
 *       "testdata/users/valid-users.json", UserRequest.class);
 */
@Log4j2
public final class TestDataLoader {

    private static final ObjectMapper mapper = new ObjectMapper();

    private TestDataLoader() {}

    public static <T> List<T> loadList(String classpathPath, Class<T> type) {
        try (InputStream is = getStream(classpathPath)) {
            return mapper.readValue(is,
                    mapper.getTypeFactory().constructCollectionType(List.class, type));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load test data from: " + classpathPath, e);
        }
    }

    public static <T> T loadSingle(String classpathPath, Class<T> type) {
        try (InputStream is = getStream(classpathPath)) {
            return mapper.readValue(is, type);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load test data from: " + classpathPath, e);
        }
    }

    public static <T> T loadAs(String classpathPath, TypeReference<T> typeRef) {
        try (InputStream is = getStream(classpathPath)) {
            return mapper.readValue(is, typeRef);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load test data from: " + classpathPath, e);
        }
    }

    private static InputStream getStream(String path) {
        InputStream is = TestDataLoader.class.getClassLoader().getResourceAsStream(path);
        if (is == null) {
            throw new IllegalArgumentException("Test data file not found on classpath: " + path);
        }
        return is;
    }
}