package ru.service.slice.link.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppConfig {
    private final long ttlHours;
    private final int defaultLimit;

    public AppConfig() {
        Properties properties = loadProperties();

        this.ttlHours = parsePositiveLong(
                properties.getProperty("link.ttl.hours"),
                "link.ttl.hours"
        );
        this.defaultLimit = parsePositiveInt(
                properties.getProperty("link.default.limit"),
                "link.default.limit"
        );
    }

    public long getTtlHours() {
        return ttlHours;
    }

    public int getDefaultLimit() {
        return defaultLimit;
    }

    private Properties loadProperties() {
        Properties properties = new Properties();

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (inputStream == null) {
                throw new IllegalStateException("Файл application.properties не найден");
            }

            properties.load(inputStream);
        } catch (IOException e) {
            throw new IllegalStateException("Не удалось загрузить application.properties", e);
        }

        return properties;
    }

    private long parsePositiveLong(String value, String key) {
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("В application.properties не задано значение " + key);
        }

        long parsed;

        try {
            parsed = Long.parseLong(value.trim());
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Некорректное значение " + key + " в application.properties", e);
        }

        if (parsed <= 0) {
            throw new IllegalStateException(key + " должен быть больше 0");
        }

        return parsed;
    }

    private int parsePositiveInt(String value, String key) {
        long parsed = parsePositiveLong(value, key);

        if (parsed > Integer.MAX_VALUE) {
            throw new IllegalStateException(key + " слишком большой");
        }

        return (int) parsed;
    }
}
