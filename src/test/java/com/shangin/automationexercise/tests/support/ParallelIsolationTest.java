package com.shangin.automationexercise.tests.support;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Callable;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import com.shangin.automationexercise.driver.DownloadDirectory;
import com.shangin.automationexercise.driver.BrowserOptionsFactory;
import com.shangin.automationexercise.factories.UserFactory;
import com.shangin.automationexercise.resources.DownloadHelper;
import com.shangin.automationexercise.support.TestRandom;
import com.shangin.automationexercise.api.specs.ApiSpecifications;

public class ParallelIsolationTest {
    @DataProvider public Object[][] workers() { return new Object[][] {{1}, {4}}; }

    @Test(dataProvider = "workers")
    public void repeatedWorkersReadAndRemoveOnlyOwnedInvoices(int workers) throws Exception {
        var pool = Executors.newFixedThreadPool(workers);
        Path sentinel = Files.createTempFile(DownloadDirectory.current().getParent(), "unrelated-", ".txt");
        Files.writeString(sentinel, "unrelated");
        try {
            List<Callable<Path>> tasks = new ArrayList<>();
            for (int run = 0; run < 12; run++) {
                String content = "Invoice for invocation " + run;
                tasks.add(() -> {
                    Path directory = DownloadDirectory.current();
                    org.openqa.selenium.WebDriver driver = (org.openqa.selenium.WebDriver) java.lang.reflect.Proxy.newProxyInstance(
                            getClass().getClassLoader(), new Class<?>[] {org.openqa.selenium.WebDriver.class},
                            (proxy, method, arguments) -> null);
                    com.shangin.automationexercise.driver.DriverManager.setDriver(driver);
                    TestRandom.begin(1234, message -> { });
                    try {
                        Assert.assertSame(com.shangin.automationexercise.driver.DriverManager.getDriver(), driver);
                        DownloadHelper.prepareFile("invoice.txt");
                        Files.writeString(directory.resolve("invoice.txt"), content);
                        Assert.assertEquals(DownloadHelper.waitAndRead("invoice.txt", Duration.ofSeconds(3)), content);
                        return directory;
                    } finally {
                        com.shangin.automationexercise.driver.DriverManager.quitDriver();
                        TestRandom.clear();
                        Assert.assertFalse(com.shangin.automationexercise.driver.DriverManager.hasDriver());
                    }
                });
            }
            var paths = pool.invokeAll(tasks);
            var unique = new java.util.HashSet<Path>();
            for (var result : paths) {
                Path directory = result.get();
                Assert.assertFalse(Files.exists(directory));
                Assert.assertTrue(unique.add(directory), "A reused worker must get a new directory");
            }
            Assert.assertEquals(Files.readString(sentinel), "unrelated");
        } finally {
            pool.shutdownNow();
            Files.deleteIfExists(sentinel);
            DownloadDirectory.close();
        }
    }

    @DataProvider public Object[][] temporarySuffixes() {
        return new Object[][] {{".crdownload"}, {".part"}, {".tmp"}, {""}};
    }

    @Test(dataProvider = "temporarySuffixes")
    public void waitsForTemporaryFilesAndIncompleteWrites(String suffix) throws Exception {
        Path directory = DownloadDirectory.current();
        var pool = Executors.newSingleThreadExecutor();
        try {
            Path file = directory.resolve("invoice.txt");
            Path partial = directory.resolve("invoice.txt" + suffix);
            if (!suffix.isEmpty()) { Files.writeString(partial, "pending"); }
            Files.writeString(file, "first");
            var writer = pool.submit(() -> {
                try {
                    Thread.sleep(suffix.isEmpty() ? 300 : 800);
                    Files.writeString(file, "complete invoice");
                    Thread.sleep(200);
                    if (!suffix.isEmpty()) { Files.delete(partial); }
                } catch (Exception failure) { throw new RuntimeException(failure); }
            });
            Assert.assertEquals(DownloadHelper.waitAndRead("invoice.txt", Duration.ofSeconds(4)), "complete invoice");
            writer.get();
            Files.writeString(file, "");
            Assert.expectThrows(AssertionError.class,
                    () -> DownloadHelper.waitForFile("invoice.txt", Duration.ofMillis(200)));
        } finally { pool.shutdownNow(); DownloadDirectory.close(); }
    }

    @Test public void failedShutdownStillClearsDriverAndOwnedFiles() throws Exception {
        Path directory = DownloadDirectory.current();
        Files.writeString(directory.resolve("invoice.txt"), "owned");
        var failure = new IllegalStateException("Injected shutdown failure");
        org.openqa.selenium.WebDriver driver = (org.openqa.selenium.WebDriver) java.lang.reflect.Proxy.newProxyInstance(
                getClass().getClassLoader(), new Class<?>[] {org.openqa.selenium.WebDriver.class},
                (proxy, method, arguments) -> { throw failure; });
        com.shangin.automationexercise.driver.DriverManager.setDriver(driver);
        Assert.assertSame(Assert.expectThrows(IllegalStateException.class,
                com.shangin.automationexercise.driver.DriverManager::quitDriver), failure);
        Assert.assertFalse(com.shangin.automationexercise.driver.DriverManager.hasDriver());
        Assert.assertFalse(Files.exists(directory));
    }

