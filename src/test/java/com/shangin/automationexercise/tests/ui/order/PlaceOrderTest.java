package com.shangin.automationexercise.tests.ui.order;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.github.javafaker.Faker;
import com.shangin.automationexercise.assertions.AddressAssertions;
import com.shangin.automationexercise.base.BaseTest;
import com.shangin.automationexercise.components.AddToCartModalComponent;
import com.shangin.automationexercise.components.CartItemComponent;
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
import com.shangin.automationexercise.steps.UserRegistrationResult;
import com.shangin.automationexercise.steps.UserSteps;

import io.qameta.allure.Description;

@Test(groups = "ui")
public class PlaceOrderTest extends BaseTest {
    private static final Faker FAKER = new Faker();

    @Test @Description("Test Case 14: Place Order: Register while Checkout")
    public void shouldPlaceOrderRegisterWhileCheckout() {
        // 1. Launch browser
        // 2. Navigate to url 'http://automationexercise.com'
        HomePage homePage = HomePage.open();

        // 3. Verify that home page is visible successfully
        Assert.assertTrue(homePage.isLoaded(), "Home page should be loaded");

        // 4. Add products to cart
        List<ExpectedProduct> expectedProducts = new ArrayList<>();
        for (int index : List.of(0, 1, 2)) {
            ProductCardComponent product = homePage.products().getProductCard(index);
            expectedProducts.add(new ExpectedProduct(product.getName(), product.getPrice(), 1));
            homePage.addProductToCart(index).continueShopping();
        }

        // 5. Click 'Cart' button
        CartPage cart = homePage.header().openCart();

        // 6. Verify that cart page is displayed
        Assert.assertTrue(cart.isLoaded(), "Cart page should be loaded");

        // 7. Click Proceed To Checkout
        // 8. Click 'Register / Login' button
        SignupLoginPage loginPage = cart.proceedToCheckoutAsGuest().registerOrLogin();

        // 9. Fill all details in Signup and create account
        User newUser = UserFactory.randomUser();
        AccountCreatedPage accountCreated = loginPage.register(newUser).createAccount(newUser);

        // 10. Verify 'ACCOUNT CREATED!' and click 'Continue' button
        Assert.assertTrue(accountCreated.isAccountCreated());
        homePage = accountCreated.continueShopping();

        // 11. Verify ' Logged in as username' at top
        Assert.assertTrue(homePage.header().isLoggedInAs(newUser));

        // 12.Click 'Cart' button
        // 13. Click 'Proceed To Checkout' button
        CheckoutPage checkOutPage = homePage.header().openCart().proceedToCheckoutAsLoggedInUser();

        // 14. Verify Address Details
        AddressAssertions.assertMatchesUser(checkOutPage.deliveryAddress(), newUser);
        AddressAssertions.assertMatchesUser(checkOutPage.billingAddress(), newUser);

        // and Review Your Order
        List<CartItemComponent> cartItems = checkOutPage.getCartItems();
        Assert.assertEquals(
                cartItems.size(),
                expectedProducts.size(),
                "Cart items count is incorrect");

        for (ExpectedProduct expected : expectedProducts) {
            CartItemComponent actual = checkOutPage.getProductByName(expected.name());
            Assert.assertEquals(actual.getName(), expected.name(), "Product name is incorrect");
            Assert.assertEquals(actual.getPrice(), expected.price(), "Product price is incorrect");
        }

        // 15. Enter description in comment text area and click 'Place Order'
        checkOutPage.addComment(FAKER.lorem().sentence());
        PaymentPage paymentPage = checkOutPage.placeOrder();

        // 16. Enter payment details: Name on Card, Card Number, CVC, Expiration date
        CardDetails card = CardFactory.randomCard();
        paymentPage.fillPaymentDetails(card);

        // 17. Click 'Pay and Confirm Order' button
        String successMessage = paymentPage.payAndGetSuccessMessage();

        // 18. Verify success message 'Your order has been placed successfully!'
        Assert.assertEquals(
                successMessage,
                UiMessages.ORDER_PLACED_SUCCESSFULLY,
                "Success message is incorrect");

        // 19. Click 'Delete Account' button
        PaymentDonePage paymentDonePage = new PaymentDonePage();
        paymentDonePage.waitUntilLoaded();
        AccountDeletedPage accountDeletedPage = paymentDonePage.header().deleteAccount();

        // 20. Verify 'ACCOUNT DELETED!' and click 'Continue' button
        Assert.assertEquals(
                accountDeletedPage.getAccountDeletedMessage(),
                UiMessages.ACCOUNT_DELETED);
        accountDeletedPage.continueShopping();
    }

