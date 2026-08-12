package com.shangin.automationexercise.cucumber.hooks;

import com.shangin.automationexercise.driver.DriverFactory;
import com.shangin.automationexercise.driver.DriverManager;

import io.cucumber.java.After;
import io.cucumber.java.Before;

public class UiHooks {

    @Before("@ui")
    public void setUpDriver() {
        DriverManager.setDriver(DriverFactory.createDriver());
    }

    @After("@ui")
    public void tearDownDriver() {
        DriverManager.quitDriver();
    }
}
