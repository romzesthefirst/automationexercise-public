package com.shangin.automationexercise.components;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import com.shangin.automationexercise.base.BaseComponent;

public class CategoriesComponent extends BaseComponent {

    private static final By PANELS = By.cssSelector(".panel.panel-default");

    public CategoriesComponent(WebElement root) {
        super(root);
    }

    public List<CategoryPanelComponent> getCategories() {
        return findAll(PANELS).stream().map(CategoryPanelComponent::new).toList();
    }

    public CategoryPanelComponent getCategory(String name) {
        return getCategories().stream()
                .filter(category -> category.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(
                        "Category not found: " + name
                ));
    }
    
    public CategoryPanelComponent selectCategory(String name) {
        CategoryPanelComponent category = getCategory(name);
        category.expand();
        return category;
    }
}
