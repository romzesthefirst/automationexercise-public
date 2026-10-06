package com.shangin.automationexercise.tests.support;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.testng.Assert;
import org.testng.TestNG;
import org.testng.TestListenerAdapter;
import org.testng.annotations.Test;

import com.shangin.automationexercise.api.clients.AccountApiClient;
import com.shangin.automationexercise.api.support.ApiCleanupHelper;
import com.shangin.automationexercise.api.support.OwnedAccounts;
import com.shangin.automationexercise.cucumber.hooks.AccountHooks;
import com.shangin.automationexercise.model.User;
import com.shangin.automationexercise.steps.ApiUserSteps;
import io.cucumber.java.Scenario;
import io.restassured.builder.ResponseBuilder;
import io.restassured.response.Response;

public class AccountCleanupTest {
    private static Response response(int http, int code) {
        return new ResponseBuilder().setStatusCode(http).setContentType("text/html")
                .setBody("{\"responseCode\":" + code + "}").build();
    }

    static class FakeClient extends AccountApiClient {
        final List<String> existing = new ArrayList<>();
        final List<String> deleted = new ArrayList<>();
        boolean failCreation;
        String failDeletion;

        @Override public Response createAccount(User user) {
            existing.add(user.email());
            if (failCreation) {
                throw new IllegalStateException("Lost creation response");
            }
            return response(200, 201);
        }
        @Override public Response deleteAccount(User user) {
            deleted.add(user.email());
            if (user.email().equals(failDeletion)) {
                return response(200, 500);
            }
            return response(200, existing.remove(user.email()) ? 200 : 404);
        }
        @Override public Response getUserByEmail(String email) {
            return response(200, existing.contains(email) ? 200 : 404);
        }
    }

    @Test public void cleansAccountWhenCreationResponseIsLost() {
        OwnedAccounts accounts = new OwnedAccounts();
        FakeClient client = new FakeClient();
        client.failCreation = true;
        Assert.expectThrows(IllegalStateException.class, () -> new ApiUserSteps(client, accounts).createUser());
        accounts.cleanup(client);
        Assert.assertTrue(client.existing.isEmpty());
        Assert.assertEquals(client.deleted.size(), 1);
    }

    @Test public void distinguishesDeletedAndAbsent() {
        OwnedAccounts accounts = new OwnedAccounts();
        FakeClient client = new FakeClient();
        User user = accounts.newUser();
        client.createAccount(user);
        Assert.assertEquals(ApiCleanupHelper.deleteAccount(client, user), ApiCleanupHelper.Outcome.DELETED);
        Assert.assertEquals(ApiCleanupHelper.deleteAccount(client, user), ApiCleanupHelper.Outcome.ALREADY_ABSENT);
        accounts.cleanup(client);
    }

    @Test public void rejectsHttpAndApplicationFailuresAndUnconfirmedAbsence() {
        User user = new OwnedAccounts().newUser();
        for (int[] status : new int[][] {{503, 200}, {200, 500}, {404, 404}}) {
            AccountApiClient client = new FakeClient() {
                @Override public Response deleteAccount(User ignored) { return response(status[0], status[1]); }
            };
            Assert.expectThrows(AssertionError.class, () -> ApiCleanupHelper.deleteAccount(client, user));
        }
        for (int deletion : new int[] {200, 404}) {
            AccountApiClient client = new FakeClient() {
                @Override public Response deleteAccount(User ignored) { return response(200, deletion); }
                @Override public Response getUserByEmail(String ignored) { return response(200, 200); }
            };
            Assert.expectThrows(AssertionError.class, () -> ApiCleanupHelper.deleteAccount(client, user));
        }
        AccountApiClient badLookup = new FakeClient() {
            @Override public Response getUserByEmail(String ignored) { return response(503, 404); }
        };
        Assert.expectThrows(AssertionError.class, () -> ApiCleanupHelper.deleteAccount(badLookup, user));
    }