    @Test @Description("Test Case 15: Place Order: Register before Checkout")
    public void shouldPlaceOrderRegisterBeforeCheckout() {
        // 1. Launch browser
        // 2. Navigate to url 'http://automationexercise.com'
        HomePage homePage = HomePage.open();

        // 3. Verify that home page is visible successfully
        Assert.assertTrue(homePage.isLoaded(), "Home page should be loaded");

        // 4. Click 'Signup / Login' button
        // 5. Fill all details in Signup and create account
        User newUser = UserFactory.randomUser();
        AccountCreatedPage accountCreatedPage
                = homePage.header().openSignupLoginPage().register(newUser).createAccount(newUser);

        // 6. Verify 'ACCOUNT CREATED!' and click 'Continue' button
        Assert.assertTrue(accountCreatedPage.isAccountCreated());
        homePage = accountCreatedPage.continueShopping();

        // 7. Verify ' Logged in as username' at top
        homePage.header().isLoggedInAs(newUser);

        // 8. Add products to cart
        List<ExpectedProduct> expectedProducts = new ArrayList<>();
        for (int index : List.of(0, 1, 2)) {
            ProductCardComponent product = homePage.products().getProductCard(index);
            expectedProducts.add(new ExpectedProduct(product.getName(), product.getPrice(), 1));
            homePage.addProductToCart(index).continueShopping();
        }

        // 9. Click 'Cart' button
        CartPage cart = homePage.header().openCart();

        // 10. Verify that cart page is displayed
        Assert.assertTrue(cart.isLoaded(), "Cart page should be loaded");

        // 11. Click Proceed To Checkout
        CheckoutPage checkOutPage = cart.proceedToCheckoutAsLoggedInUser();

        // 12. Verify Address Details and Review Your Order
        AddressAssertions.assertMatchesUser(checkOutPage.deliveryAddress(), newUser);
        AddressAssertions.assertMatchesUser(checkOutPage.billingAddress(), newUser);
        List<CartItemComponent> cartItems = checkOutPage.getCartItems();
        Assert.assertEquals(
                cartItems.size(),
                expectedProducts.size(),
                "Cart items count is incorrect");

        for (ExpectedProduct expected : expectedProducts) {
            CartItemComponent actual = checkOutPage.getProductByName(expected.name());
            Assert.assertEquals(actual.getName(), expected.name(), "Product name is incorrect");
            Assert.assertEquals(actual.getPrice(), expected.price(), "Product price is incorrect");
        }

        // 13. Enter description in comment text area and click 'Place Order'
        checkOutPage.addComment(FAKER.lorem().sentence());
        PaymentPage paymentPage = checkOutPage.placeOrder();

        // 14. Enter payment details: Name on Card, Card Number, CVC, Expiration date
        CardDetails card = CardFactory.randomCard();
        paymentPage.fillPaymentDetails(card);

        // 15. Click 'Pay and Confirm Order' button
        String successMessage = paymentPage.payAndGetSuccessMessage();

        // 16. Verify success message 'Your order has been placed successfully!'
        Assert.assertEquals(
                successMessage,
                UiMessages.ORDER_PLACED_SUCCESSFULLY,
                "Success message is incorrect");

        // 17. Click 'Delete Account' button
        PaymentDonePage paymentDonePage = new PaymentDonePage();
        paymentDonePage.waitUntilLoaded();
        AccountDeletedPage accountDeletedPage = paymentDonePage.header().deleteAccount();

        // 18. Verify 'ACCOUNT DELETED!' and click 'Continue' button
        Assert.assertEquals(
                accountDeletedPage.getAccountDeletedMessage(),
                UiMessages.ACCOUNT_DELETED);
        accountDeletedPage.continueShopping();
    }

