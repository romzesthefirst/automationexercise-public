package com.shangin.automationexercise.model;

import java.math.BigDecimal;

public record ExpectedProduct(String name, String price, int quantity) {

    public BigDecimal total() {
        BigDecimal unitPrice = MonetaryValues.parse(price);

        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
