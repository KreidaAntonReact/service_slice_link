package ru.service.slice.link.app.command;

import ru.service.slice.link.entity.UserEntity;
import ru.service.slice.link.service.AuthService;
import ru.service.slice.link.service.UrlService;
import ru.service.slice.link.util.UrlValidator;

public class ShortenCommand implements Command {
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

        String longUrl = args[0];
        UrlValidator.requireValidUrl(longUrl);

        boolean isNewUser = !authService.hasCurrentUser();
        UserEntity user = isNewUser ? authService.register() : authService.requireCurrentUser();

        if (isNewUser) {
            System.out.printf("Создан пользователь. Ваш UUID: %s%n", user.getId());
        }

        Integer requestedLimit = null;

        if (args.length >= 2) {
            requestedLimit = parseLimit(args[1]);
        }

        String shortCode = urlService.convertToShortUrl(longUrl, user.getId(), requestedLimit);
        System.out.printf(
                "Короткая ссылка: %s%s%s%n",
                extractProtocol(longUrl),
                UrlValidator.SHORT_URL_PREFIX,
                shortCode
        );
    }

    @Override
    public String getDescription() {
        return "создать короткую ссылку - shorten <url> [limit]";
    }

    private int parseLimit(String value) {
        int limit;

        try {
            limit = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Лимит переходов должен быть числом");
        }

        if (limit <= 0) {
            throw new IllegalArgumentException("Лимит переходов должен быть больше 0");
        }

        return limit;
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
        return "Ошибка при вводе. Нужно использовать: shorten <url> [limit]";
    }
}
