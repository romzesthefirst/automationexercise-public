package com.shangin.automationexercise.pages;

import org.openqa.selenium.By;

import com.shangin.automationexercise.base.BasePage;
import com.shangin.automationexercise.components.AddToCartModalComponent;
import com.shangin.automationexercise.components.BrandsComponent;
import com.shangin.automationexercise.components.ProductListComponent;

public class ProductsPage extends BasePage {

    private static final By PRODUCTS_SECTION = By.cssSelector(".features_items");
    private static final By PAGE_TITLE = By.cssSelector("h2.title.text-center");
    private static final By SEARCH_INPUT = By.id("search_product");
    private static final By SEARCH_BUTTON = By.id("submit_search");
    private static final By CATEGORIES = By.id("accordian");
    private static final By BRANDS = By.cssSelector(".brands_products");

    @Override
    public boolean isLoaded() {
        return isDisplayed(PAGE_TITLE);
    }

    @Override
    public void waitUntilLoaded() {
        waitUntilVisible(PAGE_TITLE);
    }

    public ProductListComponent products() {
        return new ProductListComponent(find(PRODUCTS_SECTION));
    }

    public boolean isOpened() {
        return getCurrentUrl().contains("/products");
    }

    public ProductsPage search(String query) {
        String oldTitle = getText(PAGE_TITLE);
        type(SEARCH_INPUT, query);
        click(SEARCH_BUTTON);
        waitUntilTextChanged(PAGE_TITLE, oldTitle);
        return this;
    }

    public String getPageTitle() {
        return getText(PAGE_TITLE);
    }

    public AddToCartModalComponent addProductToCart(int index) {
        products().addProductToCart(index);
        return waitForAddToCartModal();
    }


    public void addAllProductsToCart() {
        int productCount = products().getProductCount();
        for (int i = 0; i < productCount; i++) {
            addProductToCart(i).continueShopping();
        }
    }
    
    public boolean isCategoriesVisible() {
        return isDisplayed(CATEGORIES);
    }

    public boolean isBrandsVisible() {
        return isDisplayed(BRANDS);
    }

    public BrandsComponent brands() {
        return new BrandsComponent(find(BRANDS));
    }


}
