package com.shangin.automationexercise.components;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

import com.shangin.automationexercise.base.BaseComponent;
import com.shangin.automationexercise.pages.CategoryProductsPage;

public class CategoriesComponent extends BaseComponent {

    private static final By CATEGORY = By.cssSelector(".panel.panel-default");
    private static final By CATEGORY_NAME = By.cssSelector(".panel-title > a");
    private static final By SUBCATEGORY_NAME = By.cssSelector(".panel-body li > a");

    public CategoriesComponent(By rootLocator) {
        super(rootLocator);
    }

    public List<String> getCategoryNames() {
        return findAll(CATEGORY).stream()
                .map(category -> category.findElement(CATEGORY_NAME).getText().trim()).toList();
    }

    private WebElement getCategory(String categoryName) {
        return findAll(CATEGORY).stream()
                .filter(
                        category -> category.findElement(CATEGORY_NAME).getText().trim()
                                .equalsIgnoreCase(categoryName))
                .findFirst().orElseThrow(
                        () -> new NoSuchElementException("Category not found: " + categoryName));
    }

    private WebElement getSubcategory(WebElement category, String subcategoryName) {

        return category.findElements(SUBCATEGORY_NAME).stream()
                .filter(element -> element.getText().trim().equalsIgnoreCase(subcategoryName))
                .findFirst().orElseThrow(
                        () -> new NoSuchElementException(
                                "Subcategory not found: " + subcategoryName));
    }

    public List<String> getSubcategories(String categoryName) {
        WebElement category = getCategory(categoryName);

        return category.findElements(SUBCATEGORY_NAME).stream().map(WebElement::getText)
                .map(String::trim).toList();
    }

    public Map<String, List<String>> getCategories() {

        Map<String, List<String>> categories = new LinkedHashMap<>();

        for (WebElement category : findAll(CATEGORY)) {

            String categoryName = category.findElement(CATEGORY_NAME).getText().trim();

            List<String> subcategories = category.findElements(SUBCATEGORY_NAME).stream()
                    .map(element -> element.getDomProperty("textContent")).map(String::trim)
                    .toList();

            categories.put(categoryName, subcategories);
        }

        return categories;
    }

    private boolean isCategoryExpanded(WebElement category) {
        return category.findElement(By.cssSelector(".panel-collapse")).getAttribute("class")
                .contains("in");
    }

    public CategoryProductsPage openSubcategory(String categoryName, String subcategoryName) {

        WebElement category = getCategory(categoryName);

        if (!isCategoryExpanded(category)) {
            category.findElement(CATEGORY_NAME).click();
        }

        WebElement subcategory = waitUntilSubcategoryVisible(categoryName, subcategoryName);

        navigate(subcategory);

        CategoryProductsPage categoryProductsPage = new CategoryProductsPage();
        categoryProductsPage.waitUntilLoaded();

        return categoryProductsPage;
    }

    private WebElement waitUntilSubcategoryVisible(String categoryName, String subcategoryName) {

        return wait.until(ignored -> {
            try {
                WebElement category = getCategory(categoryName);

                WebElement subcategory = getSubcategory(category, subcategoryName);

                return subcategory.isDisplayed() ? subcategory : null;

            } catch (NoSuchElementException | StaleElementReferenceException e) {
                return null;
            }
        });
    }
}
