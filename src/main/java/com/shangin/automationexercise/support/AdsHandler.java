package com.shangin.automationexercise.support;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

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
}
