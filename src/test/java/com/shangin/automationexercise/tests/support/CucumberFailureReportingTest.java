package com.shangin.automationexercise.tests.support;

import com.shangin.automationexercise.driver.DriverManager;
import com.shangin.automationexercise.tests.support.cucumberprobe.FailureProbeSteps;
import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.model.Status;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CucumberFailureReportingTest {
    @Test
    public void actualCucumberAdapterKeepsEvidenceInFixtureContext() throws Exception {
        for (boolean broken : new boolean[] {false, true}) {
            runProbe(false, broken);
        }
    }

    @Test
    public void liveCucumberFailureRetainsScreenshotAndUrl() throws Exception {
        if (!Boolean.getBoolean("driver.lifecycle.live")) {
            throw new org.testng.SkipException("Enable with -Ddriver.lifecycle.live=true");
        }
        runProbe(true, false);
    }

    private static void runProbe(boolean live, boolean broken) throws Exception {
        AllureLifecycle previous = Allure.getLifecycle();
        var writer =
                new DriverReportingTest.RecordingWriter(
                        live ? "live-cucumber-failure" : "cucumber-adapter");
        Path feature =
                Path.of("target", "driver-lifecycle-validation", UUID.randomUUID() + ".feature");
        Files.writeString(
                feature,
                (live ? "@ui" : "@fake-browser")
                        + "\nFeature: Failure evidence\n"
                        + "  Scenario: Preserve original Cucumber failure\n"
                        + "    Given a local browser failure probe is opened\n"
                        + "    Then the browser probe deliberately fails\n");
        try {
            Allure.setLifecycle(new AllureLifecycle(writer));
            FailureProbeSteps.brokenSession = broken;
            FailureProbeSteps.quitCalled = false;
            FailureProbeSteps.screenshotCalls = 0;
            FailureProbeSteps.urlCalls = 0;
            var arguments =
                    new java.util.ArrayList<>(
                            List.of(
                                    "--glue",
                                    "com.shangin.automationexercise.tests.support.cucumberprobe",
                                    "--plugin",
                                    "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"));
            if (live) {
                arguments.addAll(
                        List.of("--glue", "com.shangin.automationexercise.cucumber.hooks"));
            }
            arguments.add(feature.toString());
            System.out.println(
                    "[EXPECTED FAILURE PROBE] The nested Cucumber scenario deliberately fails; "
                            + "the outer support test verifies its diagnostics.");
            byte exit =
                    io.cucumber.core.cli.Main.run(
                            arguments.toArray(String[]::new),
                            CucumberFailureReportingTest.class.getClassLoader());
            Assert.assertEquals(exit, (byte) 1, "The inner scenario must fail");
            System.out.println(
                    "[EXPECTED FAILURE PROBE] Nested exit code 1 verified; "
                            + "continuing support-test assertions.");
            Assert.assertFalse(DriverManager.hasDriver());
            var report =
                    writer.results.stream()
                            .filter(r -> r.getName().equals("Preserve original Cucumber failure"))
                            .findFirst()
                            .orElseThrow();
            Assert.assertEquals(report.getStatus(), Status.FAILED);
            Assert.assertTrue(
                    report.getStatusDetails().getMessage().contains("Original Cucumber failure"));
            Assert.assertEquals(
                    report.getAttachments().stream().map(a -> a.getName()).toList(),
                    broken
                            ? List.of("Browser diagnostics")
                            : List.of(
                                    "Current URL", "Screenshot on failure", "Browser diagnostics"));
            if (!live) {
                Assert.assertTrue(FailureProbeSteps.quitCalled);
                Assert.assertEquals(FailureProbeSteps.screenshotCalls, 1);
                Assert.assertEquals(FailureProbeSteps.urlCalls, 1);
            }
        } finally {
            if (DriverManager.hasDriver()) {
                DriverManager.quitDriver();
            }
            Allure.setLifecycle(previous);
            Files.deleteIfExists(feature);
            FailureProbeSteps.brokenSession = false;
        }
    }
}
