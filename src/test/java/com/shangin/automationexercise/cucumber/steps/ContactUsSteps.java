package com.shangin.automationexercise.cucumber.steps;

import org.testng.Assert;

import com.shangin.automationexercise.constants.UiMessages;
import com.shangin.automationexercise.factories.FeedbackFactory;
import com.shangin.automationexercise.model.Feedback;
import com.shangin.automationexercise.pages.ContactUsPage;
import com.shangin.automationexercise.resources.TestResources;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class ContactUsSteps {
    
    private final ContactUsPage contactUsPage = new ContactUsPage();

    @Then("the Get In Touch section should be displayed")
    public void getInTouchSectionShouldBeDisplayed() {

        Assert.assertTrue(
                contactUsPage.isLoaded(),
                "Get In Touch section should be displayed");
    }

    @When("the user fills in the contact form")
    public void userFillsInTheContactForm() {

        Feedback feedback = FeedbackFactory.randomFeedback();

        contactUsPage.fillInSubmitForm(feedback);
    }

    @When("uploads a file")
    public void userUploadFile() {

        contactUsPage.uploadAttachment(TestResources.testTextFile());
    }

    @When("submits the contact form")
    public void userSubmitContactForm() {

        contactUsPage.submitFeedback();
    }

    @When("confirms the submission alert")
    public void userConfirmsTheSubmissionAlert() {

        contactUsPage.confirmAlert();
    }

    @Then("the contact form success message should be displayed")
    public void contactFormSuccessMessageShouldBeDisplayed() {

        Assert.assertEquals(
                contactUsPage.getSuccessMessage(),
                UiMessages.SUCCESS_FEEDBACK_MESSAGE);
    }
}
