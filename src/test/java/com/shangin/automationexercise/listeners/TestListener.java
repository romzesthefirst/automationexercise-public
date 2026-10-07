package com.shangin.automationexercise.listeners;

import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestResult;

public class TestListener implements IInvokedMethodListener {
    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult result) {
        if (method.isTestMethod()) {
            io.qameta.allure.Allure.getLifecycle().getCurrentTestCase().ifPresent(uuid ->
                    io.qameta.allure.Allure.getLifecycle().updateTestCase(uuid, ReportMetadata::enrich));
        }
        // TestNG invokes this before @AfterMethod, including failed setup methods.
        if (result.getStatus() == ITestResult.FAILURE) {
            BrowserFailureAttachments.captureCurrentTest();
        }
    }
}
