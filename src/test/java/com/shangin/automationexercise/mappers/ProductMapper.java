package com.shangin.automationexercise.mappers;

import com.shangin.automationexercise.components.ProductListComponent;
import com.shangin.automationexercise.model.ExpectedProduct;
import java.util.List;

public class ProductMapper {

    private ProductMapper() {}

    public static List<ExpectedProduct> toExpectedProducts(ProductListComponent products) {
        return products.getProducts().stream()
                .map(product -> new ExpectedProduct(product.getName(), product.getPrice(), 1))
                .toList();
    }

    public static ExpectedProduct toExpectedProduct(ExpectedProduct product) {
        return new ExpectedProduct(product.name(), product.price(), 1);
    }
}
