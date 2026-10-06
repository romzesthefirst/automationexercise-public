package com.shangin.automationexercise.tests.support;

import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import com.shangin.automationexercise.api.models.ProductDto;
import com.shangin.automationexercise.api.support.OwnedAccounts;
import com.shangin.automationexercise.assertions.CartAssertions;
import com.shangin.automationexercise.assertions.ProductApiAssertions;
import com.shangin.automationexercise.components.ProductListComponent;
import com.shangin.automationexercise.constants.UiMessages;
import com.shangin.automationexercise.cucumber.context.ScenarioContext;
import com.shangin.automationexercise.cucumber.steps.NavigationSteps;
import com.shangin.automationexercise.cucumber.steps.ProductsSteps;
import com.shangin.automationexercise.driver.DriverManager;
import com.shangin.automationexercise.model.ActualProduct;
import com.shangin.automationexercise.model.ExpectedProduct;
import com.shangin.automationexercise.pages.CartPage;
import com.shangin.automationexercise.pages.CheckoutPage;

public class AssertionCorrectnessTest {
    private static final ExpectedProduct EXPECTED = new ExpectedProduct("Blue Top", "Rs. 500", 4);

    @AfterMethod(alwaysRun = true)
    public void clearDriver() {
        DriverManager.quitDriver();
    }

    @Test public void positiveApiSearchRejectsEmptyAndUnrelatedResults() {
        fails("Expected search results for: tshirt",
                () -> ProductApiAssertions.assertValidSearchProducts(List.of(), "tshirt"));
        ProductApiAssertions.assertValidSearchProducts(List.of(product("Men T-Shirt")), "tshirt");
        fails("Incorrect search result", () -> ProductApiAssertions.assertValidSearchProducts(
                List.of(product("Blue Top")), "tshirt"));
    }

    @Test public void positiveUiSearchRejectsEmptyResultsInActualBddStep() {
        WebElement empty = element("", Map.of());
        useDom(Map.of(By.cssSelector(".features_items"), List.of(empty)));
        ProductListComponent products = new ProductListComponent(empty);
        // A negative case checks emptiness explicitly, rather than an all-match predicate.
        Assert.assertFalse(products.hasProducts());
        Assert.assertEquals(products.getProductCount(), 0);
        Assert.assertFalse(products.allProductsContain("tshirt"));
        fails("Expected nonempty search results", () -> new ProductsSteps(context())
                .allDisplayedProductsShouldMatchSearchQuery("tshirt"));
    }

    @Test public void positiveUiSearchRequiresEveryResultToMatch() {
        WebElement card = element("", Map.of(By.cssSelector(".productinfo > p"),
                List.of(element("Men T-Shirt", Map.of()))));
        WebElement section = element("", Map.of(By.cssSelector(".product-image-wrapper"), List.of(card)));
        useDom(Map.of(By.cssSelector(".features_items"), List.of(section)));
        ProductsSteps steps = new ProductsSteps(context());
        steps.allDisplayedProductsShouldMatchSearchQuery("tshirt");
        fails("Expected nonempty search results", () -> steps.allDisplayedProductsShouldMatchSearchQuery("saree"));
    }

    @Test public void textStepUsesItsExpectedArgument() {
        useDom(Map.of(By.cssSelector(".item.active h2"), List.of(element(UiMessages.CAROUSEL_SUBTITLE, Map.of()))));
        NavigationSteps steps = new NavigationSteps(context());
        steps.homePageTextIsVisible(UiMessages.CAROUSEL_SUBTITLE);
        fails("Unexpected home page subtitle", () -> steps.homePageTextIsVisible("Wrong subtitle"));
    }

    @Test public void quantityStepUsesItsArgumentWithoutChangingContext() {
        useCart("Rs. 2000");
        ScenarioContext context = context();
        context.addExpectedProduct(EXPECTED);
        ProductsSteps steps = new ProductsSteps(context);
        steps.productShouldBePresentInCartWithQuantity(4);
        fails("Incorrect quantity for product: Blue Top", () -> steps.productShouldBePresentInCartWithQuantity(5));
        Assert.assertEquals(context.getExpectedProducts(), List.of(EXPECTED));
    }

