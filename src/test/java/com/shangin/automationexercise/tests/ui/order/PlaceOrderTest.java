package com.shangin.automationexercise.tests.ui.order;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import net.datafaker.Faker;
import com.shangin.automationexercise.assertions.AddressAssertions;
import com.shangin.automationexercise.assertions.CartAssertions;
import com.shangin.automationexercise.base.BaseTest;
import com.shangin.automationexercise.components.AddToCartModalComponent;
import com.shangin.automationexercise.components.CheckoutModalComponent;
import com.shangin.automationexercise.components.ProductCardComponent;
import com.shangin.automationexercise.constants.UiMessages;
import com.shangin.automationexercise.factories.CardFactory;
import com.shangin.automationexercise.factories.UserFactory;
import com.shangin.automationexercise.model.CardDetails;
import com.shangin.automationexercise.model.ExpectedProduct;
import com.shangin.automationexercise.model.User;
import com.shangin.automationexercise.pages.AccountCreatedPage;
import com.shangin.automationexercise.pages.AccountDeletedPage;
import com.shangin.automationexercise.pages.CartPage;
import com.shangin.automationexercise.pages.CheckoutPage;
import com.shangin.automationexercise.pages.HomePage;
import com.shangin.automationexercise.pages.PaymentDonePage;
import com.shangin.automationexercise.pages.PaymentPage;
import com.shangin.automationexercise.pages.SignupLoginPage;
import com.shangin.automationexercise.resources.DownloadHelper;

import io.qameta.allure.Description;

@Test(groups = "ui")
public class PlaceOrderTest extends BaseTest {

    private static final Faker FAKER = new Faker();

    @Test @Description("Test Case 14: Place Order: Register while Checkout")
    public void shouldPlaceOrderRegisterWhileCheckout() {

        HomePage homePage = HomePage.open();

        List<ExpectedProduct> expectedProducts = new ArrayList<>();

        String[] productNames
                = { "Fancy Green Top", "Premium Polo T-Shirts", "Soft Stretch Jeans" };

        for (String productName : productNames) {
            ProductCardComponent product = homePage.products().getProductCard(productName);
            expectedProducts.add(new ExpectedProduct(product.getName(), product.getPrice(), 1));
            homePage.addProductToCart(productName).continueShopping();
        }

        CartPage cart = homePage.header().openCart();

        SignupLoginPage loginPage = cart.proceedToCheckoutAsGuest().registerOrLogin();

        User newUser = UserFactory.randomUser();
        AccountCreatedPage accountCreated = loginPage.register(newUser).createAccount(newUser);

        Assert.assertTrue(accountCreated.isAccountCreated());
        homePage = accountCreated.continueShopping();

        Assert.assertTrue(homePage.header().isLoggedInAs(newUser));

        CheckoutPage checkoutPage = homePage.header().openCart().proceedToCheckoutAsLoggedInUser();

        AddressAssertions.assertMatchesUser(checkoutPage.deliveryAddress(), newUser);
        AddressAssertions.assertMatchesUser(checkoutPage.billingAddress(), newUser);

        CartAssertions.assertProductsMatch(checkoutPage.getActualProducts(), expectedProducts);
        CartAssertions.assertProductsTotalPrice(checkoutPage.getTotalPrice(), expectedProducts);

        checkoutPage.addComment(FAKER.lorem().sentence());
        PaymentPage paymentPage = checkoutPage.placeOrder();

        CardDetails card = CardFactory.randomCard();
        paymentPage.fillPaymentDetails(card);

        String successMessage = paymentPage.payAndGetResultMessage();
        Assert.assertEquals(
                successMessage,
                UiMessages.ORDER_PLACED_SUCCESSFULLY,
                "Success message is incorrect");

        PaymentDonePage paymentDonePage = new PaymentDonePage();
        paymentDonePage.waitUntilLoaded();
        AccountDeletedPage accountDeletedPage = paymentDonePage.header().deleteAccount();

        Assert.assertEquals(
                accountDeletedPage.getAccountDeletedMessage(),
                UiMessages.ACCOUNT_DELETED);
        accountDeletedPage.continueShopping();
    }

