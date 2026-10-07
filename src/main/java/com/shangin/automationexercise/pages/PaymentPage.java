package com.shangin.automationexercise.pages;

import com.shangin.automationexercise.base.BasePage;
import com.shangin.automationexercise.model.CardDetails;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;

public class PaymentPage extends BasePage {

    private static final By CARD_NAME_ON_INPUT = By.cssSelector("input[data-qa='name-on-card']");
    private static final By CARD_NUMBER_INPUT = By.cssSelector("input[data-qa='card-number']");
    private static final By CARD_CVC = By.cssSelector("input[data-qa='cvc']");
    private static final By CARD_EXPIRY_MONTH = By.cssSelector("input[data-qa='expiry-month']");
    private static final By CARD_EXPIRY_YEAR = By.cssSelector("input[data-qa='expiry-year']");
    private static final By PAY_AND_CONFIRM_BUTTON = By.cssSelector("button[data-qa='pay-button']");
    private static final By SUCCESS_MESSAGE = By.cssSelector("#success_message .alert-success");

    @Override
    public boolean isLoaded() {
        return isDisplayed(CARD_NUMBER_INPUT) && isDisplayed(PAY_AND_CONFIRM_BUTTON);
    }

    @Override
    public void waitUntilLoaded() {
        wait.until(ignored -> isLoaded());
        waitUntilClickable(PAY_AND_CONFIRM_BUTTON);
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
        // The site briefly shows confirmation and immediately redirects. A browser-side
        // observer preserves that transient text in same-origin sessionStorage so Selenium
        // can await it after navigation, rather than missing it between remote commands.
        JavascriptExecutor js = (JavascriptExecutor) driver;

        js.executeScript(
                """
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

                        if (visible && target.innerText.trim()) {
                            sessionStorage.setItem(
                                'orderSuccessMessage',
                                target.innerText.trim()
                            );

                            observer.disconnect();
                        }
                    });

                    observer.observe(target, {
                        attributes: true,
                        childList: true,
                        subtree: true,
                        characterData: true
                    });
                """);

        click(PAY_AND_CONFIRM_BUTTON);

        String message =
                wait.withMessage("Order success message was not captured before redirect")
                        .until(
                                ignored -> {
                                    String captured =
                                            (String)
                                                    js.executeScript(
                                                            "return sessionStorage.getItem('orderSuccessMessage');");
                                    return captured == null || captured.isBlank() ? null : captured;
                                });
        new PaymentDonePage().waitUntilLoaded();
        js.executeScript("sessionStorage.removeItem('orderSuccessMessage');");
        return message;
    }

    public void waitUntilSuccessMessageVisible() {
        waitUntilVisible(SUCCESS_MESSAGE);
    }

    public String getSuccessMessage() {
        return getText(SUCCESS_MESSAGE);
    }
}
