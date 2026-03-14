package com.vinoth.automation.utils;

import com.vinoth.automation.config.ConfigManager;
import lombok.extern.log4j.Log4j2;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * TestNG retry analyzer.
 *
 * Registered as a TestNG listener in testng-suites/*.xml — NOT via
 * @Test(retryAnalyzer=...) annotation. This means zero boilerplate
 * in any test class; retry policy is controlled from one place.
 *
 * Max retry count is driven by ConfigManager → config/{env}.properties:
 *   max.retries = 2
 *
 * Only retries on genuine test failures (FAILURE status).
 * Does not retry on assertion errors that indicate real bugs.
 */
@Log4j2
public class RetryAnalyzer implements IRetryAnalyzer {

    private int currentRetryCount = 0;

    // Read max retries from ConfigManager using the correct typed getter
    private final int maxRetryCount = ConfigManager.getInstance().getMaxRetries();

    @Override
    public boolean retry(ITestResult result) {
        if (currentRetryCount < maxRetryCount) {
            currentRetryCount++;
            log.warn("Retrying test [{}] — attempt {}/{}",
                    result.getName(), currentRetryCount, maxRetryCount);
            return true;
        }
        log.info("Test [{}] exhausted {} retry attempt(s) — marking FAILED",
                result.getName(), maxRetryCount);
        return false;
    }
}