package com.shangin.automationexercise.pages;

import com.shangin.automationexercise.base.BasePage;
import org.openqa.selenium.By;

public class AccountDeletedPage extends BasePage {

    private static final By ACCOUNT_DELETED_MESSAGE = By.cssSelector("[data-qa='account-deleted']");
    private static final By CONTINUE_BUTTON = By.cssSelector("[data-qa='continue-button']");

    public boolean isAccountDeleted() {
        return isDisplayed(ACCOUNT_DELETED_MESSAGE);
    }

    public HomePage continueShopping() {
        navigate(CONTINUE_BUTTON);
        HomePage page = new HomePage();
        page.waitUntilLoaded();
        return page;
    }

    public String getAccountDeletedMessage() {
        return waitUntilVisible(ACCOUNT_DELETED_MESSAGE).getText();
    }

    @Override
    public boolean isLoaded() {
        return isDisplayed(ACCOUNT_DELETED_MESSAGE);
    }

    @Override
    public void waitUntilLoaded() {
        waitUntilVisible(ACCOUNT_DELETED_MESSAGE);
    }
}
