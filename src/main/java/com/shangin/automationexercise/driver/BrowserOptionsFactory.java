package com.shangin.automationexercise.driver;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;

import com.shangin.automationexercise.config.ConfigReader;

public final class BrowserOptionsFactory {
    private BrowserOptionsFactory() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static ChromeOptions chrome() {

        Map<String, Object> prefs = new HashMap<>();
        prefs.put(
                "download.default_directory",
                ConfigReader.getDownloadDirectory().toAbsolutePath().toString());
        prefs.put("download.prompt_for_download", false);

        ChromeOptions options = new ChromeOptions();
        
        options.addArguments(windowSizeArgument());

        options.addArguments("--disable-notifications");
        
        options.setExperimentalOption("prefs", prefs);

        if (ConfigReader.isHeadless()) {
            options.addArguments("--headless=new");
        }

        if (ConfigReader.isIncognito()) {
            options.addArguments("--incognito");
        }

        options.setPageLoadStrategy(PageLoadStrategy.EAGER);

        return options;
    }

    public static FirefoxOptions firefox() {
        FirefoxOptions options = new FirefoxOptions().configureFromEnv();
        // Page objects await usable DOM; unrelated ad resources must not delay navigation.
        options.setPageLoadStrategy(PageLoadStrategy.EAGER);
        options.addArguments("--width=" + ConfigReader.getBrowserWidth(),
                "--height=" + ConfigReader.getBrowserHeight());
        if (ConfigReader.isHeadless()) {
            options.addArguments("-headless");
        }
        return options;
    }

    public static EdgeOptions edge() {
        EdgeOptions options = new EdgeOptions();
        options.addArguments(windowSizeArgument());
        if (ConfigReader.isHeadless()) {
            options.addArguments("--headless=new");
        }
        return options;
    }

    private static String windowSizeArgument() {
        return "--window-size=%d,%d".formatted(ConfigReader.getBrowserWidth(), ConfigReader.getBrowserHeight());
    }
}
