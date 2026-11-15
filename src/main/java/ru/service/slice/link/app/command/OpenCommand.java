package ru.service.slice.link.app.command;

import ru.service.slice.link.service.UrlService;
import ru.service.slice.link.util.UrlValidator;

import java.awt.Desktop;
import java.net.URI;

public class OpenCommand implements Command {
    private final UrlService urlService;

    public OpenCommand(UrlService urlService) {
        this.urlService = urlService;
    }

    @Override
    public void execute(String... args) {
        if (args.length < 1) {
            throw new IllegalArgumentException(getMessageHowUseCommand());
        }

        String shortCode = UrlValidator.extractShortCode(args[0]);
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
        return "открыть короткую ссылку в браузере - open <https://"
                + UrlValidator.SHORT_URL_PREFIX
                + "code>";
    }

    private String getMessageHowUseCommand() {
        return "Ошибка при вводе. Нужно использовать: open <https://"
                + UrlValidator.SHORT_URL_PREFIX
                + "code>";
    }
}
