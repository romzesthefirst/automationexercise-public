package com.shangin.automationexercise.cucumber.steps;

import org.testng.Assert;

import com.shangin.automationexercise.constants.UiMessages;
import com.shangin.automationexercise.pages.BrandProductsPage;
import com.shangin.automationexercise.pages.CategoryProductsPage;
import com.shangin.automationexercise.pages.HomePage;
import com.shangin.automationexercise.pages.ProductsPage;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class CategoryBrandSteps {

    HomePage homePage = new HomePage();
    ProductsPage productsPage = new ProductsPage();
    CategoryProductsPage categoryProductsPage = new CategoryProductsPage();
    BrandProductsPage brandProductsPage = new BrandProductsPage();

    @When("the user opens {string} from the {string} category")
    public void userOpensSubcategory(String subcategory, String category) {

        homePage.categories().openSubcategory(category, subcategory);
    }

    @When("opens {string} brand from Products page")
    public void userOpensBrandFromProducts(String brand) {

        productsPage.brands().openBrand(brand);
    }

    @When("opens {string} brand from Brand page")
    public void userOpensBrandFromBrand(String brand) {

        brandProductsPage.brands().openBrand(brand);
    }

    @Then("the {string} - {string} category page should be displayed")
    public void categoryPageShouldBeDisplayed(String category, String subcategory) {

        Assert.assertEquals(
                categoryProductsPage.getTitle(),
                UiMessages.categoryProductsTitle(category, subcategory));

        Assert.assertTrue(categoryProductsPage.products().hasProducts());
    }

    @Then("the {string} brand page should be displayed")
    public void brandPageShouldBeDisplayed(String brand) {

        Assert.assertEquals(brandProductsPage.getTitle(), UiMessages.brandProductsTitle(brand));

        Assert.assertTrue(brandProductsPage.products().hasProducts());
    }

}
