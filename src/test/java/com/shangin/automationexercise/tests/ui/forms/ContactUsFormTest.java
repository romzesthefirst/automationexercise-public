package com.shangin.automationexercise.tests.ui.forms;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.shangin.automationexercise.base.BaseTest;
import com.shangin.automationexercise.constants.UiMessages;
import com.shangin.automationexercise.factories.FeedbackFactory;
import com.shangin.automationexercise.model.Feedback;
import com.shangin.automationexercise.pages.ContactUsPage;
import com.shangin.automationexercise.pages.HomePage;
import com.shangin.automationexercise.resources.TestResources;

import io.qameta.allure.Description;

@Test(groups = "ui")
public class ContactUsFormTest extends BaseTest {
    @Test @Description("Test Case 6: Contact Us Form")
    public void shouldSendMessage() {
        // 1. Launch browser
        // 2. Navigate to url 'http://automationexercise.com'
        HomePage homePage = HomePage.open();

        // 3. Verify that home page is visible successfully
        Assert.assertTrue(homePage.isLoaded(), "Home page should be loaded");

        // 4. Click on 'Contact Us' button
        ContactUsPage contactUsPage = homePage.header().openContactUs();

        // 5. Verify 'GET IN TOUCH' is visible
        Assert.assertTrue(contactUsPage.isLoaded(), "Contact Us page should be loaded");

        // 6. Enter name, email, subject and message
        // 7. Upload file
        // 8. Click 'Submit' button
        // 9. Click OK button
        Feedback feedback = FeedbackFactory.randomFeedback();
        contactUsPage.submitFeedback(feedback, TestResources.testTextFile());

        // 10. Verify success message 'Success! Your details have been submitted successfully.' is visible
        Assert.assertEquals(contactUsPage.getSuccessMessage(), UiMessages.SUCCESS_FEEDBACK_MESSAGE);

        // 11. Click 'Home' button and verify that landed to home page successfully
        homePage = contactUsPage.goToHome();
        Assert.assertTrue(homePage.isLoaded(), "Home page should be loaded");;
    }
}
