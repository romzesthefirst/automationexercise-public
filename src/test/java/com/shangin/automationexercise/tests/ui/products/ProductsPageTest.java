package com.shangin.automationexercise.tests.ui.products;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.shangin.automationexercise.base.BaseTest;
import com.shangin.automationexercise.components.AddToCartModalComponent;
import com.shangin.automationexercise.components.ProductCardComponent;
import com.shangin.automationexercise.constants.UiMessages;
import com.shangin.automationexercise.factories.ReviewFactory;
import com.shangin.automationexercise.model.Review;
import com.shangin.automationexercise.pages.CartPage;
import com.shangin.automationexercise.pages.HomePage;
import com.shangin.automationexercise.pages.ProductDetailsPage;
import com.shangin.automationexercise.pages.ProductsPage;
import com.shangin.automationexercise.components.CartItemComponent;
import io.qameta.allure.Description;

public class ProductsPageTest extends BaseTest {

    @Test @Description("Test Case 8: Verify All Products and product detail page")
    public void shoulViewAllProductsAndProductDetail() {
        // 1. Launch browser
        // 2. Navigate to url 'http://automationexercise.com'
        HomePage homePage = HomePage.open();

        // 3. Verify that home page is visible successfully
        Assert.assertTrue(homePage.isLoaded(), "Home page should be loaded");

        // 4. Click on 'Products' button
        ProductsPage productsPage = homePage.header().openProducts();

        // 5. Verify user is navigated to ALL PRODUCTS page successfully
        Assert.assertTrue(productsPage.isLoaded(), "All products page should be loaded");

        // 6. The products list is visible
        Assert.assertTrue(
                productsPage.products().hasProducts(),
                "Some products should be presented on the products page");

        // 7. Click on 'View Product' of first product
        ProductDetailsPage productDetailsPage
                = productsPage.products().getProducts().get(0).viewProduct();

        // 8. User is landed to product detail page
        Assert.assertTrue(productDetailsPage.isOpened(), "Product detail page should be loaded");

        // 9. Verify that detail detail is visible: product name, category, price,
        // availability, condition, brand
        Assert.assertFalse(
                productDetailsPage.getProductName().isBlank(),
                "Product name should be visible");
        Assert.assertFalse(
                productDetailsPage.getProductCategory().isBlank(),
                "Product category should be visible");
        Assert.assertFalse(
                productDetailsPage.getProductPrice().isBlank(),
                "Product price should be visible");
        Assert.assertFalse(
                productDetailsPage.getProductAvailability().isBlank(),
                "Product availability should be visible");
        Assert.assertFalse(
                productDetailsPage.getProductCondition().isBlank(),
                "Product condition should be visible");
        Assert.assertFalse(
                productDetailsPage.getProductBrand().isBlank(),
                "Product brand should be visible");
    }

    @Test @Description("Test Case 9: Search Product")
    public void shouldSearchProducts() {
        // 1. Launch browser
        // 2. Navigate to url 'http://automationexercise.com'
        String searchText = "Saree";
        HomePage homePage = HomePage.open();

        // 3. Verify that home page is visible successfully
        Assert.assertTrue(homePage.isLoaded(), "Home page should be loaded");

        // 4. Click on 'Products' button
        ProductsPage productsPage = homePage.header().openProducts();

        // 5. Verify user is navigated to ALL PRODUCTS page successfully
        Assert.assertTrue(productsPage.isLoaded(), "All products page should be loaded");

        // 6. Enter product name in search input and click search button
        productsPage = productsPage.search(searchText);

        // 7. Verify 'SEARCHED PRODUCTS' is visible
        Assert.assertEquals(
                productsPage.getPageTitle(),
                UiMessages.SEARCHED_PRODUCTS,
                "Search results title is incorrect");

        // 8. Verify all the products related to search are visible
        Assert.assertTrue(
                productsPage.products().allProductsContain(searchText),
                "All search results should match the search query");

    }

