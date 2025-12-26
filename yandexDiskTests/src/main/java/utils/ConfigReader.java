package utils;

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

    public static String getApiBaseUrl() {
        return getProperty("api.base.url");
    }

    public static String getAuthToken() {
        return getProperty("auth.token");
    }

    public static String getResourceUrl() {
        return getProperty("resource.url");
    }

    public static String getTrashUrl() {
        return getProperty("trash.url");
    }
}