    @Test @Description("Test Case 15: Place Order: Register before Checkout")
    public void shouldPlaceOrderRegisterBeforeCheckout() {

        HomePage homePage = HomePage.open();

        User newUser = UserFactory.randomUser();
        AccountCreatedPage accountCreatedPage
                = homePage.header().openSignupLoginPage().register(newUser).createAccount(newUser);

        Assert.assertTrue(accountCreatedPage.isAccountCreated());
        homePage = accountCreatedPage.continueShopping();

        homePage.header().isLoggedInAs(newUser);

        List<ExpectedProduct> expectedProducts = new ArrayList<>();

        String[] productNames
                = { "Fancy Green Top", "Premium Polo T-Shirts", "Soft Stretch Jeans" };

        for (String productName : productNames) {
            ProductCardComponent product = homePage.products().getProductCard(productName);
            expectedProducts.add(new ExpectedProduct(product.getName(), product.getPrice(), 1));
            homePage.addProductToCart(productName).continueShopping();
        }

        CartPage cart = homePage.header().openCart();

        CheckoutPage checkoutPage = cart.proceedToCheckoutAsLoggedInUser();

        AddressAssertions.assertMatchesUser(checkoutPage.deliveryAddress(), newUser);
        AddressAssertions.assertMatchesUser(checkoutPage.billingAddress(), newUser);

        CartAssertions.assertProductsMatch(checkoutPage.getActualProducts(), expectedProducts);
        CartAssertions.assertProductsTotalPrice(checkoutPage.getTotalPrice(), expectedProducts);

        checkoutPage.addComment(FAKER.lorem().sentence());
        PaymentPage paymentPage = checkoutPage.placeOrder();

        CardDetails card = CardFactory.randomCard();
        paymentPage.fillPaymentDetails(card);

        String successMessage = paymentPage.payAndGetResultMessage();

        Assert.assertEquals(
                successMessage,
                UiMessages.ORDER_PLACED_SUCCESSFULLY,
                "Success message is incorrect");

        PaymentDonePage paymentDonePage = new PaymentDonePage();
        paymentDonePage.waitUntilLoaded();
        AccountDeletedPage accountDeletedPage = paymentDonePage.header().deleteAccount();

        Assert.assertEquals(
                accountDeletedPage.getAccountDeletedMessage(),
                UiMessages.ACCOUNT_DELETED);

        accountDeletedPage.continueShopping();
    }

    @Test @Description("Test Case 16: Place Order: Login before Checkout")
    public void shouldPlaceOrderLoginBeforeCheckout() {

        User user = apiUserSteps.createUser();

        HomePage homePage = HomePage.open();

        homePage = homePage.header().openSignupLoginPage().successLogin(user);

        homePage.header().isLoggedInAs(user);

        List<ExpectedProduct> expectedProducts = new ArrayList<>();

        String[] productNames
                = { "Fancy Green Top", "Premium Polo T-Shirts", "Soft Stretch Jeans" };

        for (String productName : productNames) {
            ProductCardComponent product = homePage.products().getProductCard(productName);
            expectedProducts.add(new ExpectedProduct(product.getName(), product.getPrice(), 1));
            homePage.addProductToCart(productName).continueShopping();
        }

        CartPage cartPage = homePage.header().openCart();

        Assert.assertTrue(cartPage.isLoaded(), "Cart page should be loaded");

        CheckoutPage checkoutPage = cartPage.proceedToCheckoutAsLoggedInUser();

        AddressAssertions.assertMatchesUser(checkoutPage.deliveryAddress(), user);
        AddressAssertions.assertMatchesUser(checkoutPage.billingAddress(), user);

        CartAssertions.assertProductsMatch(checkoutPage.getActualProducts(), expectedProducts);
        CartAssertions.assertProductsTotalPrice(checkoutPage.getTotalPrice(), expectedProducts);

        checkoutPage.addComment(FAKER.lorem().sentence());
        PaymentPage paymentPage = checkoutPage.placeOrder();

        CardDetails card = CardFactory.randomCard();
        paymentPage.fillPaymentDetails(card);

        String successMessage = paymentPage.payAndGetResultMessage();

        Assert.assertEquals(
                successMessage,
                UiMessages.ORDER_PLACED_SUCCESSFULLY,
                "Success message is incorrect");

        PaymentDonePage paymentDonePage = new PaymentDonePage();
        paymentDonePage.waitUntilLoaded();
        AccountDeletedPage accountDeletedPage = paymentDonePage.header().deleteAccount();

        Assert.assertEquals(
                accountDeletedPage.getAccountDeletedMessage(),
                UiMessages.ACCOUNT_DELETED);
        accountDeletedPage.continueShopping();
    }

