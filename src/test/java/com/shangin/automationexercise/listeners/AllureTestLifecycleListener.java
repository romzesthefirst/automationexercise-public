package com.shangin.automationexercise.listeners;

import com.shangin.automationexercise.driver.DriverManager;
import io.qameta.allure.listener.TestLifecycleListener;
import io.qameta.allure.model.Status;
import io.qameta.allure.model.TestResult;

public class AllureTestLifecycleListener implements TestLifecycleListener {
    @Override
    public void beforeTestStop(TestResult result) {
        ReportMetadata.enrich(result);
        if ((result.getStatus() == Status.FAILED || result.getStatus() == Status.BROKEN)
                && DriverManager.hasDriver()) {
            BrowserFailureAttachments.capture(result);
        }
    }
}
