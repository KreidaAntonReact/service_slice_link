package ru.service.slice.link.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class UrlDto {
    public long id;
    public String urlLong;
    public String urlShort;
    public UUID userId;
    public LocalDateTime expiresDate;
    public int limit;
    public int count;
    public boolean blocked;
}
