package com.shangin.automationexercise.pages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;

import com.shangin.automationexercise.base.BasePage;
import com.shangin.automationexercise.components.AddToCartModalComponent;
import com.shangin.automationexercise.components.BrandsComponent;
import com.shangin.automationexercise.components.CategoriesComponent;
import com.shangin.automationexercise.components.ProductCardComponent;
import com.shangin.automationexercise.components.ProductListComponent;
import com.shangin.automationexercise.config.ConfigReader;
import com.shangin.automationexercise.driver.DriverManager;
import com.shangin.automationexercise.model.AddToCartResult;
import com.shangin.automationexercise.model.ExpectedProduct;

public class HomePage extends BasePage {

    private static final By SLIDER_CAROUSEL = By.id("slider-carousel");
    private static final By PRODUCTS_SECTION = By.cssSelector(".features_items");
    private static final By CATEGORIES = By.id("accordian");
    private static final By BRANDS = By.cssSelector(".brands_products");
    private static final By RECOMMENDED_ITEMS_CAROUSEL = By.id("recommended-item-carousel");
    private static final By RECOMMENDED_ITEMS_CAROUSEL_TITLE
            = By.cssSelector(".recommended_items > .title");
    private static final By VISIBLE_PRODUCTS_IN_ITEM_CAROUSEL
            = By.cssSelector(".item.active .product-image-wrapper");
    
    private static final By ACTIVE_SLIDE_TITLE = By.cssSelector(".item.active h1");
    private static final By ACTIVE_SLIDE_SUBTITLE = By.cssSelector(".item.active h2");
    private static final By ACTIVE_SLIDE_DESCRIPTION = By.cssSelector(".item.active p");

    public static HomePage open() {
        WebDriver driver = DriverManager.getDriver();
        driver.get(ConfigReader.getBaseUrl());
        HomePage page = new HomePage();
        page.waitUntilLoaded();
        return page;
    }

    public CategoriesComponent categories() {
        return new CategoriesComponent(CATEGORIES);
    }

    public BrandsComponent brands() {
        return new BrandsComponent(BRANDS);
    }

    public boolean isOpened() {
        return getCurrentUrl().equals(ConfigReader.getBaseUrl());
    }

    @Override
    public void waitUntilLoaded() {
        removeAds();
        waitUntilVisible(SLIDER_CAROUSEL);
    }

    @Override
    public boolean isLoaded() {
        return isDisplayed(SLIDER_CAROUSEL);
    }

    public ProductListComponent products() {
        return new ProductListComponent(PRODUCTS_SECTION);
    }

    public ProductListComponent recommendedItems() {
        return new ProductListComponent(RECOMMENDED_ITEMS_CAROUSEL);
    }

    public AddToCartModalComponent addProductToCart(int index) {
        products().addProductToCart(index);
        return waitForAddToCartModal();
    }
    
    public AddToCartModalComponent addProductToCart(String name) {
        products().addProductToCart(name);
        return waitForAddToCartModal();
    }

    public boolean isCategoriesVisible() {
        return isDisplayed(CATEGORIES);
    }

    public boolean isBrandsVisible() {
        return isDisplayed(BRANDS);
    }

    public void scrollToRecommendedItems() {
        scrollPageToElement(find(RECOMMENDED_ITEMS_CAROUSEL));
    }

    public String getCarouselItemsTitle() {
        return getText(RECOMMENDED_ITEMS_CAROUSEL_TITLE);
    }

    public List<ProductCardComponent> getVisibleProducts() {
        return findAll(VISIBLE_PRODUCTS_IN_ITEM_CAROUSEL).stream().map(ProductCardComponent::new)
                .toList();
    }

    public ProductCardComponent getFirstVisibleRecommendedProductToCart() {
        return getVisibleProducts().stream().findFirst()
                .orElseThrow(() -> new NoSuchElementException("No visible product found"));
    }

    public AddToCartResult addFirstVisibleRecommendedProductToCart() {
        ProductCardComponent product = getFirstVisibleRecommendedProductToCart();
        ExpectedProduct productInfo = product.getInfo();
        product.addToCart();
        AddToCartModalComponent modal = waitForAddToCartModal();
        return new AddToCartResult(productInfo, modal);
    }
    
    public String getActiveSlideTitle() {
        return find(ACTIVE_SLIDE_TITLE).getText();
    }
    
    public String getActiveSlideSubtitle() {
        return find(ACTIVE_SLIDE_SUBTITLE).getText();
    }
    
    public String getActiveSlideDescribtion() {
        return find(ACTIVE_SLIDE_DESCRIPTION).getText();
    }

}
