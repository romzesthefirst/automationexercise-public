package com.shangin.automationexercise.components;

import com.shangin.automationexercise.base.BaseComponent;
import com.shangin.automationexercise.pages.CartPage;
import org.openqa.selenium.By;

public class AddToCartModalComponent extends BaseComponent {

    public AddToCartModalComponent(By rootLocator) {
        super(rootLocator);
    }

    private static final By TITLE = By.cssSelector(".modal-title");
    private static final By MESSAGE = By.cssSelector(".modal-body > p:first-child");
    private static final By VIEW_CART_LINK = By.cssSelector("a[href='/view_cart']");
    private static final By CONTINUE_SHOPPING_BUTTON = By.cssSelector(".close-modal");

    public String getTitle() {
        return getText(TITLE);
    }

    public String getMessage() {
        return getText(MESSAGE);
    }

    public CartPage viewCart() {
        navigate(VIEW_CART_LINK);
        CartPage cartPage = new CartPage();
        cartPage.waitUntilLoaded();
        return cartPage;
    }

    public void continueShopping() {
        click(CONTINUE_SHOPPING_BUTTON);
        waitUntilInvisible(CONTINUE_SHOPPING_BUTTON);
    }
}
