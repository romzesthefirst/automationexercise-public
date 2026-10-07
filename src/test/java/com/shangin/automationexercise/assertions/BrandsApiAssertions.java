package com.shangin.automationexercise.assertions;

import com.shangin.automationexercise.api.models.BrandsDto;
import java.util.List;
import org.testng.Assert;

public class BrandsApiAssertions {

    private BrandsApiAssertions() {}

    public static void assertValidBrands(List<BrandsDto> brands) {

        Assert.assertFalse(brands.isEmpty(), "Brands list should not be empty");

        for (BrandsDto brand : brands) {

            Assert.assertTrue(brand.id() > 0, "Brand id should be positive");

            Assert.assertNotNull(brand.brand(), "Brand name should not be null");

            Assert.assertFalse(brand.brand().isBlank(), "Brand name should not be blank");
        }
    }
}
