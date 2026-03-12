package com.vinoth.api.base;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.vinoth.api.client.ApiClient;
import com.vinoth.api.utils.ExtentManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.PrintWriter;
import java.io.StringWriter;

public class BaseTest {

    private static final Logger log = LogManager.getLogger(BaseTest.class);

    // One ExtentReports for the whole suite
    private static final ExtentReports extent = ExtentManager.getInstance();

    // One ExtentTest node per test method, thread-safe
    private static final ThreadLocal<ExtentTest> TEST_NODE = new ThreadLocal<>();

    // One ApiClient per thread
    protected static final ThreadLocal<ApiClient> CLIENT = new ThreadLocal<>();

    // ── Suite lifecycle ───────────────────────────────────────────────────────

    @BeforeSuite(alwaysRun = true)
    public void suiteSetup() {
        log.info("Suite starting");
    }

    @AfterSuite(alwaysRun = true)
    public void suiteTearDown() {
        extent.flush();   // writes the HTML file to disk
        log.info("Suite complete — Extent report flushed");
        log.info("Report: {}/target/extent-reports/TestReport.html",
                System.getProperty("user.dir"));
    }

    // ── Test lifecycle ────────────────────────────────────────────────────────

    @BeforeMethod(alwaysRun = true)
    public void setUp(java.lang.reflect.Method method) {
        ApiClient client = new ApiClient();
        CLIENT.set(client);

        // Create a test node in the Extent report
        ExtentTest test = extent.createTest(
                method.getDeclaringClass().getSimpleName()
                + " » " + method.getName());
        TEST_NODE.set(test);

        log.info("Starting: {} | correlationId={}",
                method.getName(), client.getCorrelationId());
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        ExtentTest test = TEST_NODE.get();
        long duration = result.getEndMillis() - result.getStartMillis();

        switch (result.getStatus()) {
            case ITestResult.SUCCESS:
                test.log(Status.PASS,
                        "PASSED in " + duration + "ms");
                log.info("[PASS] {} | {}ms", result.getName(), duration);
                break;
            case ITestResult.FAILURE:
                test.log(Status.FAIL, "FAILED: " + result.getThrowable().getMessage());
                test.fail(result.getThrowable());
                log.error("[FAIL] {} | {}ms", result.getName(), duration);
                break;
            case ITestResult.SKIP:
                test.log(Status.SKIP, "SKIPPED");
                log.warn("[SKIP] {}", result.getName());
                break;
        }

        CLIENT.remove();
        TEST_NODE.remove();
    }

    // ── Helper for subclasses ─────────────────────────────────────────────────

    protected ApiClient api() {
        return CLIENT.get();
    }

    protected void log(String message) {
        TEST_NODE.get().log(Status.INFO, message);
    }
}
