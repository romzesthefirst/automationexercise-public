package com.shangin.automationexercise.tests.ui.pages;

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

        HomePage homePage = HomePage.open();

        TestCasesPage testCasesPage = homePage.header().openTestCases();

        Assert.assertTrue(testCasesPage.isOpened(), "Test Cases page should be loaded");
        ;
    }
}
