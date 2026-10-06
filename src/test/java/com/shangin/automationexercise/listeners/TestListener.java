package com.shangin.automationexercise.listeners;

import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestResult;

public class TestListener implements IInvokedMethodListener {
    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult result) {
        // TestNG invokes this before @AfterMethod, including failed setup methods.
        if (result.getStatus() == ITestResult.FAILURE) {
            BrowserFailureAttachments.captureCurrentTest();
        }
    }
}
