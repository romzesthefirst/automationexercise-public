package com.shangin.automationexercise.api.support;

import com.shangin.automationexercise.api.clients.AccountApiClient;
import com.shangin.automationexercise.model.User;
import io.restassured.response.Response;
import tools.jackson.databind.JsonNode;

public final class ApiCleanupHelper {
    public enum Outcome {
        DELETED,
        ALREADY_ABSENT
    }

    private ApiCleanupHelper() {}

    public static Outcome deleteAccount(AccountApiClient client, User user) {
        int deletionCode = responseCode(client.deleteAccount(user));
        if (deletionCode != 200 && deletionCode != 404) {
            throw new AssertionError("Unexpected deletion responseCode: " + deletionCode);
        }
        // A deletion 404 can also mean wrong credentials; verify absence independently.
        int lookupCode = responseCode(client.getUserByEmail(user.email()));
        if (lookupCode != 404) {
            throw new AssertionError(
                    "Account absence was not confirmed; lookup responseCode: " + lookupCode);
        }
        return deletionCode == 200 ? Outcome.DELETED : Outcome.ALREADY_ABSENT;
    }

    private static int responseCode(Response response) {
        if (response.statusCode() != 200) {
            throw new AssertionError("Unexpected cleanup HTTP status: " + response.statusCode());
        }
        JsonNode code = ApiResponseParser.extractJson(response).get("responseCode");
        if (code == null || !code.isIntegralNumber()) {
            throw new AssertionError("Missing or invalid cleanup responseCode");
        }
        return code.asInt();
    }
}
