package com.shangin.automationexercise.cucumber.steps;

import com.shangin.automationexercise.components.HeaderAccessor;
import com.shangin.automationexercise.constants.UiMessages;
import com.shangin.automationexercise.cucumber.context.ScenarioContext;
import com.shangin.automationexercise.pages.AccountDeletedPage;
import com.shangin.automationexercise.pages.HomePage;
import com.shangin.automationexercise.pages.SignupLoginPage;
import com.shangin.automationexercise.pages.TestCasesPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;

public class NavigationSteps {

    private final ScenarioContext context;

    HomePage homePage = new HomePage();
    AccountDeletedPage accountDeletedPage = new AccountDeletedPage();
    HeaderAccessor headerAccessor = new HeaderAccessor();

    public NavigationSteps(ScenarioContext context) {
        this.context = context;
    }

    @Given("the home page is opened")
    public void homePageIsOpened() {
        HomePage.open();
    }

    @When("the user opens the Contact Us page from the header")
    public void userOpensTheContactUsPage() {

        headerAccessor.header().openContactUs();
    }

    @When("the user returns to the home page from the header")
    public void userReturnsToHomePage() {

        headerAccessor.header().openHomePage();
    }

    @When("the user opens the Test Case page from the header")
    public void userOpensTheTestCasePage() {

        headerAccessor.header().openTestCases();
    }

    @When("the user opens the Products page from the header")
    public void userOpensTheProductsPage() {

        headerAccessor.header().openProducts();
    }

    @When("the user opens the Cart page from the header")
    public void userOpensCartFromHeader() {
        headerAccessor.header().openCart();
    }

    @When("the user opens a random product on the home page")
    public void userOpensRandomProductOnHomePage() {

        homePage.products().getRandomProduct().viewProduct();
    }

    @When("the user opens the Signup Login page from the header")
    public void userOpensSignupLoginPage() {

        headerAccessor.header().openSignupLoginPage();
    }

    @When("the user scrolls to the Recommended Items section")
    public void userScrollsToRecommendedItemsSection() {

        homePage.scrollToRecommendedItems();
    }

    // modals
    @When("continues shopping from modal window")
    public void userContinuesShopping() {

        context.requireAddToCartModal().continueShopping();
        context.setAddToCartModal(null);
    }

    @When("opens the Cart page from modal window")
    public void userOpensCartPageFromModal() {

        context.requireAddToCartModal().viewCart();
        context.setAddToCartModal(null);
    }

    // end modals

    @When("the home page is scroll to the bottom")
    public void homePageIsScrollToBottom() {

        homePage.scrollToBottom();
    }

    @When("the home page is scroll to the up")
    public void homePageIsScrollToTop() {
        homePage.scrollToTop();
    }

    @When("the user clicks on the arrow up")
    public void userClicksOnArrowUp() {

        homePage.clickScrollUp();
    }

    @Then("the home page should be displayed")
    public void homePageShouldBeDisplayed() {

        Assert.assertTrue(new HomePage().isOpened(), "User should be navigated to Home page");
    }

    @Then("the login page should be displayed")
    public void loginPageShouldBeDisplayed() {

        Assert.assertTrue(
                new SignupLoginPage().isLoaded(), "User should be navigated to Login page");
    }

    @Then("the Test Cases page should be displayed")
    public void testCasesPageShouldBeDisplayed() {

        Assert.assertTrue(new TestCasesPage().isLoaded(), "Test Cases page should be displayed");
    }

    @Then("the account deleted page should be displayed")
    public void accountDeletedPageShouldBeDisplayed() {

        Assert.assertEquals(
                accountDeletedPage.getAccountDeletedMessage(), UiMessages.ACCOUNT_DELETED);

        accountDeletedPage.continueShopping();
    }

    @Then("subscription section is visible")
    public void subscriptionSectionIsVisible() {

        Assert.assertTrue(homePage.footer().isDisplayed());
    }

    @Then("the home page is scrolled up to top")
    public void homePageIsScrolledUpToTop() {

        Assert.assertTrue(homePage.isPageAtTop(), "Page should be scrolled to the top");
    }

    @Then("{string} text is visible")
    public void homePageTextIsVisible(String text) {

        Assert.assertEquals(
                homePage.getActiveSlideSubtitle(), text, "Unexpected home page subtitle");
    }
}
