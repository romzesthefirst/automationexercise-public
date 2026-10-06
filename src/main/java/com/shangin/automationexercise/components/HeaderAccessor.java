package com.shangin.automationexercise.components;

import org.openqa.selenium.By;


public class HeaderAccessor {

    private static final By HEADER = By.id("header");

    public HeaderComponent header() {
        
        return new HeaderComponent(HEADER);
    }
}