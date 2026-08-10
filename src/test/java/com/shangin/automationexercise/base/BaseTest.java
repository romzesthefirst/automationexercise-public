package com.shangin.automationexercise.base;

import java.time.Duration;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;

import com.shangin.automationexercise.config.ConfigReader;
import com.shangin.automationexercise.driver.DriverFactory;
import com.shangin.automationexercise.driver.DriverManager;
import com.shangin.automationexercise.listeners.TestListener;

@Listeners(TestListener.class)
public abstract class BaseTest {

	@BeforeMethod(alwaysRun = true)
	public void setup() {

	    WebDriver driver = DriverFactory.createDriver();
	    
	    DriverManager.setDriver(driver);

        driver
        	.manage()
        	.timeouts()
        	.implicitlyWait(Duration.ofSeconds(
        						ConfigReader.getImplicitWait()));

        driver
        	.manage()
        	.timeouts()
        	.pageLoadTimeout(Duration.ofSeconds(
        						ConfigReader.getPageLoadTimeout()));

        driver
        	.manage()
        	.timeouts()
        	.scriptTimeout(Duration.ofSeconds(
        						ConfigReader.getScriptTimeout()));
	}

	@AfterMethod
	public void tearDown() {
        DriverManager.quitDriver();
    }
}
