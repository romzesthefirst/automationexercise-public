package com.shangin.automationexercise.components;

import com.shangin.automationexercise.base.BaseComponent;
import org.openqa.selenium.By;

public class FooterComponent extends BaseComponent {

    public FooterComponent(By rootLocator) {
        super(rootLocator);
    }

    private static final By SUBSCRIPTION_HEADER = By.cssSelector(".single-widget h2");
    private static final By SUBSCRIPTION_EMAIL = By.id("susbscribe_email");
    private static final By SUBSCRIPTION_BUTTON = By.id("subscribe");
    private static final By SUBSCRIBE_RESULT_MESSAGE = By.id("success-subscribe");

    public void subscribe(String email) {
        type(SUBSCRIPTION_EMAIL, email);
        click(SUBSCRIPTION_BUTTON);
    }

    public String getSubscriptionHeader() {
        return getText(SUBSCRIPTION_HEADER);
    }

    public String getSubscriptionResult() {
        return getText(SUBSCRIBE_RESULT_MESSAGE);
    }

    public boolean isDisplayed() {
        return root().isDisplayed();
    }
}
