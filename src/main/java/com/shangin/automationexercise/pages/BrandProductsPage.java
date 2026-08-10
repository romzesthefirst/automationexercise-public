package com.shangin.automationexercise.pages;

import org.openqa.selenium.By;

import com.shangin.automationexercise.base.BasePage;

public class BrandProductsPage extends BasePage {

    private static final By HEADER = By.cssSelector(".features_items > h2");
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
