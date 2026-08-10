package com.shangin.automationexercise.pages;

import org.openqa.selenium.By;

import com.shangin.automationexercise.base.BasePage;
import com.shangin.automationexercise.components.AddToCartModalComponent;
import com.shangin.automationexercise.model.Review;

public class ProductDetailsPage extends BasePage {

    // product-information
    private static final By PRODUCT_NAME = By.cssSelector(".product-information h2");
    private static final By PRODUCT_CATEGORY = By.cssSelector(".product-information h2 + p");
    private static final By PRODUCT_PRICE = By.cssSelector(".product-information span > span");
    private static final By PRODUCT_AVAILABILITY
            = By.xpath("//p[b[normalize-space()='Availability:']]");
    private static final By PRODUCT_CONDITION = By.xpath("//p[b[normalize-space()='Condition:']]");
    private static final By PRODUCT_BRAND = By.xpath("//p[b[normalize-space()='Brand:']]");
    private static final By PRODUCT_QUANTITY = By.id("quantity");
    private static final By ADD_TO_CART_BUTTON = By.cssSelector(".cart");
    
    // review
    private static final By WRITE_YOUR_REVIEW_LABEL = By.cssSelector("li[class='active'] > a[href='#reviews']");
    private static final By REVIEW_NAME_INPUT = By.id("name");
    private static final By REVIEW_EMAIL_INPUT = By.id("email");
    private static final By REVIEW_INPUT = By.id("review");
    private static final By SUBMIT_BUTTON = By.id("button-review");
    private static final By SUCCESS_ALERT = By.cssSelector("#review-form .alert-success");
    
    @Override
    public boolean isLoaded() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void waitUntilLoaded() {
        // TODO Auto-generated method stub

    }

    public boolean isOpened() {
        return getCurrentUrl().contains("/product_details/");
    }

    public String getProductName() {
        return getText(PRODUCT_NAME);
    }

    public String getProductCategory() {
        return getText(PRODUCT_CATEGORY);
    }

    public String getProductPrice() {
        return getText(PRODUCT_PRICE);
    }

    public String getProductAvailability() {
        return getText(PRODUCT_AVAILABILITY);
    }

    public String getProductCondition() {
        return getText(PRODUCT_CONDITION);
    }

    public String getProductBrand() {
        return getText(PRODUCT_BRAND);
    }

    public void setQuantity(int quantity) {
        if (quantity < 1) {
            throw new IllegalArgumentException("Quantity must be at least 1");
        }

        clear(PRODUCT_QUANTITY);
        type(PRODUCT_QUANTITY, String.valueOf(quantity));
    }

    public AddToCartModalComponent addToCart() {
        click(ADD_TO_CART_BUTTON);
        return waitForAddToCartModal();
    }

    public boolean isWriteYourReviewIsVisible() {
        return isDisplayed(WRITE_YOUR_REVIEW_LABEL);
    }

    public void fillReview(Review review) {
        //name, email and review
        type(REVIEW_NAME_INPUT, review.name());
        type(REVIEW_EMAIL_INPUT, review.email());
        type(REVIEW_INPUT, review.review());
    }

    public void submitReview() {
        click(SUBMIT_BUTTON);
    }
    
    public String getSuccessMessage() {
        return find(SUCCESS_ALERT).getText();
    }

}
