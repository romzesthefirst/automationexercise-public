package com.shangin.automationexercise.pages;

import org.openqa.selenium.By;

import com.shangin.automationexercise.base.BasePage;
import com.shangin.automationexercise.model.User;

public class SignupLoginPage extends BasePage {

    // Login to your account
    private static final By LOGIN_TO_YOUR_ACCOUNT_MESSAGE
            = By.cssSelector("div[class='login-form'] h2");
    private static final By LOGIN_EMAIL_INPUT = By.cssSelector("input[data-qa='login-email']");
    private static final By LOGIN_PASSWORD_INPUT
            = By.cssSelector("input[data-qa='login-password']");
    private static final By LOGIN_BUTTON = By.cssSelector("button[data-qa='login-button']");
    private static final By LOGIN_ERROR_MESSAGE = By.cssSelector("form[action='/login'] p");

    // New User SignUp
    private static final By NEW_USERS_SIGNUP_MESSAGE
            = By.cssSelector("div[class='signup-form'] h2");
    private static final By SIGNUP_NAME_INPUT = By.cssSelector("input[data-qa='signup-name']");
    private static final By SIGNUP_EMAIL_INPUT = By.cssSelector("input[data-qa='signup-email']");
    private static final By SIGNUP_BUTTON = By.cssSelector("button[data-qa='signup-button']");
    private static final By SIGNUP_ERROR_MESSAGE = By.cssSelector("form[action='/signup'] p");

    public AccountInformationPage register(User user) {
        type(SIGNUP_NAME_INPUT, user.firstName());
        type(SIGNUP_EMAIL_INPUT, user.email());
        click(SIGNUP_BUTTON);
        return new AccountInformationPage();
    }

    // result is void because of incorrect name/login
    public HomePage successLogin(User user) {
        type(LOGIN_EMAIL_INPUT, user.email());
        type(LOGIN_PASSWORD_INPUT, user.password());
        click(LOGIN_BUTTON);
        HomePage homePage = new HomePage();
        homePage.waitUntilLoaded();
        return homePage;
    }

    public void attemptToLogin(User user) {
        type(LOGIN_EMAIL_INPUT, user.email());
        type(LOGIN_PASSWORD_INPUT, user.password());
        click(LOGIN_BUTTON);
    }

    public String getLoginErrorMessage() {
        return getText(LOGIN_ERROR_MESSAGE);
    }

    public String getSignUpErrorMessage() {
        return getText(SIGNUP_ERROR_MESSAGE);
    }

    public String getUserSignupHeader() {
        return getText(NEW_USERS_SIGNUP_MESSAGE);
    }

    public String getUserLoginHeader() {
        return getText(LOGIN_TO_YOUR_ACCOUNT_MESSAGE);
    }

    public boolean isOpened() {
        return getCurrentUrl().endsWith("/login");
    }

    @Override
    public boolean isLoaded() {
        return isDisplayed(LOGIN_BUTTON);
    }

    @Override
    public void waitUntilLoaded() {
        waitUntilVisible(LOGIN_BUTTON);
    }
}