    @Test public void rejectsPathsOutsideOwnedDirectory() {
        for (String path : List.of("../invoice.txt", "/tmp/invoice.txt", "", ".", "..")) {
            Assert.expectThrows(IllegalArgumentException.class, () -> DownloadHelper.prepareFile(path));
        }
    }

    @Test public void browserOptionsUseTheSameOwnedDirectory() {
        try {
            String directory = DownloadDirectory.current().toString();
            var chrome = (java.util.Map<?, ?>) BrowserOptionsFactory.chrome().getCapability("goog:chromeOptions");
            Assert.assertEquals(((java.util.Map<?, ?>) chrome.get("prefs")).get("download.default_directory"), directory);
            var edge = (java.util.Map<?, ?>) BrowserOptionsFactory.edge().getCapability("ms:edgeOptions");
            Assert.assertEquals(((java.util.Map<?, ?>) edge.get("prefs")).get("download.default_directory"), directory);
            var firefox = (java.util.Map<?, ?>) BrowserOptionsFactory.firefox().getCapability("moz:firefoxOptions");
            Assert.assertEquals(((java.util.Map<?, ?>) firefox.get("prefs")).get("browser.download.dir"), directory);
        } finally { DownloadDirectory.close(); }
    }

    @Test public void seedReplayPreservesDataButNeverReusesAccountIdentity() {
        try {
            TestRandom.begin(1234, message -> { });
            var first = UserFactory.randomUser();
            int product = TestRandom.productIndex(100);
            TestRandom.clear();
            TestRandom.begin(1234, message -> { });
            var replay = UserFactory.randomUser();
            Assert.assertEquals(replay.firstName(), first.firstName());
            Assert.assertEquals(replay.password(), first.password());
            Assert.assertNotEquals(replay.email(), first.email());
            Assert.assertEquals(TestRandom.productIndex(100), product);
        } finally {
            TestRandom.clear();
        }
    }

    @Test(dataProvider = "workers")
    public void replayIsIndependentOfWorkerScheduling(int workers) throws Exception {
        var pool = Executors.newFixedThreadPool(workers);
        try {
            List<Callable<com.shangin.automationexercise.model.User>> tasks = new ArrayList<>();
            for (int run = 0; run < 12; run++) {
                tasks.add(() -> {
                    TestRandom.begin(1234, message -> { });
                    try { return UserFactory.randomUser(); }
                    finally { TestRandom.clear(); }
                });
            }
            var results = pool.invokeAll(tasks);
            var emails = new java.util.HashSet<String>();
            String expectedName = results.get(0).get().firstName();
            for (var result : results) {
                var user = result.get();
                Assert.assertEquals(user.firstName(), expectedName);
                Assert.assertTrue(emails.add(user.email()), "Replay must retain unique ownership identities");
            }
        } finally { pool.shutdownNow(); }
    }

    @Test(dataProvider = "workers")
    public void parallelRequestsDoNotShareHeadersParametersOrFilters(int workers) throws Exception {
        var server = com.sun.net.httpserver.HttpServer.create(new java.net.InetSocketAddress("127.0.0.1", 0), 0);
        var requests = Executors.newFixedThreadPool(workers);
        var handlers = Executors.newFixedThreadPool(workers);
        server.setExecutor(handlers);
        server.createContext("/", exchange -> {
            byte[] response = (exchange.getRequestHeaders().getFirst("X-Invocation") + ":"
                    + exchange.getRequestURI().getRawQuery()).getBytes(java.nio.charset.StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, response.length);
            try (var output = exchange.getResponseBody()) { output.write(response); }
        });
        server.start();
        try {
            String uri = "http://127.0.0.1:" + server.getAddress().getPort();
            List<Callable<Void>> tasks = new ArrayList<>();
            for (int run = 0; run < 12; run++) {
                String id = Integer.toString(run);
                boolean form = run % 2 == 0;
                tasks.add(() -> {
                    var specification = (form ? ApiSpecifications.formRequest() : ApiSpecifications.defaultRequest()).baseUri(uri)
                            .header("X-Invocation", id).queryParam("id", id);
                    Assert.assertEquals(specification.get("/").asString(), id + ":id=" + id);
                    var fresh = (io.restassured.specification.FilterableRequestSpecification) ApiSpecifications.defaultRequest();
                    Assert.assertNull(fresh.getHeaders().getValue("X-Invocation"));
                    Assert.assertTrue(fresh.getQueryParams().isEmpty());
                    Assert.assertNotSame(fresh.getDefinedFilters().get(0),
                            ((io.restassured.specification.FilterableRequestSpecification) specification).getDefinedFilters().get(0));
                    return null;
                });
            }
            for (var task : requests.invokeAll(tasks)) { task.get(); }
        } finally { server.stop(0); requests.shutdownNow(); handlers.shutdownNow(); }
    }
}
