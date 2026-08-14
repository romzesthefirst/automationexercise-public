package com.shangin.automationexercise.cucumber.steps;

import org.testng.Assert;

import com.shangin.automationexercise.constants.UiMessages;
import com.shangin.automationexercise.cucumber.context.ScenarioContext;
import com.shangin.automationexercise.model.User;
import com.shangin.automationexercise.pages.AccountCreatedPage;
import com.shangin.automationexercise.pages.AccountDeletedPage;
import com.shangin.automationexercise.pages.AccountInformationPage;
import com.shangin.automationexercise.pages.HomePage;
import com.shangin.automationexercise.pages.SignupLoginPage;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class LoginAndSingupSteps {

    private final ScenarioContext context;

    public LoginAndSingupSteps(ScenarioContext context) {
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

        Assert.assertEquals(
                new HomePage().header().getLoggedInUserName(),
                user.firstName(),
                "Unexpected logged in user");
    }

    @When("the user attempts to log in")
    public void userAttemptsToLogIn() {

        User user = context.getUser();

        SignupLoginPage loginPage = HomePage.open().header().openSignupLoginPage();

        loginPage.attemptToLogin(user);
    }

    @Then("an invalid login error message should be displayed")
    public void invalidLoginErrorShouldBeDisplayed() {

        Assert.assertEquals(
                new SignupLoginPage().getLoginErrorMessage(),
                UiMessages.INCORRECT_EMAIL_PASSWORD);
    }

    @When("a new user signs up with name and email")
    public void userSignsUpWithNameAndEmail() {

        User newUser = context.getUser();

        new HomePage().header().openSignupLoginPage().register(newUser);
    }

    @And("completes the account registration form")
    public void userCompletesAccountRegistrationForm() {

        User newUser = context.getUser();

        new AccountInformationPage().createAccount(newUser);

    }

    @Then("the account created page should be displayed")
    public void accountCreatedPageShouldBeSisplayed() {

        Assert.assertTrue(
                new AccountCreatedPage().isLoaded(),
                "User should be navigated to Account Created page");
    }

    @When("the user continues to the application")
    public void userContinuesAfterRegistration() {

        new AccountCreatedPage().continueShopping();
    }

    @Then("the account should be deleted successfully")
    public void accountShouldBeDeletedSuccessfully() {
        Assert.assertEquals(
                new AccountDeletedPage().getAccountDeletedMessage(),
                UiMessages.ACCOUNT_DELETED);
    }
    
    @When("the user attempts to sign up with the registered email")
    public void userAttemptsToSignupWithTheRegisteredEmail() {
        User user = context.getUser();
        
        new HomePage().header().openSignupLoginPage().register(user);
    }
    
    @Then("an email already exists error message should be displayed")
    public void emailAlreadyExistsErrorMessageDisplayed() {
        
    }

}
