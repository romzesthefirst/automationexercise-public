package com.shangin.automationexercise.driver;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import com.sun.net.httpserver.HttpServer;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.HttpCommandExecutor;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;
import com.shangin.automationexercise.listeners.BrowserFailureAttachments;
import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.FileSystemResultsWriter;
import io.qameta.allure.model.Status;
import io.qameta.allure.model.StatusDetails;
import io.qameta.allure.model.TestResult;

public class DriverTransportTimeoutTest {
    @Test public void transportAllowsEveryConfiguredBrowserWaitToFinish() throws Exception {
        String page = System.getProperty("page.load.timeout");
        String script = System.getProperty("script.timeout");
        try {
            for (int[] settings : new int[][] {{30, 30, 40}, {90, 30, 100}, {30, 120, 130}}) {
                System.setProperty("page.load.timeout", Integer.toString(settings[0]));
                System.setProperty("script.timeout", Integer.toString(settings[1]));
                Assert.assertEquals(DriverFactory.clientConfiguration().readTimeout(), Duration.ofSeconds(settings[2]));
            }
        } finally {
            if (page == null) { System.clearProperty("page.load.timeout"); }
            else { System.setProperty("page.load.timeout", page); }
            if (script == null) { System.clearProperty("script.timeout"); }
            else { System.setProperty("script.timeout", script); }
        }
    }

    @Test public void stalledScreenshotTimesOutWithoutReplacingFailureOrPreventingQuit() throws Exception {
        var previous = Allure.getLifecycle();
        var executor = Executors.newCachedThreadPool();
        var server = HttpServer.create(new java.net.InetSocketAddress("127.0.0.1", 0), 0);
        server.setExecutor(executor);
        server.createContext("/", exchange -> {
            try {
                String uri = exchange.getRequestURI().getPath();
                String body;
                if (uri.endsWith("/screenshot")) {
                    Thread.sleep(1500);
                    body = "{\"value\":\"AQID\"}";
                } else if (uri.endsWith("/url")) {
                    body = "{\"value\":\"https://example.invalid/stalled-screenshot\"}";
                } else if (uri.equals("/session")) {
                    body = "{\"value\":{\"sessionId\":\"probe\",\"capabilities\":{\"browserName\":\"chrome\"}}}";
                } else { body = "{\"value\":null}"; }
                byte[] response = body.getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.length);
                exchange.getResponseBody().write(response);
            } catch (InterruptedException failure) {
                Thread.currentThread().interrupt();
            } catch (java.io.IOException ignored) {
                // Expected when the timed-out HTTP request disconnects.
            } finally { exchange.close(); }
        });
        server.start();
        try {
            Allure.setLifecycle(new AllureLifecycle(new FileSystemResultsWriter(
                    Path.of("target", "browser-fixes", "transport-timeout"))));
            var config = DriverFactory.clientConfiguration().readTimeout(Duration.ofMillis(200))
                    .baseUri(java.net.URI.create("http://127.0.0.1:" + server.getAddress().getPort()));
            DriverManager.setDriver(new RemoteWebDriver(new HttpCommandExecutor(config), new ChromeOptions()));
            var result = new TestResult().setStatus(Status.FAILED)
                    .setStatusDetails(new StatusDetails().setMessage("Original failure"));
            long started = System.nanoTime();
            BrowserFailureAttachments.capture(result);
            Assert.assertTrue(Duration.ofNanos(System.nanoTime() - started).toMillis() < 2500,
                    "Evidence collection must return when the transport times out");
            Assert.assertEquals(result.getStatusDetails().getMessage(), "Original failure");
            Assert.assertEquals(result.getStatus(), Status.FAILED);
            Assert.assertEquals(result.getAttachments().stream().map(a -> a.getName()).toList(),
                    java.util.List.of("Current URL", "Browser diagnostics"));
            DriverManager.quitDriver();
            Assert.assertFalse(DriverManager.hasDriver());
        } finally {
            if (DriverManager.hasDriver()) { DriverManager.quitDriver(); }
            Allure.setLifecycle(previous);
            server.stop(0);
            executor.shutdownNow();
            Assert.assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));
        }
    }
}
