package com.shangin.automationexercise.components;

import org.openqa.selenium.By;

import com.shangin.automationexercise.driver.DriverManager;

public class HeaderAccessor {

    private static final By HEADER = By.id("header");

    public HeaderComponent header() {
        
        return new HeaderComponent(DriverManager.getDriver().findElement(HEADER));
    }
}