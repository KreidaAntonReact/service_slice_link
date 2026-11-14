package ru.service.slice.link.service;

import ru.service.slice.link.entity.UrlEntity;
import ru.service.slice.link.repository.UrlRepository;

import java.time.LocalDateTime;
import java.util.UUID;

public class UrlService {
    private final UrlRepository urlRepository;
    private final BaseConversion baseConversion;

    public UrlService(UrlRepository urlRepository, BaseConversion baseConversion) {
        this.urlRepository = urlRepository;
        this.baseConversion = baseConversion;
    }

    public String convertToShortUrl(String url, UUID userId) {
        UrlEntity newEntity = new UrlEntity();

        newEntity.setUrlLong(url);
        newEntity.setUserId(userId);

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

        return findUrlEntity.getUrlLong();
    }
}
