package com.shangin.automationexercise.listeners;

import java.io.ByteArrayInputStream;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import com.shangin.automationexercise.driver.DriverManager;

import io.qameta.allure.Allure;
import io.qameta.allure.model.Status;
import io.qameta.allure.model.TestResult;
import io.qameta.allure.listener.TestLifecycleListener;

public class AllureTestLifecycleListener implements TestLifecycleListener {

    @Override
    public void beforeTestStop(TestResult result) {

        if (!shouldCaptureScreenshot(result.getStatus()) || !DriverManager.hasDriver()) {
            return;
        }

        WebDriver driver = DriverManager.getDriver();

        if (driver == null) {
            return;
        }

        byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);

        Allure.addAttachment(
                "Screenshot on failure",
                "image/png",
                new ByteArrayInputStream(screenshot),
                ".png");

        Allure.addAttachment("Current URL", "text/plain", driver.getCurrentUrl());
    }

    private boolean shouldCaptureScreenshot(Status status) {
        return status == Status.FAILED || status == Status.BROKEN;
    }
}
