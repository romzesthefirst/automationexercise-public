package com.shangin.automationexercise.assertions;

import java.util.List;

import org.testng.Assert;

import com.shangin.automationexercise.components.CartItemComponent;
import com.shangin.automationexercise.model.ExpectedProduct;
import com.shangin.automationexercise.pages.CartPage;

public class CartAssertions {
    private CartAssertions() {
    }

    public static
            void
            assertContainsProducts(CartPage cart, List<ExpectedProduct> expectedProducts) {
        List<CartItemComponent> actualProducts = cart.getCartItems();

        Assert.assertEquals(
                actualProducts.size(),
                expectedProducts.size(),
                "Cart items count is incorrect");

        for (ExpectedProduct expected : expectedProducts) {
            CartItemComponent actual = cart.getProduct(expected.name());

            Assert.assertEquals(
                    actual.getPrice(),
                    expected.price(),
                    "Incorrect price for: " + expected.name());

            Assert.assertEquals(
                    actual.getQuantity(),
                    expected.quantity(),
                    "Incorrect quantity for: " + expected.name());
        }
    }
}
