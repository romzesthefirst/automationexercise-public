package com.shangin.automationexercise.tests.ui.pages;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;

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
@Epic("Automation Exercise")
@Feature("Pages")
public class ContactUsTest extends BaseTest {
    @Test @Description("Test Case 6: Contact Us Form")
    public void shouldSendMessage() {

        HomePage homePage = HomePage.open();

        ContactUsPage contactUsPage = homePage.header().openContactUs();

        Assert.assertTrue(contactUsPage.isLoaded(), "Contact Us page should be loaded");

        Feedback feedback = FeedbackFactory.randomFeedback();
        
        contactUsPage.fillInSubmitForm(feedback);
        contactUsPage.uploadAttachment(TestResources.testTextFile());
        contactUsPage.submitFeedback();
        contactUsPage.confirmAlert();

        Assert.assertEquals(contactUsPage.getSuccessMessage(), UiMessages.SUCCESS_FEEDBACK_MESSAGE);

        homePage = contactUsPage.goToHome();
        
        Assert.assertTrue(homePage.isLoaded(), "Home page should be loaded");
    }
}
