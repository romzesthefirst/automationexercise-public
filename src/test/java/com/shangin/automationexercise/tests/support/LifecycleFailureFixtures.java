package com.shangin.automationexercise.tests.support;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.shangin.automationexercise.base.BaseTest;
import com.shangin.automationexercise.driver.DriverFactory;
import com.shangin.automationexercise.driver.DriverManager;
import com.shangin.automationexercise.listeners.TestListener;

/** Deliberate failures executed only by the lifecycle acceptance harness. */
public final class LifecycleFailureFixtures {
    private LifecycleFailureFixtures() { }

    public static class UiFailureFixture extends BaseTest {
        @Override @BeforeMethod(alwaysRun = true) public void setup() {
            if (DriverReportingTest.realBrowser) {
                WebDriver browser = DriverFactory.createDriver();
                DriverManager.setDriver(browser);
                browser.get("data:text/html,<title>Failure evidence</title><h1>Lifecycle failure probe</h1>");
                DriverManager.setDriver(DriverReportingTest.recordingDriver(browser));
            } else { DriverManager.setDriver(DriverReportingTest.fakeDriver("none")); }
            if (DriverReportingTest.setupFailure) {
                throw new IllegalStateException("Original UI setup failure");
            }
        }
        @Test public void fails() { throw new AssertionError("Original UI failure"); }
    }

    @Listeners(TestListener.class)
    public static class ApiFailureFixture {
        @Test public void fails() { throw new AssertionError("Original API failure"); }
    }
}
