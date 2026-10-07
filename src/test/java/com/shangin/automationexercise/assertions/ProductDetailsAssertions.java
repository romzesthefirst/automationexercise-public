package com.shangin.automationexercise.assertions;

import com.shangin.automationexercise.components.ProductDetailsComponent;
import org.testng.Assert;

public class ProductDetailsAssertions {

    private ProductDetailsAssertions() {}

    public static void assertProductDetailInfoIsVisible(ProductDetailsComponent productDetails) {

        Assert.assertFalse(productDetails.getName().isBlank(), "Product name should be visible");
        Assert.assertFalse(
                productDetails.getCategory().isBlank(), "Product category should be visible");
        Assert.assertFalse(productDetails.getPrice().isBlank(), "Product price should be visible");
        Assert.assertFalse(
                productDetails.getAvailability().isBlank(),
                "Product availability should be visible");
        Assert.assertFalse(
                productDetails.getCondition().isBlank(), "Product condition should be visible");
        Assert.assertFalse(productDetails.getBrand().isBlank(), "Product brand should be visible");
    }
}
