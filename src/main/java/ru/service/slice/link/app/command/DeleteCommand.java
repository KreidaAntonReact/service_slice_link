package ru.service.slice.link.app.command;

import ru.service.slice.link.entity.UserEntity;
import ru.service.slice.link.service.AuthService;
import ru.service.slice.link.service.UrlService;

public class DeleteCommand implements Command {
    private static final String SHORT_URL_PREFIX = "clck.ru/";

    private final AuthService authService;
    private final UrlService urlService;

    public DeleteCommand(AuthService authService, UrlService urlService) {
        this.authService = authService;
        this.urlService = urlService;
    }

    @Override
    public void execute(String... args) {
        if (args.length < 1) {
            throw new IllegalArgumentException(getMessageHowUseCommand());
        }

        UserEntity user = authService.requireCurrentUser();
        String shortCode = extractShortCode(args[0]);

        urlService.deleteUrl(shortCode, user.getId());
        System.out.println("Ссылка успешно удалена");
    }

    @Override
    public String getDescription() {
        return "удалить короткую ссылку - delete <url>";
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
        return "Ошибка при вводе. Нужно использовать: delete <url>";
    }
}
