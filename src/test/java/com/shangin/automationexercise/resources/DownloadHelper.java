package com.shangin.automationexercise.resources;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Duration;
import com.shangin.automationexercise.driver.DownloadDirectory;

public final class DownloadHelper {
    private DownloadHelper() { }

    private static Path ownedFile(String fileName) {
        Path name = Path.of(fileName);
        if (fileName.isBlank() || !name.toString().equals(fileName) || name.isAbsolute() || name.getNameCount() != 1 || fileName.equals(".") || fileName.equals("..")) {
            throw new IllegalArgumentException("Expected a download file name: " + fileName);
        }
        return DownloadDirectory.current().resolve(name);
    }

    public static void prepareFile(String fileName) throws IOException {
        Files.deleteIfExists(ownedFile(fileName));
    }

    public static Path waitForFile(String fileName, Duration timeout) {
        Path file = ownedFile(fileName);
        long deadline = System.nanoTime() + timeout.toNanos();
        long stableSince = 0;
        long previousSize = -1;
        FileTime previousModified = null;
        while (System.nanoTime() < deadline) {
            try {
                boolean partial;
                try (var paths = Files.list(file.getParent())) {
                    partial = paths.anyMatch(path -> {
                        String name = path.getFileName().toString();
                        return name.endsWith(".crdownload") || name.endsWith(".part") || name.endsWith(".tmp");
                    });
                }
                if (!partial && Files.isRegularFile(file) && Files.size(file) > 0) {
                    long size = Files.size(file);
                    FileTime modified = Files.getLastModifiedTime(file);
                    if (size != previousSize || !modified.equals(previousModified)) {
                        stableSince = System.nanoTime();
                        previousSize = size;
                        previousModified = modified;
                    } else if (System.nanoTime() - stableSince >= Duration.ofMillis(600).toNanos()) {
                        return file;
                    }
                } else {
                    previousSize = -1;
                    stableSince = 0;
                }
            } catch (IOException failure) {
                // Browser renames/removes temporary files while the directory is polled.
                previousSize = -1;
                stableSince = 0;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException failure) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Interrupted waiting for download: " + file, failure);
            }
        }
        throw new AssertionError("Download did not complete (nonempty stable file without temporary downloads): " + file);
    }

    public static String waitAndRead(String fileName) throws IOException {
        return waitAndRead(fileName, Duration.ofSeconds(
                com.shangin.automationexercise.config.ConfigReader.getDownloadTimeout()));
    }

    public static String waitAndRead(String fileName, Duration timeout) throws IOException {
        Path file = waitForFile(fileName, timeout);
        io.qameta.allure.Allure.addAttachment("Downloaded file", "text/plain", file.toString());
        return Files.readString(file);
    }
}
