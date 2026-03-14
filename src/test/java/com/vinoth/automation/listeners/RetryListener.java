package com.vinoth.automation.listeners;

import com.vinoth.automation.utils.RetryAnalyzer;
import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * TestNG listener that attaches RetryAnalyzer to every @Test method.
 * Registered in testng-suites/*.xml — zero annotation boilerplate in test classes.
 * <p>
 * Register in suite XML:
 * <listeners>
 * <listener class-name="com.vinoth.automation.listeners.RetryListener"/>
 * </listeners>
 */
public class RetryListener implements IAnnotationTransformer {

    @Override
    public void transform(ITestAnnotation annotation, Class testClass,
                          Constructor testConstructor, Method testMethod) {
        annotation.setRetryAnalyzer(RetryAnalyzer.class);
    }
}