package com.shangin.automationexercise.pages;

import com.shangin.automationexercise.base.BasePage;
import com.shangin.automationexercise.components.BrandsComponent;
import com.shangin.automationexercise.components.CategoriesComponent;
import com.shangin.automationexercise.components.ProductListComponent;
import org.openqa.selenium.By;

public class CategoryProductsPage extends BasePage {

    private static final By HEADER = By.cssSelector(".features_items > h2");
    private static final By PRODUCTS_SECTION = By.cssSelector(".features_items");
    private static final By CATEGORIES = By.id("accordian");
    private static final By BRANDS = By.cssSelector(".brands_products");

    @Override
    public boolean isLoaded() {
        return getCurrentUrl().contains("/category_products/")
                && isDisplayed(By.cssSelector(".features_items .product-image-wrapper"));
    }

    @Override
    public void waitUntilLoaded() {
        wait.until(ignored -> isLoaded());
        removeAds();
    }

    public ProductListComponent products() {
        return new ProductListComponent(PRODUCTS_SECTION);
    }

    public CategoriesComponent categories() {
        return new CategoriesComponent(CATEGORIES);
    }

    public BrandsComponent brands() {
        return new BrandsComponent(BRANDS);
    }

    public String getTitle() {
        return getText(HEADER);
    }
}
