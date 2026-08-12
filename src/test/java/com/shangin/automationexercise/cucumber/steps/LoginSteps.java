package com.shangin.automationexercise.cucumber.steps;

import org.testng.Assert;

import com.shangin.automationexercise.cucumber.context.ScenarioContext;
import com.shangin.automationexercise.model.User;
import com.shangin.automationexercise.pages.HomePage;
import com.shangin.automationexercise.pages.SignupLoginPage;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class LoginSteps {

    private final ScenarioContext context;

    public LoginSteps(ScenarioContext context) {
        this.context = context;
    }

    @When("the user logs in with valid credentials")
    public void userLogsInWithValidCredentials() {

        User user = context.getUser();

        SignupLoginPage loginPage = HomePage.open().header().openSignupLoginPage();

        loginPage.successLogin(user);
    }

    @Then("the user should be logged in")
    public void userShouldBeLoggedIn() {

        User user = context.getUser();

        HomePage homePage = new HomePage();

        Assert.assertEquals(
                homePage.header().getLoggedInUserName(),
                user.firstName(),
                "Unexpected logged in user");
    }
}
