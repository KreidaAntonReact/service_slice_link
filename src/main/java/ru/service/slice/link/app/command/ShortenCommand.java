package ru.service.slice.link.app.command;

import ru.service.slice.link.service.UrlService;

public class ShortenCommand implements Command {
    private static final String SHORT_URL_PREFIX = "clck.ru/";

    private final UrlService urlService;

    public ShortenCommand(UrlService urlService) {
        this.urlService = urlService;
    }

    @Override
    public void execute(String... args) {
        if (args.length < 1) {
            throw new IllegalArgumentException(getMessageHowUseCommand());
        }

        String shortCode = urlService.convertToShortUrl(args[0]);
        System.out.printf("Короткая ссылка: %s%s%n", SHORT_URL_PREFIX, shortCode);
    }

    @Override
    public String getDescription() {
        return "создать короткую ссылку - shorten <url>";
    }

    private String getMessageHowUseCommand() {
        return "Ошибка при вводе. Нужно использовать: shorten <url>";
    }
}
