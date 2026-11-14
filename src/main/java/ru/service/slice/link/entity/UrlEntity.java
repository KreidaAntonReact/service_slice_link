package ru.service.slice.link.entity;

import java.util.UUID;

public class UrlEntity {
    private long id;
    private String urlLong;
    private UUID userId;

    public UrlEntity() {
        this.id = this.generateId();
    }

    public String getUrlLong() {
        return urlLong;
    }

    public void setUrlLong(String urlLong) {
        this.urlLong = urlLong;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public long generateId() {
        return Math.abs(UUID.randomUUID().getMostSignificantBits());
    }
}
