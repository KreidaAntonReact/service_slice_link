package ru.service.slice.link.entity;

import java.util.UUID;

public class UrlEntity {
    private final long id;
    private String urlLong;

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

    public long generateId() {
        return Math.abs(UUID.randomUUID().getMostSignificantBits());
    }
}
