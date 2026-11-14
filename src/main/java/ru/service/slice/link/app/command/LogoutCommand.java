package ru.service.slice.link.app.command;

import ru.service.slice.link.entity.UserEntity;
import ru.service.slice.link.service.AuthService;

public class LogoutCommand implements Command {
    private final AuthService authService;

    public LogoutCommand(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void execute(String... args) {
        UserEntity user = authService.requireCurrentUser();
        authService.logout();
        System.out.printf("Пользователь %s вышел из сессии%n", user.getId());
    }

    @Override
    public String getDescription() {
        return "завершить текущую сессию пользователя - logout";
    }
}
