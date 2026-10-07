package com.shangin.automationexercise.tests.support;

import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import com.sun.net.httpserver.HttpServer;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;
import com.shangin.automationexercise.driver.BrowserNetworkDiagnostics;
import com.shangin.automationexercise.driver.DriverFactory;
import com.shangin.automationexercise.driver.DriverManager;
import com.shangin.automationexercise.listeners.BrowserFailureAttachments;
import io.qameta.allure.model.TestResult;

/** Real concurrent browser sessions against controlled HTTP 520 responses. */
public class BrowserNetworkDiagnosticsLiveTest {
    @Test public void captures520AndIsolatesConcurrentSessions() throws Exception {
        if (!Boolean.getBoolean("network.diagnostics.live")) {
            throw new SkipException("Enable with -Dnetwork.diagnostics.live=true");
        }
        var server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", exchange -> {
            String marker = exchange.getRequestURI().getQuery();
            exchange.getResponseHeaders().add("CF-Ray", "probe-" + marker);
            exchange.getResponseHeaders().add("Content-Type", "text/html");
            byte[] body = "<title>Controlled 520</title><p>Host error</p>".getBytes(java.nio.charset.StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(520, body.length);
            exchange.getResponseBody().write(body);
            exchange.close();
        });
        server.start();
        String originalBase = System.getProperty("base.url");
        String originalEnabled = System.getProperty("network.diagnostics.enabled");
        var workers = Executors.newFixedThreadPool(4);
        try {
            String base = "http://127.0.0.1:" + server.getAddress().getPort() + "/";
            System.setProperty("base.url", base);
            System.setProperty("network.diagnostics.enabled", "true");
            var futures = new ArrayList<java.util.concurrent.Future<?>>();
            var markers = java.util.stream.IntStream.range(0, 4)
                    .mapToObj(ignored -> UUID.randomUUID().toString()).toList();
            for (String marker : markers) {
                futures.add(workers.submit(() -> {
                    try {
                        Assert.assertNull(BrowserNetworkDiagnostics.snapshot());
                        var driver = DriverFactory.createDriver();
                        DriverManager.setDriver(driver);
                        driver.get(base + "?" + marker);
                        String json = new WebDriverWait(driver, Duration.ofSeconds(10)).until(ignored -> {
                            String snapshot = BrowserNetworkDiagnostics.snapshot();
                            return snapshot.contains("probe-" + marker) ? snapshot : null;
                        });
                        Assert.assertTrue(json.contains("520"));
                        Assert.assertTrue(json.contains("requestHeaders"));
                        Assert.assertTrue(json.contains("responseHeaders"));
                        var result = new TestResult();
                        BrowserFailureAttachments.capture(result);
                        BrowserFailureAttachments.capture(result);
                        var attachments = result.getAttachments().stream()
                                .filter(a -> a.getName().equals("Browser network responses")).toList();
                        Assert.assertEquals(attachments.size(), 1);
                        Path evidence = Path.of(System.getProperty("allure.results.directory"))
                                .resolve(attachments.get(0).getSource());
                        String attached = Files.readString(evidence);
                        Assert.assertTrue(attached.contains("probe-" + marker));
                        for (String other : markers) {
                            if (!other.equals(marker)) { Assert.assertFalse(attached.contains(other)); }
                        }
                        Assert.assertFalse(attached.contains("collectionError\":\"org."), attached);
                    } catch (Exception failure) {
                        throw new RuntimeException(failure);
                    } finally {
                        DriverManager.quitDriver();
                        Assert.assertNull(BrowserNetworkDiagnostics.snapshot());
                    }
                }));
            }
            for (var future : futures) { future.get(120, TimeUnit.SECONDS); }
        } finally {
            workers.shutdownNow();
            workers.awaitTermination(45, TimeUnit.SECONDS);
            server.stop(0);
            if (originalBase == null) { System.clearProperty("base.url"); }
            else { System.setProperty("base.url", originalBase); }
            if (originalEnabled == null) { System.clearProperty("network.diagnostics.enabled"); }
            else { System.setProperty("network.diagnostics.enabled", originalEnabled); }
        }
    }
}
