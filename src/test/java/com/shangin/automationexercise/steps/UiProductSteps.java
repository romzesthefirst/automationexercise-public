package com.shangin.automationexercise.steps;

import java.util.ArrayList;
import java.util.List;

import com.shangin.automationexercise.components.AddToCartModalComponent;
import com.shangin.automationexercise.components.ProductCardComponent;
import com.shangin.automationexercise.model.AddProductsResult;
import com.shangin.automationexercise.model.ExpectedProduct;
import com.shangin.automationexercise.pages.ProductsPage;

public class UiProductSteps {

    public AddProductsResult addProductsToCart(ProductsPage productsPage, String... productNames) {

        if (productNames.length == 0) {
            throw new IllegalArgumentException("At least one product must be provided");
        }

        List<ExpectedProduct> expectedProducts = new ArrayList<>();

        for (int i = 0; i < productNames.length; i++) {
            ProductCardComponent productCard
                    = productsPage.products().getProductCard(productNames[i]);

            expectedProducts
                    .add(new ExpectedProduct(productCard.getName(), productCard.getPrice(), 1));

            AddToCartModalComponent modal = productsPage.addProductToCart(productNames[i]);

            if (i == productNames.length - 1) {
                return new AddProductsResult(modal, expectedProducts);
            }

            modal.continueShopping();
        }

        throw new IllegalStateException("Unexpected state");
    }

}
