package ru.service.slice.link.entity;

import java.time.LocalDateTime;
import java.util.UUID;

public class UrlEntity {
    private long id;
    private String urlLong;
    private String urlShort;
    private UUID userId;
    private LocalDateTime expiresDate;
    private int limit;
    private int count;
    private boolean isBlocked;

    public UrlEntity(long lifeTimeHours) {
        if (lifeTimeHours <= 0) {
            throw new IllegalArgumentException("Время жизни ссылки должно быть больше 0");
        }

        this.id = this.generateId();
        this.expiresDate = LocalDateTime.now().plusHours(lifeTimeHours);
        this.count = 0;
        this.isBlocked = false;
    }

    public String getUrlLong() {
        return urlLong;
    }

    public void setUrlLong(String urlLong) {
        this.urlLong = urlLong;
    }

    public String getUrlShort() {
        return urlShort;
    }

    public void setUrlShort(String urlShort) {
        this.urlShort = urlShort;
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

    public void increaseTtlByHours(int hours) {
        if (hours <= 0) {
            throw new IllegalArgumentException("Количество часов должно быть больше 0");
        }

        this.expiresDate = this.expiresDate.plusHours(hours);
    }

    public void increaseTtlByDays(int days) {
        if (days <= 0) {
            throw new IllegalArgumentException("Количество дней должно быть больше 0");
        }

        this.expiresDate = this.expiresDate.plusDays(days);
    }

    public long generateId() {
        return Math.abs(UUID.randomUUID().getMostSignificantBits());
    }
}
