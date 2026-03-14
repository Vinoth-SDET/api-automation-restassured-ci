package com.vinoth.automation.listeners;

import io.qameta.allure.Allure;
import lombok.extern.log4j.Log4j2;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * TestNG listener that enriches Allure reports with environment context.
 * Adds thread ID and environment label to each test result.
 */
@Log4j2
public class AllureListener implements ITestListener {

    @Override
    public void onTestStart(ITestResult result) {
        Allure.label("thread", String.valueOf(Thread.currentThread().getId()));
        Allure.label("env", System.getProperty("env", "qa"));
        log.debug("Allure: test started [{}]", result.getName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        Throwable t = result.getThrowable();
        if (t != null) {
            Allure.addAttachment("Failure Cause", t.getMessage() != null ? t.getMessage() : t.toString());
        }
        log.debug("Allure: test failed [{}]", result.getName());
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        log.debug("Allure: test skipped [{}]", result.getName());
    }
}