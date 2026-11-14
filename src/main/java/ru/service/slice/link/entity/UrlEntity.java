package ru.service.slice.link.entity;

import java.time.LocalDateTime;
import java.util.UUID;

public class UrlEntity {
    private static final long LIFE_TIME_HOURS = 1;

    private long id;
    private String urlLong;
    private UUID userId;
    private LocalDateTime createAt;
    private LocalDateTime expiresDate;

    public UrlEntity() {
        this.id = this.generateId();
        this.createAt = LocalDateTime.now();
        this.expiresDate = this.createAt.plusHours(LIFE_TIME_HOURS);
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

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    public LocalDateTime getExpiresDate() {
        return expiresDate;
    }

    public void setExpiresDate(LocalDateTime expiresDate) {
        this.expiresDate = expiresDate;
    }

    public long generateId() {
        return Math.abs(UUID.randomUUID().getMostSignificantBits());
    }
}
