package com.shangin.automationexercise.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;

import com.shangin.automationexercise.base.BasePage;
import com.shangin.automationexercise.model.CardDetails;

public class PaymentPage extends BasePage {

    private static final By PAYMENT_HEADING = By.cssSelector(".heading");
    private static final By CARD_NAME_ON_INPUT = By.cssSelector("input[data-qa='name-on-card']");
    private static final By CARD_NUMBER_INPUT = By.cssSelector("input[data-qa='card-number']");
    private static final By CARD_CVC = By.cssSelector("input[data-qa='cvc']");
    private static final By CARD_EXPIRY_MONTH = By.cssSelector("input[data-qa='expiry-month']");
    private static final By CARD_EXPIRY_YEAR = By.cssSelector("input[data-qa='expiry-year']");
    private static final By PAY_AND_CONFIRM_BUTTON = By.cssSelector("button[data-qa='pay-button']");
    private static final By SUCCESS_MESSAGE = By.cssSelector("#success_message .alert-success");

    @Override
    public boolean isLoaded() {
        return isDisplayed(PAYMENT_HEADING);
    }

    @Override
    public void waitUntilLoaded() {
        waitUntilVisible(PAYMENT_HEADING);
        removeAds();
    }

    public void fillPaymentDetails(CardDetails card) {
        type(CARD_NAME_ON_INPUT, card.nameOnCard());
        type(CARD_NUMBER_INPUT, card.number());
        type(CARD_CVC, card.cvc());
        type(CARD_EXPIRY_MONTH, card.expirationMonth());
        type(CARD_EXPIRY_YEAR, card.expirationYear());
    }

    public String payAndGetResultMessage() {
        // System.out.println(driver.getCurrentUrl());
        // System.out.println(find(By.id("success_message")).getAttribute("class"));
        // click(PAY_AND_CONFIRM_BUTTON);
        // System.out.println(driver.getCurrentUrl());
        // System.out.println(find(By.id("success_message")).getAttribute("class"));
        // System.out.println(driver.getCurrentUrl());
        // return waitUntilVisible(SUCCESS_MESSAGE).getText();
        JavascriptExecutor js = (JavascriptExecutor) driver;

        js.executeScript("""
                    sessionStorage.removeItem('orderSuccessMessage');

                    const target = document.querySelector('#success_message');

                    if (!target) {
                        throw new Error('#success_message not found');
                    }

                    const observer = new MutationObserver(() => {
                        const style = window.getComputedStyle(target);

                        const visible =
                            style.display !== 'none'
                            && style.visibility !== 'hidden'
                            && target.offsetParent !== null;

                        if (visible) {
                            sessionStorage.setItem(
                                'orderSuccessMessage',
                                target.innerText.trim()
                            );

                            observer.disconnect();
                        }
                    });

                    observer.observe(target, {
                        attributes: true,
                        attributeFilter: ['class', 'style']
                    });
                """);

        click(PAY_AND_CONFIRM_BUTTON);

        String message = (String) js
                .executeScript("return sessionStorage.getItem('orderSuccessMessage');");

        if (message == null) {
            throw new AssertionError("Order success message was not displayed before redirect");
        }

        return message;
    }

    public void waitUntilSuccessMessageVisible() {
        waitUntilVisible(SUCCESS_MESSAGE);
    }

    public String getSuccessMessage() {
        return getText(SUCCESS_MESSAGE);
    }

}
