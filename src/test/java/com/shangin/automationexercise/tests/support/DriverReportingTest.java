package com.shangin.automationexercise.tests.support;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Proxy;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;
import org.testng.TestNG;
import org.testng.annotations.Test;

import com.shangin.automationexercise.driver.DriverManager;
import com.shangin.automationexercise.listeners.AllureTestLifecycleListener;
import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.AllureResultsWriter;
import io.qameta.allure.model.Status;
import io.qameta.allure.model.TestResult;
import io.qameta.allure.model.TestResultContainer;
import io.qameta.allure.testng.AllureTestNg;

public class DriverReportingTest {
    static final List<String> EVENTS = new ArrayList<>();
    static boolean realBrowser;
    static boolean setupFailure;
    static boolean quitFailure;

    @Test public void capturesOnceBeforeTeardownWithActualTestNgAndAllureListeners() throws Exception {
        verifyRunner(false, false, false);
    }

    @Test public void failingSetupAndQuitDoNotLeakDriverOrHideOriginalFailure() throws Exception {
        verifyRunner(false, false, true);
        verifyRunner(false, true, true);
    }

    @Test public void apiFailureHasNoMissingDriverError() throws Exception {
        AllureLifecycle previous = Allure.getLifecycle();
        RecordingWriter writer = new RecordingWriter("api-failure");
        try {
            Allure.setLifecycle(new AllureLifecycle(writer));
            var results = run(LifecycleFailureFixtures.ApiFailureFixture.class);
            Assert.assertEquals(results.getFailedTests().get(0).getThrowable().getMessage(), "Original API failure");
            TestResult report = writer.results.stream().filter(r -> r.getName().equals("fails")).findFirst().orElseThrow();
            Assert.assertEquals(report.getStatus(), Status.FAILED);
            Assert.assertEquals(report.getStatusDetails().getMessage(), "Original API failure");
            Assert.assertTrue(report.getAttachments().isEmpty());
        } finally { Allure.setLifecycle(previous); }
    }

    @Test public void brokenEvidenceCommandsAreIndependentAndDoNotEscape() throws Exception {
        AllureLifecycle previous = Allure.getLifecycle();
        RecordingWriter writer = new RecordingWriter("broken-evidence");
        try {
            Allure.setLifecycle(new AllureLifecycle(writer));
            for (String broken : List.of("getCurrentUrl", "getScreenshotAs", "both")) {
                EVENTS.clear();
                DriverManager.setDriver(fakeDriver(broken));
                TestResult result = new TestResult().setUuid(UUID.randomUUID().toString()).setStatus(Status.FAILED);
                new AllureTestLifecycleListener().beforeTestStop(result);
                new AllureTestLifecycleListener().beforeTestStop(result);
                Assert.assertEquals(EVENTS, List.of("getCurrentUrl", "getScreenshotAs"));
                Assert.assertEquals(result.getAttachments().size(), broken.equals("both") ? 1 : 2);
                Assert.assertEquals(result.getAttachments().stream().filter(a -> a.getName().equals("Browser diagnostics")).count(), 1L);
                DriverManager.quitDriver();
            }
        } finally {
            if (DriverManager.hasDriver()) { DriverManager.quitDriver(); }
            Allure.setLifecycle(previous);
        }
    }