    @Test public void cartAndCheckoutReadDisplayedLineTotals() {
        useCart("Rs. 2000");
        CartAssertions.assertProductsMatch(new CartPage().getActualProducts(), List.of(EXPECTED));
        CartAssertions.assertProductsMatch(new CheckoutPage().getActualProducts(), List.of(EXPECTED));
        useCart("Rs. 1999");
        fails("Incorrect displayed line total for product: Blue Top", () -> CartAssertions
                .assertProductsMatch(new CartPage().getActualProducts(), List.of(EXPECTED)));
        fails("Incorrect displayed line total for product: Blue Top", () -> CartAssertions
                .assertProductsMatch(new CheckoutPage().getActualProducts(), List.of(EXPECTED)));
    }

    @Test public void wrongPriceQuantityAndMissingProductCannotPass() {
        fails("Incorrect price", () -> match(new ActualProduct("Blue Top", "Rs. 501", 4, "Rs. 2000")));
        fails("Incorrect quantity", () -> match(new ActualProduct("Blue Top", "Rs. 500", 3, "Rs. 2000")));
        fails("Unexpected number of products", () -> CartAssertions.assertProductsMatch(List.of(), List.of(EXPECTED)));
    }

    @Test public void checkoutTotalAndIndividualTotalsAreIndependent() {
        List<ExpectedProduct> expected = List.of(EXPECTED, new ExpectedProduct("Men Tshirt", "Rs. 400", 1));
        List<ActualProduct> actual = List.of(new ActualProduct("Blue Top", "Rs. 500", 4, "Rs. 1999"),
                new ActualProduct("Men Tshirt", "Rs. 400", 1, "Rs. 401"));
        CartAssertions.assertProductsTotalPrice(new BigDecimal("2400"), expected);
        fails("Incorrect displayed line total", () -> CartAssertions.assertProductsMatch(actual, expected));
        fails("Unexpected total price", () -> CartAssertions.assertProductsTotalPrice(new BigDecimal("2399"), expected));
    }

    @Test public void lineTotalsPreserveDecimalPricesAndIgnoreScale() {
        CartAssertions.assertProductsMatch(List.of(new ActualProduct("Top", "Rs. 12.50", 2, "Rs. 25.0")),
                List.of(new ExpectedProduct("Top", "Rs. 12.50", 2)));
    }

    private static void match(ActualProduct actual) {
        CartAssertions.assertProductsMatch(List.of(actual), List.of(EXPECTED));
    }

    private static ScenarioContext context() {
        return new ScenarioContext(new OwnedAccounts());
    }

    private static ProductDto product(String name) {
        return new ProductDto(1, name, "Rs. 500", "Brand", null);
    }

    private static void fails(String message, Runnable assertion) {
        AssertionError error = Assert.expectThrows(AssertionError.class, assertion::run);
        Assert.assertTrue(error.getMessage().contains(message), "Unexpected assertion explanation: " + error.getMessage());
    }

    private static void useCart(String total) {
        WebElement row = element("", Map.of(
                By.cssSelector(".cart_description h4"), List.of(element("Blue Top", Map.of())),
                By.cssSelector(".cart_price p"), List.of(element("Rs. 500", Map.of())),
                By.cssSelector(".cart_quantity button"), List.of(element("4", Map.of())),
                By.cssSelector(".cart_total_price"), List.of(element(total, Map.of()))));
        useDom(Map.of(By.cssSelector("tr[id^='product-']"), List.of(row)));
    }

    private static void useDom(Map<By, List<WebElement>> children) {
        DriverManager.setDriver((WebDriver) Proxy.newProxyInstance(WebDriver.class.getClassLoader(),
                new Class<?>[] { WebDriver.class }, (proxy, method, args) -> switch (method.getName()) {
                    case "findElement" -> first(children, (By) args[0]);
                    case "findElements" -> children.getOrDefault((By) args[0], List.of());
                    case "quit" -> null;
                    case "toString" -> "Assertion DOM driver";
                    default -> throw new UnsupportedOperationException(method.getName());
                }));
    }

    private static WebElement element(String text, Map<By, List<WebElement>> children) {
        return (WebElement) Proxy.newProxyInstance(WebElement.class.getClassLoader(),
                new Class<?>[] { WebElement.class }, (proxy, method, args) -> switch (method.getName()) {
                    case "getText" -> text;
                    case "isDisplayed", "isEnabled" -> true;
                    case "findElement" -> first(children, (By) args[0]);
                    case "findElements" -> children.getOrDefault((By) args[0], List.of());
                    case "toString" -> "Assertion DOM element: " + text;
                    default -> throw new UnsupportedOperationException(method.getName());
                });
    }

    private static WebElement first(Map<By, List<WebElement>> children, By locator) {
        return children.getOrDefault(locator, List.of()).stream().findFirst()
                .orElseThrow(() -> new NoSuchElementException(locator.toString()));
    }
}
