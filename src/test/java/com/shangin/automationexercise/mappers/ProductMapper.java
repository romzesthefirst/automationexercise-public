package com.shangin.automationexercise.mappers;

import java.util.List;

import com.shangin.automationexercise.components.ProductListComponent;
import com.shangin.automationexercise.model.ExpectedProduct;

public class ProductMapper {
    
    private ProductMapper() {
    }

    public static List<ExpectedProduct> toExpectedProducts(ProductListComponent products) {
        return products.getProducts().stream()
                .map(product -> new ExpectedProduct(product.getName(), product.getPrice(), 1))
                .toList();
    }
}
