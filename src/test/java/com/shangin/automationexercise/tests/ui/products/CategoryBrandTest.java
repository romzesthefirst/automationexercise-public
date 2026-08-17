package com.shangin.automationexercise.tests.ui.products;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.shangin.automationexercise.assertions.CategoryBrandAssertions;
import com.shangin.automationexercise.base.BaseTest;
import com.shangin.automationexercise.constants.UiMessages;
import com.shangin.automationexercise.pages.BrandProductsPage;
import com.shangin.automationexercise.pages.CategoryProductsPage;
import com.shangin.automationexercise.pages.HomePage;
import com.shangin.automationexercise.pages.ProductsPage;
import com.shangin.automationexercise.testdata.ExpectedCategories;

import io.qameta.allure.Description;

@Test(groups = "ui")
public class CategoryBrandTest extends BaseTest {
    @Test @Description("Test Case 18: View Category Products")
    public void shouldViewCategory() {

        HomePage homePage = HomePage.open();

        // 3. Verify that categories are visible on left side bar
        Assert.assertTrue(homePage.isCategoriesVisible());

        CategoryBrandAssertions.assertCategoriesMatch(
                homePage.categories().getCategories(),
                ExpectedCategories.ALL);

        CategoryProductsPage categoryProductsPage
                = homePage.categories().openSubcategory("Women", "Tops");

        Assert.assertEquals(
                categoryProductsPage.getTitle(),
                UiMessages.categoryProductsTitle("Women", "Tops"));

        Assert.assertTrue(categoryProductsPage.products().hasProducts());

        categoryProductsPage = categoryProductsPage.categories().openSubcategory("Men", "Jeans");

        Assert.assertEquals(
                categoryProductsPage.getTitle(),
                UiMessages.categoryProductsTitle("Men", "Jeans"));

        Assert.assertTrue(categoryProductsPage.products().hasProducts());
    }

    @Test @Description("Test Case 19: View & Cart Brand Products")
    public void shouldViewBrand() {

        HomePage homePage = HomePage.open();

        ProductsPage productPage = homePage.header().openProducts();

        Assert.assertTrue(productPage.isBrandsVisible());

        String brand = "Biba";
        BrandProductsPage brandProductsPage = productPage.brands().openBrand(brand);

        Assert.assertEquals(brandProductsPage.getTitle(), UiMessages.brandProductsTitle(brand));
        
        Assert.assertTrue(brandProductsPage.products().hasProducts());

        brand = "Madame";
        brandProductsPage = brandProductsPage.brands().openBrand(brand);

        Assert.assertEquals(brandProductsPage.getTitle(), UiMessages.brandProductsTitle(brand));
        
        Assert.assertTrue(brandProductsPage.products().hasProducts());
    }

}
