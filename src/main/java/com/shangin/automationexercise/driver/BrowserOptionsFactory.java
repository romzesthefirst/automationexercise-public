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
                DownloadDirectory.current().toAbsolutePath().toString());
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
        options.addPreference("browser.download.folderList", 2);
        options.addPreference("browser.download.dir", DownloadDirectory.current().toString());
        options.addPreference("browser.download.useDownloadDir", true);
        options.addPreference("browser.helperApps.neverAsk.saveToDisk", ConfigReader.getDownloadMimeTypes());
        options.addPreference("browser.privatebrowsing.autostart", ConfigReader.isIncognito());
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
        options.setPageLoadStrategy(PageLoadStrategy.EAGER);
        options.addArguments("--disable-notifications");
        // Prevent temporary test profiles from pinning Edge to the macOS Dock.
        options.addArguments("--disable-features=EdgePinToDockNewUser,EdgePinToDockExistingUser");
        if (ConfigReader.isIncognito()) { options.addArguments("--inprivate"); }
        options.setExperimentalOption("prefs", Map.of(
                "download.default_directory", DownloadDirectory.current().toString(),
                "download.prompt_for_download", false));
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
