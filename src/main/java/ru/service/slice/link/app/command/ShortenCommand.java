package ru.service.slice.link.app.command;

import ru.service.slice.link.entity.UserEntity;
import ru.service.slice.link.service.AuthService;
import ru.service.slice.link.service.UrlService;

public class ShortenCommand implements Command {
    private static final String SHORT_URL_PREFIX = "clck.ru/";

    private final AuthService authService;
    private final UrlService urlService;

    public ShortenCommand(AuthService authService, UrlService urlService) {
        this.authService = authService;
        this.urlService = urlService;
    }

    @Override
    public void execute(String... args) {
        if (args.length < 1) {
            throw new IllegalArgumentException(getMessageHowUseCommand());
        }

        boolean isNewUser = !authService.hasCurrentUser();
        UserEntity user = isNewUser ? authService.register() : authService.requireCurrentUser();

        if (isNewUser) {
            System.out.printf("Создан пользователь. Ваш UUID: %s%n", user.getId());
        }

        String longUrl = args[0];
        String shortCode = urlService.convertToShortUrl(longUrl, user.getId());
        System.out.printf("Короткая ссылка: %s%s%s%n", extractProtocol(longUrl), SHORT_URL_PREFIX, shortCode);
    }

    @Override
    public String getDescription() {
        return "создать короткую ссылку - shorten <url>";
    }

    private String extractProtocol(String url) {
        if (url.startsWith("https://")) {
            return "https://";
        }

        if (url.startsWith("http://")) {
            return "http://";
        }

        return "";
    }

    private String getMessageHowUseCommand() {
        return "Ошибка при вводе. Нужно использовать: shorten <url>";
    }
}
