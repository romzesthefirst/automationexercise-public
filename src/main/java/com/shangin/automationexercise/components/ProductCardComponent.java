package com.shangin.automationexercise.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.shangin.automationexercise.base.BaseComponent;
import com.shangin.automationexercise.model.ExpectedProduct;
import com.shangin.automationexercise.pages.ProductDetailsPage;

public class ProductCardComponent extends BaseComponent {

    private static final By NAME = By.cssSelector(".productinfo > p");
    private static final By PRICE = By.cssSelector(".productinfo > h2");
    private static final By ADD_TO_CART = By.cssSelector(".add-to-cart");
    private static final By VIEW_PRODUCT = By.cssSelector("a[href*='product_details']");

    public ProductCardComponent(WebElement root) {
        super(root);
    }

    public String getIdentifier() {
        return find(VIEW_PRODUCT).getAttribute("href");
    }

    public String getName() {
        return getText(NAME);
    }

    public String getPrice() {
        return getText(PRICE);
    }

    public ProductDetailsPage viewProduct() {
        navigate(VIEW_PRODUCT);
        ProductDetailsPage productDetailsPage = new ProductDetailsPage();
        productDetailsPage.waitUntilLoaded();
        return productDetailsPage;
    }

    public void addToCart() {
        hover(find(ADD_TO_CART));
        click(ADD_TO_CART);
    }
    
    public ExpectedProduct getInfo() {
        return new ExpectedProduct(
                getName(),
                getPrice(),
                1
        );
    }

}
