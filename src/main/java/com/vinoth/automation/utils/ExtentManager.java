package com.vinoth.automation.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.vinoth.automation.config.ConfigManager;
import lombok.extern.log4j.Log4j2;
import org.testng.ITestResult;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Thread-safe singleton ExtentReports manager.
 *
 * Provides startTest / endTest lifecycle methods consumed by BaseTest.
 * Each parallel thread gets its own ExtentTest node via ThreadLocal —
 * no cross-thread report contamination.
 *
 * Report is written to: target/extent-reports/TestReport.html
 */
@Log4j2
public final class ExtentManager {

    private static final String REPORT_DIR  = "target/extent-reports/";
    private static final String REPORT_FILE = REPORT_DIR + "TestReport.html";

    private static volatile ExtentReports    extent;
    private static final ThreadLocal<ExtentTest> testNode = new ThreadLocal<>();

    private ExtentManager() {}

    // ── Singleton initialisation ───────────────────────────────────────────────

    public static ExtentReports getInstance() {
        if (extent == null) {
            synchronized (ExtentManager.class) {
                if (extent == null) {
                    extent = createReports();
                    log.info("ExtentReports initialised → {}", REPORT_FILE);
                }
            }
        }
        return extent;
    }

    private static ExtentReports createReports() {
        new File(REPORT_DIR).mkdirs();

        ExtentSparkReporter spark = new ExtentSparkReporter(REPORT_FILE);
        spark.config().setTheme(Theme.DARK);
        spark.config().setDocumentTitle("API Automation Report");
        spark.config().setReportName("Enterprise API Automation Suite");
        spark.config().setTimeStampFormat("dd MMM yyyy HH:mm:ss");

        ExtentReports reports = new ExtentReports();
        reports.attachReporter(spark);
        reports.setSystemInfo("Environment", System.getProperty("env", "qa"));
        reports.setSystemInfo("Java Version",  System.getProperty("java.version"));
        reports.setSystemInfo("OS",            System.getProperty("os.name"));
        reports.setSystemInfo("Executed By",   System.getProperty("user.name", "CI"));
        reports.setSystemInfo("Timestamp",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss")));
        return reports;
    }

    // ── Test lifecycle — called by BaseTest ────────────────────────────────────

    /**
     * Start a new test node for the current thread.
     * Called in @BeforeMethod by BaseTest.
     */
    public static void startTest(String testName) {
        ExtentTest node = getInstance().createTest(testName);
        testNode.set(node);
        log.debug("ExtentReports: started test node [{}]", testName);
    }

    /**
     * End the current test node, set status from ITestResult, and flush.
     * Called in @AfterMethod by BaseTest.
     */
    public static void endTest(ITestResult result) {
        ExtentTest node = testNode.get();
        if (node == null) {
            log.warn("ExtentReports: endTest called but no active test node found");
            return;
        }

        switch (result.getStatus()) {
            case ITestResult.SUCCESS -> node.log(Status.PASS,  "Test PASSED");
            case ITestResult.FAILURE -> {
                node.log(Status.FAIL, "Test FAILED");
                if (result.getThrowable() != null) {
                    node.log(Status.FAIL, result.getThrowable());
                }
            }
            case ITestResult.SKIP   -> node.log(Status.SKIP,  "Test SKIPPED");
            default                 -> node.log(Status.INFO,  "Test status: " + result.getStatus());
        }

        testNode.remove();       // prevent ThreadLocal memory leak
        getInstance().flush();   // write to disk immediately
        log.debug("ExtentReports: flushed report after [{}]", result.getName());
    }

    // ── Access current node (for inline logging from tests if needed) ──────────

    /**
     * Returns the ExtentTest node for the currently running thread.
     * Rarely needed directly — tests log via @Step / Allure instead.
     */
    public static ExtentTest getTest() {
        return testNode.get();
    }

    /**
     * Log an info message on the current test node.
     * Safe to call — silently skips if no node is active.
     */
    public static void log(String message) {
        ExtentTest node = testNode.get();
        if (node != null) {
            node.log(Status.INFO, message);
        }
    }
}