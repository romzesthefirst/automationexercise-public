package com.shangin.automationexercise.pages;

import org.openqa.selenium.By;

import com.shangin.automationexercise.base.BasePage;

public class AccountCreatedPage extends BasePage {

    private static final By ACCOUNT_CREATED_MESSAGE = By.cssSelector("[data-qa='account-created']");
    private static final By CONTINUE_BUTTON = By.cssSelector("[data-qa='continue-button']");

    public boolean isAccountCreated() {
        return isDisplayed(ACCOUNT_CREATED_MESSAGE);
    }

    public HomePage continueShopping() {
        waitUntilClickable(CONTINUE_BUTTON).click();
        return new HomePage();
    }

    public String getAccountCreatedMessage() {
        return waitUntilVisible(ACCOUNT_CREATED_MESSAGE).getText();
    }

    @Override
    public void waitUntilLoaded() {
        waitUntilVisible(ACCOUNT_CREATED_MESSAGE);
        removeAds();
    }

    @Override
    public boolean isLoaded() {
        return isDisplayed(ACCOUNT_CREATED_MESSAGE);
    }
}
