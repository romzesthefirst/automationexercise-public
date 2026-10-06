package com.shangin.automationexercise.pages;

import org.openqa.selenium.By;

import com.shangin.automationexercise.base.BasePage;

public class PaymentDonePage extends BasePage {
    
    private static final By ORDER_PLACED_LABEL = By.cssSelector("[data-qa='order-placed']");
    private static final By CONTINUE_BUTTON = By.cssSelector("[data-qa='continue-button']");
    private static final By DOWNLOAD_INVICE_BUTTON = By.cssSelector("a[href*='/download_invoice/']");

    @Override
    public boolean isLoaded() {
        return isDisplayed(ORDER_PLACED_LABEL);
    }

    @Override
    public void waitUntilLoaded() {
        waitUntilVisible(ORDER_PLACED_LABEL);
        removeAds();
    }
    
    public HomePage continueButton() {
        navigate(CONTINUE_BUTTON);
        HomePage homePage = new HomePage();
        homePage.waitUntilLoaded();
        return homePage;
    }
    
    public void downloadInvoice() {
        click(DOWNLOAD_INVICE_BUTTON);
    }

}
