package com.shangin.automationexercise.listeners;

import com.shangin.automationexercise.cucumber.runners.CucumberTest;
import com.shangin.automationexercise.tests.support.cucumberprobe.FailureProbeSteps;
import io.cucumber.testng.PickleWrapper;
import io.cucumber.testng.TestNGCucumberRunner;
import io.qameta.allure.model.TestResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import tools.jackson.databind.ObjectMapper;

public class ReportMetadataTest {
    @Test
    public void immutableCucumberParametersAndRepeatedCallbacksRemainUsable() {
        var result =
                new TestResult().setName("scenario").setParameters(List.of()).setLabels(List.of());
        ReportMetadata.enrich(result);
        ReportMetadata.enrich(result);
        Assert.assertEquals(
                result.getParameters().stream()
                        .filter(p -> p.getName().equals("Execution"))
                        .count(),
                1L);
        Assert.assertTrue(
                result.getParameters().stream()
                        .anyMatch(p -> p.getName().equals("Source revision")));
    }

    @Test
    public void discoveryCannotLookLikeExecutionEvidence() {
        String previous = System.getProperty("cucumber.execution.dry-run");
        try {
            System.setProperty("cucumber.execution.dry-run", "true");
            var result = new TestResult().setName("scenario").setParameters(List.of());
            ReportMetadata.enrich(result);
            ReportMetadata.enrich(result);
            Assert.assertEquals(result.getName(), "[DISCOVERY ONLY] scenario");
            Assert.assertTrue(
                    result.getParameters().stream()
                            .anyMatch(p -> p.getValue().contains("no test execution")));
        } finally {
            if (previous == null) {
                System.clearProperty("cucumber.execution.dry-run");
            } else {
                System.setProperty("cucumber.execution.dry-run", previous);
            }
        }
    }

    @DataProvider(name = "cucumberDryRunSources")
    public Object[][] cucumberDryRunSources() {
        return new Object[][] {
            {null, null, null, false},
            {null, "true", null, true},
            {"true", null, null, true},
            {"true", "false", null, false},
            {"true", "true", "false", false},
            {"false", "false", "true", true},
            {null, "yes", null, true},
            {null, null, "1", true}
        };
    }

    @Test(dataProvider = "cucumberDryRunSources")
    public void actualCucumberModeMatchesAllureEvidence(
            String file, String environment, String system, boolean discovery) throws Exception {
        Path directory =
                Files.createTempDirectory(Path.of("target"), "report-metadata-").toAbsolutePath();
        if (file != null) {
            Files.writeString(
                    directory.resolve("cucumber.properties"),
                    "cucumber.execution.dry-run=" + file + "\n");
        }
        Path feature = directory.resolve("probe.feature");
        Files.writeString(
                feature,
                "@fake-browser\nFeature: Execution metadata\n"
                        + "  Scenario: Metadata follows runtime\n"
                        + "    Given a local browser failure probe is opened\n");
        String javaExecutable = Path.of(System.getProperty("java.home"), "bin", "java").toString();
        String classpath =
                directory
                        + java.io.File.pathSeparator
                        + System.getProperty(
                                "surefire.test.class.path", System.getProperty("java.class.path"));
        var command =
                new ArrayList<>(
                        List.of(
                                javaExecutable,
                                "-cp",
                                classpath,
                                "-Dallure.results.directory=" + directory.resolve("allure"),
                                "-Dsource.revision=metadata-regression",
                                "-Dcucumber.features=" + feature,
                                "-Dcucumber.glue=com.shangin.automationexercise.tests.support.cucumberprobe"));
        if (system != null) {
            command.add("-Dcucumber.execution.dry-run=" + system);
        }
        command.add(ReportMetadataTest.class.getName());
        command.add(Boolean.toString(discovery));
        var builder =
                new ProcessBuilder(command)
                        .redirectErrorStream(true)
                        .redirectOutput(directory.resolve("console.log").toFile());
        builder.environment()
                .keySet()
                .removeIf(key -> key.startsWith("CUCUMBER_") || key.startsWith("cucumber"));
        if (environment != null) {
            builder.environment().put("CUCUMBER_EXECUTION_DRY_RUN", environment);
        }
        Process process = builder.start();
        if (!process.waitFor(45, TimeUnit.SECONDS)) {
            process.destroyForcibly();
            Assert.fail("Cucumber metadata probe timed out: " + directory);
        }
        Assert.assertEquals(
                process.exitValue(), 0, Files.readString(directory.resolve("console.log")));
    }

    /** Isolated JVM: exercise real Cucumber resolution and the Allure SPI callback. */
    public static void main(String[] args) throws Exception {
        boolean discovery = Boolean.parseBoolean(args[0]);
        var runner = new TestNGCucumberRunner(CucumberTest.class);
        try {
            Object[][] scenarios = runner.provideScenarios();
            Assert.assertEquals(scenarios.length, 1);
            runner.runScenario(((PickleWrapper) scenarios[0][0]).getPickle());
        } finally {
            runner.finish();
        }
        Assert.assertEquals(
                FailureProbeSteps.quitCalled,
                !discovery,
                "Hooks must match the actual execution mode");
        Path output = Path.of(System.getProperty("allure.results.directory"));
        List<Path> results;
        try (var files = Files.list(output)) {
            results = files.filter(path -> path.toString().endsWith("-result.json")).toList();
        }
        Assert.assertEquals(results.size(), 1, "One real Cucumber Allure scenario result");
        var result = new ObjectMapper().readTree(results.get(0).toFile());
        Assert.assertEquals(result.get("status").asText(), "passed");
        Assert.assertEquals(
                result.get("name").asText(),
                (discovery ? "[DISCOVERY ONLY] " : "") + "Metadata follows runtime");
        String execution = discovery ? "DISCOVERY ONLY - no test execution" : "Real execution";
        int executionParameters = 0;
        for (var parameter : result.get("parameters")) {
            if ("Execution".equals(parameter.get("name").asText())) {
                executionParameters++;
                Assert.assertEquals(parameter.get("value").asText(), execution);
            }
        }
        Assert.assertEquals(executionParameters, 1);
        var properties = new Properties();
        try (var input = Files.newInputStream(output.resolve("environment.properties"))) {
            properties.load(input);
        }
        Assert.assertEquals(properties.getProperty("Execution"), execution);
    }
}
