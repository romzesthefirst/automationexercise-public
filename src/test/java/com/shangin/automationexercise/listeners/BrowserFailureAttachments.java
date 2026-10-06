package com.shangin.automationexercise.listeners;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import com.shangin.automationexercise.driver.DriverManager;
import io.qameta.allure.Allure;
import io.qameta.allure.model.Attachment;
import io.qameta.allure.model.TestResult;

/** Best-effort evidence, collected once per Allure test before browser shutdown. */
public final class BrowserFailureAttachments {
    private BrowserFailureAttachments() { }

    public static void captureCurrentTest() {
        Allure.getLifecycle().getCurrentTestCase().ifPresent(BrowserFailureAttachments::captureTest);
    }

    public static void captureTest(String uuid) {
        Allure.getLifecycle().updateTestCase(uuid, BrowserFailureAttachments::capture);
    }

    public static void capture(TestResult result) {
        if (!DriverManager.hasDriver() || result.getAttachments().stream()
                .anyMatch(attachment -> attachment.getName().equals("Browser diagnostics"))) {
            return;
        }
        WebDriver driver = DriverManager.getDriver();
        StringBuilder diagnostics = new StringBuilder();
        try {
            attach(result, "Current URL", "text/plain", ".txt",
                    driver.getCurrentUrl().getBytes(StandardCharsets.UTF_8));
        } catch (RuntimeException failure) {
            diagnostics.append("URL collection failed: ").append(failure).append('\n');
        }
        try {
            if (driver instanceof TakesScreenshot screenshotDriver) {
                attach(result, "Screenshot on failure", "image/png", ".png",
                        screenshotDriver.getScreenshotAs(OutputType.BYTES));
            } else {
                diagnostics.append("Driver does not support screenshots.\n");
            }
        } catch (RuntimeException failure) {
            diagnostics.append("Screenshot collection failed: ").append(failure).append('\n');
        }
        if (diagnostics.isEmpty()) {
            diagnostics.append("URL and screenshot collected before browser shutdown.\n");
        }
        try {
            attach(result, "Browser diagnostics", "text/plain", ".txt",
                    diagnostics.toString().getBytes(StandardCharsets.UTF_8));
        } catch (RuntimeException ignored) {
            // Report storage failures must not replace the test's original failure.
        }
    }

    private static void attach(TestResult result, String name, String type, String extension, byte[] content) {
        String source = UUID.randomUUID() + "-attachment" + extension;
        Allure.getLifecycle().writeAttachment(source, new ByteArrayInputStream(content));
        result.getAttachments().add(new Attachment().setName(name).setType(type).setSource(source));
    }
}
