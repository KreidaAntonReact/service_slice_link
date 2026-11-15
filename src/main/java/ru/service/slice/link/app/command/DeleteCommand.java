package ru.service.slice.link.app.command;

import ru.service.slice.link.entity.UserEntity;
import ru.service.slice.link.service.AuthService;
import ru.service.slice.link.service.UrlService;
import ru.service.slice.link.util.UrlValidator;

public class DeleteCommand implements Command {
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
        String shortCode = UrlValidator.extractShortCode(args[0]);

        urlService.deleteUrl(shortCode, user.getId());
        System.out.println("Ссылка успешно удалена");
    }

    @Override
    public String getDescription() {
        return "удалить короткую ссылку - delete <https://" + UrlValidator.SHORT_URL_PREFIX + "code>";
    }

    private String getMessageHowUseCommand() {
        return "Ошибка при вводе. Нужно использовать: delete <https://"
                + UrlValidator.SHORT_URL_PREFIX
                + "code>";
    }
}
