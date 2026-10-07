package com.shangin.automationexercise.tests.api;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.shangin.automationexercise.api.clients.BrandsApiClient;
import com.shangin.automationexercise.api.models.BrandsDto;
import com.shangin.automationexercise.api.support.ApiResponseParser;
import com.shangin.automationexercise.assertions.BrandsApiAssertions;
import com.shangin.automationexercise.constants.ApiMessages;

import io.qameta.allure.Description;
import io.restassured.response.Response;
import tools.jackson.databind.JsonNode;

@Test(groups = "api")
@Epic("Automation Exercise")
@Feature("Brands")
public class BrandsApiTest {

    private final BrandsApiClient brandsApiClient = new BrandsApiClient();

    @Test @Description("API 3: Get All Brands List")
    public void shouldReturnBrandList() {
        
        // API URL: https://automationexercise.com/api/brandsList
        // Request Method: GET
        Response response = brandsApiClient.getBrandsList();
        
        Assert.assertEquals(response.statusCode(), 200, "Unexpected HTTP status code");
        
        JsonNode body = ApiResponseParser.extractJson(response);

        // Response Code: 200
        Assert.assertEquals(
                body.get("responseCode").asInt(),
                200,
                "Unexpected responseCode in response body");

        // Response JSON: All brands list
        List<BrandsDto> brands = ApiResponseParser.extractList(response, "brands", BrandsDto.class);
        BrandsApiAssertions.assertValidBrands(brands);
    }
    
    @Test @Description("API 4: PUT To All Brands List")
    public void shouldRejectPutRequestToBrandsList() {
        
        // API URL: https://automationexercise.com/api/brandsList
        // Request Method: PUT
        Response response = brandsApiClient.putBrandsList();
        
        JsonNode body = ApiResponseParser.extractJson(response);
        
        // Response Code: 405
        Assert.assertEquals(response.statusCode(), 200, "Unexpected HTTP status code");
        Assert.assertEquals(
                body.get("responseCode").asInt(),
                405,
                "Unexpected responseCode in response body");
        
        // Response Message: This request method is not supported.
        Assert.assertEquals(
                body.get("message").asString(),
                ApiMessages.REQUEST_METHOD_IS_NOT_SUPPORTED,
                "Unexpected response message");
    }
    
}
