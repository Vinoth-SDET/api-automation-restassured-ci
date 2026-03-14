package com.vinoth.automation.utils;

import com.vinoth.automation.config.ConfigManager;
import lombok.extern.log4j.Log4j2;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * TestNG retry analyzer — retries genuinely FAILED tests only.
 *
 * KEY DESIGN: TestNG creates ONE RetryAnalyzer instance per test method
 * invocation when attached via IAnnotationTransformer (RetryListener).
 * The counter is therefore naturally scoped per-method — no manual reset needed.
 *
 * Only retries on ITestResult.FAILURE.
 * Does NOT retry on SKIP or SUCCESS — this was the CI bug where passing
 * tests were being retried, causing @BeforeMethod to be skipped on the
 * retry path and NPEs on the 4th invocation.
 */
@Log4j2
public class RetryAnalyzer implements IRetryAnalyzer {

    private int currentRetryCount = 0;
    private final int maxRetryCount = ConfigManager.getInstance().getMaxRetries();

    @Override
    public boolean retry(ITestResult result) {
        // Only retry genuine failures — never retry passing or skipped tests
        if (result.getStatus() != ITestResult.FAILURE) {
            return false;
        }

        if (currentRetryCount < maxRetryCount) {
            currentRetryCount++;
            log.warn("Test FAILED [{}] — retry {}/{}",
                    result.getName(), currentRetryCount, maxRetryCount);
            return true;
        }

        log.info("Test [{}] exhausted {} retry attempts — marking FAILED",
                result.getName(), maxRetryCount);
        return false;
    }
}