package com.shangin.automationexercise.api.support;

import java.util.List;

import io.restassured.response.Response;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

public final class ApiResponseParser {

    private static final ObjectMapper OBJECT_MAPPER = JsonMapper.builder()
            .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).build();

    private ApiResponseParser() {
    }

    public static JsonNode extractJson(Response response) {
        String json = response.htmlPath().getString("body");

        try {
            return OBJECT_MAPPER.readTree(json);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse API response body as JSON", e);
        }
    }

    public static <T> List<T> extractList(Response response, String field, Class<T> type) {
        JsonNode root = extractJson(response);

        try {
            return OBJECT_MAPPER.convertValue(
                    root.get(field),
                    OBJECT_MAPPER.getTypeFactory().constructCollectionType(List.class, type));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to deserialize field: " + field, e);
        }
    }

    public static <T> List<T> extractList(JsonNode body, String field, Class<T> type) {
        try {
            return OBJECT_MAPPER.convertValue(
                    body.get(field),
                    OBJECT_MAPPER.getTypeFactory().constructCollectionType(List.class, type));
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException(
                    "Failed to deserialize field '" + field + "' to " + type.getSimpleName(), e);
        }
    }
}