package com.shangin.automationexercise.tests.reportdemo;

import com.shangin.automationexercise.api.clients.ProductsApiClient;
import com.shangin.automationexercise.driver.DriverFactory;
import com.shangin.automationexercise.driver.DriverManager;
import com.shangin.automationexercise.listeners.TestListener;
import io.qameta.allure.Allure;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/** Explicitly selected demo, excluded from all normal suite profiles. */
@Epic("Report demonstration")
@Feature("Synthetic failure diagnostics")
@Listeners(TestListener.class)
public class AllureDemoTest {
    @Test(description = "Browse the public product catalog with HTTP evidence")
    public void catalogResponse() {
        Allure.step("Verify that browsing the catalog succeeds", () -> {
            var response = new ProductsApiClient().getProductsList();
            Assert.assertEquals(response.statusCode(), 200);
            Assert.assertTrue(response.body().asString().contains("products"));
        });
    }

    @Test(description = "Deliberate UI assertion failure on a synthetic local page")
    public void syntheticCheckoutFailure() {
        DriverManager.setDriver(DriverFactory.createDriver());
        Allure.step("Open a synthetic checkout confirmation", () -> DriverManager.getDriver().get(
                "data:text/html,<title>Report demonstration</title><h1>Order pending</h1><p>Synthetic checkout; no account or payment.</p>"));
        Allure.step("Verify that the order was confirmed (deliberate failure)", () ->
                Assert.assertEquals(DriverManager.getDriver().getTitle(), "Order confirmed",
                        "Demo failure: checkout is pending"));
    }

    @AfterMethod(alwaysRun = true)
    public void closeBrowser() { DriverManager.quitDriver(); }
}