    @Test public void rejectsMalformedResponseAndTransportFailure() {
        User user = new OwnedAccounts().newUser();
        AccountApiClient malformed = new FakeClient() {
            @Override public Response deleteAccount(User ignored) {
                return new ResponseBuilder().setStatusCode(200).setBody("{}").build();
            }
        };
        Assert.expectThrows(AssertionError.class, () -> ApiCleanupHelper.deleteAccount(malformed, user));
        AccountApiClient transport = new FakeClient() {
            @Override public Response deleteAccount(User ignored) { throw new IllegalStateException("Network failure"); }
        };
        Assert.expectThrows(IllegalStateException.class, () -> ApiCleanupHelper.deleteAccount(transport, user));
    }

    @Test public void attemptsEveryOwnedAccountAndDoesNotTouchUnrelatedAccounts() {
        FakeClient client = new FakeClient();
        client.existing.add("unrelated@example.com");
        OwnedAccounts accounts = new OwnedAccounts();
        User first = new ApiUserSteps(client, accounts).createUser();
        User second = new ApiUserSteps(client, accounts).createUser();
        client.failDeletion = first.email();
        AssertionError failure = Assert.expectThrows(AssertionError.class, () -> accounts.cleanup(client));
        Assert.assertEquals(failure.getSuppressed().length, 1);
        Assert.assertEquals(client.deleted, List.of(first.email(), second.email()));
        Assert.assertTrue(client.existing.contains("unrelated@example.com"));
        client.deleted.clear();
        accounts.cleanup(client);
        Assert.assertTrue(client.deleted.isEmpty(), "Next invocation must not reuse old ownership");
    }

    @Test public void isolatesWorkerThreads() throws Exception {
        OwnedAccounts accounts = new OwnedAccounts();
        var pool = Executors.newFixedThreadPool(2);
        try {
            var tasks = new ArrayList<java.util.concurrent.Callable<String>>();
            for (int i = 0; i < 2; i++) {
                tasks.add(() -> {
                    FakeClient client = new FakeClient();
                    User user = new ApiUserSteps(client, accounts).createUser();
                    accounts.cleanup(client);
                    Assert.assertEquals(client.deleted, List.of(user.email()));
                    Assert.assertTrue(client.existing.isEmpty());
                    return user.email();
                });
            }
            var results = pool.invokeAll(tasks);
            Assert.assertNotEquals(results.get(0).get(), results.get(1).get());
        } finally {
            pool.shutdownNow();
            Assert.assertTrue(pool.awaitTermination(5, TimeUnit.SECONDS));
        }
    }

    @Test public void testNgAlwaysRunsCleanupAndPreservesOriginalFailure() {
        for (boolean setupFailure : new boolean[] {false, true}) {
            FakeClient client = new FakeClient();
            var listener = runFixture(new AccountFailureFixture(client, setupFailure));
            Assert.assertTrue(client.existing.isEmpty());
            Assert.assertEquals(client.deleted.size(), 1);
            if (setupFailure) {
                Assert.assertEquals(listener.getConfigurationFailures().get(0).getThrowable().getMessage(), "Original setup failure");
            } else {
                Assert.assertEquals(listener.getFailedTests().get(0).getThrowable().getMessage(), "Original test failure");
            }
        }
        FakeClient client = new FakeClient() {
            @Override public Response createAccount(User user) {
                failDeletion = user.email();
                return super.createAccount(user);
            }
        };
        var listener = runFixture(new AccountFailureFixture(client, false));
        Throwable original = listener.getFailedTests().get(0).getThrowable();
        Assert.assertEquals(original.getMessage(), "Original test failure");
        Assert.assertEquals(original.getSuppressed().length, 1);
    }

    @Test public void testNgFailsTeardownWhenSuccessfulTestCannotCleanUp() {
        FakeClient successfulClient = new FakeClient();
        var successfulRun = runFixture(new AccountFailureFixture(successfulClient, false, false));
        Assert.assertEquals(successfulRun.getPassedTests().size(), 1);
        Assert.assertTrue(successfulRun.getConfigurationFailures().isEmpty());
        Assert.assertTrue(successfulClient.existing.isEmpty());
        Assert.assertEquals(successfulClient.deleted.size(), 1);

        FakeClient client = new FakeClient() {
            @Override public Response createAccount(User user) {
                failDeletion = user.email();
                return super.createAccount(user);
            }
        };
        var listener = runFixture(new AccountFailureFixture(client, false, false));
        Assert.assertEquals(listener.getPassedTests().size(), 1);
        Assert.assertEquals(listener.getConfigurationFailures().size(), 1);
        Assert.assertEquals(listener.getConfigurationFailures().get(0).getThrowable().getMessage(),
                "Test account cleanup failed");
    }

