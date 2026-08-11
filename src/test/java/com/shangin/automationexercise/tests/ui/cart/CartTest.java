package com.shangin.automationexercise.tests.ui.cart;

import java.util.ArrayList;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.shangin.automationexercise.assertions.CartAssertions;
import com.shangin.automationexercise.base.BaseTest;
import com.shangin.automationexercise.components.CartItemComponent;
import com.shangin.automationexercise.components.ProductCardComponent;
import com.shangin.automationexercise.constants.UiMessages;
import com.shangin.automationexercise.mappers.ProductMapper;
import com.shangin.automationexercise.model.AddToCartResult;
import com.shangin.automationexercise.model.ExpectedProduct;
import com.shangin.automationexercise.model.User;
import com.shangin.automationexercise.pages.CartPage;
import com.shangin.automationexercise.pages.HomePage;
import com.shangin.automationexercise.pages.ProductsPage;
import com.shangin.automationexercise.steps.UserRegistrationResult;
import com.shangin.automationexercise.steps.UserSteps;

import io.qameta.allure.Description;

@Test(groups = "ui")
public class CartTest extends BaseTest {

    @Test @Description("Test Case 17: Remove Products From Cart")
    public void shouldRemoveProductFromCart() {
        // 1. Launch browser
        // 2. Navigate to url 'http://automationexercise.com'
        HomePage homePage = HomePage.open();

        // 3. Verify that home page is visible successfully
        Assert.assertTrue(homePage.isLoaded(), "Home page should be loaded");

        // 4. Add products to cart
        List<ExpectedProduct> expectedProducts = new ArrayList<>();
        for (int index : List.of(0, 1, 2)) {
            ProductCardComponent product = homePage.products().getProductCard(index);
            expectedProducts.add(new ExpectedProduct(product.getName(), product.getPrice(), 1));
            homePage.addProductToCart(index).continueShopping();
        }

        // 5. Click 'Cart' button
        CartPage cart = homePage.header().openCart();

        // 6. Verify that cart page is displayed
        Assert.assertTrue(cart.isLoaded(), "Cart page should be loaded");
        List<CartItemComponent> cartItems = cart.getCartItems();
        Assert.assertEquals(
                cartItems.size(),
                expectedProducts.size(),
                "Cart items count is incorrect");

        // 7. Click 'X' button corresponding to particular product
        ExpectedProduct removedProduct = expectedProducts.get(0);
        cart.deleteProductByIndex(0);
        expectedProducts.remove(0);

        // 8. Verify that product is removed from the cart
        cartItems = cart.getCartItems();
        Assert.assertEquals(
                cartItems.size(),
                expectedProducts.size(),
                "Cart items count is incorrect");
        Assert.assertFalse(
                cart.hasProduct(removedProduct.name()),
                "Removed product should not be present in cart");
    }

    @Test @Description("Test Case 20: Search Products and Verify Cart After Login")
    public void shoulSaveCartAfterLogin() {
        // 0
        UserRegistrationResult result = UserSteps.registerNewUserWithLogout();
        User user = result.user();
        // 1. Launch browser
        // 2. Navigate to url 'http://automationexercise.com'

        HomePage homePage = result.homePage();

        // 3. Click on 'Products' button
        ProductsPage productsPage = homePage.header().openProducts();

        // 4. Verify user is navigated to ALL PRODUCTS page successfully
        Assert.assertTrue(productsPage.isLoaded());

        // 5. Enter product name in search input and click search button
        String query = "tshirt";
        productsPage = productsPage.search(query);

        // 6. Verify 'SEARCHED PRODUCTS' is visible
        Assert.assertEquals(productsPage.getPageTitle(), UiMessages.SEARCHED_PRODUCTS);

        // 7. Verify all the products related to search are visible
        Assert.assertTrue(
                productsPage.products().allProductsContain(query),
                "Not all products match search query: " + query);
        List<ExpectedProduct> expectedProducts
                = ProductMapper.toExpectedProducts(productsPage.products());

        // 8. Add those products to cart
        productsPage.addAllProductsToCart();

        // 9. Click 'Cart' button and verify that products are visible in cart
        CartPage cartPage = productsPage.header().openCart();
        CartAssertions.assertContainsProducts(cartPage, expectedProducts);

        // 10. Click 'Signup / Login' button and submit login details
        homePage = productsPage.header().openSignupLoginPage().successLogin(user);

        // 11. Again, go to Cart page
        cartPage = homePage.header().openCart();

        // 12. Verify that those products are visible in cart after login as well
        CartAssertions.assertContainsProducts(cartPage, expectedProducts);
    }

    @Test @Description("Test Case 22: Add to cart from Recommended items")
    public void shouldAddToCartFromRecommended() {
        // 1. Launch browser
        // 2. Navigate to url 'http://automationexercise.com'
        HomePage homePage = HomePage.open();

        // 3. Scroll to bottom of page
        homePage.scrollToCarousel();

        // 4. Verify 'RECOMMENDED ITEMS' are visible
        Assert.assertEquals(homePage.getCarouselItemsTitle(), UiMessages.RECOMMENDED_ITEMS);

        // 5. Click on 'Add To Cart' on Recommended product
        AddToCartResult result = homePage.addFirstVisibleRecommendedProductToCart();

        // 6. Click on 'View Cart' button
        CartPage cartPage = result.modal().viewCart();
        
        // 7. Verify that product is displayed in cart page
        Assert.assertTrue(
                cartPage.hasProduct(result.product().name()),
                "Added product should be present in cart");
    }
}
