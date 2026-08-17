package com.shangin.automationexercise.model;

import java.util.List;

import com.shangin.automationexercise.components.AddToCartModalComponent;

public record AddProductsResult(
        AddToCartModalComponent modal,
        List<ExpectedProduct> products
) {}