package ru.service.slice.link.service;

import ru.service.slice.link.entity.UrlEntity;
import ru.service.slice.link.repository.UrlRepository;

import java.time.LocalDateTime;
import java.util.UUID;

public class UrlService {
    private static final String LIMIT_EXHAUSTED_MESSAGE = "Лимит переходов исчерпан. Ссылка заблокирована";

    private final UrlRepository urlRepository;
    private final BaseConversion baseConversion;

    public UrlService(UrlRepository urlRepository, BaseConversion baseConversion) {
        this.urlRepository = urlRepository;
        this.baseConversion = baseConversion;
    }

    public String convertToShortUrl(String url, UUID userId, int limit) {
        UrlEntity newEntity = new UrlEntity();

        newEntity.setUrlLong(url);
        newEntity.setUserId(userId);
        newEntity.setLimit(limit);

        Boolean isCreate = this.urlRepository.setEntity(newEntity);

        while (!isCreate) {
            long newId = newEntity.generateId();

            newEntity.setId(newId);

            isCreate = this.urlRepository.setEntity(newEntity);
        }

        return this.baseConversion.encode(newEntity.getId());
    }

    public String getLongUrl(String shortUrl) {
        long id = baseConversion.decode(shortUrl);
        UrlEntity findUrlEntity = urlRepository.getEntity(id);

        if (LocalDateTime.now().isAfter(findUrlEntity.getExpiresDate())) {
            urlRepository.deleteEntity(id);
            throw new IllegalStateException("У ссылки истекло время дейстия и была удалена");
        }

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

    private boolean blockIfLimitExhausted(UrlEntity entity) {
        if (entity.getCount() < entity.getLimit()) {
            return false;
        }

        entity.setBlocked(true);
        return true;
    }
}
