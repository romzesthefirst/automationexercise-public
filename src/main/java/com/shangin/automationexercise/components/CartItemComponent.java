package com.shangin.automationexercise.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.shangin.automationexercise.base.BaseComponent;

public class CartItemComponent extends BaseComponent {

    //[id*='product']
    private static final By NAME = By.cssSelector(".cart_description h4");
    private static final By PRICE = By.cssSelector(".cart_price p");
    private static final By QUANTITY = By.cssSelector(".cart_quantity button");
    private static final By TOTAL = By.cssSelector(".cart_total_price");
    private static final By DELETE_ITEM = By.cssSelector(".cart_quantity_delete");
    
    public CartItemComponent(WebElement root) {
        super(root);
    }
    
    public String getName() {
        return getText(NAME);
    }

    public String getPrice() {
        return getText(PRICE);
    }

    public int getQuantity() {
        return Integer.parseInt(getText(QUANTITY));
    }

    public String getTotal() {
        return getText(TOTAL);
    }
    
    public void delete() {
        click(DELETE_ITEM);
        waitUntilRootDetached();
    }
}
