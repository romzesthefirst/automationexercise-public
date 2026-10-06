package com.shangin.automationexercise.tests.support;

import java.util.ArrayList;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;
import com.shangin.automationexercise.api.clients.AccountApiClient;
import com.shangin.automationexercise.api.support.ApiResponseParser;
import com.shangin.automationexercise.model.User;
import io.restassured.response.Response;

/** Explicitly selected external-service fault injection; excluded from suite profiles. */
public class AccountCleanupLiveTest {
    public static class RecordingClient extends AccountApiClient {
        private final boolean loseResponse;
        final List<User> created = new ArrayList<>();

        public RecordingClient(boolean loseResponse) { this.loseResponse = loseResponse; }

        @Override public Response createAccount(User user) {
            created.add(user);
            Response response = super.createAccount(user);
            if (loseResponse) {
                Assert.assertEquals(response.statusCode(), 200);
                Assert.assertEquals(ApiResponseParser.extractJson(response).get("responseCode").asInt(), 201);
                throw new IllegalStateException("Deliberately lost creation response");
            }
            return response;
        }
    }

    @Test public void verifiesAbsenceAfterSuccessSetupFailureTestFailureAndLostResponse() {
        for (String mode : List.of("success", "setup", "test", "lost-response")) {
            RecordingClient client = new RecordingClient(mode.equals("lost-response"));
            var results = AccountCleanupTest.runFixture(new AccountFailureFixture(
                    client, mode.equals("setup"), mode.equals("test")));
            if (mode.equals("success")) {
                Assert.assertEquals(results.getPassedTests().size(), 1);
            } else if (mode.equals("test")) {
                Assert.assertEquals(results.getFailedTests().get(0).getThrowable().getMessage(), "Original test failure");
            } else {
                Assert.assertEquals(results.getConfigurationFailures().size(), 1);
                Assert.assertEquals(results.getConfigurationFailures().get(0).getThrowable().getMessage(),
                        mode.equals("setup") ? "Original setup failure" : "Deliberately lost creation response");
            }
            Assert.assertEquals(results.getConfigurationFailures().size(),
                    mode.equals("setup") || mode.equals("lost-response") ? 1 : 0,
                    "Unexpected cleanup/configuration failure in " + mode);
            Assert.assertEquals(client.created.size(), 1);
            Response lookup = client.getUserByEmail(client.created.get(0).email());
            Assert.assertEquals(lookup.statusCode(), 200);
            Assert.assertEquals(ApiResponseParser.extractJson(lookup).get("responseCode").asInt(), 404);
        }
    }
}
