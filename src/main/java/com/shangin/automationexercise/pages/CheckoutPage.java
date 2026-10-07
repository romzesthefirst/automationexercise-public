package com.shangin.automationexercise.pages;

import com.shangin.automationexercise.base.BasePage;
import com.shangin.automationexercise.components.AddressComponent;
import com.shangin.automationexercise.components.CartItemComponent;
import com.shangin.automationexercise.model.ActualProduct;
import com.shangin.automationexercise.model.MonetaryValues;
import java.math.BigDecimal;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;

public class CheckoutPage extends BasePage {

    private static final By CHECKOUT_INFO = By.cssSelector("[data-qa='checkout-info']");
    private static final By DELIVERY_ADDRESS = By.id("address_delivery");
    private static final By BILLING_ADDRESS = By.id("address_invoice");
    private static final By CART_ITEMS = By.cssSelector("tr[id^='product-']");
    private static final By COMMENT_INPUT = By.cssSelector("textarea[name='message']");
    private static final By PLACE_ORDER_BUTTON = By.cssSelector("a[href='/payment']");
    private static final By TOTAL_AMOUNT =
            By.xpath(
                    "//tr[td[contains(normalize-space(), 'Total Amount')]]//p[contains(@class,'cart_total_price')]");

    @Override
    public boolean isLoaded() {
        return isDisplayed(CHECKOUT_INFO) && isDisplayed(PLACE_ORDER_BUTTON);
    }

    @Override
    public void waitUntilLoaded() {
        wait.until(ignored -> isLoaded());
        waitUntilClickable(PLACE_ORDER_BUTTON);
        removeAds();
    }

    public List<CartItemComponent> getCartItems() {
        return findAll(CART_ITEMS).stream().map(CartItemComponent::new).toList();
    }

    public AddressComponent deliveryAddress() {
        return new AddressComponent(DELIVERY_ADDRESS);
    }

    public AddressComponent billingAddress() {
        return new AddressComponent(BILLING_ADDRESS);
    }

    public void addComment(String message) {
        type(COMMENT_INPUT, message);
    }

    public PaymentPage placeOrder() {
        navigate(PLACE_ORDER_BUTTON);
        PaymentPage paymentPage = new PaymentPage();
        paymentPage.waitUntilLoaded();
        return paymentPage;
    }

    public CartItemComponent getProductByName(String name) {
        return getCartItems().stream()
                .filter(product -> product.getName().equals(name))
                .findFirst()
                .orElseThrow(
                        () -> new NoSuchElementException("Product not found in cart: " + name));
    }

    public String getTotalAmount() {
        return getText(TOTAL_AMOUNT);
    }

    public BigDecimal getTotalPrice() {
        return MonetaryValues.parse(getTotalAmount());
    }

    public List<ActualProduct> getActualProducts() {
        return getCartItems().stream()
                .map(
                        item ->
                                new ActualProduct(
                                        item.getName(),
                                        item.getPrice(),
                                        item.getQuantity(),
                                        item.getTotal()))
                .toList();
    }
}
