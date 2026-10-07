package com.shangin.automationexercise.assertions;

import com.shangin.automationexercise.model.ActualProduct;
import com.shangin.automationexercise.model.ExpectedProduct;
import com.shangin.automationexercise.model.MonetaryValues;
import java.math.BigDecimal;
import java.util.List;
import org.testng.Assert;

public class CartAssertions {

    private CartAssertions() {}

    public static BigDecimal calculateExpectedTotal(List<ExpectedProduct> products) {

        return products.stream()
                .map(ExpectedProduct::total)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public static void assertProductsMatch(
            List<ActualProduct> actualProducts, List<ExpectedProduct> expectedProducts) {

        Assert.assertEquals(
                actualProducts.size(), expectedProducts.size(), "Unexpected number of products");

        for (ExpectedProduct expected : expectedProducts) {

            ActualProduct actual =
                    actualProducts.stream()
                            .filter(product -> product.name().equalsIgnoreCase(expected.name()))
                            .findFirst()
                            .orElseThrow(
                                    () ->
                                            new AssertionError(
                                                    "Product not found: " + expected.name()));

            Assert.assertEquals(
                    MonetaryValues.parse(actual.price())
                            .compareTo(MonetaryValues.parse(expected.price())),
                    0,
                    "Incorrect price for product: "
                            + expected.name()
                            + ". Expected: "
                            + expected.price()
                            + ", displayed: "
                            + actual.price());

            Assert.assertEquals(
                    actual.quantity(),
                    expected.quantity(),
                    "Incorrect quantity for product: " + expected.name());

            Assert.assertEquals(
                    actual.total().compareTo(expected.total()),
                    0,
                    "Incorrect displayed line total for product: "
                            + expected.name()
                            + ". Expected: "
                            + expected.total()
                            + ", displayed: "
                            + actual.displayedTotal());
        }
    }

    public static void assertProductsTotalPrice(
            BigDecimal actualTotal, List<ExpectedProduct> expectedProducts) {
        BigDecimal expectedTotal = calculateExpectedTotal(expectedProducts);
        Assert.assertEquals(
                actualTotal.compareTo(expectedTotal),
                0,
                "Unexpected total price of products. Expected: "
                        + expectedTotal
                        + ", displayed: "
                        + actualTotal);
    }
}
