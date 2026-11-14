package ru.service.slice.link.service;

import ru.service.slice.link.entity.UserEntity;
import ru.service.slice.link.repository.UserRepository;

import java.util.UUID;

public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserEntity create() {
        UserEntity user = new UserEntity();
        userRepository.setEntity(user);
        return user;
    }

    public UserEntity getById(UUID id) {
        return userRepository.getEntity(id);
    }
}
