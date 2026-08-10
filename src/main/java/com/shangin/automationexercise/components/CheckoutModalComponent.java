package com.shangin.automationexercise.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.shangin.automationexercise.base.BaseComponent;
import com.shangin.automationexercise.pages.SignupLoginPage;

public class CheckoutModalComponent extends BaseComponent {

    private static final By TITLE = By.cssSelector(".modal-title");
    private static final By MESSAGE = By.cssSelector(".modal-body > p:first-child");
    private static final By REGISTER_LOGIN_LINK = By.cssSelector("a[href='/login']");
    private static final By CONTINUE_ON_CART_BUTTON = By.cssSelector(".close-modal");

    public CheckoutModalComponent(WebElement root) {
        super(root);
    }

    public String getTitle() {
        return getText(TITLE);
    }

    public String getMessage() {
        return getText(MESSAGE);
    }

    public SignupLoginPage registerOrLogin() {
        click(REGISTER_LOGIN_LINK);
        SignupLoginPage login = new SignupLoginPage();
        login.waitUntilLoaded();
        return login;
    }

    public void continueOnCart() {
        click(CONTINUE_ON_CART_BUTTON);
    }

}
