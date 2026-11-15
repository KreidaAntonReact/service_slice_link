package ru.service.slice.link.util;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public final class UrlValidator {
    public static final String SHORT_URL_HOST = "clck.ru";
    public static final String SHORT_URL_PREFIX = SHORT_URL_HOST + "/";

    private UrlValidator() {
    }

    public static void requireValidUrl(String value) {
        parseUrl(value);
    }

    public static String extractShortCode(String value) {
        URL url = parseUrl(value);

        if (!SHORT_URL_HOST.equalsIgnoreCase(url.getHost())) {
            throw new IllegalArgumentException(getMessageHowUseShortUrl());
        }

        String path = url.getPath();

        if (path == null || path.isBlank() || "/".equals(path)) {
            throw new IllegalArgumentException(getMessageHowUseShortUrl());
        }

        String shortCode = path.startsWith("/") ? path.substring(1) : path;

        if (shortCode.isBlank() || shortCode.contains("/")) {
            throw new IllegalArgumentException(getMessageHowUseShortUrl());
        }

        return shortCode;
    }

    private static URL parseUrl(String value) {
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

        return url;
    }

    private static String getMessageHowUseShortUrl() {
        return "Нужно передать полную короткую ссылку с протоколом, например https://"
                + SHORT_URL_PREFIX
                + "<code>";
    }
}
