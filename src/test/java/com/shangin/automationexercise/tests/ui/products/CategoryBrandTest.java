package com.shangin.automationexercise.tests.ui.products;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.shangin.automationexercise.base.BaseTest;
import com.shangin.automationexercise.constants.UiMessages;
import com.shangin.automationexercise.pages.BrandProductsPage;
import com.shangin.automationexercise.pages.CategoryProductsPage;
import com.shangin.automationexercise.pages.HomePage;
import com.shangin.automationexercise.pages.ProductsPage;

import io.qameta.allure.Description;

public class CategoryBrandTest extends BaseTest {
    @Test @Description("Test Case 18: View Category Products")
    public void shouldViewCategory() {
        // 1. Launch browser
        // 2. Navigate to url 'http://automationexercise.com'
        HomePage homePage = HomePage.open();

        // 3. Verify that categories are visible on left side bar
        Assert.assertTrue(homePage.isCategoriesVisible());

        // 4. Click on 'Women' category
        // 5. Click on any category link under 'Women' category, for example: Dress
        CategoryProductsPage categoryProductsPage
                = homePage.categories().selectCategory("Women").openSubcategory("Tops");

        // 6. Verify that category page is displayed and confirm text 'WOMEN - TOPS
        // PRODUCTS'
        Assert.assertEquals(
                categoryProductsPage.getTitle(),
                UiMessages.categoryProductsTitle("Women", "Tops"));

        // 7. On left side bar, click on any sub-category link of 'Men' category
        categoryProductsPage
                = categoryProductsPage.categories().selectCategory("Men").openSubcategory("Jeans");

        // 8. Verify that user is navigated to that category page
        Assert.assertEquals(
                categoryProductsPage.getTitle(),
                UiMessages.categoryProductsTitle("Men", "Jeans"));
    }

    @Test @Description("Test Case 19: View & Cart Brand Products")
    public void shouldViewBrand() {
        // 1. Launch browser
        // 2. Navigate to url 'http://automationexercise.com'
        HomePage homePage = HomePage.open();

        // 3. Click on 'Products' button
        ProductsPage productPage = homePage.header().openProducts();

        // 4. Verify that Brands are visible on left side bar
        Assert.assertTrue(productPage.isBrandsVisible());

        // 5. Click on any brand name
        String brand = "Biba";
        BrandProductsPage brandProductsPage = productPage.brands().openBrand(brand);

        // 6. Verify that user is navigated to brand page and brand products are
        // displayed
        Assert.assertEquals(brandProductsPage.getTitle(), UiMessages.brandProductsTitle(brand));

        // 7. On left side bar, click on any other brand link
        brand = "Madame";
        brandProductsPage = productPage.brands().openBrand(brand);
        
        // 8. Verify that user is navigated to that brand page and can see products
        Assert.assertEquals(brandProductsPage.getTitle(), UiMessages.brandProductsTitle(brand));
    }

}
