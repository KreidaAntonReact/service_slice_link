package ru.service.slice.link.entity;

import java.time.LocalDateTime;
import java.util.UUID;

public class UrlEntity {
    private static final long LIFE_TIME_HOURS = 1;
    private static final int DEFAULT_LIMIT = 10;

    private long id;
    private String urlLong;
    private UUID userId;
    private LocalDateTime createAt;
    private LocalDateTime expiresDate;
    private int limit;
    private int count;
    private boolean isBlocked;

    public UrlEntity() {
        this.id = this.generateId();
        this.createAt = LocalDateTime.now();
        this.expiresDate = this.createAt.plusHours(LIFE_TIME_HOURS);
        this.limit = DEFAULT_LIMIT;
        this.count = 0;
        this.isBlocked = false;
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

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    public long generateId() {
        return Math.abs(UUID.randomUUID().getMostSignificantBits());
    }
}
