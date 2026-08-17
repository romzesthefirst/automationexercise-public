package com.shangin.automationexercise.pages;

import org.openqa.selenium.By;

import com.shangin.automationexercise.base.BasePage;
import com.shangin.automationexercise.components.BrandsComponent;
import com.shangin.automationexercise.components.CategoriesComponent;
import com.shangin.automationexercise.components.ProductListComponent;

public class BrandProductsPage extends BasePage {

    private static final By HEADER = By.cssSelector(".features_items > h2");
    private static final By PRODUCTS_SECTION = By.cssSelector(".features_items");
    private static final By CATEGORIES = By.id("accordian");
    private static final By BRANDS = By.cssSelector(".brands_products");

    @Override
    public boolean isLoaded() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void waitUntilLoaded() {
        // TODO Auto-generated method stub

    }
    
    public CategoriesComponent categories() {
        return new CategoriesComponent(find(CATEGORIES));
    }

    public BrandsComponent brands() {
        return new BrandsComponent(find(BRANDS));
    }

    public ProductListComponent products() {
        return new ProductListComponent(find(PRODUCTS_SECTION));
    }

    public boolean isCategoriesVisible() {
        return isDisplayed(CATEGORIES);
    }

    public boolean isBrandsVisible() {
        return isDisplayed(BRANDS);
    }

    public String getTitle() {
        return find(HEADER).getText();
    }
}
