package ru.service.slice.link.service;

import ru.service.slice.link.entity.UrlEntity;
import ru.service.slice.link.repository.UrlRepository;

public class UrlService {
    private final UrlRepository urlRepository;

    public UrlService(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    public String convertToShortUrl(String url) {
        UrlEntity newEntity = new UrlEntity();

        Boolean isCreate = this.urlRepository.setEntity(newEntity);

        while (isCreate) {
            long newId = newEntity.generateId();

            newEntity.setId(newId);

            isCreate = this.urlRepository.setEntity(newEntity);
        }


        return this.urlRepository.getEntity(newEntity.getId()).toString();
    }
}
