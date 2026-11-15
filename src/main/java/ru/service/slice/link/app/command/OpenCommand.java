package ru.service.slice.link.app.command;

import ru.service.slice.link.service.UrlService;
import ru.service.slice.link.util.UrlValidator;

import java.awt.Desktop;
import java.net.URI;

public class OpenCommand implements Command {
    private static final String SHORT_URL_PREFIX = "clck.ru/";

    private final UrlService urlService;

    public OpenCommand(UrlService urlService) {
        this.urlService = urlService;
    }

    @Override
    public void execute(String... args) {
        if (args.length < 1) {
            throw new IllegalArgumentException(getMessageHowUseCommand());
        }

        String input = args[0];

        UrlValidator.requireValidUrl(input);

        String shortCode = extractShortCode(input);
        String longUrl = urlService.getLongUrl(shortCode);
        UrlValidator.requireValidUrl(longUrl);

        try {
            Desktop.getDesktop().browse(new URI(longUrl));
            System.out.printf("Переход по ссылке: %s%n", longUrl);
        } catch (Exception e) {
            throw new IllegalStateException("Не удалось открыть ссылку в браузере: " + e.getMessage(), e);
        }
    }

    @Override
    public String getDescription() {
        return "открыть короткую ссылку в браузере - open <shortUrl>";
    }

    private String extractShortCode(String input) {
        String value = input.trim();

        if (value.startsWith("https://")) {
            value = value.substring("https://".length());
        } else if (value.startsWith("http://")) {
            value = value.substring("http://".length());
        }

        if (value.startsWith(SHORT_URL_PREFIX)) {
            value = value.substring(SHORT_URL_PREFIX.length());
        }

        return value;
    }

    private String getMessageHowUseCommand() {
        return "Ошибка при вводе. Нужно использовать: open <shortUrl>";
    }
}
