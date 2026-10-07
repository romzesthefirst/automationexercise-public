package com.shangin.automationexercise.components;

import com.shangin.automationexercise.base.BaseComponent;
import com.shangin.automationexercise.support.TestRandom;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;

public class ProductListComponent extends BaseComponent {

    private static final By PRODUCT_CARDS = By.cssSelector(".product-image-wrapper");

    public ProductListComponent(By rootLocator) {
        super(rootLocator);
    }

    public List<ProductCardComponent> getProducts() {
        return findAll(PRODUCT_CARDS).stream().map(ProductCardComponent::new).toList();
    }

    public ProductCardComponent getProductCard(int index) {
        List<ProductCardComponent> products = getProducts();
        validateIndex(index, products.size());
        return products.get(index);
    }

    public ProductCardComponent getProductCard(String productName) {
        return getProducts().stream()
                .filter(product -> product.getName().equalsIgnoreCase(productName))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Product not found: " + productName));
    }

    public boolean hasProducts() {
        return !getProducts().isEmpty();
    }

    private String normalize(String value) {
        return value.toLowerCase().replaceAll("[\\s-]", "");
    }

    public boolean allProductsContain(String query) {
        String normalizedQuery = normalize(query);
        List<ProductCardComponent> products = getProducts();
        return !products.isEmpty()
                && products.stream()
                        .allMatch(
                                product -> normalize(product.getName()).contains(normalizedQuery));
    }

    public void addProductToCart(int index) {
        getProductCard(index).addToCart();
    }

    public void addProductToCart(String name) {
        getProductCard(name).addToCart();
    }

    public ProductCardComponent getRandomProduct() {
        List<ProductCardComponent> products = getProducts();
        if (products.isEmpty()) {
            throw new IllegalStateException("Product list is empty");
        }
        int randomIndex = TestRandom.productIndex(products.size());
        ProductCardComponent selected = products.get(randomIndex);
        TestRandom.selectedProduct(selected.getIdentifier());
        return selected;
    }

    private void validateIndex(int index, int size) {
        if (index < 0 || index >= size) {
            throw new IllegalArgumentException("Product index out of range: " + index);
        }
    }

    public int getProductCount() {
        return getProducts().size();
    }
}
