package com.shangin.automationexercise.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class ConfigReader {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input
                = ConfigReader.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new RuntimeException("config.properties not found");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    public static String getBaseUrl() {
        return getProperty("base.url");
    }

    public static int getScriptTimeout() {
        return Integer.parseInt(getProperty("script.timeout"));
    }

    public static int getPageLoadTimeout() {
        return Integer.parseInt(getProperty("page.load.timeout"));
    }

    public static long getImplicitWait() {
        return Integer.parseInt(getProperty("implicit.wait"));
    }

    public static long getExplicitWait() {
        return Long.parseLong(getProperty("explicit.wait"));
    }

    public static String getBrowser() {
        return getProperty("browser");
    }

    public static boolean isHeadless() {
        return Boolean.parseBoolean(
                System.getProperty("headless", properties.getProperty("headless", "false")));
    }

    public static boolean isIncognito() {
        return Boolean.parseBoolean(getProperty("incognito"));
    }

    public static Path getDownloadDirectory() {
        return Paths.get(System.getProperty("user.dir"), "target", "downloads");
    }

    public static int getBrowserWidth() {
        return Integer.parseInt(properties.getProperty("browser.width"));
    }

    public static int getBrowserHeight() {
        return Integer.parseInt(properties.getProperty("browser.height"));
    }
}
