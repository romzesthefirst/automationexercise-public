package com.shangin.automationexercise.pages;

import org.openqa.selenium.By;

import com.shangin.automationexercise.base.BasePage;
import com.shangin.automationexercise.components.AddToCartModalComponent;
import com.shangin.automationexercise.components.ProductDetailsComponent;
import com.shangin.automationexercise.model.Review;

public class ProductDetailsPage extends BasePage {

    // product-information
    private static final By PRODUCT_INFORMATION = By.cssSelector(".product-information");

    // review
    private static final By WRITE_YOUR_REVIEW_LABEL
            = By.cssSelector("li[class='active'] > a[href='#reviews']");
    private static final By REVIEW_NAME_INPUT = By.id("name");
    private static final By REVIEW_EMAIL_INPUT = By.id("email");
    private static final By REVIEW_INPUT = By.id("review");
    private static final By SUBMIT_BUTTON = By.id("button-review");
    private static final By SUCCESS_ALERT = By.cssSelector("#review-form .alert-success");

    @Override
    public boolean isLoaded() {
        return isDisplayed(PRODUCT_INFORMATION);
    }

    @Override
    public void waitUntilLoaded() {
        waitUntilVisible(PRODUCT_INFORMATION);
    }

    public ProductDetailsComponent productDetails() {
        return new ProductDetailsComponent(PRODUCT_INFORMATION);
    }

    public boolean isOpened() {
        return getCurrentUrl().contains("/product_details/");
    }

    public AddToCartModalComponent addToCart() {
        productDetails().addToCart();
        return waitForAddToCartModal();
    }

    public boolean isWriteYourReviewIsVisible() {
        return isDisplayed(WRITE_YOUR_REVIEW_LABEL);
    }

    public void fillReview(Review review) {
        type(REVIEW_NAME_INPUT, review.name());
        type(REVIEW_EMAIL_INPUT, review.email());
        type(REVIEW_INPUT, review.review());
    }

    public void submitReview() {
        click(SUBMIT_BUTTON);
    }

    public String getSuccessMessage() {
        return getText(SUCCESS_ALERT);
    }

}
