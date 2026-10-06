package com.shangin.automationexercise.tests.support;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import com.sun.net.httpserver.HttpServer;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.Assert;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.shangin.automationexercise.driver.DriverFactory;
import com.shangin.automationexercise.driver.DriverManager;
import com.shangin.automationexercise.pages.*;
import com.shangin.automationexercise.components.ProductCardComponent;
import com.shangin.automationexercise.support.AdsHandler;

/** Browser acceptance for delayed DOM changes, followed by real-site navigation/search. */
public class PageReadinessLiveTest {
    private HttpServer server;
    private String origin;
    private volatile String html;

    @BeforeClass public void startServer() throws Exception {
        if (!Boolean.getBoolean("readiness.live")) {
            throw new org.testng.SkipException("Enable with -Dreadiness.live=true");
        }
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", exchange -> {
            String content = exchange.getRequestURI().getPath().equals("/payment_done")
                    ? "<h2 data-qa='order-placed'>Order placed</h2>" : html;
            byte[] body = content.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=utf-8");
            exchange.sendResponseHeaders(200, body.length);
            exchange.getResponseBody().write(body);
            exchange.close();
        });
        server.start();
        origin = "http://127.0.0.1:" + server.getAddress().getPort();
    }

    @BeforeMethod public void startBrowser() { DriverManager.setDriver(DriverFactory.createDriver()); }
    @AfterMethod(alwaysRun = true) public void closeBrowser() { DriverManager.quitDriver(); }
    @AfterClass(alwaysRun = true) public void stopServer() { if (server != null) { server.stop(0); } }

    private void open(String path, String body) {
        html = "<!doctype html><html><body>" + body + "</body></html>";
        DriverManager.getDriver().get(origin + path);
    }
    private void script(String code) { ((JavascriptExecutor) DriverManager.getDriver()).executeScript(code); }

    @Test public void delayedPageMarkersMustBeReady() {
        open("/category_products/1", "<div class='features_items'><h2>Women - Dress Products</h2></div>");
        CategoryProductsPage category = new CategoryProductsPage();
        Assert.assertFalse(category.isLoaded());
        script("setTimeout(() => document.querySelector('.features_items').insertAdjacentHTML('beforeend', '<div class=product-image-wrapper>Product</div>'), 300)");
        category.waitUntilLoaded();
        Assert.assertTrue(category.isLoaded());
        open("/brand_products/Polo", "<div class='features_items'><h2>Brand - Polo Products</h2><div class='product-image-wrapper'>Product</div></div>");
        BrandProductsPage brand = new BrandProductsPage();
        brand.waitUntilLoaded();
        Assert.assertTrue(brand.isLoaded());
        open("/api_list", "<h2>APIs List for practice</h2><a href='#collapse1'>API 1</a>");
        ApiListPage api = new ApiListPage();
        api.waitUntilLoaded();
        Assert.assertTrue(api.isLoaded());
        open("/checkout", "<div data-qa='checkout-info'>Checkout</div><div id='address_delivery'>Delivery</div><div id='address_invoice'>Invoice</div><table><tr id='product-1'><td>Product</td></tr></table><textarea name='message'></textarea>");
        CheckoutPage checkout = new CheckoutPage();
        Assert.assertFalse(checkout.isLoaded());
        script("setTimeout(() => document.body.insertAdjacentHTML('beforeend', '<a href=/payment>Place order</a>'), 300)");
        checkout.waitUntilLoaded();
        Assert.assertTrue(checkout.isLoaded());
    }

    @Test public void delayedMessagesAreAwaited() {
        open("/contact_us", "<div class='status alert alert-success' style='display:none'>Contact accepted</div>");
        script("setTimeout(() => document.querySelector('.status').style.display='block', 300)");
        Assert.assertEquals(new ContactUsPage().getSuccessMessage(), "Contact accepted");
        open("/product_details/1", "<div id='review-form'><div class='alert-success' style='display:none'>Review accepted</div></div>");
        script("setTimeout(() => document.querySelector('.alert-success').style.display='block', 300)");
        Assert.assertEquals(new ProductDetailsPage().getSuccessMessage(), "Review accepted");
    }

    @Test public void containerIsReacquiredAndItemSnapshotFailsClearly() {
        open("/products", "<div class='features_items'><div class='product-image-wrapper'><div class='productinfo'><p>Old</p></div></div></div>");
        var list = new ProductsPage().products();
        ProductCardComponent item = list.getProductCard(0);
        script("document.querySelector('.features_items').outerHTML = '<div class=features_items><div class=product-image-wrapper><div class=productinfo><p>New</p></div></div></div>'");
        Assert.assertEquals(list.getProductCard(0).getName(), "New");
        Assert.expectThrows(IllegalStateException.class, item::getName);
    }

    @Test public void delayedPaymentMessageSurvivesRedirectAndOldValueIsCleared() {
        open("/payment", """
                <div id='success_message' style='display:none'>Order accepted</div>
                <button data-qa='pay-button' onclick="setTimeout(() => {
                    document.querySelector('#success_message').style.display='block';
                    setTimeout(() => location.href='/payment_done', 20);
                }, 300)">Pay</button>
                """);
        script("sessionStorage.setItem('orderSuccessMessage', 'Old order')");
        Assert.assertEquals(new PaymentPage().payAndGetResultMessage(), "Order accepted");
        Assert.assertTrue(new PaymentDonePage().isLoaded());
    }

    @Test public void adsHandlingCanBeDisabledAndEnabled() {
        String original = System.getProperty("ads.handling.enabled");
        try {
            System.setProperty("ads.handling.enabled", "false");
            open("/ads", """
                    <div class='adsbygoogle'>Ad</div>
                    <p id='product-name'>Premium Polo <a class='google-anno'><svg></svg>&nbsp;<span class='google-anno-t'>T-Shirts</span></a><div class='google-anno-sc'>Shop T-Shirts</div></p>
                    """);
            AdsHandler.removeGoogleAds();
            AdsHandler.disableGoogleAnnotations();
            Assert.assertEquals(DriverManager.getDriver().findElements(By.cssSelector(".adsbygoogle, .google-anno, .google-anno-sc")).size(), 3);
            System.setProperty("ads.handling.enabled", "true");
            AdsHandler.removeGoogleAds();
            AdsHandler.disableGoogleAnnotations();
            Assert.assertTrue(DriverManager.getDriver().findElements(By.cssSelector(".adsbygoogle, .google-anno, .google-anno-sc")).isEmpty());
            Assert.assertEquals(DriverManager.getDriver().findElement(By.id("product-name")).getText(), "Premium Polo T-Shirts");
            script("setTimeout(() => document.querySelector('#product-name').insertAdjacentHTML('beforeend', '<span class=google-anno-sc>Injected ad</span><span id=ad-injected></span>'), 300)");
            new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(10)).until(d ->
                    d.findElements(By.id("ad-injected")).size() == 1
                    && d.findElements(By.cssSelector(".google-anno-sc")).isEmpty()
                    && d.findElement(By.id("product-name")).getText().equals("Premium Polo T-Shirts"));
            Assert.assertEquals(DriverManager.getDriver().manage().timeouts().getImplicitWaitTimeout(), Duration.ZERO);
        } finally {
            if (original == null) { System.clearProperty("ads.handling.enabled"); }
            else { System.setProperty("ads.handling.enabled", original); }
        }
    }

    @Test public void advertisementVignetteDoesNotConsumeHeaderNavigation() {
        open("/", """
                <header id='header'><div class='shop-menu'>
                    <a href='/products' onclick="if (!sessionStorage.getItem('vignetteShown')) {
                        event.preventDefault(); sessionStorage.setItem('vignetteShown', 'true');
                        location.hash='google_vignette';
                    }">Products</a>
                </div></header>
                <div class='features_items'><h2 class='title text-center'>All Products</h2></div>
                <input id='search_product'><button id='submit_search'>Search</button>
                """);
        Assert.assertTrue(new HomePage().header().openProducts().isLoaded());
        Assert.assertEquals(DriverManager.getDriver().getCurrentUrl(), origin + "/products");
        // Checkout is an onclick control without an href, rather than an ordinary link.
        open("/view_cart", """
                <a class='check_out' onclick="setTimeout(() => location.href='/checkout', 300)">Checkout</a>
                <div data-qa='checkout-info'>Checkout</div>
                <div id='address_delivery'>Delivery</div><div id='address_invoice'>Invoice</div>
                <table><tr id='product-1'><td>Product</td></tr></table>
                <textarea name='message'></textarea><a href='/payment'>Place order</a>
                """);
        Assert.assertTrue(new CartPage().proceedToCheckoutAsLoggedInUser().isLoaded());
        Assert.assertEquals(DriverManager.getDriver().getCurrentUrl(), origin + "/checkout");
    }

    @Test public void offscreenProductIsScrolledBeforeHovering() {
        for (int height : new int[] {32, 1400}) {
            open("/products", """
                <div style='height:1800px'></div>
                <div class='features_items'><div class='product-image-wrapper'>
                    <div class='productinfo'><p>Offscreen product</p></div>
                    <button class='add-to-cart' style='height:%dpx' onclick="this.textContent='Added'">Add</button>
                </div></div>
                """.formatted(height));
            new ProductsPage().products().getProductCard(0).addToCart();
            Assert.assertEquals(DriverManager.getDriver().findElement(By.cssSelector(".add-to-cart")).getText(), "Added");
        }
    }

    @Test public void realSiteNavigationAndRepeatedSearches() {
        var home = HomePage.open();
        Assert.assertTrue(home.header().openApiTesting().isLoaded());
        var products = new ApiListPage().header().openProducts();
        var list = products.products();
        products.search("blue top");
        Assert.assertTrue(list.allProductsContain("blue top"));
        products.search("tshirt");
        Assert.assertTrue(list.allProductsContain("tshirt"));
        products.search("tshirt");
        Assert.assertTrue(list.allProductsContain("tshirt"));
        products.search("no-such-product-readiness-92746");
        Assert.assertFalse(list.hasProducts());
        var category = new HomePage().header().openHomePage().categories().openSubcategory("Women", "Dress");
        Assert.assertTrue(category.isLoaded());
        Assert.assertTrue(category.brands().openBrand("Polo").isLoaded());
    }
}
