package com.shangin.automationexercise.driver;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

public class DriverLifecycleTest {
    @Test
    public void removesDriverAfterFailedQuitOnReusedWorker() throws Exception {
        var worker = Executors.newSingleThreadExecutor();
        try {
            worker.submit(
                            () -> {
                                DriverManager.setDriver(driver(new ArrayList<>(), true));
                                Assert.expectThrows(
                                        IllegalStateException.class, DriverManager::quitDriver);
                                Assert.assertFalse(DriverManager.hasDriver());
                            })
                    .get();
            worker.submit(
                            () -> {
                                Assert.assertFalse(DriverManager.hasDriver());
                                List<String> calls = new ArrayList<>();
                                DriverManager.setDriver(driver(calls, false));
                                DriverManager.quitDriver();
                                Assert.assertEquals(calls, List.of("quit"));
                                Assert.assertFalse(DriverManager.hasDriver());
                            })
                    .get();
        } finally {
            worker.shutdownNow();
            Assert.assertTrue(worker.awaitTermination(5, TimeUnit.SECONDS));
        }
    }

    @Test
    public void initializationFailureClosesBrowserAndKeepsOriginalError() {
        for (boolean failQuit : new boolean[] {false, true}) {
            List<String> calls = new ArrayList<>();
            WebDriver driver = driver(calls, failQuit);
            IllegalStateException original = new IllegalStateException("Initialization failure");
            var failure =
                    Assert.expectThrows(
                            IllegalStateException.class,
                            () ->
                                    DriverFactory.createDriver(
                                            () -> driver,
                                            created -> {
                                                throw original;
                                            }));
            Assert.assertSame(failure, original);
            Assert.assertEquals(calls, List.of("quit"));
            Assert.assertEquals(failure.getSuppressed().length, failQuit ? 1 : 0);
            Assert.assertFalse(DriverManager.hasDriver());
        }
    }

    @Test
    public void successfulInitializationLeavesBrowserOpen() {
        List<String> calls = new ArrayList<>();
        WebDriver driver = driver(calls, false);
        Assert.assertSame(
                DriverFactory.createDriver(() -> driver, created -> calls.add("initialize")),
                driver);
        Assert.assertEquals(calls, List.of("initialize"));
    }

    @Test
    public void windowAndTimeoutFailuresCloseTheUnpublishedBrowser() {
        for (String failingCommand :
                List.of("setSize", "implicitlyWait", "pageLoadTimeout", "scriptTimeout")) {
            List<String> calls = new ArrayList<>();
            var timeouts =
                    (WebDriver.Timeouts)
                            Proxy.newProxyInstance(
                                    WebDriver.class.getClassLoader(),
                                    new Class<?>[] {WebDriver.Timeouts.class},
                                    (proxy, method, args) -> {
                                        calls.add(method.getName());
                                        if (method.getName().equals(failingCommand)) {
                                            throw new IllegalStateException(
                                                    "Timeout failure: " + failingCommand);
                                        }
                                        return proxy;
                                    });
            var window =
                    (WebDriver.Window)
                            Proxy.newProxyInstance(
                                    WebDriver.class.getClassLoader(),
                                    new Class<?>[] {WebDriver.Window.class},
                                    (proxy, method, args) -> {
                                        calls.add(method.getName());
                                        if (method.getName().equals(failingCommand)) {
                                            throw new IllegalStateException(
                                                    "Timeout failure: " + failingCommand);
                                        }
                                        return null;
                                    });
            var options =
                    (WebDriver.Options)
                            Proxy.newProxyInstance(
                                    WebDriver.class.getClassLoader(),
                                    new Class<?>[] {WebDriver.Options.class},
                                    (proxy, method, args) ->
                                            method.getName().equals("window") ? window : timeouts);
            var browser =
                    (WebDriver)
                            Proxy.newProxyInstance(
                                    WebDriver.class.getClassLoader(),
                                    new Class<?>[] {WebDriver.class},
                                    (proxy, method, args) -> {
                                        if (method.getName().equals("manage")) {
                                            return options;
                                        }
                                        if (method.getName().equals("quit")) {
                                            calls.add("quit");
                                        }
                                        return null;
                                    });
            var failure =
                    Assert.expectThrows(
                            IllegalStateException.class,
                            () ->
                                    DriverFactory.createDriver(
                                            () -> browser, DriverFactory::initialize));
            Assert.assertEquals(failure.getMessage(), "Timeout failure: " + failingCommand);
            Assert.assertEquals(calls.get(calls.size() - 1), "quit");
            Assert.assertFalse(DriverManager.hasDriver());
        }
    }

    private static WebDriver driver(List<String> calls, boolean failQuit) {
        return (WebDriver)
                Proxy.newProxyInstance(
                        WebDriver.class.getClassLoader(),
                        new Class<?>[] {WebDriver.class},
                        (proxy, method, args) -> {
                            calls.add(method.getName());
                            if (failQuit) {
                                throw new IllegalStateException("Shutdown failure");
                            }
                            return null;
                        });
    }
}
