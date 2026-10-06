package com.shangin.automationexercise.pages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;

import com.shangin.automationexercise.base.BasePage;
import com.shangin.automationexercise.components.AddToCartModalComponent;
import com.shangin.automationexercise.components.CartItemComponent;
import com.shangin.automationexercise.components.CheckoutModalComponent;
import com.shangin.automationexercise.components.ProductListComponent;
import com.shangin.automationexercise.model.ActualProduct;

public class CartPage extends BasePage {

    private final static By PROCEED_TO_CHECKOUT_BUTTON = By.cssSelector(".check_out");
    private static final By CART_ITEMS = By.cssSelector("tr[id^='product-']");
    private static final By PRODUCTS_SECTION = By.cssSelector(".features_items");
    private static final By CHECKOUT_MODAL = By.cssSelector("#checkoutModal .modal-content");
    private static final By CART_INFO = By.cssSelector(".cart_info");

    public List<CartItemComponent> getCartItems() {
        return findAll(CART_ITEMS).stream().map(CartItemComponent::new).toList();
    }

    @Override
    public boolean isLoaded() {
        return isDisplayed(CART_INFO);
    }

    @Override
    public void waitUntilLoaded() {
        removeAds();
        waitUntilVisible(CART_INFO);

    }

    public final CheckoutModalComponent proceedToCheckoutAsGuest() {
        click(PROCEED_TO_CHECKOUT_BUTTON);
        return waitForCheckoutModal();
    }

    public final CheckoutPage proceedToCheckoutAsLoggedInUser() {
        navigate(PROCEED_TO_CHECKOUT_BUTTON, "/checkout");
        CheckoutPage checkoutPage = new CheckoutPage();
        checkoutPage.waitUntilLoaded();
        return checkoutPage;
    }

    public final CheckoutModalComponent waitForCheckoutModal() {
        waitUntilVisible(CHECKOUT_MODAL);
        return new CheckoutModalComponent(CHECKOUT_MODAL);
    }

    public final ProductListComponent products() {
        return new ProductListComponent(PRODUCTS_SECTION);
    }

    public final AddToCartModalComponent addProductToCart(int index) {
        products().addProductToCart(index);
        return waitForAddToCartModal();
    }

    public final CartItemComponent getProduct(String productName) {
        return getCartItems().stream().filter(product -> product.getName().equals(productName))
                .findFirst().orElseThrow(
                        () -> new NoSuchElementException(
                                "Product not found in cart: " + productName));
    }

    public final void deleteProduct(int index) {
        getCartItems().get(index).delete();
    }

    public final void deleteProduct(String productName) {
        CartItemComponent product = getProduct(productName);

        product.delete();

        wait.until(
                ignored -> getCartItems().stream()
                        .noneMatch(item -> item.getName().equalsIgnoreCase(productName)));
    }

    public boolean hasProduct(String name) {
        return getCartItems().stream().anyMatch(product -> product.getName().equals(name));
    }

    public List<ActualProduct> getActualProducts() {
        return getCartItems().stream()
                .map(item -> new ActualProduct(item.getName(), item.getPrice(), item.getQuantity(), item.getTotal()))
                .toList();
    }

}
