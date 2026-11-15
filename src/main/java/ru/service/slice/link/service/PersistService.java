package ru.service.slice.link.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import ru.service.slice.link.dto.UrlDto;
import ru.service.slice.link.dto.UserDto;
import ru.service.slice.link.entity.UrlEntity;
import ru.service.slice.link.entity.UserEntity;
import ru.service.slice.link.repository.UrlRepository;
import ru.service.slice.link.repository.UserRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class PersistService {
    private final Path pathToFile = Path.of(System.getProperty("user.dir"), "data.json").toAbsolutePath().normalize();
    private final ObjectMapper mapper;
    private final UserRepository userRepository;
    private final UrlRepository urlRepository;

    public PersistService(UserRepository userRepository, UrlRepository urlRepository) {
        this.userRepository = userRepository;
        this.urlRepository = urlRepository;
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
        this.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public void load() {
        if (!Files.exists(pathToFile)) {
            return;
        }

        try {
            UserDto[] usersDto = mapper.readValue(pathToFile.toFile(), UserDto[].class);
            List<UserEntity> users = new ArrayList<>();
            List<UrlEntity> urls = new ArrayList<>();

            for (UserDto userDto : usersDto) {
                users.add(toUser(userDto));

                if (userDto.urls == null) {
                    continue;
                }

                for (UrlDto urlDto : userDto.urls) {
                    urls.add(toUrl(urlDto));
                }
            }

            userRepository.replaceAll(users);
            urlRepository.replaceAll(urls);
        } catch (IOException e) {
            throw new IllegalStateException("Не удалось загрузить данные из файла: " + e.getMessage(), e);
        }
    }

    public void save() {
        try {
            List<UserEntity> users = userRepository.findAll();
            List<UrlEntity> urls = urlRepository.findAll();
            List<UserDto> usersDto = new ArrayList<>();

            for (UserEntity user : users) {
                usersDto.add(toUserDto(user, urls));
            }

            mapper.writeValue(pathToFile.toFile(), usersDto);
        } catch (IOException e) {
            throw new IllegalStateException("Не удалось сохранить данные в файл: " + e.getMessage(), e);
        }
    }

    private UserEntity toUser(UserDto dto) {
        UserEntity user = new UserEntity();
        user.setId(dto.id);
        return user;
    }

    private UrlEntity toUrl(UrlDto dto) {
        UrlEntity entity = new UrlEntity(1);
        entity.setId(dto.id);
        entity.setUrlLong(dto.urlLong);
        entity.setUrlShort(dto.urlShort);
        entity.setUserId(dto.userId);
        entity.setExpiresDate(dto.expiresDate);
        entity.setLimit(dto.limit);
        entity.setCount(dto.count);
        entity.setBlocked(dto.blocked);
        return entity;
    }

    private UserDto toUserDto(UserEntity user, List<UrlEntity> urls) {
        UserDto dto = new UserDto();
        dto.id = user.getId();

        for (UrlEntity url : urls) {
            if (user.getId().equals(url.getUserId())) {
                dto.urls.add(toUrlDto(url));
            }
        }

        return dto;
    }

    private UrlDto toUrlDto(UrlEntity entity) {
        UrlDto dto = new UrlDto();
        dto.id = entity.getId();
        dto.urlLong = entity.getUrlLong();
        dto.urlShort = entity.getUrlShort();
        dto.userId = entity.getUserId();
        dto.expiresDate = entity.getExpiresDate();
        dto.limit = entity.getLimit();
        dto.count = entity.getCount();
        dto.blocked = entity.isBlocked();
        return dto;
    }
}
