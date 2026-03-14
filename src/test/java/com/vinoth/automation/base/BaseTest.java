package com.vinoth.automation.base;

import com.vinoth.automation.client.ApiClient;
import com.vinoth.automation.utils.ExtentManager;
import io.qameta.allure.Allure;
import lombok.extern.log4j.Log4j2;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import java.lang.reflect.Method;

/**
 * Thread-safe base test class.
 *
 * Each parallel test thread owns its isolated ApiClient via ThreadLocal —
 * no shared mutable state, no race conditions.
 *
 * Two accessor methods are provided for backward compatibility:
 *   client() — preferred, new naming convention
 *   api()    — legacy alias; existing test classes continue to compile unchanged
 *
 * Lifecycle:
 *   @BeforeSuite  → init ExtentReports singleton
 *   @BeforeMethod → new ApiClient per thread + start Extent/Allure test node
 *   @AfterMethod  → log result + remove ThreadLocal (prevent memory leak)
 *   @AfterSuite   → flush and close reports
 */
@Log4j2
public abstract class BaseTest {

    private static final ThreadLocal<ApiClient> CLIENT_HOLDER = new ThreadLocal<>();

    @BeforeSuite(alwaysRun = true)
    public void initSuite() {
        ExtentManager.getInstance(); // eager init — thread-safe singleton
        log.info("═══════════════════════════════════════════════");
        log.info("  Test suite initialised | env={}",
                System.getProperty("env", "qa"));
        log.info("═══════════════════════════════════════════════");
    }

    @BeforeMethod(alwaysRun = true)
    public void setUp(Method method) {
        ApiClient apiClient = new ApiClient();
        CLIENT_HOLDER.set(apiClient);

        String testName = getClass().getSimpleName() + "." + method.getName();
        log.info("▶ Starting: {}", testName);

        ExtentManager.startTest(testName);

        Allure.label("thread", String.valueOf(Thread.currentThread().getId()));
        Allure.label("host",   System.getProperty("env", "qa"));
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        // Log result to ExtentReports before removing ThreadLocal
        ExtentManager.endTest(result);

        // IMPORTANT: remove from ThreadLocal to prevent memory leak in parallel runs
        CLIENT_HOLDER.remove();

        String status = switch (result.getStatus()) {
            case ITestResult.SUCCESS -> "✔ PASSED";
            case ITestResult.FAILURE -> "✘ FAILED";
            case ITestResult.SKIP    -> "⊘ SKIPPED";
            default                  -> "? UNKNOWN";
        };
        log.info("{} — {}.{}",
                status,
                getClass().getSimpleName(),
                result.getMethod().getMethodName());
    }

    @AfterSuite(alwaysRun = true)
    public void tearDownSuite() {
        log.info("Test suite complete. Reports written.");
    }

    // ── ApiClient accessors ────────────────────────────────────────────────────

    /**
     * Preferred accessor — returns the ApiClient for the current thread.
     * Use this in new test classes.
     */
    protected ApiClient client() {
        ApiClient c = CLIENT_HOLDER.get();
        if (c == null) {
            throw new IllegalStateException(
                    "ApiClient not initialised for thread " + Thread.currentThread().getId() +
                            ". Ensure setUp() has run (@BeforeMethod). " +
                            "If calling from @BeforeClass, move service init to @BeforeMethod.");
        }
        return c;
    }

    /**
     * Legacy alias for client() — keeps existing test classes compiling
     * without any changes.
     *
     * @deprecated Use client() in new code. This alias will be removed in v3.0.
     */
    @Deprecated(since = "2.0", forRemoval = true)
    protected ApiClient api() {
        return client();
    }
}