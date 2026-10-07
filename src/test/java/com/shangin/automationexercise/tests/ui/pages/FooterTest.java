package com.shangin.automationexercise.tests.ui.pages;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.shangin.automationexercise.factories.TestData;
import com.shangin.automationexercise.base.BaseTest;
import com.shangin.automationexercise.components.FooterComponent;
import com.shangin.automationexercise.constants.UiMessages;
import com.shangin.automationexercise.pages.HomePage;

import io.qameta.allure.Description;

@Test(groups = "ui")
@Epic("Automation Exercise")
@Feature("Pages")
public class FooterTest extends BaseTest {
    
    @Test @Description("Test Case 10: Verify Subscription in home page")
    public void shouldSubscribeFromHomePage() {
        // 1. Launch browser
        // 2. Navigate to url 'http://automationexercise.com'
        HomePage homePage = HomePage.open();
        
        // 3. Verify that home page is visible successfully
        Assert.assertTrue(homePage.isLoaded(), "Home page should be loaded");
        
        // 4. Scroll down to footer
        homePage.scrollToFooter();
        FooterComponent footer = homePage.footer();
        
        // 5. Verify text 'SUBSCRIPTION'
        Assert.assertEquals(footer.getSubscriptionHeader(), UiMessages.SUBSCRIPTION);
        
        // 6. Enter email address in input and click arrow button
        footer.subscribe(TestData.faker().internet().emailAddress());
        
        // 7. Verify success message 'You have been successfully subscribed!' is visible
        Assert.assertEquals(footer.getSubscriptionResult(), UiMessages.SUCCESS_SUBSCRIBE);
        
    }
    
    @Test @Description("Test Case 11: Verify Subscription in Cart page")
    public void shouldSubscribeFromCartPage() {
        // 1. Launch browser
        // 2. Navigate to url 'http://automationexercise.com'
        HomePage homePage = HomePage.open();
        
        // 3. Verify that home page is visible successfully
        Assert.assertTrue(homePage.isLoaded(), "Home page should be loaded");
        
        // 4. Click 'Cart' button
        homePage.header().openCart();
        
        // 5. Scroll down to footer
        homePage.scrollToFooter();
        FooterComponent footer = homePage.footer();
        
        // 6. Verify text 'SUBSCRIPTION'
        Assert.assertEquals(footer.getSubscriptionHeader(), UiMessages.SUBSCRIPTION);
        
        // 7. Enter email address in input and click arrow button
        footer.subscribe(TestData.faker().internet().emailAddress());
        
        // 8. Verify success message 'You have been successfully subscribed!' is visible
        Assert.assertEquals(footer.getSubscriptionResult(), UiMessages.SUCCESS_SUBSCRIBE);
        
    }
}
