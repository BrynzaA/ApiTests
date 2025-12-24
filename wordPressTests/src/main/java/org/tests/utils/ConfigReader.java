package org.tests.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {

    private static Properties properties;
    private static final String CONFIG_PATH = "src/main/resources/config.properties";

    static {
        properties = new Properties();
        try {
            FileInputStream fileInputStream = new FileInputStream(CONFIG_PATH);
            properties.load(fileInputStream);
            fileInputStream.close();
        } catch (IOException e) {
            throw new RuntimeException("Не удалось загрузить config.properties: " + e.getMessage());
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    public static String getValidBasicUsername() {
        return getProperty("api.valid.username");

    }

    public static String getValidBasicPassword() {
        return getProperty("api.valid.password");
    }

    public static String getApiBaseUrl() {
        return getProperty("api.base.url");
    }

    public static String getDbUrl() {
        return properties.getProperty("db.url");
    }

    public static String getDbUsername() {
        return properties.getProperty("db.username");
    }

    public static String getDbPassword() {
        return properties.getProperty("db.password");
    }

    public static String getDbDriver() {
        return properties.getProperty("db.driver");
    }

    public static String getRestRouteName() {
        return getProperty("api.rest.route");
    }

    public static String getRestRouteValue() {
        return getProperty("api.rest.route.value");
    }

    public static String getRestRouteCommentsValue() {
        return getProperty("api.rest.route.comments");
    }
}
