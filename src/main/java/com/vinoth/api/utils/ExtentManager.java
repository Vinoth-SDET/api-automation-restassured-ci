package com.vinoth.api.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Singleton ExtentReports manager.
 *
 * <p>Produces a self-contained HTML report at target/extent-reports/TestReport.html.
 * Call ExtentManager.getInstance() to access the shared instance;
 * call flush() once in @AfterSuite to write the final file.
 */
public final class ExtentManager {

    private static final Logger log = LogManager.getLogger(ExtentManager.class);
    private static final String REPORT_PATH = "target/extent-reports/TestReport.html";

    private static volatile ExtentReports instance;

    private ExtentManager() {}

    public static ExtentReports getInstance() {
        if (instance == null) {
            synchronized (ExtentManager.class) {
                if (instance == null) {
                    instance = createInstance();
                }
            }
        }
        return instance;
    }

    private static ExtentReports createInstance() {
        ExtentSparkReporter spark = new ExtentSparkReporter(REPORT_PATH);
        spark.config().setTheme(Theme.DARK);
        spark.config().setDocumentTitle("API Automation Report");
        spark.config().setReportName("Enterprise API Test Suite");
        spark.config().setEncoding("UTF-8");

        ExtentReports extent = new ExtentReports();
        extent.attachReporter(spark);
        extent.setSystemInfo("Framework", "RestAssured + TestNG");
        extent.setSystemInfo("Author", "Vinoth M");
        extent.setSystemInfo("Environment", System.getProperty("env", "qa"));

        log.info("ExtentReports initialised | output={}", REPORT_PATH);
        return extent;
    }
}