package com.shangin.automationexercise.cucumber.hooks;

import com.shangin.automationexercise.driver.DriverFactory;
import com.shangin.automationexercise.driver.DriverManager;
import com.shangin.automationexercise.listeners.BrowserFailureAttachments;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

public class UiHooks {

    @Before("@ui")
    public void setUpDriver() {
        DriverManager.setDriver(DriverFactory.createDriver());
    }

    @After(order = 3)
    public void recordEnvironment(Scenario scenario) {
        io.qameta.allure.Allure.getLifecycle()
                .updateTestCase(
                        scenario.getId(),
                        com.shangin.automationexercise.listeners.ReportMetadata::enrich);
    }

    @After(value = "@ui", order = 2)
    public void addFailureInfo(Scenario scenario) {

        if (!scenario.isFailed() || !DriverManager.hasDriver()) {
            return;
        }

        // During an Allure hook fixture, the thread context contains the fixture UUID.
        // Cucumber's scenario ID is the UUID used by the Allure scenario result.
        BrowserFailureAttachments.captureTest(scenario.getId());
    }

    @After(value = "@ui", order = 1)
    public void tearDownDriver() {
        DriverManager.quitDriver();
    }
}
