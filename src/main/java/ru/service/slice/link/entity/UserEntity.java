package ru.service.slice.link.entity;

import java.util.UUID;

public class UserEntity {
    private UUID id;

    public UserEntity() {
        this.id = UUID.randomUUID();
    }

    public UUID getId() {
        return id;
    }
}
