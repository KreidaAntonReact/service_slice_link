package ru.service.slice.link.service;

import ru.service.slice.link.entity.UrlEntity;
import ru.service.slice.link.repository.UrlRepository;

import java.util.NoSuchElementException;

public class UrlService {
    private final UrlRepository urlRepository;
    private final BaseConversion baseConversion;

    public UrlService(UrlRepository urlRepository, BaseConversion baseConversion) {
        this.urlRepository = urlRepository;
        this.baseConversion = baseConversion;
    }

    public String convertToShortUrl(String url) {
        UrlEntity newEntity = new UrlEntity();

        newEntity.setUrlLong(url);

        Boolean isCreate = this.urlRepository.setEntity(newEntity);

        while (isCreate) {
            long newId = newEntity.generateId();

            newEntity.setId(newId);

            isCreate = this.urlRepository.setEntity(newEntity);
        }


        return this.baseConversion.encode(newEntity.getId());
    }

    public String getLongUrl(String shortUrl) {
        long id = baseConversion.decode(shortUrl);

        try {
            UrlEntity findUrlEntity = urlRepository.getEntity(id);

            return findUrlEntity.getUrlLong();
        } catch (NoSuchElementException error) {
            this.logError(error);
        }
    }


    private void logError(Exception error) {
        System.err.println(error.getMessage());
    }
}
