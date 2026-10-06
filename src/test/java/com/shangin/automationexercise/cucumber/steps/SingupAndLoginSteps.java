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

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class SingupAndLoginSteps {

    private final ScenarioContext context;
    
    SignupLoginPage signupLoginPage = new SignupLoginPage();

    public SingupAndLoginSteps(ScenarioContext context) {
        this.context = context;
    }

    @When("the user logs in with valid credentials")
    public void userLogsInWithValidCredentials() {

        User user = context.getUser();
        SignupLoginPage loginPage = HomePage.open().header().openSignupLoginPage();
        loginPage.successLogin(user);
    }

    @When("the user attempts to log in")
    public void userAttemptsToLogIn() {

        User user = context.getUser();
        SignupLoginPage loginPage = HomePage.open().header().openSignupLoginPage();
        loginPage.attemptToLogin(user);
    }

    @When("a new user signs up with name and email")
    public void userSignsUpWithNameAndEmail() {

        User newUser = context.getUser();
        new HomePage().header().openSignupLoginPage().register(newUser);
    }

    @When("completes the account registration form")
    public void userCompletesAccountRegistrationForm() {

        User newUser = context.getUser();
        new AccountInformationPage().createAccount(newUser);

    }

    @When("the user continues to the application")
    public void userContinuesAfterRegistration() {

        new AccountCreatedPage().continueShopping();
    }

    @When("the user attempts to sign up with the registered email")
    public void userAttemptsToSignupWithTheRegisteredEmail() {

        User user = context.getUser();
        new HomePage().header().openSignupLoginPage().attemptToRegister(user);
    }
    
    @When("the user creates an account from checkout modal window")
    public void userCreatesAnAccountFromCheckoutModal() {

        User newUser = context.getUser();

        signupLoginPage = context.requireCheckoutModal().registerOrLogin();
        signupLoginPage.register(newUser).createAccount(newUser).continueShopping();
    }
    
    @When("the user creates an account")
    public void userCreatesAnAccount() {

        User newUser = context.getUser();

        signupLoginPage.register(newUser).createAccount(newUser).continueShopping();
    }

    @Then("the user should be logged in")
    public void userShouldBeLoggedIn() {

        User user = context.getUser();
        Assert.assertEquals(
                new HomePage().header().getLoggedInUserName(),
                user.firstName(),
                "Unexpected logged in user");
    }

    @Then("an invalid login error message should be displayed")
    public void invalidLoginErrorShouldBeDisplayed() {

        Assert.assertEquals(
                new SignupLoginPage().getLoginErrorMessage(),
                UiMessages.INCORRECT_EMAIL_PASSWORD);
    }

    @Then("the account created page should be displayed")
    public void accountCreatedPageShouldBeSisplayed() {

        Assert.assertTrue(
                new AccountCreatedPage().isLoaded(),
                "User should be navigated to Account Created page");
    }

    @Then("the account should be deleted successfully")
    public void accountShouldBeDeletedSuccessfully() {

        Assert.assertEquals(
                new AccountDeletedPage().getAccountDeletedMessage(),
                UiMessages.ACCOUNT_DELETED);
    }

    @Then("an email already exists error message should be displayed")
    public void emailAlreadyExistsErrorMessageDisplayed() {

        Assert.assertEquals(
                new SignupLoginPage().getSignUpErrorMessage(),
                UiMessages.EMAIL_ALREADY_EXISTS);
    }

}