    @Test @Description("Test Case 16: Place Order: Login before Checkout")
    public void shouldPlaceOrderLoginBeforeCheckout() {
        // 0
        UserRegistrationResult result = UserSteps.registerNewUserWithLogout();
        User user = result.user();

        // 1. Launch browser
        // 2. Navigate to url 'http://automationexercise.com'
        HomePage homePage = result.homePage();

        // 3. Verify that home page is visible successfully
        Assert.assertTrue(homePage.isLoaded(), "Home page should be loaded");

        // 4. Click 'Signup / Login' button
        // 5. Fill email, password and click 'Login' button
        homePage = homePage.header().openSignupLoginPage().successLogin(user);

        // 6. Verify 'Logged in as username' at top
        homePage.header().isLoggedInAs(user);

        // 7. Add products to cart
        List<ExpectedProduct> expectedProducts = new ArrayList<>();
        for (int index : List.of(0, 1, 2)) {
            ProductCardComponent product = homePage.products().getProductCard(index);
            expectedProducts.add(new ExpectedProduct(product.getName(), product.getPrice(), 1));
            homePage.addProductToCart(index).continueShopping();
        }

        // 8. Click 'Cart' button
        CartPage cartPage = homePage.header().openCart();

        // 9. Verify that cart page is displayed
        Assert.assertTrue(cartPage.isLoaded(), "Cart page should be loaded");

        // 10. Click Proceed To Checkout
        CheckoutPage checkOutPage = cartPage.proceedToCheckoutAsLoggedInUser();

        // 11. Verify Address Details and Review Your Order
        AddressAssertions.assertMatchesUser(checkOutPage.deliveryAddress(), user);
        AddressAssertions.assertMatchesUser(checkOutPage.billingAddress(), user);
        List<CartItemComponent> cartItems = checkOutPage.getCartItems();
        Assert.assertEquals(
                cartItems.size(),
                expectedProducts.size(),
                "Cart items count is incorrect");

        for (ExpectedProduct expected : expectedProducts) {
            CartItemComponent actual = checkOutPage.getProductByName(expected.name());
            Assert.assertEquals(actual.getName(), expected.name(), "Product name is incorrect");
            Assert.assertEquals(actual.getPrice(), expected.price(), "Product price is incorrect");
        }

        // 12. Enter description in comment text area and click 'Place Order'
        checkOutPage.addComment(FAKER.lorem().sentence());
        PaymentPage paymentPage = checkOutPage.placeOrder();

        // 13. Enter payment details: Name on Card, Card Number, CVC, Expiration date
        CardDetails card = CardFactory.randomCard();
        paymentPage.fillPaymentDetails(card);

        // 14. Click 'Pay and Confirm Order' button
        String successMessage = paymentPage.payAndGetSuccessMessage();

        // 15. Verify success message 'Your order has been placed successfully!'
        Assert.assertEquals(
                successMessage,
                UiMessages.ORDER_PLACED_SUCCESSFULLY,
                "Success message is incorrect");

        // 16. Click 'Delete Account' button
        PaymentDonePage paymentDonePage = new PaymentDonePage();
        paymentDonePage.waitUntilLoaded();
        AccountDeletedPage accountDeletedPage = paymentDonePage.header().deleteAccount();

        // 17. Verify 'ACCOUNT DELETED!' and click 'Continue' button
        Assert.assertEquals(
                accountDeletedPage.getAccountDeletedMessage(),
                UiMessages.ACCOUNT_DELETED);
        accountDeletedPage.continueShopping();
    }

    @Test @Description("Test Case 23: Verify address details in checkout page")
    public void shouldDisplayCorrectDeliveryAndBillingAddressesOnCheckoutPage() {
        // 0
        User newUser = UserFactory.randomUser();
        // 1. Launch browser
        // 2. Navigate to url 'http://automationexercise.com'
        HomePage homePage = HomePage.open();

        // 3. Verify that home page is visible successfully
        Assert.assertTrue(homePage.isLoaded());

        // 4. Click 'Signup / Login' button
        // 5. Fill all details in Signup and create account
        AccountCreatedPage accountCreatedPage
                = homePage.header().openSignupLoginPage().register(newUser).createAccount(newUser);

        // 6. Verify 'ACCOUNT CREATED!' and click 'Continue' button
        Assert.assertTrue(accountCreatedPage.isAccountCreated());
        homePage = accountCreatedPage.continueShopping();

        // 7. Verify ' Logged in as username' at top
        Assert.assertTrue(homePage.header().isLoggedInAs(newUser));

        // 8. Add products to cart
        AddToCartModalComponent modal = homePage.addProductToCart(1);
        modal.continueShopping();
        modal = homePage.addProductToCart(3);
        modal.continueShopping();

        // 9. Click 'Cart' button
        CartPage cartPage = homePage.header().openCart();

        // 10. Verify that cart page is displayed
        Assert.assertTrue(cartPage.isLoaded());

        // 11. Click Proceed To Checkout
        CheckoutPage checkoutPage = cartPage.proceedToCheckoutAsLoggedInUser();

        // 12. Verify that the delivery address is same address filled at the time
        // registration of account
        AddressAssertions.assertMatchesUser(checkoutPage.deliveryAddress(), newUser);

        // 13. Verify that the billing address is same address filled at the time
        // registration of account
        AddressAssertions.assertMatchesUser(checkoutPage.billingAddress(), newUser);

        // 14. Click 'Delete Account' button
        AccountDeletedPage accountDeletedPage = checkoutPage.header().deleteAccount();

        // 15. Verify 'ACCOUNT DELETED!' and click 'Continue' button
        Assert.assertTrue(accountDeletedPage.isAccountDeleted());
    }

