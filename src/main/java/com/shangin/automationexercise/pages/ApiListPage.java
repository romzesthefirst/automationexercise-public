package com.shangin.automationexercise.pages;

import org.openqa.selenium.By;

import com.shangin.automationexercise.base.BasePage;

public class ApiListPage extends BasePage {
    private static final By TITLE = By.xpath("//h2[contains(normalize-space(), 'APIs List for practice')]");

    @Override
    public boolean isLoaded() {
        return isDisplayed(TITLE);
    }

    @Override
    public void waitUntilLoaded() {
        wait.until(ignored -> isLoaded());
        removeAds();
    }
}
