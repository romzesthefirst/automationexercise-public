package com.shangin.automationexercise.cucumber.steps;

import com.shangin.automationexercise.components.HeaderAccessor;
import com.shangin.automationexercise.cucumber.context.ScenarioContext;
import com.shangin.automationexercise.model.User;
import com.shangin.automationexercise.pages.AccountDeletedPage;
import com.shangin.automationexercise.pages.HomePage;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;

public class HeaderSteps {

    private final ScenarioContext context;

    HeaderAccessor headerAccessor = new HeaderAccessor();

    public HeaderSteps(ScenarioContext context) {
        this.context = context;
    }

    @When("the user deletes the account")
    public void userDeletesTheAccount() {

        headerAccessor.header().deleteAccount();
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

    @Then("the user can return to the home page")
    public void userCanReturnToTheHomePageFromAccountDeleted() {

        Assert.assertTrue(new AccountDeletedPage().header().openHomePage().isLoaded());
    }
}
