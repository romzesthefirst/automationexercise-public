package com.shangin.automationexercise.driver;

import java.time.Duration;
import java.util.function.Consumer;
import java.util.function.Supplier;

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

        return createDriver(() -> switch (browser) {

        case CHROME -> new ChromeDriver(BrowserOptionsFactory.chrome());

        case FIREFOX -> new FirefoxDriver(BrowserOptionsFactory.firefox());

        case EDGE -> new EdgeDriver(BrowserOptionsFactory.edge());

        }, DriverFactory::initialize);
    }

    static WebDriver createDriver(Supplier<WebDriver> browser, Consumer<WebDriver> initialize) {
        WebDriver driver = browser.get();
        try {
            initialize.accept(driver);
            return driver;
        } catch (RuntimeException | Error failure) {
            try {
                driver.quit();
            } catch (RuntimeException | Error shutdownFailure) {
                if (shutdownFailure != failure) {
                    failure.addSuppressed(shutdownFailure);
                }
            }
            throw failure;
        }
    }

    static void initialize(WebDriver driver) {
        AdsHandler.blockGoogleAds(driver);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(ConfigReader.getImplicitWait()));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(ConfigReader.getPageLoadTimeout()));
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(ConfigReader.getScriptTimeout()));
    }

}
