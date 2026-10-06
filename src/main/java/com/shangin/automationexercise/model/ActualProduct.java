package com.shangin.automationexercise.model;

import java.math.BigDecimal;

public record ActualProduct(String name, String price, int quantity, String displayedTotal) {

    public BigDecimal total() {
        return new BigDecimal(displayedTotal.replaceFirst("^[^\\d]*", "").replaceAll("[^\\d.]", ""));
    }
}
