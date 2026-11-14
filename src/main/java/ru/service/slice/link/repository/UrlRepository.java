package ru.service.slice.link.repository;

import ru.service.slice.link.entity.UrlEntity;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class UrlRepository {
    private final Map<Long, UrlEntity> urls = new LinkedHashMap<Long, UrlEntity>();

    public Boolean setEntity(UrlEntity entity) {
        if(urls.containsKey(entity.getId())) {
            return false;
        }

        urls.put(entity.getId(), entity);
        return  true;
    }

    public UrlEntity getEntity(Long id) {
        UrlEntity entity = urls.get(id);

        if(entity == null) {
            throw new NoSuchElementException("URL не найден");
        }

        return entity;
    }

    public void deleteEntity(Long id) {
        urls.remove(id);
    }
}
