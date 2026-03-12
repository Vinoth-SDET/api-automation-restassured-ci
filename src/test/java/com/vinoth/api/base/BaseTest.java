package com.vinoth.api.base;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.vinoth.api.client.ApiClient;
import com.vinoth.api.config.ConfigManager;
import com.vinoth.api.utils.ExtentManager;
import io.qameta.allure.restassured.AllureRestAssured;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;

import java.lang.reflect.Method;

/**
 * BaseTest wires up two independent reporting pipelines:
 *
 * <ul>
 *   <li><b>Allure</b> — attached via {@code @Listeners(AllureTestNg.class)}.
 *       Captures HTTP traffic through {@link AllureRestAssured} filter in ApiClient.
 *       Output: target/allure-results/ (publish with mvn allure:report).</li>
 *   <li><b>ExtentReports</b> — driven by ThreadLocal ExtentTest nodes created in
 *       @BeforeMethod and flushed in @AfterSuite.
 *       Output: target/extent-reports/TestReport.html.</li>
 * </ul>
 * <p>
 * Both reports are uploaded as CI artifacts so recruiters can open either.
 */
@Listeners({io.qameta.allure.testng.AllureTestNg.class})
public abstract class BaseTest {

    private static final Logger log = LogManager.getLogger(BaseTest.class);

    // One ApiClient per thread - essential for parallel safety
    private static final ThreadLocal<ApiClient> CLIENT = new ThreadLocal<>();

    // One ExtentTest node per thread - essential for parallel-safe Extent logging
    private static final ThreadLocal<ExtentTest> EXTENT_TEST = new ThreadLocal<>();

    private static final ExtentReports extent = ExtentManager.getInstance();

    // -----------------------------------------------------------------------
    // Suite lifecycle
    // -----------------------------------------------------------------------

    @BeforeSuite(alwaysRun = true)
    public void globalSetup() {
        ConfigManager cfg = ConfigManager.get();
        log.info("Suite starting | env={} | baseUrl={}",
                System.getProperty("env", "qa"), cfg.baseUrl());
    }

    @AfterSuite(alwaysRun = true)
    public void globalTearDown() {
        extent.flush();
        log.info("ExtentReports flushed to target/extent-reports/TestReport.html");
    }

    // -----------------------------------------------------------------------
    // Method lifecycle
    // -----------------------------------------------------------------------

    @BeforeMethod(alwaysRun = true)
    public void setUp(Method method) {
        CLIENT.set(new ApiClient());

        // Create an Extent test node for this method
        String testName = getClass().getSimpleName() + " :: " + method.getName();
        ExtentTest test = extent.createTest(testName);
        EXTENT_TEST.set(test);

        log.info("[START] {}", testName);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        long dur = result.getEndMillis() - result.getStartMillis();
        String methodName = result.getMethod().getMethodName();
        String className = getClass().getSimpleName();

        ExtentTest test = EXTENT_TEST.get();

        switch (result.getStatus()) {
            case ITestResult.SUCCESS:
                test.log(Status.PASS, "Test PASSED");
                log.info("[PASS] {}.{} | {}ms", className, methodName, dur);
                break;

            case ITestResult.FAILURE:
                test.log(Status.FAIL, "Test FAILED: " + result.getThrowable());
                log.error("[FAIL] {}.{} | {}ms | {}",
                        className, methodName, dur, result.getThrowable().getMessage());
                break;

            case ITestResult.SKIP:
                test.log(Status.SKIP, "Test SKIPPED");
                log.warn("[SKIP] {}.{}", className, methodName);
                break;

            default:
                break;
        }

        // Clean up ThreadLocals to prevent memory leaks in parallel runs
        CLIENT.remove();
        EXTENT_TEST.remove();
    }

    // -----------------------------------------------------------------------
    // Accessors for sub-classes
    // -----------------------------------------------------------------------

    /**
     * Returns the thread-local ApiClient. Never instantiate ApiClient directly in tests.
     */
    protected ApiClient api() {
        return CLIENT.get();
    }

    /**
     * Returns the thread-local ExtentTest node for optional inline logging from tests.
     */
    protected ExtentTest extentTest() {
        return EXTENT_TEST.get();
    }
}