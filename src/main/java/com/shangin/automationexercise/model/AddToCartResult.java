package com.shangin.automationexercise.model;

import com.shangin.automationexercise.components.AddToCartModalComponent;

public record AddToCartResult(
        ProductInfo product,
        AddToCartModalComponent modal
) {}