    @Test @Description("Test Case 24: Download Invoice after purchase order")
    public void shouldDownloadInvoiceAfterPurchase() throws IOException {
        // 1. Launch browser
        // 2. Navigate to url 'http://automationexercise.com'
        HomePage homePage = HomePage.open();

        // 3. Verify that home page is visible successfully
        Assert.assertTrue(homePage.isLoaded());

        // 4. Add products to cart
        List<ExpectedProduct> expectedProducts = new ArrayList<>();
        for (int index : List.of(3, 6)) {
            ProductCardComponent product = homePage.products().getProductCard(index);
            expectedProducts.add(new ExpectedProduct(product.getName(), product.getPrice(), 1));
            homePage.addProductToCart(index).continueShopping();
        }

        // 5. Click 'Cart' button
        CartPage cartPage = homePage.header().openCart();

        // 6. Verify that cart page is displayed
        Assert.assertTrue(cartPage.isLoaded());

        // 7. Click Proceed To Checkout
        CheckoutModalComponent checkoutModal = cartPage.proceedToCheckoutAsGuest();

        // 8. Click 'Register / Login' button
        SignupLoginPage signupLoginPage = checkoutModal.registerOrLogin();

        // 9. Fill all details in Signup and create account
        User newUser = UserFactory.randomUser();
        AccountCreatedPage accountCreatedPage
                = signupLoginPage.register(newUser).createAccount(newUser);

        // 10. Verify 'ACCOUNT CREATED!' and click 'Continue' button
        Assert.assertTrue(accountCreatedPage.isAccountCreated());
        homePage = accountCreatedPage.continueShopping();

        // 11. Verify ' Logged in as username' at top
        homePage.header().isLoggedInAs(newUser);

        // 12.Click 'Cart' button
        cartPage = homePage.header().openCart();

        // 13. Click 'Proceed To Checkout' button
        CheckoutPage checkoutPage = cartPage.proceedToCheckoutAsLoggedInUser();

        // 14. Verify Address Details and Review Your Order
        AddressAssertions.assertMatchesUser(checkoutPage.deliveryAddress(), newUser);
        AddressAssertions.assertMatchesUser(checkoutPage.billingAddress(), newUser);
        List<CartItemComponent> cartItems = checkoutPage.getCartItems();
        Assert.assertEquals(
                cartItems.size(),
                expectedProducts.size(),
                "Cart items count is incorrect");

        for (ExpectedProduct expected : expectedProducts) {
            CartItemComponent actual = checkoutPage.getProductByName(expected.name());
            Assert.assertEquals(actual.getName(), expected.name(), "Product name is incorrect");
            Assert.assertEquals(actual.getPrice(), expected.price(), "Product price is incorrect");
        }

        String totalAmount = checkoutPage.getTotalAmount().replaceAll("\\D", "");

        // 15. Enter description in comment text area and click 'Place Order'
        checkoutPage.addComment(FAKER.lorem().sentence());
        PaymentPage paymentPage = checkoutPage.placeOrder();

        // 16. Enter payment details: Name on Card, Card Number, CVC, Expiration date
        CardDetails card = CardFactory.randomCard();
        paymentPage.fillPaymentDetails(card);

        // 17. Click 'Pay and Confirm Order' button
        String paymentResult = paymentPage.payAndGetSuccessMessage();
        PaymentDonePage paymentDonePage = new PaymentDonePage();
        paymentDonePage.waitUntilLoaded();

        // 18. Verify success message 'Your order has been placed successfully!'
        Assert.assertEquals(paymentResult, UiMessages.ORDER_PLACED_SUCCESSFULLY);

        // 19. Click 'Download Invoice' button and verify invoice is downloaded
        // successfully.
        DownloadHelper.prepareFile("invoice.txt");
        paymentDonePage.downloadInvoice();
        String invoiceContent = DownloadHelper.waitAndRead("invoice.txt", Duration.ofSeconds(10));
        Assert.assertEquals(
                invoiceContent,
                UiMessages.invoiceText(newUser.firstName(), newUser.lastName(), totalAmount));

        // 20. Click 'Continue' button
        homePage = paymentDonePage.continueButton();

        // 21. Click 'Delete Account' button
        AccountDeletedPage accountDeletedPage = homePage.header().deleteAccount();

        // 22. Verify 'ACCOUNT DELETED!' and click 'Continue' button
        Assert.assertTrue(accountDeletedPage.isAccountDeleted());
    }
}
