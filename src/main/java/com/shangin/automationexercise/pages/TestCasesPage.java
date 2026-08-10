package com.shangin.automationexercise.pages;

import org.openqa.selenium.By;

import com.shangin.automationexercise.base.BasePage;

public class TestCasesPage extends BasePage {

    private static final By TEST_CASES_HEADER = By.cssSelector("h2 b");

    @Override
    public boolean isLoaded() {
        return isDisplayed(TEST_CASES_HEADER);
    }

    @Override
    public void waitUntilLoaded() {
        waitUntilVisible(TEST_CASES_HEADER);
    }

    public boolean isOpened() {
        return getCurrentUrl().endsWith("/test_cases");
    }

}
