package com.shangin.automationexercise.tests.support;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chromium.ChromiumDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;
import com.shangin.automationexercise.config.ConfigReader;
import com.shangin.automationexercise.driver.DriverFactory;
import com.shangin.automationexercise.driver.DriverManager;
import com.shangin.automationexercise.pages.HomePage;
import com.shangin.automationexercise.api.specs.ApiSpecifications;
import com.shangin.automationexercise.driver.DownloadDirectory;
import com.shangin.automationexercise.resources.DownloadHelper;
import java.time.Duration;
import com.sun.net.httpserver.HttpServer;

/** Verifies actual session mode and window dimensions, including the file default. */
public class BrowserConfigurationLiveTest {
    @Test public void configuredModeAndOverrideUseTheSameWindowSize() throws Exception {
        if (!Boolean.getBoolean("driver.lifecycle.live")) {
            throw new org.testng.SkipException("Enable with -Ddriver.lifecycle.live=true");
        }
        String original = System.getProperty("headless");
        String originalBase = System.getProperty("base.url");
        String originalApi = System.getProperty("api.base.url");
        String originalPrivate = System.getProperty("incognito");
        HttpServer server = HttpServer.create(new java.net.InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", exchange -> {
            String body = exchange.getRequestURI().getPath().equals("/download") ? "configuration download" :
                    exchange.getRequestURI().getPath().equals("/api/configuration") ? "configuration API" :
                    "<title>Browser configuration probe</title><div id='slider-carousel'>Home</div><a id='download' href='/download'>Download</a>";
            if (exchange.getRequestURI().getPath().equals("/download")) {
                exchange.getResponseHeaders().set("Content-Disposition", "attachment; filename=probe.txt");
                exchange.getResponseHeaders().set("Content-Type", "text/plain");
            } else { exchange.getResponseHeaders().set("Content-Type", "text/html"); }
            byte[] bytes = body.getBytes(java.nio.charset.StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, bytes.length);
            exchange.getResponseBody().write(bytes);
            exchange.close();
        });
        server.start();
        StringBuilder evidence = new StringBuilder("mode_source,headless,incognito,width,height\n");
        try {
            System.setProperty("base.url", "http://127.0.0.1:" + server.getAddress().getPort() + "/");
            System.setProperty("api.base.url", "http://127.0.0.1:" + server.getAddress().getPort() + "/api");
            var response = ApiSpecifications.defaultRequest().get("/configuration");
            Assert.assertEquals(response.statusCode(), 200);
            Assert.assertEquals(response.asString(), "configuration API");
            System.clearProperty("headless");
            boolean configuredMode = ConfigReader.isHeadless();
            for (int run = 0; run < 4; run++) {
                System.setProperty("incognito", Boolean.toString(run >= 2));
                if (run % 2 == 1) { System.setProperty("headless", Boolean.toString(!configuredMode)); }
                if (run % 2 == 0) { System.clearProperty("headless"); }
                boolean expectedMode = run % 2 == 0 ? configuredMode : !configuredMode;
                WebDriver driver = DriverFactory.createDriver();
                DriverManager.setDriver(driver);
                try {
                    Dimension size = driver.manage().window().getSize();
                    Assert.assertEquals(size, new Dimension(ConfigReader.getBrowserWidth(), ConfigReader.getBrowserHeight()));
                    boolean actualMode;
                    if (driver instanceof ChromiumDriver chromium) {
                        var command = chromium.executeCdpCommand("Browser.getBrowserCommandLine", Map.of());
                        var arguments = (java.util.List<?>) command.get("arguments");
                        Assert.assertEquals(arguments.contains(ConfigReader.getBrowser().equals("edge") ? "--inprivate" : "--incognito"), run >= 2);
                        actualMode = arguments.stream()
                                .anyMatch(arg -> arg.toString().startsWith("--headless"));
                    } else {
                        Path profile = Path.of(((RemoteWebDriver) driver).getCapabilities().getCapability("moz:profile").toString());
                        Assert.assertTrue(Files.readString(profile.resolve("user.js")).contains(
                                "user_pref(\"browser.privatebrowsing.autostart\", " + (run >= 2) + ");"));
                        actualMode = Boolean.TRUE.equals(((RemoteWebDriver) driver).getCapabilities().getCapability("moz:headless"));
                    }
                    Assert.assertEquals(actualMode, expectedMode);
                    Assert.assertEquals(driver.manage().timeouts().getPageLoadTimeout(), Duration.ofSeconds(ConfigReader.getPageLoadTimeout()));
                    Assert.assertEquals(driver.manage().timeouts().getScriptTimeout(), Duration.ofSeconds(ConfigReader.getScriptTimeout()));
                    Assert.assertEquals(driver.manage().timeouts().getImplicitWaitTimeout(), Duration.ZERO);
                    Assert.assertTrue(HomePage.open().isOpened());
                    Assert.assertEquals(driver.getTitle(), "Browser configuration probe");
                    driver.findElement(org.openqa.selenium.By.id("download")).click();
                    Assert.assertEquals(DownloadHelper.waitAndRead("probe.txt"), "configuration download");
                    Assert.assertTrue(DownloadDirectory.current().startsWith(ConfigReader.getDownloadDirectory()));
                    evidence.append(run % 2 == 0 ? "environment/file" : "system property").append(',')
                            .append(actualMode).append(',').append(run >= 2).append(',').append(size.width).append(',').append(size.height).append('\n');
                } finally { DriverManager.quitDriver(); }
            }
            Path directory = Path.of("target", "configuration-validation", "configuration");
            Files.createDirectories(directory);
            Files.writeString(directory.resolve(ConfigReader.getBrowser() + ".csv"), evidence.toString());
        } finally {
            server.stop(0);
            if (originalBase == null) { System.clearProperty("base.url"); }
            else { System.setProperty("base.url", originalBase); }
            if (originalApi == null) { System.clearProperty("api.base.url"); }
            else { System.setProperty("api.base.url", originalApi); }
            if (originalPrivate == null) { System.clearProperty("incognito"); }
            else { System.setProperty("incognito", originalPrivate); }
            if (original == null) { System.clearProperty("headless"); }
            else { System.setProperty("headless", original); }
        }
    }
}
