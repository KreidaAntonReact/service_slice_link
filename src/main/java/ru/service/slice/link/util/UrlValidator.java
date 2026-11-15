package ru.service.slice.link.util;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public final class UrlValidator {
    public static void requireValidUrl(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Ссылка не указана");
        }

        URL url;

        try {
            url = new URI(value.trim()).toURL();
        } catch (URISyntaxException | MalformedURLException | IllegalArgumentException e) {
            throw new IllegalArgumentException("Некорректная ссылка");
        }

        String protocol = url.getProtocol();

        if (!"http".equalsIgnoreCase(protocol) && !"https".equalsIgnoreCase(protocol)) {
            throw new IllegalArgumentException("Ссылка должна начинаться с http:// или https://");
        }

        if (url.getHost() == null || url.getHost().isBlank()) {
            throw new IllegalArgumentException("Некорректная ссылка");
        }
    }
}
