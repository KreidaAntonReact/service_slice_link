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

    public void setId(UUID id) {
        this.id = id;
    }
}
