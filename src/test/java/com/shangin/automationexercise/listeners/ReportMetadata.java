package com.shangin.automationexercise.listeners;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import com.shangin.automationexercise.config.ConfigReader;
import com.shangin.automationexercise.driver.DriverManager;
import io.cucumber.core.options.Constants;
import io.cucumber.core.options.CucumberProperties;
import io.cucumber.core.options.CucumberPropertiesParser;
import io.qameta.allure.model.Label;
import io.qameta.allure.model.Parameter;
import io.qameta.allure.model.TestResult;
import org.openqa.selenium.HasCapabilities;

/** Explicit provenance for both adapters; never records machine/user identity. */
public final class ReportMetadata {
    private static final String REVISION = revision();
    private ReportMetadata() { }

    public static synchronized void enrich(TestResult result) {
        result.setParameters(new java.util.ArrayList<>(result.getParameters()));
        result.setLabels(new java.util.ArrayList<>(result.getLabels()));
        boolean discovery = cucumberDryRun() || Boolean.getBoolean("testng.mode.dryrun");
        String execution = discovery ? "DISCOVERY ONLY - no test execution" : "Real execution";
        parameter(result, "Execution", execution);
        parameter(result, "Source revision", REVISION);
        parameter(result, "Java", System.getProperty("java.version"));
        parameter(result, "UI environment", ConfigReader.getBaseUrl());
        parameter(result, "API environment", ConfigReader.getApiBaseUrl());
        String fullName = result.getFullName() == null ? "" : result.getFullName();
        if (fullName.contains(".tests.api.")) {
            suite(result, "API");
        } else if (fullName.contains(".tests.ui.")) {
            suite(result, "TestNG UI");
        }
        if (DriverManager.hasDriver()) {
            String browser = ConfigReader.getBrowser();
            try {
                if (DriverManager.getDriver() instanceof HasCapabilities driver) {
                    browser = driver.getCapabilities().getBrowserName() + " "
                            + driver.getCapabilities().getBrowserVersion();
                }
            } catch (RuntimeException unavailable) { browser += " (version unavailable)"; }
            parameter(result, "Browser", browser);
            parameter(result, "Headless", String.valueOf(ConfigReader.isHeadless()));
        }
        if (discovery && !result.getName().startsWith("[DISCOVERY ONLY]")) {
            result.setName("[DISCOVERY ONLY] " + result.getName());
        }
        Properties environment = new Properties();
        environment.setProperty("Java", System.getProperty("java.version"));
        environment.setProperty("UI environment", ConfigReader.getBaseUrl());
        environment.setProperty("API environment", ConfigReader.getApiBaseUrl());
        environment.setProperty("Source revision", REVISION);
        environment.setProperty("Execution", execution);
        environment.setProperty("Browser configuration", ConfigReader.getBrowser());
        environment.setProperty("Headless configuration", String.valueOf(ConfigReader.isHeadless()));
        Path directory = Path.of(System.getProperty("allure.results.directory", "target/allure-results"));
        try {
            Files.createDirectories(directory);
            try (var output = Files.newOutputStream(directory.resolve("environment.properties"))) {
                environment.store(output, "Automation Exercise test environment");
            }
        } catch (IOException failure) {
            System.err.println("Cannot write Allure environment metadata: " + failure.getMessage());
        }
    }

    private static boolean cucumberDryRun() {
        var properties = CucumberProperties.create();
        // Resolve aliases and source precedence with Cucumber itself; parse only
        // this flag so unrelated Cucumber options cannot disrupt API reporting.
        return new CucumberPropertiesParser()
                .parse(key -> Constants.EXECUTION_DRY_RUN_PROPERTY_NAME.equals(key) ? properties.get(key) : null)
                .build().isDryRun();
    }

    private static void suite(TestResult result, String value) {
        result.getLabels().removeIf(label -> "parentSuite".equals(label.getName()));
        result.getLabels().add(new Label().setName("parentSuite").setValue(value));
    }

    private static void parameter(TestResult result, String name, String value) {
        if (result.getParameters().stream().noneMatch(p -> name.equals(p.getName()))) {
            result.getParameters().add(new Parameter().setName(name).setValue(value).setExcluded(true));
        }
    }

    private static String revision() {
        String override = System.getProperty("source.revision");
        if (override != null && !override.isBlank()) { return override; }
        try {
            Process process = new ProcessBuilder("git", "rev-parse", "HEAD").redirectErrorStream(true).start();
            if (!process.waitFor(5, TimeUnit.SECONDS)) { process.destroyForcibly(); return "unavailable"; }
            String hash = new String(process.getInputStream().readAllBytes(), java.nio.charset.StandardCharsets.UTF_8).strip();
            if (process.exitValue() != 0) { return "unavailable"; }
            Process dirty = new ProcessBuilder("git", "status", "--porcelain", "--untracked-files=no").start();
            if (!dirty.waitFor(5, TimeUnit.SECONDS)) { dirty.destroyForcibly(); return hash + " (state unavailable)"; }
            return hash + (dirty.getInputStream().readAllBytes().length == 0 ? "" : " + working tree changes");
        } catch (IOException failure) { return "unavailable (set -Dsource.revision for exported sources)"; }
        catch (InterruptedException failure) { Thread.currentThread().interrupt(); return "unavailable"; }
    }
}
