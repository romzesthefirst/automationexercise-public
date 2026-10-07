package com.shangin.automationexercise.config;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Properties;
import java.util.function.Function;

/** Shared configuration for API, TestNG UI, and Cucumber. */
public final class ConfigReader {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = ConfigReader.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) { throw new IllegalStateException("config.properties not found"); }
            properties.load(input);
        } catch (IOException failure) {
            throw new IllegalStateException("Cannot read config.properties", failure);
        }
    }

    private ConfigReader() { }

    static String resolve(String key, Function<String, String> system, Function<String, String> environment,
            Properties defaults) {
        String value = system.apply(key);
        if (value == null) { value = environment.apply(environmentKey(key)); }
        if (value == null) { value = defaults.getProperty(key); }
        if (value == null || value.isBlank()) { throw invalid(key, "a nonempty value is required"); }
        return value.strip();
    }

    public static String environmentKey(String key) {
        return "AE_" + key.toUpperCase(Locale.ROOT).replace('.', '_');
    }

    public static String getProperty(String key) {
        return resolve(key, System::getProperty, System::getenv, properties);
    }

    private static IllegalArgumentException invalid(String key, String expected) {
        return new IllegalArgumentException("Invalid configuration '" + key + "' (" + environmentKey(key)
                + "): " + expected);
    }

    private static int positiveInt(String key) {
        try {
            int value = Integer.parseInt(getProperty(key));
            if (value > 0) { return value; }
        } catch (NumberFormatException ignored) { }
        throw invalid(key, "expected an integer from 1 to " + Integer.MAX_VALUE);
    }

    private static boolean booleanValue(String key) {
        String value = getProperty(key);
        if (value.equalsIgnoreCase("true")) { return true; }
        if (value.equalsIgnoreCase("false")) { return false; }
        throw invalid(key, "expected true or false");
    }

    private static String url(String key) {
        String value = getProperty(key);
        try {
            URI uri = URI.create(value);
            if (("http".equalsIgnoreCase(uri.getScheme()) || "https".equalsIgnoreCase(uri.getScheme()))
                    && uri.getHost() != null && uri.getPort() <= 65535 && uri.getUserInfo() == null
                    && uri.getFragment() == null && uri.getQuery() == null) { return value; }
        } catch (IllegalArgumentException ignored) { }
        throw invalid(key, "expected an absolute HTTP(S) URL without credentials, query, or fragment");
    }

    public static String getBaseUrl() { return url("base.url"); }
    public static String getApiBaseUrl() { return url("api.base.url"); }
    public static int getDownloadTimeout() { return positiveInt("download.timeout"); }
    public static int getScriptTimeout() { return positiveInt("script.timeout"); }
    public static int getPageLoadTimeout() { return positiveInt("page.load.timeout"); }
    public static long getExplicitWait() { return positiveInt("explicit.wait"); }
    public static int getBrowserWidth() { return positiveInt("browser.width"); }
    public static int getBrowserHeight() { return positiveInt("browser.height"); }
    public static boolean isHeadless() { return booleanValue("headless"); }
    public static boolean isIncognito() { return booleanValue("incognito"); }
    public static boolean isNetworkDiagnosticsEnabled() { return booleanValue("network.diagnostics.enabled"); }
    public static boolean isAdsHandlingEnabled() { return booleanValue("ads.handling.enabled"); }

    public static String getBrowser() {
        String value = getProperty("browser").toLowerCase(Locale.ROOT);
        if (value.equals("chrome") || value.equals("firefox") || value.equals("edge")) { return value; }
        throw invalid("browser", "expected chrome, firefox, or edge");
    }

    public static Path getDownloadDirectory() {
        try {
            Path directory = Path.of(getProperty("download.directory"));
            return (directory.isAbsolute() ? directory : Path.of(System.getProperty("user.dir")).resolve(directory))
                    .toAbsolutePath().normalize();
        } catch (java.nio.file.InvalidPathException failure) {
            throw invalid("download.directory", "expected a valid directory path");
        }
    }

    public static String getDownloadMimeTypes() {
        String value = getProperty("download.mime.types");
        if (!value.matches("[\\w.+-]+/[\\w.+-]+(\\s*,\\s*[\\w.+-]+/[\\w.+-]+)*")) {
            throw invalid("download.mime.types", "expected comma-separated MIME types");
        }
        return String.join(",", value.split("\\s*,\\s*"));
    }

    /** Validate before launching a browser or making an API request. */
    public static void validate() {
        getBaseUrl(); getApiBaseUrl(); getBrowser();
        getScriptTimeout(); getPageLoadTimeout(); getExplicitWait();
        getBrowserWidth(); getBrowserHeight(); isHeadless(); isIncognito(); isAdsHandlingEnabled(); isNetworkDiagnosticsEnabled();
        getDownloadDirectory(); getDownloadMimeTypes(); getDownloadTimeout();
    }
}
