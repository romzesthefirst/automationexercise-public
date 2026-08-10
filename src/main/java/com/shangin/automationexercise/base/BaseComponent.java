package com.shangin.automationexercise.base;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

import com.shangin.automationexercise.core.AbstractPageObject;

public abstract class BaseComponent extends AbstractPageObject{
    protected final WebElement root;

    protected BaseComponent(WebElement root) {
        this.root = root;
    }
    
    @Override
    protected WebElement find(By locator) {
        return root.findElement(locator);
    }
    
    @Override
    protected List<WebElement> findAll(By locator) {
        return root.findElements(locator);
    }

    protected final void hover() {
        hover(root);
    }
    
    protected final void waitUntilRootDetached() {
        wait.until(ignored -> {
            try {
                root.isDisplayed();
                return false;
            } catch (StaleElementReferenceException e) {
                return true;
            }
        });
    }
    
    /* protected List<WebElement> findAll(By locator)
     * root.findElements(locator);
     * 
     * protected int count(By locator)
     * return findAll(locator).size();
     * 
     * protected boolean isDisplayed(By locator)
     * isPresent(locator) && find(locator).isDisplayed();
     * 
     * protected String getAttribute(By locator, String attribute)
     * 
     * protected String getCssValue(By locator, String property)
     * 
     * protected List<String> getTexts(By locator)
     * 
     * protected void waitUntilHidden()
     */
}
