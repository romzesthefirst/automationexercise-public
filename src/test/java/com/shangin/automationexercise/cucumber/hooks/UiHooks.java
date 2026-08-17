package com.shangin.automationexercise.cucumber.hooks;

import java.io.ByteArrayInputStream;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import com.shangin.automationexercise.driver.DriverFactory;
import com.shangin.automationexercise.driver.DriverManager;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.qameta.allure.Allure;

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

        WebDriver driver = DriverManager.getDriver();

        Allure.addAttachment("Current URL", "text/plain", driver.getCurrentUrl());

        byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);

        Allure.addAttachment(
                "Failure screenshot",
                "image/png",
                new ByteArrayInputStream(screenshot),
                ".png");
    }

    @After(value = "@ui", order = 1)
    public void tearDownDriver() {
        DriverManager.quitDriver();
    }
}
