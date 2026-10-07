package com.shangin.automationexercise.tests.ui.products;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;

import java.util.ArrayList;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.shangin.automationexercise.assertions.CartAssertions;
import com.shangin.automationexercise.assertions.ProductDetailsAssertations;
import com.shangin.automationexercise.base.BaseTest;
import com.shangin.automationexercise.constants.UiMessages;
import com.shangin.automationexercise.factories.ReviewFactory;
import com.shangin.automationexercise.model.AddProductsResult;
import com.shangin.automationexercise.model.ExpectedProduct;
import com.shangin.automationexercise.pages.CartPage;
import com.shangin.automationexercise.pages.HomePage;
import com.shangin.automationexercise.pages.ProductDetailsPage;
import com.shangin.automationexercise.pages.ProductsPage;
import io.qameta.allure.Description;

@Test(groups = "ui")
@Epic("Automation Exercise")
@Feature("Products")
public class ProductsPageTest extends BaseTest {

    @Test @Description("Test Case 8: Verify All Products and product detail page")
    public void shoulViewAllProductsAndProductDetail() {

        HomePage homePage = HomePage.open();

        ProductsPage productsPage = homePage.header().openProducts();

        Assert.assertTrue(productsPage.isLoaded(), "All products page should be loaded");

        Assert.assertTrue(
                productsPage.products().hasProducts(),
                "Some products should be presented on the products page");

        ProductDetailsPage productDetailsPage
                = productsPage.products().getProductCard(0).viewProduct();

        Assert.assertTrue(productDetailsPage.isLoaded(), "Product detail page should be loaded");

        ProductDetailsAssertations
                .assertProductDetailInfoIsVisible(productDetailsPage.productDetails());

    }

    @Test @Description("Test Case 9: Search Product")
    public void shouldSearchProducts() {

        HomePage homePage = HomePage.open();

        ProductsPage productsPage = homePage.header().openProducts();

        Assert.assertTrue(productsPage.isLoaded(), "All products page should be loaded");

        String searchText = "Saree";
        productsPage = productsPage.search(searchText);

        Assert.assertEquals(
                productsPage.getPageTitle(),
                UiMessages.SEARCHED_PRODUCTS,
                "Search results title is incorrect");

        Assert.assertTrue(
                productsPage.products().allProductsContain(searchText),
                "Expected nonempty search results matching the search query");

    }

    @Test @Description("Test Case 12: Add Products in Cart")
    public void shouldAddProductsToCart() {

        HomePage homePage = HomePage.open();

        ProductsPage productsPage = homePage.header().openProducts();

        String[] productNames = { "Blue Top", "Men Tshirt" };

        AddProductsResult expectedProducts
                = uiProductSteps.addProductsToCart(productsPage, productNames);

        CartPage cartPage = expectedProducts.modal().viewCart();

        CartAssertions
                .assertProductsMatch(cartPage.getActualProducts(), expectedProducts.products());

    }

    @Test @Description("Test Case 13: Verify Product quantity in Cart")
    public void shouldAddSpecificQuantityOfProduct() {

        HomePage homePage = HomePage.open();

        ProductDetailsPage productDetailsPage
                = homePage.products().getRandomProduct().viewProduct();

        ProductDetailsAssertations
                .assertProductDetailInfoIsVisible(productDetailsPage.productDetails());

        int expectedQuantity = 4;
        productDetailsPage.productDetails().setQuantity(expectedQuantity);

        List<ExpectedProduct> expectedProducts = new ArrayList<>();
        expectedProducts.add(
                new ExpectedProduct(productDetailsPage.productDetails().getName(),
                        productDetailsPage.productDetails().getPrice(), expectedQuantity));

        CartPage cartPage = productDetailsPage.addToCart().viewCart();

        CartAssertions.assertProductsMatch(cartPage.getActualProducts(), expectedProducts);
    }

    @Test @Description("Test Case 21: Add review on product")
    public void shouldAddReviewOnProduct() {

        HomePage homePage = HomePage.open();

        ProductsPage productsPage = homePage.header().openProducts();

        ProductDetailsPage productDetailsPage
                = productsPage.products().getRandomProduct().viewProduct();

        Assert.assertTrue(productDetailsPage.isWriteYourReviewIsVisible());

        productDetailsPage.fillReview(ReviewFactory.randomReview());

        productDetailsPage.submitReview();

        Assert.assertEquals(
                productDetailsPage.getSuccessMessage(),
                UiMessages.SUCCESS_REVIEW_MESSAGE);
    }
}
