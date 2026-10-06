package com.shangin.automationexercise.cucumber.steps;

import java.io.IOException;

import org.testng.Assert;

import com.shangin.automationexercise.factories.TestData;
import com.shangin.automationexercise.assertions.AddressAssertions;
import com.shangin.automationexercise.assertions.CartAssertions;
import com.shangin.automationexercise.constants.UiMessages;
import com.shangin.automationexercise.model.MonetaryValues;
import com.shangin.automationexercise.cucumber.context.ScenarioContext;
import com.shangin.automationexercise.factories.CardFactory;
import com.shangin.automationexercise.model.CardDetails;
import com.shangin.automationexercise.model.User;
import com.shangin.automationexercise.pages.CartPage;
import com.shangin.automationexercise.pages.CheckoutPage;
import com.shangin.automationexercise.pages.PaymentDonePage;
import com.shangin.automationexercise.pages.PaymentPage;
import com.shangin.automationexercise.resources.DownloadHelper;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class CheckoutSteps {

    private final ScenarioContext context;

    CartPage cartPage = new CartPage();
    CheckoutPage checkoutPage = new CheckoutPage();
    PaymentPage paymentPage = new PaymentPage();
    PaymentDonePage paymentDonePage = new PaymentDonePage();

    public CheckoutSteps(ScenarioContext context) {
        this.context = context;
    }

    @When("the user proceeds to checkout as guest")
    public void userProceedsToCheckoutAsGuest() {

        context.setCheckoutModal(cartPage.proceedToCheckoutAsGuest());

    }

    @When("the user proceeds to checkout as logged in")
    public void userProceedsToCheckoutAsLoggedIn() {

        cartPage.proceedToCheckoutAsLoggedInUser();
    }

    @When("the user enters an order comment")
    public void userEntersOrderComment() {

        checkoutPage.addComment(TestData.faker().lorem().sentence());
    }

    @When("the user places the order")
    public void userPlacesOrder() {

        checkoutPage.placeOrder();
    }

    @When("the user enters valid payment details")
    public void userEntersValidPaymentDetails() {

        CardDetails card = CardFactory.randomCard();
        paymentPage.fillPaymentDetails(card);
    }

    @When("the user confirms the payment")
    public void userConfirmsThePayment() {

        context.setPaymentResultMessage(paymentPage.payAndGetResultMessage());
    }

    @When("the user downloads invoice")
    public void userDownloadsInvoice() throws IOException {

        DownloadHelper.prepareFile("invoice.txt");
        paymentDonePage.downloadInvoice();
        context.setInvoiceText(DownloadHelper.waitAndRead("invoice.txt"));
    }

    @Then("the delivery address details should be correct")
    public void deliveryAddressDetailsShouldBeCorrect() {

        AddressAssertions.assertMatchesUser(checkoutPage.deliveryAddress(), context.getUser());
    }

    @Then("the billing address details should be correct")
    public void billingAddressDetailsShouldBeCorrect() {

        AddressAssertions.assertMatchesUser(checkoutPage.billingAddress(), context.getUser());
    }

    @Then("the order details should be correct")
    public void orderDetailsShouldBeCorrect() {

        CartAssertions.assertProductsMatch(
                checkoutPage.getActualProducts(),
                context.getExpectedProducts());
    }

    @Then("the total amount should be correct")
    public void totalAmountShouldBeCorrect() {

        CartAssertions.assertProductsTotalPrice(
                checkoutPage.getTotalPrice(),
                context.getExpectedProducts());
    }

    @Then("the order success message should be displayed")
    public void orderSuccessMessageShouldBeSisplayed() {

        Assert.assertEquals(
                context.getPaymentResultMessage(),
                UiMessages.ORDER_PLACED_SUCCESSFULLY,
                "Success message is incorrect");
    }

    @Then("the user redirects to Payment Done page")
    public void userRedirectsToPaymentDonePage() {

        paymentDonePage.waitUntilLoaded();
    }

    @Then("the text in invoice should be correct")
    public void textInInvoiceShouldBeCorrect() {

        User user = context.getUser();
        String totalAmountStr
                = MonetaryValues.format(CartAssertions.calculateExpectedTotal(context.getExpectedProducts()));

        Assert.assertEquals(
                context.getInvoiceText(),
                UiMessages.invoiceText(user.firstName(), user.lastName(), totalAmountStr));
    }

}
