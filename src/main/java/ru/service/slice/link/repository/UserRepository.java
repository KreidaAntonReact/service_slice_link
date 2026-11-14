package ru.service.slice.link.repository;

import ru.service.slice.link.entity.UserEntity;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

public class UserRepository {
    private final Map<UUID, UserEntity> users = new LinkedHashMap<>();

    public Boolean setEntity(UserEntity entity) {
        if (users.containsKey(entity.getId())) {
            return false;
        }

        users.put(entity.getId(), entity);
        return true;
    }

    public UserEntity getEntity(UUID id) {
        UserEntity entity = users.get(id);

        if (entity == null) {
            throw new NoSuchElementException("Пользователь не найден");
        }

        return entity;
    }

    public boolean exists(UUID id) {
        return users.containsKey(id);
    }
}
