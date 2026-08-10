package com.shangin.automationexercise.resources;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

import com.shangin.automationexercise.config.ConfigReader;

public final class DownloadHelper {

    private DownloadHelper() {
    }

    public static void prepareFile(String fileName) throws IOException {
        Path directory = ConfigReader.getDownloadDirectory();
        Files.createDirectories(directory);
        Files.deleteIfExists(directory.resolve(fileName));
    }

    public static Path waitForFile(String fileName, Duration timeout) {
        Path file = ConfigReader.getDownloadDirectory().resolve(fileName);
        long deadline = System.nanoTime() + timeout.toNanos();

        while (System.nanoTime() < deadline) {
            if (Files.exists(file)) {
                return file;
            }

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }
        throw new AssertionError("Downloaded file was not found: " + file);
    }

    public static String waitAndRead(String fileName, Duration timeout) throws IOException {
        Path file = waitForFile(fileName, timeout);
        if (Files.size(file) == 0) {
            throw new AssertionError("Downloaded file is empty: " + file);
        }
        return Files.readString(file);
    }
}