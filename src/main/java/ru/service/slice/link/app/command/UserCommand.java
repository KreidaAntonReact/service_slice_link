package ru.service.slice.link.app.command;

import ru.service.slice.link.entity.UserEntity;
import ru.service.slice.link.service.AuthService;

public class UserCommand implements Command {
    private final AuthService authService;

    public UserCommand(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void execute(String... args) {
        if (args.length > 0 && "new".equalsIgnoreCase(args[0])) {
            UserEntity user = authService.register();
            System.out.printf("Создан новый пользователь. UUID: %s%n", user.getId());
            return;
        }

        if (args.length > 0) {
            throw new IllegalArgumentException(getMessageHowUseCommand());
        }

        if (!authService.hasCurrentUser()) {
            System.out.println("Пользователь ещё не создан. Выполните shorten или user new");
            return;
        }

        System.out.printf("Текущий UUID: %s%n", authService.getCurrentUser().getId());
    }

    @Override
    public String getDescription() {
        return "текущий пользователь / создать нового - user | user new";
    }

    private String getMessageHowUseCommand() {
        return "Ошибка при вводе. Нужно использовать: user | user new";
    }
}
