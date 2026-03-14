package com.vinoth.automation.base;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import lombok.extern.log4j.Log4j2;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

/**
 * Base class for WireMock-backed contract tests.
 * Starts a WireMock server on a dynamic port before the test class runs,
 * stops it after all tests in the class complete.
 * <p>
 * Subclasses access the stub URL via stubBaseUrl().
 * Define stubs by overriding registerStubs().
 */
@Log4j2
public abstract class WireMockBase {

    private WireMockServer wireMockServer;

    @BeforeClass(alwaysRun = true)
    public void startWireMock() {
        wireMockServer = new WireMockServer(
                WireMockConfiguration.wireMockConfig().dynamicPort());
        wireMockServer.start();
        log.info("WireMock started on port {}", wireMockServer.port());
        registerStubs();
    }

    @AfterClass(alwaysRun = true)
    public void stopWireMock() {
        if (wireMockServer != null && wireMockServer.isRunning()) {
            wireMockServer.stop();
            log.info("WireMock stopped");
        }
    }

    /**
     * Override in subclasses to register WireMock stubs.
     * Called after the server starts, before any tests run.
     */
    protected abstract void registerStubs();

    protected String stubBaseUrl() {
        return "http://localhost:" + wireMockServer.port();
    }

    protected WireMockServer wireMock() {
        return wireMockServer;
    }
}