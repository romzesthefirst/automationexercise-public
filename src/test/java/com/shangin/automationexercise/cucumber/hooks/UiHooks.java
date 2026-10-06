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

    @After(value = "@ui", order = 2)
    public void addFailureInfo(Scenario scenario) {

        if (!scenario.isFailed() || !DriverManager.hasDriver()) {
            return;
        }

        BrowserFailureAttachments.captureCurrentTest();
    }

    @After(value = "@ui", order = 1)
    public void tearDownDriver() {
        DriverManager.quitDriver();
    }
}
