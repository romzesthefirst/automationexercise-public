package com.shangin.automationexercise.cucumber.steps;

import org.testng.Assert;

import com.shangin.automationexercise.cucumber.context.ScenarioContext;
import com.shangin.automationexercise.model.User;
import com.shangin.automationexercise.pages.AccountDeletedPage;
import com.shangin.automationexercise.pages.HomePage;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class HeaderSteps {

    private final ScenarioContext context;

    public HeaderSteps(ScenarioContext context) {
        this.context = context;
    }

    @Then("the user logs out")
    public void userLogsOut() {
        Assert.assertTrue(new HomePage().header().logout().isLoaded());
    }

    @Then("the username should be visible in the header")
    public void usernameShouldBeVisibleInTheHeader() {
        User newUser = context.getUser();
        Assert.assertTrue(new HomePage().header().isLoggedInAs(newUser));

    }

    @When("the user deletes the account")
    public void userDeletesTheAccount() {
        new HomePage().header().deleteAccount();
    }

    @Then("the user can return to the home page")
    public void userCanReturnToTheHomePageFromAccountDeleted() {
        new AccountDeletedPage().header().openHomePage();
    }

}
