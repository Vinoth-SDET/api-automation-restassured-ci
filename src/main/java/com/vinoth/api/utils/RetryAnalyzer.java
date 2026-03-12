package com.vinoth.api.utils;

import com.vinoth.api.config.ConfigManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.IAnnotationTransformer;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;
import org.testng.annotations.ITestAnnotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TestNG retry analyzer using ConcurrentHashMap per test method name.
 *
 * Uses a static map keyed on thread+method so each test tracks its own
 * retry count independently — prevents ghost retries on already-passed tests.
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private static final Logger log = LogManager.getLogger(RetryAnalyzer.class);

    private static final int MAX;

    static {
        int retries;
        try {
            retries = ConfigManager.get().retries();
        } catch (Exception e) {
            retries = 2;
        }
        MAX = retries;
    }

    // Key = threadId + methodName to isolate each test's retry count
    private static final ConcurrentHashMap<String, AtomicInteger> COUNTERS
            = new ConcurrentHashMap<>();

    @Override
    public boolean retry(ITestResult result) {
        String key = Thread.currentThread().getId()
                + "-" + result.getMethod().getMethodName();

        AtomicInteger counter = COUNTERS.computeIfAbsent(key, k -> new AtomicInteger(0));
        int attempt = counter.incrementAndGet();

        if (attempt <= MAX) {
            log.warn("Retrying failed test [{}/{}]: {} | cause: {}",
                    attempt, MAX,
                    result.getMethod().getMethodName(),
                    result.getThrowable() != null
                            ? result.getThrowable().getMessage()
                            : "unknown");
            return true;
        }

        log.error("Test permanently failed after {} attempt(s): {}",
                MAX, result.getMethod().getMethodName());
        COUNTERS.remove(key); // clean up
        return false;
    }

    // ── Listener: attaches RetryAnalyzer to every @Test automatically ─────────

    public static class RetryListener implements IAnnotationTransformer {

        @Override
        public void transform(ITestAnnotation annotation,
                              Class          testClass,
                              Constructor    testConstructor,
                              Method         testMethod) {
            annotation.setRetryAnalyzer(RetryAnalyzer.class);
        }
    }
}