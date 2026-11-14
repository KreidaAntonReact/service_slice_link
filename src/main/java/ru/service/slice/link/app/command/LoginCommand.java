package ru.service.slice.link.app.command;

import ru.service.slice.link.entity.UserEntity;
import ru.service.slice.link.service.AuthService;

import java.util.UUID;

public class LoginCommand implements Command {
    private final AuthService authService;

    public LoginCommand(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void execute(String... args) {
        if (args.length < 1) {
            throw new IllegalArgumentException(getMessageHowUseCommand());
        }

        UUID id = UUID.fromString(args[0]);
        UserEntity user = authService.login(id);
        System.out.printf("Вы идентифицированы как %s%n", user.getId());
    }

    @Override
    public String getDescription() {
        return "продолжить работу по UUID - login <uuid>";
    }

    private String getMessageHowUseCommand() {
        return "Ошибка при вводе. Нужно использовать: login <uuid>";
    }
}
