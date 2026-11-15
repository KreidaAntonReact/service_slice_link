package ru.service.slice.link.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserDto {
    public UUID id;
    public List<UrlDto> urls = new ArrayList<>();
}
