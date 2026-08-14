package com.shangin.automationexercise.cucumber.steps;

import org.testng.Assert;

import com.shangin.automationexercise.components.HeaderAccessor;
import com.shangin.automationexercise.pages.HomePage;
import com.shangin.automationexercise.pages.SignupLoginPage;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class NavigationSteps {

    @Given("the home page is opened")
    public void homePageIsOpened() {
        HomePage.open();
    }

    @Then("the login page should be displayed")
    public void loginPageShouldBeDisplayed() {

        Assert.assertTrue(
                new SignupLoginPage().isLoaded(),
                "User should be navigated to Login page");
    }

    @When("the user opens the Contact Us page")
    public void userOpensTheContactUsPage() {

        new HomePage().header().openContactUs();
    }

    @When("the user returns to the home page")
    public void userReturnsToHomePage() {
        
        new HeaderAccessor().header().openHomePage();
    }

    @Then("the home page should be displayed")
    public void homePageShouldBeDisplayed() {
        
        Assert.assertTrue(new HomePage().isOpened(), "User should be navigated to Home page");
    }

}
