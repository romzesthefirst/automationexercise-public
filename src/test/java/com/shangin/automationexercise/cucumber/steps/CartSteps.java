package com.shangin.automationexercise.cucumber.steps;

import java.util.List;

import com.shangin.automationexercise.assertions.CartAssertions;
import com.shangin.automationexercise.components.ProductCardComponent;
import com.shangin.automationexercise.cucumber.context.ScenarioContext;
import com.shangin.automationexercise.model.AddToCartResult;
import com.shangin.automationexercise.model.ExpectedProduct;
import com.shangin.automationexercise.pages.CartPage;
import com.shangin.automationexercise.pages.HomePage;
import com.shangin.automationexercise.pages.ProductsPage;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class CartSteps {

    private final ScenarioContext context;

    HomePage homePage = new HomePage();
    ProductsPage productsPage = new ProductsPage();
    CartPage cartPage = new CartPage();

    public CartSteps(ScenarioContext context) {
        this.context = context;
    }

    @When("the user adds the following products to the cart")
    public void addsProductsToCart(List<String> productNames) {

        for (String productName : productNames) {
            ProductCardComponent product = homePage.products().getProductCard(productName);
            context.addExpectedProduct(
                    new ExpectedProduct(product.getName(), product.getPrice(), 1));
            homePage.addProductToCart(productName).continueShopping();
        }
    }

    @When("the user removes {string} from the cart")
    public void userRemovesProductFromCart(String removedProduct) {

        cartPage.deleteProduct(removedProduct);

        context.getExpectedProducts()
                .removeIf(product -> product.name().equalsIgnoreCase(removedProduct));

    }

    @When("the user adds the first recommended item to the cart")
    public void userAddsFirstRecommendedItemToCart() {

        AddToCartResult result = homePage.addFirstVisibleRecommendedProductToCart();

        context.setAddToCartModal(result.modal());
        
        context.addExpectedProduct(
                new ExpectedProduct(result.product().name(), result.product().price(), 1));
    }

    @Then("the cart should not contain removed product")
    @Then("all added products should be present in the cart")
    @Then("all previously added products should still be present in the cart")
    @Then("the cart should contain the added product")
    public void cartProductsAreCorrect() {

        CartAssertions
                .assertProductsMatch(cartPage.getActualProducts(), context.getExpectedProducts());
    }
}
