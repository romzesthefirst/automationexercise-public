package com.shangin.automationexercise.driver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.shangin.automationexercise.config.ConfigReader;
import com.shangin.automationexercise.enums.Browser;
import com.shangin.automationexercise.support.AdsHandler;

public final class DriverFactory {

    private DriverFactory() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static WebDriver createDriver() {
        Browser browser = Browser.valueOf(ConfigReader.getBrowser().toUpperCase());

        return createDriver(browser);
    }

    public static WebDriver createDriver(Browser browser) {

        WebDriver driver = switch (browser) {

        case CHROME -> new ChromeDriver(BrowserOptionsFactory.chrome());

        case FIREFOX -> new FirefoxDriver(BrowserOptionsFactory.firefox());

        case EDGE -> new EdgeDriver(BrowserOptionsFactory.edge());

        };

        AdsHandler.blockGoogleAds(driver);

        return driver;

    }

}