    @Test public void optInUiBaseCleansAccountsAfterSetupAndTestFailures() {
        for (boolean setupFailure : new boolean[] {false, true}) {
            FakeClient client = new FakeClient();
            var listener = runFixture(new UiAccountFailureFixture(client, setupFailure));
            Assert.assertTrue(client.existing.isEmpty());
            Assert.assertEquals(client.deleted.size(), 1);
            if (setupFailure) {
                Assert.assertEquals(listener.getConfigurationFailures().get(0).getThrowable().getMessage(),
                        "Original UI setup failure");
            } else {
                Assert.assertEquals(listener.getFailedTests().get(0).getThrowable().getMessage(),
                        "Original UI test failure");
                Assert.assertTrue(listener.getConfigurationFailures().isEmpty());
            }
        }
    }

    static TestListenerAdapter runFixture(Object fixture) {
        TestNG runner = new TestNG();
        runner.setUseDefaultListeners(false);
        runner.setVerbose(0);
        runner.setTestSuites(List.of());
        var suite = new org.testng.xml.XmlSuite();
        var test = new org.testng.xml.XmlTest(suite);
        test.setXmlClasses(List.of(new org.testng.xml.XmlClass(fixture.getClass())));
        runner.setXmlSuites(List.of(suite));
        runner.setObjectFactory(new org.testng.ITestObjectFactory() {
            @Override public <T> T newInstance(Class<T> cls, Object... parameters) {
                return cls.isInstance(fixture) ? cls.cast(fixture)
                        : org.testng.ITestObjectFactory.super.newInstance(cls, parameters);
            }
            @Override public <T> T newInstance(java.lang.reflect.Constructor<T> constructor, Object... parameters) {
                return constructor.getDeclaringClass().isInstance(fixture)
                        ? constructor.getDeclaringClass().cast(fixture)
                        : org.testng.ITestObjectFactory.super.newInstance(constructor, parameters);
            }
        });
        TestListenerAdapter listener = new TestListenerAdapter();
        runner.addListener(listener);
        runner.run();
        return listener;
    }

    @Test public void cucumberCleanupIsUnconditionalAndReportsWithoutReplacingFailure() throws Exception {
        for (boolean failed : new boolean[] {false, true}) {
            FakeClient client = new FakeClient();
            OwnedAccounts accounts = new OwnedAccounts();
            User user = new ApiUserSteps(client, accounts).createUser();
            List<String> attachments = new ArrayList<>();
            var state = (io.cucumber.core.backend.TestCaseState) Proxy.newProxyInstance(Scenario.class.getClassLoader(),
                    new Class<?>[] {io.cucumber.core.backend.TestCaseState.class}, (proxy, method, args) -> {
                        if (method.getName().equals("isFailed")) { return failed; }
                        if (method.getName().equals("attach")) { attachments.add((String) args[0]); }
                        return null;
                    });
            var constructor = Scenario.class.getDeclaredConstructor(io.cucumber.core.backend.TestCaseState.class);
            constructor.setAccessible(true);
            Scenario scenario = constructor.newInstance(state);
            client.failDeletion = user.email();
            AccountHooks hooks = new AccountHooks(accounts, client);
            if (failed) { hooks.cleanupUser(scenario); }
            else { Assert.expectThrows(AssertionError.class, () -> hooks.cleanupUser(scenario)); }
            Assert.assertEquals(attachments.size(), 1);
            Assert.assertTrue(attachments.get(0).contains(user.email()));
        }
        Assert.assertEquals(AccountHooks.class.getMethod("cleanupUser", Scenario.class)
                .getAnnotation(io.cucumber.java.After.class).value(), "");
    }
}
