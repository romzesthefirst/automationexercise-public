package com.shangin.automationexercise.cucumber.steps;

import com.shangin.automationexercise.assertions.CartAssertions;
import com.shangin.automationexercise.assertions.ProductDetailsAssertions;
import com.shangin.automationexercise.components.AddToCartModalComponent;
import com.shangin.automationexercise.components.ProductCardComponent;
import com.shangin.automationexercise.components.ProductDetailsComponent;
import com.shangin.automationexercise.constants.UiMessages;
import com.shangin.automationexercise.cucumber.context.ScenarioContext;
import com.shangin.automationexercise.factories.ReviewFactory;
import com.shangin.automationexercise.mappers.ProductMapper;
import com.shangin.automationexercise.model.ExpectedProduct;
import com.shangin.automationexercise.model.Review;
import com.shangin.automationexercise.pages.CartPage;
import com.shangin.automationexercise.pages.ProductDetailsPage;
import com.shangin.automationexercise.pages.ProductsPage;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.List;
import org.testng.Assert;

public class ProductsSteps {

    private final ScenarioContext context;
    private final ProductsPage productsPage = new ProductsPage();
    private final ProductDetailsPage productDetailPage = new ProductDetailsPage();
    private final CartPage cartPage = new CartPage();

    public ProductsSteps(ScenarioContext context) {
        this.context = context;
    }

    @When("the user opens the first product")
    public void openFirstProductOnProductsPage() {

        productsPage.products().getProductCard(0).viewProduct();
    }

    @When("the user searches for {string}")
    public void userSearchesForProductByName(String query) {

        productsPage.search(query);
    }

    @When("adds product {string} to the cart")
    public void addProductToCart(String productName) {
        ProductsPage productsPage = new ProductsPage();

        ProductCardComponent product = productsPage.products().getProductCard(productName);

        context.addExpectedProduct(new ExpectedProduct(product.getName(), product.getPrice(), 1));

        AddToCartModalComponent modal = productsPage.addProductToCart(productName);

        context.setAddToCartModal(modal);
    }

    @When("sets the product quantity to {int}")
    public void userSetsProductQuantityTo(int quantity) {

        productDetailPage.productDetails().setQuantity(quantity);
    }

    @When("adds the product to the cart")
    public void userAddsProductToCart() {

        ProductDetailsComponent productDetails = productDetailPage.productDetails();

        context.addExpectedProduct(
                new ExpectedProduct(
                        productDetails.getName(),
                        productDetails.getPrice(),
                        productDetails.getQuantity()));

        AddToCartModalComponent modal = productDetailPage.addToCart();

        context.setAddToCartModal(modal);
    }

    @When("opens a random product on the Products page")
    public void userOpensRandomProductOnProductsPage() {

        productsPage.products().getRandomProduct().viewProduct();
    }

    @When("fills in the review form with valid data")
    public void userFillsInReviewFormWithValidData() {

        Review review = ReviewFactory.randomReview();
        productDetailPage.fillReview(review);
    }

    @When("submits the review")
    public void userSubmitsReview() {

        productDetailPage.submitReview();
    }

    @When("opens product {string} from Products page")
    public void userOpensProductFromProductsPage(String productName) {

        productsPage.products().getProductCard(productName).viewProduct();
    }

    @When("returns back to the Products page")
    public void userReturnsToProductsPage() {

        productDetailPage.goBack();
        productsPage.waitUntilLoaded();
    }

    @When("the user adds all search results to the cart")
    public void userAddsAllSearchResultsToCart() {

        productsPage.addAllProductsToCart();
        context.addExpectedProducts(ProductMapper.toExpectedProducts(productsPage.products()));
    }

    @Then("the products list should be displayed")
    public void productsListShouldBeDisplayed() {

        Assert.assertTrue(
                productsPage.products().hasProducts(), "The products list should be displayed");
    }

    @Then("the product details page should be displayed")
    public void productDetailsPageShouldBeDisplayed() {

        Assert.assertTrue(
                productDetailPage.isLoaded(), "The product detail page should be displayed");
    }

    @Then("the product information should be displayed")
    public void productInformationShouldBeDisplayed() {

        ProductDetailsAssertions.assertProductDetailInfoIsVisible(
                productDetailPage.productDetails());
    }

    @Then("the search results should be displayed")
    public void searchResultsShouldBeDisplayed() {

        Assert.assertEquals(
                productsPage.getPageTitle(),
                UiMessages.SEARCHED_PRODUCTS,
                "Search results title is incorrect");
    }

    @Then("all displayed products should match {string}")
    public void allDisplayedProductsShouldMatchSearchQuery(String productName) {

        Assert.assertTrue(
                productsPage.products().allProductsContain(productName),
                "Expected nonempty search results matching the search query: " + productName);
    }

    @Then("both products should be present in the cart with correct details")
    public void bothProductsShouldBePresentInTheCart() {

        CartAssertions.assertProductsMatch(
                cartPage.getActualProducts(), context.getExpectedProducts());
    }

    @Then("the product should be present in the cart with quantity {int}")
    public void productShouldBePresentInCartWithQuantity(int quantity) {

        Assert.assertEquals(
                context.getExpectedProducts().size(),
                1,
                "The quantity step expects exactly one added product");
        ExpectedProduct product = context.getExpectedProducts().get(0);
        CartAssertions.assertProductsMatch(
                cartPage.getActualProducts(),
                List.of(new ExpectedProduct(product.name(), product.price(), quantity)));
    }

    @Then("the review success message should be displayed")
    public void reviewSuccessMessageShouldBeDisplayed() {
        Assert.assertEquals(
                productDetailPage.getSuccessMessage(), UiMessages.SUCCESS_REVIEW_MESSAGE);
    }
}
