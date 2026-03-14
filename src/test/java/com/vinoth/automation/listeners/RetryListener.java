package com.vinoth.automation.listeners;

import com.vinoth.automation.utils.RetryAnalyzer;
import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * TestNG listener that attaches RetryAnalyzer to every @Test method.
 *
 * Registered in testng-suites XML — zero @Test annotation boilerplate.
 * RetryAnalyzer.retry() only triggers on genuine FAILURE status,
 * so passing tests are never retried and @BeforeMethod is not skipped.
 */
public class RetryListener implements IAnnotationTransformer {

    @Override
    public void transform(ITestAnnotation annotation, Class testClass,
                          Constructor testConstructor, Method testMethod) {
        annotation.setRetryAnalyzer(RetryAnalyzer.class);
    }
}