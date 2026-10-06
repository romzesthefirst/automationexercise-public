package com.shangin.automationexercise.base;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;

import com.shangin.automationexercise.driver.DriverFactory;
import com.shangin.automationexercise.driver.DriverManager;
import com.shangin.automationexercise.listeners.TestListener;
import com.shangin.automationexercise.steps.UiProductSteps;

@Listeners(TestListener.class)
public abstract class BaseTest {

    protected final UiProductSteps uiProductSteps = new UiProductSteps();

    @BeforeMethod(alwaysRun = true)
    public void setup() {
        DriverManager.setDriver(DriverFactory.createDriver());
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        DriverManager.quitDriver();
    }
}
