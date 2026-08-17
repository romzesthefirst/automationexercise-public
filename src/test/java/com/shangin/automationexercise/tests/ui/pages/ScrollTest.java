package com.shangin.automationexercise.tests.ui.pages;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.shangin.automationexercise.base.BaseTest;
import com.shangin.automationexercise.constants.UiMessages;
import com.shangin.automationexercise.pages.HomePage;

import io.qameta.allure.Description;

@Test(groups = "ui")
public class ScrollTest extends BaseTest {
    @Test
    @Description("Test Case 25: Verify Scroll Up using 'Arrow' button and Scroll Down functionality")
    public void shouldScrollUpUsingArrowButtonAndScrollDown() {

        HomePage homePage = HomePage.open();

        homePage.scrollToBottom();

        Assert.assertTrue(homePage.footer().isDisplayed());

        homePage.clickScrollUp();

        // 7. Verify that page is scrolled up and 'Full-Fledged practice website for
        // Automation Engineers' text is visible on screen
        Assert.assertTrue(homePage.isPageAtTop(), "Page should be scrolled to the top");
        Assert.assertEquals(homePage.getActiveSlideSubtitle(), UiMessages.CAROUSEL_SUBTITLE);
    }

    @Test
    @Description("Test Case 26: Verify Scroll Up without 'Arrow' button and Scroll Down functionality")
    public void shouldScrollUpWithoutArrowButtonAndScrollDown() {
        // 1. Launch browser
        // 2. Navigate to url 'http://automationexercise.com'
        HomePage homePage = HomePage.open();
        
        // 3. Verify that home page is visible successfully
        Assert.assertTrue(homePage.isLoaded());
        
        // 4. Scroll down page to bottom
        homePage.scrollToBottom();
        
        // 5. Verify 'SUBSCRIPTION' is visible
        Assert.assertTrue(homePage.footer().isDisplayed());
        
        // 6. Scroll up page to top
        homePage.scrollToTop();
        
        // 7. Verify that page is scrolled up and 'Full-Fledged practice website for
        // Automation Engineers' text is visible on screen
        Assert.assertTrue(homePage.isPageAtTop(), "Page should be scrolled to the top");
        Assert.assertEquals(homePage.getActiveSlideSubtitle(), UiMessages.CAROUSEL_SUBTITLE);
    }
}
