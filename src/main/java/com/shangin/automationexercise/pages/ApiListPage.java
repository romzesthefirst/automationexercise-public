package com.shangin.automationexercise.pages;

import com.shangin.automationexercise.base.BasePage;
import org.openqa.selenium.By;

public class ApiListPage extends BasePage {
    private static final By TITLE =
            By.xpath("//h2[contains(normalize-space(), 'APIs List for practice')]");

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
