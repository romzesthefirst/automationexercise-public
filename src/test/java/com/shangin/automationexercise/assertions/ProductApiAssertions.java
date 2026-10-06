package com.shangin.automationexercise.assertions;

import java.util.List;

import org.testng.Assert;

import com.shangin.automationexercise.api.models.ProductDto;

public class ProductApiAssertions {

    private ProductApiAssertions() {
    }

    public static void assertValidProducts(List<ProductDto> products) {
        
        Assert.assertFalse(products.isEmpty(), "Products list should not be empty");

        for (ProductDto product : products) {

            Assert.assertTrue(
                    product.id() > 0,
                    "Product id should be positive");

            Assert.assertNotNull(
                    product.name(),
                    "Product name should not be null");
            
            Assert.assertFalse(
                    product.name().isBlank(),
                    "Product name should not be blank");

            Assert.assertNotNull(
                    product.price(),
                    "Product price should not be null");
            
            Assert.assertFalse(
                    product.price().isBlank(),
                    "Product price should not be blank");

            Assert.assertNotNull(
                    product.brand(),
                    "Product brand should not be null");
            
            Assert.assertFalse(
                    product.brand().isBlank(),
                    "Product brand should not be blank");

            Assert.assertNotNull(
                    product.category(),
                    "Product category should not be null");

            Assert.assertNotNull(
                    product.category().category(),
                    "Category name should not be null");

            Assert.assertFalse(
                    product.category().category().isBlank(),
                    "Category name should not be blank");

            Assert.assertNotNull(
                    product.category().usertype(),
                    "User type should not be null");

            Assert.assertNotNull(
                    product.category().usertype().usertype(),
                    "User type value should not be null");

            Assert.assertFalse(
                    product.category().usertype().usertype().isBlank(),
                    "User type value should not be blank");
        }
    }
    
    private static String normalize(String value) {
        return value.toLowerCase().replaceAll("[\\s-]", "");
    }
    
    public static void assertValidSearchProducts(List<ProductDto> products, String query) {
        
        Assert.assertFalse(products.isEmpty(), "Expected search results for: " + query);
        String normalizedQuery = normalize(query);
        
        for (ProductDto product : products) {
            
            String normalizedProductName = normalize(product.name());
            
            Assert.assertTrue(
                    normalizedProductName.contains(normalizedQuery),
                    "Incorrect search result for \"" + query + "\". Found unexpected \"" + product.name() + "\".");
        }

    }
}
