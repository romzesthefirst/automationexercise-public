package com.shangin.automationexercise.tests.ui.forms;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.shangin.automationexercise.base.BaseTest;
import com.shangin.automationexercise.pages.HomePage;
import com.shangin.automationexercise.pages.TestCasesPage;

import io.qameta.allure.Description;

@Test(groups = "ui")
public class TestCasesTest extends BaseTest {
    @Test @Description("Test Case 7: Verify Test Cases Page")
    public void shouldOpenTestCase() {
        //1. Launch browser
        //2. Navigate to url 'http://automationexercise.com'
        HomePage homePage = HomePage.open();
        
        //3. Verify that home page is visible successfully
        Assert.assertTrue(homePage.isLoaded(), "Home page should be loaded");
        
        //4. Click on 'Test Cases' button
        TestCasesPage testCasesPage = homePage.header().openTestCases();
        
        //5. Verify user is navigated to test cases page successfully
        Assert.assertTrue(testCasesPage.isOpened(),  "Test Cases page should be loaded");;
    }
}
