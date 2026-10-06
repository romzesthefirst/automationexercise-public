package com.shangin.automationexercise.config;

import java.util.Map;
import java.util.Properties;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ConfigReaderTest {
    @Test public void precedenceAndNamesApplyToEverySetting() {
        Properties defaults = new Properties();
        for (String key : new String[] {"base.url", "api.base.url", "browser", "headless", "incognito",
                "browser.width", "browser.height", "script.timeout", "page.load.timeout", "explicit.wait",
                "ads.handling.enabled", "download.directory", "download.mime.types", "download.timeout"}) {
            defaults.setProperty(key, "file");
            var environment = Map.of(ConfigReader.environmentKey(key), "env");
            Assert.assertEquals(ConfigReader.resolve(key, ignored -> "system", environment::get, defaults), "system");
            Assert.assertEquals(ConfigReader.resolve(key, ignored -> null, environment::get, defaults), "env");
            Assert.assertEquals(ConfigReader.resolve(key, ignored -> null, ignored -> null, defaults), "file");
            Assert.expectThrows(IllegalArgumentException.class,
                    () -> ConfigReader.resolve(key, ignored -> " ", environment::get, defaults));
        }
    }

    @Test public void invalidSettingsFailBeforeBrowserLaunch() {
        for (String[] setting : new String[][] {{"browser", "safari"}, {"headless", "yes"},
                {"incognito", "1"}, {"ads.handling.enabled", "maybe"}, {"browser.width", "0"},
                {"browser.height", "-1"}, {"explicit.wait", "abc"}, {"script.timeout", "2147483648"},
                {"page.load.timeout", "-5"}, {"base.url", "/relative"}, {"api.base.url", "ftp://example.com"}, {"base.url", "http://example.com:99999"},
                {"download.directory", "\u0000"}, {"download.mime.types", "true"}, {"download.timeout", "0"}}) {
            String original = System.getProperty(setting[0]);
            try {
                System.setProperty(setting[0], setting[1]);
                var failure = Assert.expectThrows(IllegalArgumentException.class, ConfigReader::validate);
                Assert.expectThrows(IllegalArgumentException.class,
                        com.shangin.automationexercise.driver.DriverFactory::createDriver);
                Assert.expectThrows(IllegalArgumentException.class,
                        com.shangin.automationexercise.api.specs.ApiSpecifications::defaultRequest);
                Assert.assertTrue(failure.getMessage().contains("'" + setting[0] + "'"), failure.getMessage());
            } finally {
                if (original == null) { System.clearProperty(setting[0]); }
                else { System.setProperty(setting[0], original); }
            }
        }
    }
}
