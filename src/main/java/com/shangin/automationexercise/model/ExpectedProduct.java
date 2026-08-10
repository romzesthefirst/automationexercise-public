package com.shangin.automationexercise.model;

public record ExpectedProduct(
        String name,
        String price,
        int count
)
{
    public int quantity() {
        return count;
    }
}
