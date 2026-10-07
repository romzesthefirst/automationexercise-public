package com.shangin.automationexercise.driver;

import com.shangin.automationexercise.config.ConfigReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

/** A worker owns only its unique browser-session directory. */
public final class DownloadDirectory {
    private static final ThreadLocal<Path> CURRENT = new ThreadLocal<>();

    private DownloadDirectory() {}

    public static Path current() {
        Path directory = CURRENT.get();
        if (directory == null) {
            try {
                Path root = ConfigReader.getDownloadDirectory().toAbsolutePath();
                Files.createDirectories(root);
                directory = Files.createTempDirectory(root, "session-");
                CURRENT.set(directory);
            } catch (IOException failure) {
                throw new UncheckedIOException("Cannot create download directory", failure);
            }
        }
        return directory;
    }

    public static void close() {
        Path directory = CURRENT.get();
        CURRENT.remove();
        if (directory == null) {
            return;
        }
        try (var paths = Files.walk(directory)) {
            for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) {
                Files.deleteIfExists(path);
            }
        } catch (IOException failure) {
            throw new UncheckedIOException(
                    "Cannot remove owned download directory: " + directory, failure);
        }
    }
}
