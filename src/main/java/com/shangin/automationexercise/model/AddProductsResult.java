package com.shangin.automationexercise.model;

import com.shangin.automationexercise.components.AddToCartModalComponent;
import java.util.List;

public record AddProductsResult(AddToCartModalComponent modal, List<ExpectedProduct> products) {}
