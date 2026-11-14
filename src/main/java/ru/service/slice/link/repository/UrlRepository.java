package ru.service.slice.link.repository;

import ru.service.slice.link.entity.UrlEntity;

import java.util.LinkedHashMap;
import java.util.Map;

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
        return urls.get(id);
    }
}
