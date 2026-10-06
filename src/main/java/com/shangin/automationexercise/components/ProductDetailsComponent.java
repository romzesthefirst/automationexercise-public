package com.shangin.automationexercise.components;

import org.openqa.selenium.By;

import com.shangin.automationexercise.base.BaseComponent;

public class ProductDetailsComponent extends BaseComponent {

    private static final By NAME = By.cssSelector(".product-information h2");
    private static final By CATEGORY = By.cssSelector(".product-information h2 + p");
    private static final By PRICE = By.cssSelector(".product-information span > span");
    private static final By AVAILABILITY
            = By.xpath("//p[b[normalize-space()='Availability:']]");
    private static final By CONDITION = By.xpath("//p[b[normalize-space()='Condition:']]");
    private static final By BRAND = By.xpath("//p[b[normalize-space()='Brand:']]");
    private static final By QUANTITY = By.id("quantity");
    private static final By ADD_TO_CART_BUTTON = By.cssSelector(".cart");

    public ProductDetailsComponent(By rootLocator) {
        super(rootLocator);
    }

    public String getName() {
        return getText(NAME);
    }

    public String getCategory() {
        return getText(CATEGORY);
    }

    public String getPrice() {
        return getText(PRICE);
    }

    public String getAvailability() {
        return getText(AVAILABILITY);
    }

    public String getCondition() {
        return getText(CONDITION);
    }

    public String getBrand() {
        return getText(BRAND);
    }

    public int getQuantity() {
        return Integer.parseInt(find(QUANTITY).getDomProperty("value"));
    }

    public void setQuantity(int quantity) {
        if (quantity < 1) {
            throw new IllegalArgumentException("Quantity must be at least 1");
        }

        clear(QUANTITY);
        type(QUANTITY, String.valueOf(quantity));
    }

    public void addToCart() {
        click(ADD_TO_CART_BUTTON);
    }
}
