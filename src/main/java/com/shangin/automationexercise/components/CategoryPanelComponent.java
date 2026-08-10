package com.shangin.automationexercise.components;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import com.shangin.automationexercise.base.BaseComponent;
import com.shangin.automationexercise.pages.CategoryProductsPage;

public class CategoryPanelComponent extends BaseComponent {

    private static final By CATEGORY_LINK = By.cssSelector(".panel-heading .panel-title > a");
    private static final By SUBCATEGORIES = By.cssSelector(".panel-body li > a");

    protected CategoryPanelComponent(WebElement root) {
        super(root);
    }

    public String getName() {
        return getText(CATEGORY_LINK).trim();
    }

    public CategoryPanelComponent expand() {
        click(CATEGORY_LINK);
        waitUntilVisible(SUBCATEGORIES);
        return this;
    }

    public List<String> getSubcategoryNames() {
        return findAll(SUBCATEGORIES).stream().map(WebElement::getText).map(String::trim).toList();
    }

    public CategoryProductsPage openSubcategory(String name) {
        findAll(SUBCATEGORIES).stream()
                .filter(element -> element.getText().trim().equalsIgnoreCase(name)).findFirst()
                .orElseThrow(() -> new NoSuchElementException("Subcategory not found: " + name))
                .click();

        CategoryProductsPage page = new CategoryProductsPage();
        page.waitUntilLoaded();
        return page;
    }

}
