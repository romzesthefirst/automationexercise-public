package com.shangin.automationexercise.base;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.shangin.automationexercise.core.AbstractPageObject;

public abstract class BaseComponent extends AbstractPageObject {
    private final WebElement snapshot;
    private final By rootLocator;

    protected BaseComponent(WebElement root) {
        this.snapshot = root;
        this.rootLocator = null;
    }

    protected BaseComponent(By rootLocator) {
        this.snapshot = null;
        this.rootLocator = rootLocator;
    }

    protected final WebElement root() {
        if (rootLocator != null) {
            return driver.findElement(rootLocator);
        }
        try {
            snapshot.isEnabled();
            return snapshot;
        } catch (StaleElementReferenceException failure) {
            // Item snapshots have identity: never silently bind them to a different item.
            throw new IllegalStateException("Component DOM was replaced; obtain a fresh component", failure);
        }
    }

    @Override
    protected WebElement find(By locator) {
        return root().findElement(locator);
    }

    @Override
    protected List<WebElement> findAll(By locator) {
        return root().findElements(locator);
    }

    protected final void hover() {
        hover(root());
    }

    protected final void waitUntilRootDetached() {
        // The deleted row is deliberately a snapshot; do not re-resolve it here.
        wait.until(ExpectedConditions.stalenessOf(snapshot));
    }
}
