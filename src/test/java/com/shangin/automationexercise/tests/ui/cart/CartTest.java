package com.shangin.automationexercise.tests.ui.cart;

import com.shangin.automationexercise.assertions.CartAssertions;
import com.shangin.automationexercise.base.AccountUiTestBase;
import com.shangin.automationexercise.components.ProductCardComponent;
import com.shangin.automationexercise.constants.UiMessages;
import com.shangin.automationexercise.mappers.ProductMapper;
import com.shangin.automationexercise.model.ActualProduct;
import com.shangin.automationexercise.model.AddToCartResult;
import com.shangin.automationexercise.model.ExpectedProduct;
import com.shangin.automationexercise.model.User;
import com.shangin.automationexercise.pages.CartPage;
import com.shangin.automationexercise.pages.HomePage;
import com.shangin.automationexercise.pages.ProductsPage;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import java.util.ArrayList;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test(groups = "ui")
@Epic("Automation Exercise")
@Feature("Cart")
public class CartTest extends AccountUiTestBase {

    @Test
    @Description("Test Case 17: Remove Products From Cart")
    public void shouldRemoveProductFromCart() {

        HomePage homePage = HomePage.open();

        List<ExpectedProduct> expectedProducts = new ArrayList<>();

        String[] productsToBy = {
            "Sleeveless Dress", "Stylish Dress", "Rose Pink Embroidered Maxi Dress"
        };
        String productToRemove = productsToBy[1];

        for (String productName : List.of(productsToBy)) {
            ProductCardComponent product = homePage.products().getProductCard(productName);
            expectedProducts.add(new ExpectedProduct(product.getName(), product.getPrice(), 1));
            homePage.addProductToCart(productName).continueShopping();
        }

        CartPage cartPage = homePage.header().openCart();

        Assert.assertTrue(cartPage.isLoaded(), "Cart page should be loaded");

        List<ActualProduct> cartItems = cartPage.getActualProducts();

        CartAssertions.assertProductsMatch(cartItems, expectedProducts);

        expectedProducts.removeIf(product -> product.name().equalsIgnoreCase(productToRemove));

        cartPage.deleteProduct(productToRemove);

        cartItems = cartPage.getActualProducts();

        CartAssertions.assertProductsMatch(cartItems, expectedProducts);

        Assert.assertFalse(
                cartPage.hasProduct(productToRemove),
                "Removed product should not be present in cart");
    }

    @Test
    @Description("Test Case 20: Search Products and Verify Cart After Login")
    public void shouldSaveCartAfterLogin() {

        User user = accounts.createUser();

        HomePage homePage = HomePage.open();

        ProductsPage productsPage = homePage.header().openProducts();

        String query = "tshirt";
        productsPage = productsPage.search(query);

        Assert.assertEquals(productsPage.getPageTitle(), UiMessages.SEARCHED_PRODUCTS);

        Assert.assertTrue(
                productsPage.products().allProductsContain(query),
                "Expected nonempty search results matching query: " + query);
        List<ExpectedProduct> expectedProducts =
                ProductMapper.toExpectedProducts(productsPage.products());

        productsPage.addAllProductsToCart();

        CartPage cartPage = productsPage.header().openCart();
        CartAssertions.assertProductsMatch(cartPage.getActualProducts(), expectedProducts);

        homePage = productsPage.header().openSignupLoginPage().successLogin(user);

        cartPage = homePage.header().openCart();

        CartAssertions.assertProductsMatch(cartPage.getActualProducts(), expectedProducts);
    }

    @Test
    @Description("Test Case 22: Add to cart from Recommended items")
    public void shouldAddToCartFromRecommended() {

        HomePage homePage = HomePage.open();

        homePage.scrollToRecommendedItems();

        Assert.assertEquals(homePage.getCarouselItemsTitle(), UiMessages.RECOMMENDED_ITEMS);

        AddToCartResult result = homePage.addFirstVisibleRecommendedProductToCart();

        CartPage cartPage = result.modal().viewCart();

        List<ExpectedProduct> expectedProducts =
                List.of(ProductMapper.toExpectedProduct(result.product()));

        CartAssertions.assertProductsMatch(cartPage.getActualProducts(), expectedProducts);
    }
}
