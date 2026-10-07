package com.shangin.automationexercise.support;

import com.shangin.automationexercise.config.ConfigReader;
import com.shangin.automationexercise.driver.DriverManager;
import java.util.List;
import java.util.Map;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;

public class AdsHandler {

    private AdsHandler() {}

    public static void removeGoogleAds() {
        if (!ConfigReader.isAdsHandlingEnabled()) {
            return;
        }
        WebDriver driver = DriverManager.getDriver();

        ((JavascriptExecutor) driver)
                .executeScript(
                        """
                    document.querySelectorAll(
                        "iframe[id^='aswift_'], iframe[src*='doubleclick']"
                    ).forEach(e => e.remove());

                    document.querySelectorAll(
                        "[id*='google_ads'], [class*='google_ads'], [class*='adsbygoogle']"
                    ).forEach(e => e.remove());
                """);
    }

    public static void disableGoogleAnnotations() {
        if (!ConfigReader.isAdsHandlingEnabled()) {
            return;
        }
        JavascriptExecutor js = (JavascriptExecutor) DriverManager.getDriver();

        js.executeScript(
                """
                    if (!window.__googleAnnoObserver) {

                        const removeAnnotations = () => {
                            document.querySelectorAll('.google-anno-sc, [class*="google-anno-sa"]').forEach(element => element.remove());
                            document.querySelectorAll('a.google-anno').forEach(element => {
                                const original = element.querySelector('.google-anno-t');
                                if (original) {
                                    // Keep the original words, excluding the ad icon and added space.
                                    element.replaceWith(document.createTextNode(original.textContent));
                                } else {
                                    element.replaceWith(...element.childNodes);
                                }
                            });
                        };

                        removeAnnotations();

                        window.__googleAnnoObserver = new MutationObserver(() => {
                            removeAnnotations();
                        });

                        window.__googleAnnoObserver.observe(document.documentElement, {
                            childList: true,
                            subtree: true,
                            attributes: true,
                            attributeFilter: ['class']
                        });
                    }
                """);
    }

    public static void blockGoogleAds(WebDriver driver) {
        if (!ConfigReader.isAdsHandlingEnabled()) {
            return;
        }

        if (driver instanceof ChromeDriver || driver instanceof EdgeDriver) {
            executeCdp(driver, "Network.enable", Map.of());

            executeCdp(
                    driver,
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

    private static void executeCdp(
            WebDriver driver, String command, Map<String, Object> parameters) {
        if (driver instanceof ChromeDriver chrome) {
            chrome.executeCdpCommand(command, parameters);
        } else if (driver instanceof EdgeDriver edge) {
            edge.executeCdpCommand(command, parameters);
        }
    }
}
