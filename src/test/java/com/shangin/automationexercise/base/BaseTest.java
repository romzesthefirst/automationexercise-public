package com.shangin.automationexercise.base;

import com.shangin.automationexercise.driver.DriverFactory;
import com.shangin.automationexercise.driver.DriverManager;
import com.shangin.automationexercise.listeners.TestListener;
import com.shangin.automationexercise.steps.UiProductSteps;
import com.shangin.automationexercise.support.TestRandom;
import io.qameta.allure.Allure;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;

@Listeners(TestListener.class)
public abstract class BaseTest {

    protected final UiProductSteps uiProductSteps = new UiProductSteps();

    @BeforeMethod(alwaysRun = true)
    public void setup() {
        initializeRandomData();
        DriverManager.setDriver(DriverFactory.createDriver());
    }

    private void initializeRandomData() {
        TestRandom.begin(
                message -> {
                    System.out.println(message);
                    Allure.addAttachment("Test selection", "text/plain", message);
                });
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        try {
            DriverManager.quitDriver();
        } finally {
            TestRandom.clear();
        }
    }
}