    @Test @Description("Test Case 12: Add Products in Cart")
    public void shouldAddProductsToCart() {
        // 1. Launch browser
        // 2. Navigate to url 'http://automationexercise.com'
        HomePage homePage = HomePage.open();

        // 3. Verify that home page is visible successfully
        Assert.assertTrue(homePage.isLoaded(), "Home page should be loaded");

        // 4. Click 'Products' button
        ProductsPage productsPage = homePage.header().openProducts();

        ProductCardComponent firstProduct = productsPage.products().getProducts().get(0);
        String firstProductName = firstProduct.getName();
        String firstProductPrice = firstProduct.getPrice();

        ProductCardComponent secondProduct = productsPage.products().getProducts().get(1);
        String secondProductName = secondProduct.getName();
        String secondProductPrice = secondProduct.getPrice();

        // 5. Hover over first product and click 'Add to cart'
        AddToCartModalComponent modal = productsPage.addProductToCart(0);

        // 6. Click 'Continue Shopping' button
        modal.continueShopping();

        // 7. Hover over second product and click 'Add to cart'
        modal = productsPage.addProductToCart(1);

        // 8. Click 'View Cart' button
        CartPage cartPage = modal.viewCart();

        // 9. Verify both products are added to Cart
        List<CartItemComponent> cartItems = cartPage.getCartItems();
        Assert.assertEquals(cartItems.size(), 2, "In cart should be 2 different items:");
        Assert.assertEquals(
                cartItems.get(0).getName(),
                firstProductName,
                "Name of the first item should be equal to first added product name:");
        Assert.assertEquals(
                cartItems.get(1).getName(),
                secondProductName,
                "Name of the second item should be equal to second added product name:");

        // 10. Verify their prices, quantity and total price
        Assert.assertEquals(
                cartItems.get(0).getPrice(),
                firstProductPrice,
                "Price of the first item should be equal to first added product price:");
        Assert.assertEquals(
                cartItems.get(0).getQuantity(),
                1,
                "Count of the first item should be equal to first added product count:");
        Assert.assertEquals(
                cartItems.get(0).getTotal(),
                firstProductPrice,
                "Price of the first item should be equal to first added product price:");

        Assert.assertEquals(
                cartItems.get(1).getPrice(),
                secondProductPrice,
                "Name of the second item should be equal to second added product name:");
        Assert.assertEquals(
                cartItems.get(1).getQuantity(),
                1,
                "Count of the second item should be equal to second added product count:");
        Assert.assertEquals(
                cartItems.get(1).getTotal(),
                secondProductPrice,
                "Price of the second item should be equal to second added product price:");
    }

    @Test @Description("Test Case 13: Verify Product quantity in Cart")
    public void shouldAddSpecificQuantityOfProduct() {
        // 1. Launch browser
        // 2. Navigate to url 'http://automationexercise.com'
        HomePage homePage = HomePage.open();

        // 3. Verify that home page is visible successfully
        Assert.assertTrue(homePage.isLoaded(), "Home page should be loaded");

        // 4. Click 'View Product' for any product on home page
        ProductDetailsPage productDetailsPage
                = homePage.products().getRandomProduct().viewProduct();

        // 5. Verify product detail is opened
        Assert.assertFalse(
                productDetailsPage.getProductName().isBlank(),
                "Product name should be visible");
        Assert.assertFalse(
                productDetailsPage.getProductCategory().isBlank(),
                "Product category should be visible");
        Assert.assertFalse(
                productDetailsPage.getProductPrice().isBlank(),
                "Product price should be visible");
        Assert.assertFalse(
                productDetailsPage.getProductAvailability().isBlank(),
                "Product availability should be visible");
        Assert.assertFalse(
                productDetailsPage.getProductCondition().isBlank(),
                "Product condition should be visible");
        Assert.assertFalse(
                productDetailsPage.getProductBrand().isBlank(),
                "Product brand should be visible");

        // 6. Increase quantity to 4
        String expectedProductName = productDetailsPage.getProductName();
        int expectedQuantity = 4;
        productDetailsPage.setQuantity(expectedQuantity);

        // 7. Click 'Add to cart' button
        AddToCartModalComponent modal = productDetailsPage.addToCart();

        // 8. Click 'View Cart' button
        CartPage cartPage = modal.viewCart();

        // 9. Verify that product is displayed in cart page with exact quantity
        CartItemComponent cartProduct = cartPage.getProduct(expectedProductName);

        Assert.assertEquals(
                cartProduct.getQuantity(),
                expectedQuantity,
                "Product quantity is incorrect");
    }

    @Test @Description("Test Case 21: Add review on product")
    public void shouldAddReviewOnProduct() {
        // 1. Launch browser
        // 2. Navigate to url 'http://automationexercise.com'
        HomePage homePage = HomePage.open();

        // 3. Click on 'Products' button
        ProductsPage productsPage = homePage.header().openProducts();

        // 4. Verify user is navigated to ALL PRODUCTS page successfully
        Assert.assertTrue(productsPage.isLoaded());

        // 5. Click on 'View Product' button
        ProductDetailsPage productDetailsPage
                = productsPage.products().getProductCard(2).viewProduct();

        // 6. Verify 'Write Your Review' is visible
        Assert.assertTrue(productDetailsPage.isWriteYourReviewIsVisible());

        // 7. Enter name, email and review
        Review review = ReviewFactory.randomReview();
        productDetailsPage.fillReview(review);

        // 8. Click 'Submit' button
        productDetailsPage.submitReview();

        // 9. Verify success message 'Thank you for your review.'
        Assert.assertEquals(
                productDetailsPage.getSuccessMessage(),
                UiMessages.SUCCESS_REVIEW_MESSAGE);
    }
}
