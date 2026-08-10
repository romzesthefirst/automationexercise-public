package com.shangin.automationexercise.listeners;

import org.openqa.selenium.WebDriver;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.shangin.automationexercise.driver.DriverManager;

import io.qameta.allure.Allure;

public class TestListener implements ITestListener {

    @Override
    public void onTestFailure(ITestResult result) {
        WebDriver driver = DriverManager.getDriver();
        Allure.addAttachment("Current URL", "text/plain", driver.getCurrentUrl());
    }
}
