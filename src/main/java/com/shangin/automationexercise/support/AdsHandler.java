package com.shangin.automationexercise.support;

import java.util.List;
import java.util.Map;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import com.shangin.automationexercise.driver.DriverManager;

public class AdsHandler {

    private AdsHandler() {
    }

    public static void removeGoogleAds() {
        WebDriver driver = DriverManager.getDriver();

        ((JavascriptExecutor) driver).executeScript("""
                    document.querySelectorAll(
                        "iframe[id^='aswift_'], iframe[src*='doubleclick']"
                    ).forEach(e => e.remove());

                    document.querySelectorAll(
                        "[id*='google_ads'], [class*='google_ads'], [class*='adsbygoogle']"
                    ).forEach(e => e.remove());
                """);
    }

    public static void disableGoogleAnnotations() {
        JavascriptExecutor js = (JavascriptExecutor) DriverManager.getDriver();

        js.executeScript("""
                    if (!window.__googleAnnoObserver) {

                        const removeAnnotations = () => {
                            document.querySelectorAll('a.google-anno').forEach(element => {
                                element.replaceWith(...element.childNodes);
                            });
                        };

                        removeAnnotations();

                        window.__googleAnnoObserver = new MutationObserver(() => {
                            removeAnnotations();
                        });

                        window.__googleAnnoObserver.observe(document.documentElement, {
                            childList: true,
                            subtree: true
                        });
                    }
                """);
    }

    public static void blockGoogleAds(WebDriver driver) {

        if (driver instanceof ChromeDriver chromeDriver) {
            chromeDriver.executeCdpCommand("Network.enable", Map.of());

            chromeDriver.executeCdpCommand(
                    "Network.setBlockedURLs",
                    Map.of(
                            "urls",
                            List.of(
                                    "*://googleads.g.doubleclick.net/*",
                                    "*://pagead2.googlesyndication.com/*",
                                    "*://*.googlesyndication.com/*",
                                    "*://*.doubleclick.net/*")));
        }
    }
}
