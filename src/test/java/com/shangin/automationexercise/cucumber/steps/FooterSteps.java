package com.shangin.automationexercise.cucumber.steps;

import com.shangin.automationexercise.components.FooterComponent;
import com.shangin.automationexercise.constants.UiMessages;
import com.shangin.automationexercise.factories.TestData;
import com.shangin.automationexercise.pages.CartPage;
import com.shangin.automationexercise.pages.HomePage;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;

public class FooterSteps {

    private final CartPage cartPage = new CartPage();
    private final HomePage homePage = new HomePage();
    private final FooterComponent footer = homePage.footer();

    @When("the user scrolls to the Home page footer")
    public void scrollToHomeFooter() {
        Assert.assertTrue(homePage.isLoaded(), "Home page should be loaded");
        homePage.scrollToFooter();
    }

    @When("the user scrolls to the Cart page footer")
    public void scrollToCartFooter() {
        Assert.assertTrue(cartPage.isLoaded(), "Cart page should be loaded");
        cartPage.scrollToFooter();
    }

    @Then("the subscription heading should be displayed")
    public void subscriptionHeadingIsDisplayed() {
        Assert.assertEquals(
                footer.getSubscriptionHeader(),
                UiMessages.SUBSCRIPTION,
                "Unexpected subscription heading");
    }

    @When("the user subscribes with a generated email address")
    public void subscribeWithGeneratedEmail() {
        footer.subscribe(TestData.faker().internet().emailAddress());
    }

    @Then("the subscription success message should be displayed")
    public void subscriptionSuccessIsDisplayed() {
        Assert.assertEquals(
                footer.getSubscriptionResult(),
                UiMessages.SUCCESS_SUBSCRIBE,
                "Unexpected subscription result");
    }
}
