package com.shangin.automationexercise.components;

import com.shangin.automationexercise.base.BaseComponent;
import com.shangin.automationexercise.pages.BrandProductsPage;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

public class BrandsComponent extends BaseComponent {
    private static final By BRANDS = By.cssSelector(".brands-name li a");

    public BrandsComponent(By rootLocator) {
        super(rootLocator);
    }

    public List<String> getBrandNames() {
        return findAll(BRANDS).stream().map(this::getBrandName).toList();
    }

    public BrandProductsPage openBrand(String name) {
        WebElement brand =
                findAll(BRANDS).stream()
                        .filter(element -> getBrandName(element).equalsIgnoreCase(name))
                        .findFirst()
                        .orElseThrow(() -> new NoSuchElementException("Brand not found: " + name));

        navigate(brand);

        BrandProductsPage page = new BrandProductsPage();
        page.waitUntilLoaded();
        return page;
    }

    private String getBrandName(WebElement brand) {
        String href = brand.getAttribute("href");
        return href.substring(href.lastIndexOf('/') + 1);
    }
}
