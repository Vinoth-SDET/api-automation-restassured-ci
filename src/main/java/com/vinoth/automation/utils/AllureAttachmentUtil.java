package com.vinoth.automation.utils;

import io.qameta.allure.Allure;
import io.restassured.response.Response;
import lombok.extern.log4j.Log4j2;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

/**
 * Utility for attaching request/response details to Allure reports.
 *
 * Uses ByteArrayInputStream because Allure 2.27 addAttachment() accepts
 * (name, mimeType, InputStream, fileExtension) — not byte[] directly.
 */
@Log4j2
public final class AllureAttachmentUtil {

    private AllureAttachmentUtil() {}

    public static void attachResponseBody(Response response) {
        try {
            String body = response.asPrettyString();
            byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
            Allure.addAttachment(
                    "Response Body [HTTP " + response.statusCode() + "]",
                    "application/json",
                    new ByteArrayInputStream(bytes),
                    ".json");
        } catch (Exception e) {
            log.warn("Could not attach response body to Allure: {}", e.getMessage());
        }
    }

    public static void attachText(String name, String content) {
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
        Allure.addAttachment(name, "text/plain",
                new ByteArrayInputStream(bytes), ".txt");
    }

    public static void attachJson(String name, String json) {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        Allure.addAttachment(name, "application/json",
                new ByteArrayInputStream(bytes), ".json");
    }
}