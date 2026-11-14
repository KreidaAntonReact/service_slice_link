package ru.service.slice.link.service;

import ru.service.slice.link.entity.UserEntity;

import java.util.Objects;
import java.util.UUID;

public class AuthService {
    private final UserService userService;
    private UserEntity currentUser;

    public AuthService(UserService userService) {
        this.userService = userService;
    }

    public UserEntity register() {
        if (Objects.nonNull(currentUser)) {
            String message = String.format("Вы уже идентифицированы как - %s", currentUser.getId());
            throw new IllegalStateException(message);
        }

        UserEntity user = userService.create();
        this.currentUser = user;
        return user;
    }

    public UserEntity login(UUID id) {
        if (Objects.nonNull(currentUser)) {
            String message = String.format("Вы уже идентифицированы как - %s", currentUser.getId());
            throw new IllegalStateException(message);
        }

        UserEntity user = userService.getById(id);
        this.currentUser = user;
        return user;
    }

    public void logout() {
        this.currentUser = null;
    }

    public UserEntity requireCurrentUser() {
        if (Objects.isNull(currentUser)) {
            throw new IllegalStateException("Необходимо идентифицироваться. Выполните shorten, user new или login <uuid>");
        }

        return currentUser;
    }

    public UserEntity getCurrentUser() {
        return currentUser;
    }

    public boolean hasCurrentUser() {
        return Objects.nonNull(currentUser);
    }
}
