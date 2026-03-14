package com.vinoth.automation.listeners;

import com.vinoth.automation.utils.ExtentManager;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * TestNG listener that drives ExtentReports lifecycle.
 * Can be used as an alternative to BaseTest @BeforeMethod/@AfterMethod wiring.
 * Register in testng-suites/*.xml alongside RetryListener.
 */
public class ExtentListener implements ITestListener {

    @Override
    public void onTestStart(ITestResult result) {
        ExtentManager.startTest(
                result.getTestClass().getRealClass().getSimpleName()
                        + "." + result.getName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentManager.endTest(result);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        ExtentManager.endTest(result);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        ExtentManager.endTest(result);
    }
}