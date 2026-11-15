package ru.service.slice.link.service;

import ru.service.slice.link.config.AppConfig;
import ru.service.slice.link.entity.UrlEntity;
import ru.service.slice.link.repository.UrlRepository;
import ru.service.slice.link.util.UrlValidator;

import java.time.LocalDateTime;
import java.util.UUID;

public class UrlService {
    private static final String LIMIT_EXHAUSTED_MESSAGE = "Лимит переходов исчерпан. Ссылка заблокирована";
    private static final String EXPIRED_MESSAGE = "У ссылки истекло время действия и была удалена";

    private final UrlRepository urlRepository;
    private final BaseConversion baseConversion;
    private final AppConfig appConfig;

    public UrlService(UrlRepository urlRepository, BaseConversion baseConversion, AppConfig appConfig) {
        this.urlRepository = urlRepository;
        this.baseConversion = baseConversion;
        this.appConfig = appConfig;
    }

    public String convertToShortUrl(String url, UUID userId, Integer requestedLimit) {
        int limit = resolveLimit(requestedLimit);
        UrlEntity newEntity = new UrlEntity(appConfig.getTtlHours());

        newEntity.setUrlLong(url);
        newEntity.setUserId(userId);
        newEntity.setLimit(limit);

        Boolean isCreate = this.urlRepository.setEntity(newEntity);

        while (!isCreate) {
            long newId = newEntity.generateId();

            newEntity.setId(newId);

            isCreate = this.urlRepository.setEntity(newEntity);
        }

        String shortCode = this.baseConversion.encode(newEntity.getId());
        newEntity.setUrlShort(extractProtocol(url) + UrlValidator.SHORT_URL_PREFIX + shortCode);

        return shortCode;
    }

    public String getLongUrl(String shortUrl) {
        long id = baseConversion.decode(shortUrl);
        UrlEntity findUrlEntity = urlRepository.getEntity(id);

        deleteIfExpired(id, findUrlEntity);

        if (findUrlEntity.isBlocked()) {
            throw new IllegalStateException("Ссылка заблокирована");
        }

        if (blockIfLimitExhausted(findUrlEntity)) {
            throw new IllegalStateException(LIMIT_EXHAUSTED_MESSAGE);
        }

        findUrlEntity.setCount(findUrlEntity.getCount() + 1);

        if (blockIfLimitExhausted(findUrlEntity)) {
            System.out.println(LIMIT_EXHAUSTED_MESSAGE);
        }

        return findUrlEntity.getUrlLong();
    }

    public void deleteUrl(String shortUrl, UUID userId) {
        long id = baseConversion.decode(shortUrl);
        UrlEntity findUrlEntity = urlRepository.getEntity(id);

        if (!findUrlEntity.getUserId().equals(userId)) {
            throw new IllegalStateException("Вы можете удалять только свои ссылки");
        }

        urlRepository.deleteEntity(id);
    }

    public LocalDateTime extendTtl(String shortUrl, UUID userId, Integer hours, Integer days) {
        if (hours != null && days != null) {
            throw new IllegalArgumentException("Можно указать только часы или только дни");
        }

        if (hours == null && days == null) {
            throw new IllegalArgumentException("Нужно указать TTL в часах или днях");
        }

        long id = baseConversion.decode(shortUrl);
        UrlEntity findUrlEntity = requireOwnedUrl(id, userId);
        deleteIfExpired(id, findUrlEntity);

        if (hours != null) {
            findUrlEntity.increaseTtlByHours(hours);
        } else {
            findUrlEntity.increaseTtlByDays(days);
        }

        return findUrlEntity.getExpiresDate();
    }

    public int updateLimit(String shortUrl, UUID userId, int limit) {
        long id = baseConversion.decode(shortUrl);
        UrlEntity findUrlEntity = requireOwnedUrl(id, userId);
        deleteIfExpired(id, findUrlEntity);

        findUrlEntity.setLimit(limit);

        if (findUrlEntity.getCount() < limit) {
            findUrlEntity.setBlocked(false);
        } else {
            findUrlEntity.setBlocked(true);
        }

        return findUrlEntity.getLimit();
    }

    private UrlEntity requireOwnedUrl(long id, UUID userId) {
        UrlEntity findUrlEntity = urlRepository.getEntity(id);

        if (!findUrlEntity.getUserId().equals(userId)) {
            throw new IllegalStateException("Вы можете редактировать только свои ссылки");
        }

        return findUrlEntity;
    }

    private void deleteIfExpired(long id, UrlEntity entity) {
        if (LocalDateTime.now().isAfter(entity.getExpiresDate())) {
            urlRepository.deleteEntity(id);
            throw new IllegalStateException(EXPIRED_MESSAGE);
        }
    }

    private int resolveLimit(Integer requestedLimit) {
        int defaultLimit = appConfig.getDefaultLimit();

        if (requestedLimit == null) {
            return defaultLimit;
        }

        return Math.max(requestedLimit, defaultLimit);
    }

    private String extractProtocol(String url) {
        if (url.startsWith("https://")) {
            return "https://";
        }

        if (url.startsWith("http://")) {
            return "http://";
        }

        return "";
    }

    private boolean blockIfLimitExhausted(UrlEntity entity) {
        if (entity.getCount() < entity.getLimit()) {
            return false;
        }

        entity.setBlocked(true);
        return true;
    }
}
