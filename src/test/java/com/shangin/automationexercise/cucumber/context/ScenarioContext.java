package com.shangin.automationexercise.cucumber.context;

import java.util.ArrayList;
import java.util.List;

import com.shangin.automationexercise.api.support.OwnedAccounts;
import com.shangin.automationexercise.components.AddToCartModalComponent;
import com.shangin.automationexercise.components.CheckoutModalComponent;
import com.shangin.automationexercise.model.ExpectedProduct;
import com.shangin.automationexercise.model.User;

public class ScenarioContext {

    private final OwnedAccounts ownedAccounts;

    public ScenarioContext(OwnedAccounts ownedAccounts) {
        this.ownedAccounts = ownedAccounts;
    }

    public User newUser() {
        return ownedAccounts.newUser();
    }

    private User user;

    private AddToCartModalComponent addToCartModal;

    private CheckoutModalComponent checkoutModal;

    private final List<ExpectedProduct> expectedProducts = new ArrayList<>();

    private String paymentResultMessage;
    
    private String invoiceText;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setAddToCartModal(AddToCartModalComponent addToCartModal) {
        this.addToCartModal = addToCartModal;
    }

    public AddToCartModalComponent requireAddToCartModal() {
        if (addToCartModal == null) {
            throw new IllegalStateException("Add to cart modal is not available");
        }

        return addToCartModal;
    }

    public void setCheckoutModal(CheckoutModalComponent checkoutModal) {
        this.checkoutModal = checkoutModal;
    }

    public CheckoutModalComponent requireCheckoutModal() {
        if (checkoutModal == null) {
            throw new IllegalStateException("Checkout modal is not available");
        }

        return checkoutModal;
    }

    public void addExpectedProduct(ExpectedProduct product) {
        expectedProducts.add(product);
    }

    public void addExpectedProducts(List<ExpectedProduct> products) {
        expectedProducts.addAll(products);
    }

    public List<ExpectedProduct> getExpectedProducts() {
        return expectedProducts;
    }

    public void setPaymentResultMessage(String message) {
        this.paymentResultMessage = message;
    }

    public String getPaymentResultMessage() {
        return paymentResultMessage;
    }
    
    public void setInvoiceText(String text) {
        this.invoiceText = text;
    }

    public String getInvoiceText() {
        return invoiceText;
    }
}