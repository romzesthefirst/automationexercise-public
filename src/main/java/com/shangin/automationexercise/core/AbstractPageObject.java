package com.shangin.automationexercise.core;

import java.nio.file.Path;
import java.net.URI;
import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.shangin.automationexercise.config.ConfigReader;
import com.shangin.automationexercise.driver.DriverManager;
import com.shangin.automationexercise.support.AdsHandler;

public abstract class AbstractPageObject {
    protected final WebDriver driver;
    protected final WebDriverWait wait;

    protected AbstractPageObject() {

        this.driver = DriverManager.getDriver();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getExplicitWait()));

    }

    protected final WebElement waitUntilVisible(By locator) {
        return wait.until(driver -> {
            try {
                WebElement element = find(locator);
                return element.isDisplayed() ? element : null;
            } catch (NoSuchElementException | StaleElementReferenceException e) {
                return null;
            }
        });
    }

    protected final void waitUntilInvisible(By locator) {
        wait.until(ignored -> {
            try {
                return !find(locator).isDisplayed();
            } catch (NoSuchElementException | StaleElementReferenceException e) {
                return true;
            }
        });
    }

    protected final WebElement waitUntilClickable(By locator) {
        return wait.until(driver -> {
            try {
                WebElement element = find(locator);
                return element.isDisplayed() && element.isEnabled() ? element : null;
            } catch (NoSuchElementException | StaleElementReferenceException e) {
                return null;
            }
        });
    }

    protected abstract WebElement find(By locator);

    protected abstract List<WebElement> findAll(By locator);

    protected final void scrollIntoView(WebElement element) {
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block: 'center', inline: 'nearest'});", element);
    }

    protected final void hover(WebElement element) {
        scrollIntoView(element);
        wait.until(ignored -> (Boolean) ((JavascriptExecutor) driver).executeScript("""
                const r = arguments[0].getBoundingClientRect();
                return r.width > 0 && r.height > 0 && r.bottom > 0 && r.right > 0
                    && r.top < window.innerHeight && r.left < window.innerWidth;
                """, element));
        new Actions(driver).moveToElement(element).perform();
    }

    protected final void click(By locator) {
        AdsHandler.removeGoogleAds();
        AdsHandler.disableGoogleAnnotations();
        wait.until(ignored -> {
            try {
                WebElement element = find(locator);
                if (!element.isDisplayed() || !element.isEnabled()) {
                    return false;
                }
                clickElement(element);
                return true;
            } catch (NoSuchElementException | StaleElementReferenceException
                    | ElementNotInteractableException failure) {
                return false;
            }
        });
    }

    private void clickElement(WebElement element) {
        scrollIntoView(element);
        element.click();
    }

    protected final void navigate(By locator) {
        navigate(waitUntilClickable(locator));
    }

    protected final void navigate(By locator, String destination) {
        navigate(waitUntilClickable(locator), destination);
    }

    protected final void navigate(WebElement link) {
        navigate(link, URI.create(link.getAttribute("href")).getPath());
    }

    private void navigate(WebElement link, String destination) {
        AdsHandler.removeGoogleAds();
        AdsHandler.disableGoogleAnnotations();
        clickElement(link);
        wait.until(ignored -> URI.create(driver.getCurrentUrl()).getPath().equals(destination)
                || (ConfigReader.isAdsHandlingEnabled() && driver.getCurrentUrl().contains("#google_vignette")));
        if (ConfigReader.isAdsHandlingEnabled() && driver.getCurrentUrl().contains("#google_vignette")) {
            // A vignette may consume the first click instead of following the link.
            // Remove its injected DOM and retry the original navigation once.
            AdsHandler.removeGoogleAds();
            AdsHandler.disableGoogleAnnotations();
            clickElement(link);
            wait.until(ignored -> URI.create(driver.getCurrentUrl()).getPath().equals(destination));
        }
    }

    protected final void type(By locator, String text) {
        WebElement element = waitUntilVisible(locator);
        element.clear();
        element.sendKeys(text);
    }

    protected final void clear(By locator) {
        waitUntilVisible(locator).clear();
    }

    protected final String getText(By locator) {
        return waitUntilVisible(locator).getText();
    }

    protected final void selectByValue(By locator, String value) {
        WebElement dropdown = waitUntilVisible(locator);
        new Select(dropdown).selectByValue(value);
    }

    protected final void selectByVisibleText(By locator, String text) {
        WebElement dropdown = waitUntilVisible(locator);
        new Select(dropdown).selectByVisibleText(text);
    }

    protected final void check(By locator) {
        WebElement checkbox = waitUntilVisible(locator);
        if (!checkbox.isSelected()) {
            checkbox.click();
        }
    }

    protected final void uncheck(By locator) {
        WebElement checkbox = waitUntilVisible(locator);
        if (checkbox.isSelected()) {
            checkbox.click();
        }
    }

    protected final boolean isSelected(By locator) {
        return waitUntilVisible(locator).isSelected();
    }

    protected final boolean isDisplayed(By locator) {
        try {
            return find(locator).isDisplayed();
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            return false;
        }
    }

    protected final String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    protected final void uploadFile(By locator, Path file) {
        waitUntilVisible(locator).sendKeys(file.toAbsolutePath().toString());
    }

    protected final void acceptAlert() {
        wait.until(ExpectedConditions.alertIsPresent()).accept();
    }

    protected final void dismissAlert() {
        wait.until(ExpectedConditions.alertIsPresent()).dismiss();
    }

    protected final String getAlertText() {
        return wait.until(ExpectedConditions.alertIsPresent()).getText();
    }

}