    @Test @Description("Test Case 23: Verify address details in checkout page")
    public void shouldDisplayCorrectDeliveryAndBillingAddressesOnCheckoutPage() {

        User newUser = UserFactory.randomUser();

        HomePage homePage = HomePage.open();

        Assert.assertTrue(homePage.isLoaded());

        AccountCreatedPage accountCreatedPage
                = homePage.header().openSignupLoginPage().register(newUser).createAccount(newUser);

        Assert.assertTrue(accountCreatedPage.isAccountCreated());
        homePage = accountCreatedPage.continueShopping();

        Assert.assertTrue(homePage.header().isLoggedInAs(newUser));

        AddToCartModalComponent modal = homePage.addProductToCart(1);
        modal.continueShopping();
        modal = homePage.addProductToCart(3);
        modal.continueShopping();

        CartPage cartPage = homePage.header().openCart();

        Assert.assertTrue(cartPage.isLoaded());

        CheckoutPage checkoutPage = cartPage.proceedToCheckoutAsLoggedInUser();

        AddressAssertions.assertMatchesUser(checkoutPage.deliveryAddress(), newUser);

        AddressAssertions.assertMatchesUser(checkoutPage.billingAddress(), newUser);

        AccountDeletedPage accountDeletedPage = checkoutPage.header().deleteAccount();

        Assert.assertTrue(accountDeletedPage.isAccountDeleted());
    }

    @Test @Description("Test Case 24: Download Invoice after purchase order")
    public void shouldDownloadInvoiceAfterPurchase() throws IOException {

        HomePage homePage = HomePage.open();

        List<ExpectedProduct> expectedProducts = new ArrayList<>();

        String[] productNames
                = { "Fancy Green Top", "Premium Polo T-Shirts", "Soft Stretch Jeans" };

        for (String productName : productNames) {
            ProductCardComponent product = homePage.products().getProductCard(productName);
            expectedProducts.add(new ExpectedProduct(product.getName(), product.getPrice(), 1));
            homePage.addProductToCart(productName).continueShopping();
        }

        CartPage cartPage = homePage.header().openCart();

        Assert.assertTrue(cartPage.isLoaded());

        CheckoutModalComponent checkoutModal = cartPage.proceedToCheckoutAsGuest();

        SignupLoginPage signupLoginPage = checkoutModal.registerOrLogin();

        User newUser = UserFactory.randomUser();
        AccountCreatedPage accountCreatedPage
                = signupLoginPage.register(newUser).createAccount(newUser);

        Assert.assertTrue(accountCreatedPage.isAccountCreated());
        homePage = accountCreatedPage.continueShopping();

        homePage.header().isLoggedInAs(newUser);

        cartPage = homePage.header().openCart();

        CheckoutPage checkoutPage = cartPage.proceedToCheckoutAsLoggedInUser();

        AddressAssertions.assertMatchesUser(checkoutPage.deliveryAddress(), newUser);
        AddressAssertions.assertMatchesUser(checkoutPage.billingAddress(), newUser);

        CartAssertions.assertProductsMatch(checkoutPage.getActualProducts(), expectedProducts);
        CartAssertions.assertProductsTotalPrice(checkoutPage.getTotalPrice(), expectedProducts);
        
        String totalAmountStr = checkoutPage.getTotalAmount().replaceAll("\\D", "");

        checkoutPage.addComment(FAKER.lorem().sentence());
        PaymentPage paymentPage = checkoutPage.placeOrder();

        CardDetails card = CardFactory.randomCard();
        paymentPage.fillPaymentDetails(card);

        String paymentResult = paymentPage.payAndGetResultMessage();
        PaymentDonePage paymentDonePage = new PaymentDonePage();
        paymentDonePage.waitUntilLoaded();

        Assert.assertEquals(paymentResult, UiMessages.ORDER_PLACED_SUCCESSFULLY);

        DownloadHelper.prepareFile("invoice.txt");
        paymentDonePage.downloadInvoice();
        String invoiceContent = DownloadHelper.waitAndRead("invoice.txt", Duration.ofSeconds(10));
        Assert.assertEquals(
                invoiceContent,
                UiMessages.invoiceText(newUser.firstName(), newUser.lastName(), totalAmountStr));

        homePage = paymentDonePage.continueButton();

        AccountDeletedPage accountDeletedPage = homePage.header().deleteAccount();

        Assert.assertTrue(accountDeletedPage.isAccountDeleted());
    }
}