    @Test public void cucumberHooksCollectOnceBeforeQuitAndSurviveBrokenSession() throws Exception {
        AllureLifecycle previous = Allure.getLifecycle();
        RecordingWriter writer = new RecordingWriter("cucumber-hooks");
        try {
            var lifecycle = new AllureLifecycle(writer);
            Allure.setLifecycle(lifecycle);
            var hooks = new com.shangin.automationexercise.cucumber.hooks.UiHooks();
            for (String broken : List.of("none", "both")) {
                EVENTS.clear();
                String uuid = UUID.randomUUID().toString();
                lifecycle.scheduleTestCase(new TestResult().setUuid(uuid).setName("failed scenario").setStatus(Status.FAILED));
                lifecycle.startTestCase(uuid);
                DriverManager.setDriver(fakeDriver(broken));
                hooks.addFailureInfo(failedScenario());
                hooks.addFailureInfo(failedScenario());
                hooks.tearDownDriver();
                lifecycle.stopTestCase(uuid);
                lifecycle.writeTestCase(uuid);
                Assert.assertFalse(DriverManager.hasDriver());
                Assert.assertEquals(EVENTS, List.of("getCurrentUrl", "getScreenshotAs", "quit"));
                TestResult report = writer.results.get(writer.results.size() - 1);
                Assert.assertEquals(report.getAttachments().size(), broken.equals("none") ? 3 : 1);
            }
            quitFailure = true;
            DriverManager.setDriver(fakeDriver("none"));
            Assert.expectThrows(WebDriverException.class, hooks::tearDownDriver);
            Assert.assertFalse(DriverManager.hasDriver());
            Assert.assertTrue(com.shangin.automationexercise.cucumber.hooks.UiHooks.class
                    .getMethod("addFailureInfo", io.cucumber.java.Scenario.class)
                    .getAnnotation(io.cucumber.java.After.class).order()
                    > com.shangin.automationexercise.cucumber.hooks.UiHooks.class.getMethod("tearDownDriver")
                    .getAnnotation(io.cucumber.java.After.class).order());
        } finally {
            quitFailure = false;
            if (DriverManager.hasDriver()) { DriverManager.quitDriver(); }
            Allure.setLifecycle(previous);
        }
    }

    @Test public void unsupportedScreenshotStillReportsUrl() throws Exception {
        AllureLifecycle previous = Allure.getLifecycle();
        try {
            Allure.setLifecycle(new AllureLifecycle(new RecordingWriter("no-screenshot-support")));
            DriverManager.setDriver((WebDriver) Proxy.newProxyInstance(WebDriver.class.getClassLoader(),
                    new Class<?>[] {WebDriver.class}, (proxy, method, args) ->
                            method.getName().equals("getCurrentUrl") ? "https://example.invalid/" : null));
            TestResult result = new TestResult().setStatus(Status.FAILED);
            new AllureTestLifecycleListener().beforeTestStop(result);
            Assert.assertEquals(result.getAttachments().stream().map(a -> a.getName()).toList(),
                    List.of("Current URL", "Browser diagnostics"));
        } finally {
            DriverManager.quitDriver();
            Allure.setLifecycle(previous);
        }
    }

    private static io.cucumber.java.Scenario failedScenario() throws Exception {
        var state = (io.cucumber.core.backend.TestCaseState) Proxy.newProxyInstance(
                io.cucumber.java.Scenario.class.getClassLoader(),
                new Class<?>[] {io.cucumber.core.backend.TestCaseState.class},
                (proxy, method, args) -> method.getName().equals("isFailed") ? true : null);
        var constructor = io.cucumber.java.Scenario.class.getDeclaredConstructor(io.cucumber.core.backend.TestCaseState.class);
        constructor.setAccessible(true);
        return constructor.newInstance(state);
    }

    @Test public void liveChromeFailureRetainsScreenshotAndUrl() throws Exception {
        if (!Boolean.getBoolean("driver.lifecycle.live")) {
            throw new org.testng.SkipException("Enable with -Ddriver.lifecycle.live=true");
        }
        verifyRunner(true, false, false);
    }

