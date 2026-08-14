package com.shangin.automationexercise.pages;

import java.nio.file.Path;

import org.openqa.selenium.By;

import com.shangin.automationexercise.base.BasePage;
import com.shangin.automationexercise.model.Feedback;

public class ContactUsPage extends BasePage {

    private static final By GET_IN_TOUCH_TEXT = By.cssSelector(".contact-form h2");
    private static final By NAME_INPUT = By.cssSelector("input[data-qa='name']");
    private static final By EMAIL_INPUT = By.cssSelector("input[data-qa='email']");
    private static final By SUBJECT_INPUT = By.cssSelector("input[data-qa='subject']");
    private static final By MESSAGE_INPUT = By.cssSelector("textarea[data-qa='message']");
    private static final By FILE_INPUT = By.cssSelector("input[type='file']");
    private static final By SUBMIT_BUTTON = By.cssSelector("input[data-qa='submit-button']");
    private static final By SUCCESS_MESSAGE = By.cssSelector(".status.alert.alert-success");
    private static final By HOME_BUTTON = By.cssSelector(".btn.btn-success");

    public void submitFeedback() {
        click(SUBMIT_BUTTON);
    }
    
    public void confirmAlert() {
        acceptAlert();
    }

    public void fillInSubmitForm(Feedback feedback) {
        type(NAME_INPUT, feedback.name());
        type(EMAIL_INPUT, feedback.email());
        type(SUBJECT_INPUT, feedback.subject());
        type(MESSAGE_INPUT, feedback.message());
    }

    public void uploadAttachment(Path file) {
        uploadFile(FILE_INPUT, file);
    }

    public String getSuccessMessage() {
        return find(SUCCESS_MESSAGE).getText();
    }

    public HomePage goToHome() {
        click(HOME_BUTTON);
        HomePage homePage = new HomePage();
        homePage.waitUntilLoaded();
        return homePage;
    }

    @Override
    public boolean isLoaded() {
        return isDisplayed(GET_IN_TOUCH_TEXT);
    }

    @Override
    public void waitUntilLoaded() {
        waitUntilVisible(GET_IN_TOUCH_TEXT);
    }

}
