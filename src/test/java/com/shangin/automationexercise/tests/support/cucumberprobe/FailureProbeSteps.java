package com.shangin.automationexercise.tests.support.cucumberprobe;

import com.shangin.automationexercise.cucumber.hooks.UiHooks;
import com.shangin.automationexercise.driver.DriverManager;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import java.lang.reflect.Proxy;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

/** Isolated glue for deliberate failures; excluded from application scenarios. */
public class FailureProbeSteps {
    public static boolean brokenSession;
    public static boolean quitCalled;
    public static int screenshotCalls;
    public static int urlCalls;

    @Before("@fake-browser")
    public void fakeBrowser() {
        DriverManager.setDriver(
                (WebDriver)
                        Proxy.newProxyInstance(
                                WebDriver.class.getClassLoader(),
                                new Class<?>[] {WebDriver.class, TakesScreenshot.class},
                                (proxy, method, args) -> {
                                    return switch (method.getName()) {
                                        case "getCurrentUrl" -> {
                                            urlCalls++;
                                            if (brokenSession) {
                                                throw new WebDriverException("URL unavailable");
                                            }
                                            yield "https://example.invalid/cucumber-probe";
                                        }
                                        case "getScreenshotAs" -> {
                                            screenshotCalls++;
                                            if (brokenSession) {
                                                throw new WebDriverException(
                                                        "Screenshot unavailable");
                                            }
                                            yield new byte[] {1, 2, 3};
                                        }
                                        case "quit" -> {
                                            quitCalled = true;
                                            yield null;
                                        }
                                        default -> null;
                                    };
                                }));
    }

    @After(value = "@fake-browser", order = 2)
    public void capture(Scenario scenario) {
        new UiHooks().addFailureInfo(scenario);
    }

    @After(value = "@fake-browser", order = 1)
    public void quit() {
        new UiHooks().tearDownDriver();
    }

    @Given("a local browser failure probe is opened")
    public void openProbe() {
        DriverManager.getDriver()
                .get(
                        "data:text/html,<title>Cucumber failure probe</title><h1>Failure evidence</h1>");
    }

    @Then("the browser probe deliberately fails")
    public void fail() {
        throw new AssertionError("Original Cucumber failure");
    }
}
