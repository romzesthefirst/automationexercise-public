package com.shangin.automationexercise.tests.support;

import com.shangin.automationexercise.assertions.CartAssertions;
import com.shangin.automationexercise.model.ActualProduct;
import com.shangin.automationexercise.model.ExpectedProduct;
import com.shangin.automationexercise.model.MonetaryValues;
import java.math.BigDecimal;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class MonetaryValuesTest {
    @DataProvider
    public Object[][] prices() {
        return new Object[][] {
            {"Rs. 500", "500"},
            {"12.50", "12.50"},
            {" Rs. 12.50 ", "12.50"},
            {"Rs.12.5", "12.5"},
            {"Rs. 1,234.56", "1234.56"},
            {"1,234,567.89", "1234567.89"},
            {"0.00", "0.00"},
            {"999999999999999999999.1234", "999999999999999999999.1234"}
        };
    }

    @Test(dataProvider = "prices")
    public void preservesIntegerAndFractionalValues(String text, String expected) {
        Assert.assertEquals(MonetaryValues.parse(text), new BigDecimal(expected));
    }

    @DataProvider
    public Object[][] invalidPrices() {
        return new Object[][] {
            {null},
            {""},
            {" "},
            {"Rs."},
            {"Rs. -12.50"},
            {"-12"},
            {"+12"},
            {"12.50.0"},
            {"12,50"},
            {"1,23,456"},
            {"1 234.56"},
            {"1.234,56"},
            {"USD 12.50"},
            {"12abc50"},
            {"NaN"},
            {"1e3"},
            {"12."},
            {".50"}
        };
    }

    @Test(dataProvider = "invalidPrices")
    public void rejectsInvalidValuesWithoutSilentlyChangingThem(String text) {
        IllegalArgumentException error =
                Assert.expectThrows(
                        IllegalArgumentException.class, () -> MonetaryValues.parse(text));
        Assert.assertTrue(error.getMessage().contains("Invalid monetary value"));
    }

    @Test
    public void quantitiesAndTotalsUseExactArithmeticAndNumericComparison() {
        List<ExpectedProduct> expected =
                List.of(
                        new ExpectedProduct("Top", "Rs. 12.50", 3),
                        new ExpectedProduct("Shirt", "0.10", 3));
        Assert.assertEquals(
                CartAssertions.calculateExpectedTotal(expected), new BigDecimal("37.80"));
        CartAssertions.assertProductsMatch(
                List.of(
                        new ActualProduct("Top", "12.500", 3, "Rs. 37.5"),
                        new ActualProduct("Shirt", "Rs. 0.1", 3, "0.300")),
                expected);
        CartAssertions.assertProductsTotalPrice(new BigDecimal("37.8000"), expected);
        Assert.expectThrows(
                AssertionError.class,
                () -> CartAssertions.assertProductsTotalPrice(new BigDecimal("3780"), expected));
        Assert.assertEquals(new ExpectedProduct("Top", "12.50", 0).total().signum(), 0);
        Assert.assertEquals(
                new ExpectedProduct("Top", "12.50", 1).total(), new BigDecimal("12.50"));
    }

    @Test
    public void invalidProductAmountsCannotPassAssertions() {
        Assert.expectThrows(
                IllegalArgumentException.class,
                () -> new ExpectedProduct("Top", "Rs. 12abc50", 2).total());
        Assert.expectThrows(
                IllegalArgumentException.class,
                () -> new ActualProduct("Top", "12.50", 2, "Rs. 25abc").total());
        Assert.expectThrows(
                IllegalArgumentException.class,
                () ->
                        CartAssertions.assertProductsMatch(
                                List.of(new ActualProduct("Top", "bad", 2, "25")),
                                List.of(new ExpectedProduct("Top", "12.50", 2))));
    }

    @DataProvider
    public Object[][] invoiceAmounts() {
        return new Object[][] {
            {"500.00", "500"},
            {"12.50", "12.5"},
            {"0.00", "0"},
            {"1000000", "1000000"},
            {"0.0001", "0.0001"}
        };
    }

    @Test(dataProvider = "invoiceAmounts")
    public void formatsInvoiceAmountsAsPlainNumbers(String price, String expected) {
        Assert.assertEquals(MonetaryValues.format(MonetaryValues.parse("Rs. " + price)), expected);
    }

    @Test
    public void rejectsInvalidFormattingAmounts() {
        Assert.expectThrows(IllegalArgumentException.class, () -> MonetaryValues.format(null));
        Assert.expectThrows(
                IllegalArgumentException.class,
                () -> MonetaryValues.format(new BigDecimal("-0.01")));
    }
}
