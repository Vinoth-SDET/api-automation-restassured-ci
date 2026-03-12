package com.vinoth.api.base;

import com.vinoth.api.client.ApiClient;
import com.vinoth.api.config.ConfigManager;
import io.qameta.allure.testng.AllureTestNg;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.lang.reflect.Method;

/**
 * Thread-safe base class for all API test classes.
 *
 * <h3>Key design decisions:</h3>
 * <ul>
 *   <li><strong>ThreadLocal&lt;ApiClient&gt;</strong> — each parallel thread gets its own
 *       isolated HTTP client with a unique correlation-ID header. Without this, parallel
 *       threads share state and produce race conditions that are nearly impossible to debug.</li>
 *   <li><strong>@BeforeSuite</strong> — one-time global setup: config validation and Allure
 *       environment metadata. Runs once per JVM, not once per thread.</li>
 *   <li><strong>@BeforeMethod / @AfterMethod</strong> — per-test lifecycle: initialise and
 *       clean up the ThreadLocal client. {@code CLIENT.remove()} is mandatory to prevent
 *       memory leaks in long-running test suites.</li>
 *   <li><strong>@Listeners(AllureTestNg)</strong> — hooks Allure into the TestNG lifecycle
 *       to capture attachments, steps, and status automatically.</li>
 * </ul>
 */
@Listeners({AllureTestNg.class})
public abstract class BaseTest {

    // One ApiClient per thread — the single most critical pattern for parallel safety
    private static final ThreadLocal<ApiClient> CLIENT = new ThreadLocal<>();

    private static final Logger log = LogManager.getLogger(BaseTest.class);

    // ── Suite-level setup (runs once per JVM) ─────────────────────────────────

    @BeforeSuite(alwaysRun = true)
    public void globalSetup() {
        ConfigManager cfg = ConfigManager.get();
        log.info("═══ Suite starting | env={} | baseUrl={} | threads={} ═══",
                cfg.activeEnv(), cfg.baseUrl(), cfg.threads());

        // Write environment info to allure-results/environment.properties
        // so it appears in the Allure report's "Environment" widget
        writeAllureEnvironment(cfg);
    }

    @AfterSuite(alwaysRun = true)
    public void globalTearDown() {
        log.info("═══ Suite complete ═══");
    }

    // ── Method-level lifecycle (runs per test, per thread) ────────────────────

    @BeforeMethod(alwaysRun = true)
    public void setUp(Method method) {
        CLIENT.set(new ApiClient());
        log.info("[START] {}.{} | correlationId={}",
                getClass().getSimpleName(), method.getName(), CLIENT.get().getCorrelationId());
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        String status   = result.isSuccess() ? "PASS" : "FAIL";
        long   duration = result.getEndMillis() - result.getStartMillis();

        log.info("[{}] {}.{} | {}ms | correlationId={}",
                status,
                getClass().getSimpleName(),
                result.getMethod().getMethodName(),
                duration,
                CLIENT.get() != null ? CLIENT.get().getCorrelationId() : "N/A");

        CLIENT.remove(); // MANDATORY: prevents ThreadLocal memory leaks
    }

    // ── Protected API ─────────────────────────────────────────────────────────

    /**
     * Returns the {@link ApiClient} for the current thread.
     * Tests access their HTTP client exclusively through this method —
     * never instantiate ApiClient directly in a test class.
     */
    protected ApiClient api() {
        ApiClient client = CLIENT.get();
        if (client == null) {
            throw new IllegalStateException(
                    "ApiClient is not initialised for this thread. " +
                            "Ensure setUp() runs before calling api().");
        }
        return client;
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private void writeAllureEnvironment(ConfigManager cfg) {
        try {
            // Write allure-results/environment.properties for the report widget
            java.nio.file.Path dir = java.nio.file.Paths.get("allure-results");
            java.nio.file.Files.createDirectories(dir);
            java.nio.file.Path envFile = dir.resolve("environment.properties");
            java.util.Properties envProps = new java.util.Properties();
            envProps.setProperty("Environment", cfg.activeEnv());
            envProps.setProperty("Base.URL",    cfg.baseUrl());
            envProps.setProperty("Java.Version", System.getProperty("java.version"));
            envProps.setProperty("Threads",      String.valueOf(cfg.threads()));
            try (java.io.OutputStream out = java.nio.file.Files.newOutputStream(envFile)) {
                envProps.store(out, "Allure environment properties");
            }
            log.debug("Allure environment.properties written to {}", envFile.toAbsolutePath());
        } catch (Exception e) {
            log.warn("Could not write Allure environment.properties: {}", e.getMessage());
        }
    }
}