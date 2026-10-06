package com.shangin.automationexercise.model;

import java.math.BigDecimal;

public record ExpectedProduct(String name, String price, int quantity) {

    public BigDecimal total() {
        BigDecimal unitPrice = parsePrice(price);

        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    private BigDecimal parsePrice(String price) {
        return new BigDecimal(price.replaceFirst("^[^\\d]*", "").replaceAll("[^\\d.]", ""));
    }
}
