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

/** Verifies actual session mode and window dimensions, including the file default. */
public class BrowserConfigurationLiveTest {
    @Test public void configuredModeAndOverrideUseTheSameWindowSize() throws Exception {
        if (!Boolean.getBoolean("driver.lifecycle.live")) {
            throw new org.testng.SkipException("Enable with -Ddriver.lifecycle.live=true");
        }
        String original = System.getProperty("headless");
        StringBuilder evidence = new StringBuilder("source,headless,width,height\n");
        try {
            System.clearProperty("headless");
            boolean configuredMode = ConfigReader.isHeadless();
            for (int run = 0; run < 2; run++) {
                if (run == 1) { System.setProperty("headless", Boolean.toString(!configuredMode)); }
                boolean expectedMode = run == 0 ? configuredMode : !configuredMode;
                WebDriver driver = DriverFactory.createDriver();
                try {
                    Dimension size = driver.manage().window().getSize();
                    Assert.assertEquals(size, new Dimension(ConfigReader.getBrowserWidth(), ConfigReader.getBrowserHeight()));
                    boolean actualMode;
                    if (driver instanceof ChromiumDriver chromium) {
                        var command = chromium.executeCdpCommand("Browser.getBrowserCommandLine", Map.of());
                        actualMode = ((java.util.List<?>) command.get("arguments")).stream()
                                .anyMatch(arg -> arg.toString().startsWith("--headless"));
                    } else {
                        actualMode = Boolean.TRUE.equals(((RemoteWebDriver) driver).getCapabilities().getCapability("moz:headless"));
                    }
                    Assert.assertEquals(actualMode, expectedMode);
                    driver.get("data:text/html,<title>Browser configuration probe</title>");
                    Assert.assertEquals(driver.getTitle(), "Browser configuration probe");
                    evidence.append(run == 0 ? "config.properties" : "system property").append(',')
                            .append(actualMode).append(',').append(size.width).append(',').append(size.height).append('\n');
                } finally { driver.quit(); }
            }
            Path directory = Path.of("target", "browser-fixes", "configuration");
            Files.createDirectories(directory);
            Files.writeString(directory.resolve(ConfigReader.getBrowser() + ".csv"), evidence.toString());
        } finally {
            if (original == null) { System.clearProperty("headless"); }
            else { System.setProperty("headless", original); }
        }
    }
}
