package com.shangin.automationexercise.driver;

import org.openqa.selenium.WebDriver;

public final class DriverManager {
	private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();
	
	private DriverManager() {
		throw new UnsupportedOperationException("Utility class");
	}
	
	public static WebDriver getDriver() {
		WebDriver driver = DRIVER.get();
		
        if (driver == null) {
            throw new IllegalStateException(
                    "WebDriver is not initialized. Call DriverManager.setDriver() first."
            );
        }

        return driver;
	}
	
    public static void setDriver(WebDriver driver) {
        DRIVER.set(driver);
    }
    
    public static void quitDriver() {
        WebDriver driver = DRIVER.get();

        try {
            if (driver != null) {
                driver.quit();
            }
        } finally {
            DRIVER.remove();
        }
    }
    
    public static boolean hasDriver() {
        return DRIVER.get() != null;
    }
}
