package com.shangin.automationexercise.assertions;

import org.testng.Assert;

import com.shangin.automationexercise.components.ProductDetailsComponent;

public class ProductDetailsAssertations {

    private ProductDetailsAssertations() {
    }

    public static void assertProductDetailInfoIsVisible(ProductDetailsComponent productDetails) {

        Assert.assertFalse(
                productDetails.getName().isBlank(),
                "Product name should be visible");
        Assert.assertFalse(
                productDetails.getCategory().isBlank(),
                "Product category should be visible");
        Assert.assertFalse(
                productDetails.getPrice().isBlank(),
                "Product price should be visible");
        Assert.assertFalse(
                productDetails.getAvailability().isBlank(),
                "Product availability should be visible");
        Assert.assertFalse(
                productDetails.getCondition().isBlank(),
                "Product condition should be visible");
        Assert.assertFalse(
                productDetails.getBrand().isBlank(),
                "Product brand should be visible");
    }

}