    private static void verifyRunner(boolean live, boolean failSetup, boolean failQuit) throws Exception {
        AllureLifecycle previous = Allure.getLifecycle();
        RecordingWriter writer = new RecordingWriter(live ? "live-ui-failure" : failSetup ? "setup-failure" : "ui-failure");
        EVENTS.clear();
        realBrowser = live;
        setupFailure = failSetup;
        quitFailure = failQuit;
        try {
            Allure.setLifecycle(new AllureLifecycle(writer));
            var results = run(LifecycleFailureFixtures.UiFailureFixture.class);
            Assert.assertFalse(failSetup ? results.getConfigurationFailures().isEmpty() : results.getFailedTests().isEmpty(),
                    "Expected injected failure; configuration failures: " + results.getConfigurationFailures());
            ITestResult failure = failSetup ? results.getConfigurationFailures().get(0) : results.getFailedTests().get(0);
            Assert.assertEquals(failure.getThrowable().getMessage(), failSetup ? "Original UI setup failure" : "Original UI failure");
            Assert.assertFalse(DriverManager.hasDriver());
            Assert.assertTrue(EVENTS.indexOf("getScreenshotAs") < EVENTS.indexOf("quit"), EVENTS.toString());
            Assert.assertTrue(EVENTS.indexOf("getScreenshotAs") >= 0, EVENTS.toString());
            Assert.assertEquals(EVENTS.stream().filter("getCurrentUrl"::equals).count(), 1L);
            Assert.assertEquals(EVENTS.stream().filter("getScreenshotAs"::equals).count(), 1L);
            TestResult report = writer.results.stream().filter(r -> r.getName().equals(failSetup ? "setup" : "fails")).findFirst().orElseThrow();
            Assert.assertEquals(report.getAttachments().stream().filter(a -> a.getName().equals("Current URL")).count(), 1L);
            Assert.assertEquals(report.getAttachments().stream().filter(a -> a.getType().equals("image/png")).count(), 1L);
            if (!failSetup) {
                Assert.assertEquals(report.getStatus(), Status.FAILED);
                Assert.assertEquals(report.getStatusDetails().getMessage(), "Original UI failure");
            }
        } finally {
            if (DriverManager.hasDriver()) { DriverManager.quitDriver(); }
            Allure.setLifecycle(previous);
            realBrowser = false;
            setupFailure = false;
            quitFailure = false;
        }
    }

    private static TestListenerAdapter run(Class<?> fixture) {
        TestNG runner = new TestNG();
        runner.setUseDefaultListeners(false);
        runner.setVerbose(0);
        runner.setTestClasses(new Class<?>[] {fixture});
        // Both adapters participate in the real runner, including lifecycle callbacks.
        runner.addListener(new AllureTestNg());
        TestListenerAdapter results = new TestListenerAdapter();
        runner.addListener(results);
        runner.run();
        return results;
    }

    static WebDriver fakeDriver(String broken) {
        return (WebDriver) Proxy.newProxyInstance(WebDriver.class.getClassLoader(),
                new Class<?>[] {WebDriver.class, TakesScreenshot.class}, (proxy, method, args) -> {
                    EVENTS.add(method.getName());
                    if (method.getName().equals(broken) || (broken.equals("both") && !method.getName().equals("quit"))
                            || (method.getName().equals("quit") && quitFailure)) {
                        throw new WebDriverException("Injected " + method.getName() + " failure");
                    }
                    return switch (method.getName()) {
                        case "getCurrentUrl" -> "https://example.invalid/lifecycle";
                        case "getScreenshotAs" -> new byte[] {1, 2, 3};
                        default -> null;
                    };
                });
    }

    static WebDriver recordingDriver(WebDriver browser) {
        return (WebDriver) Proxy.newProxyInstance(WebDriver.class.getClassLoader(),
                new Class<?>[] {WebDriver.class, TakesScreenshot.class}, (proxy, method, args) -> {
                    EVENTS.add(method.getName());
                    try { return method.invoke(browser, args); }
                    catch (java.lang.reflect.InvocationTargetException failure) { throw failure.getCause(); }
                });
    }

    static class RecordingWriter implements AllureResultsWriter {
        final List<TestResult> results = new ArrayList<>();
        final io.qameta.allure.FileSystemResultsWriter delegate;
        RecordingWriter(String name) throws IOException {
            Path directory = Path.of("target", "driver-lifecycle-validation", name);
            Files.createDirectories(directory);
            delegate = new io.qameta.allure.FileSystemResultsWriter(directory);
        }
        @Override public void write(TestResult result) { results.add(result); delegate.write(result); }
        @Override public void write(TestResultContainer result) { delegate.write(result); }
        @Override public void write(String source, InputStream attachment) { delegate.write(source, attachment); }
    }
}
