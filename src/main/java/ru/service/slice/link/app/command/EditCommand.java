package ru.service.slice.link.app.command;

import ru.service.slice.link.entity.UserEntity;
import ru.service.slice.link.service.AuthService;
import ru.service.slice.link.service.UrlService;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditCommand implements Command {
    private static final String SHORT_URL_PREFIX = "clck.ru/";
    private static final Pattern TTL_PATTERN = Pattern.compile("^(\\d+)([hd])$", Pattern.CASE_INSENSITIVE);

    private final AuthService authService;
    private final UrlService urlService;

    public EditCommand(AuthService authService, UrlService urlService) {
        this.authService = authService;
        this.urlService = urlService;
    }

    @Override
    public void execute(String... args) {
        if (args.length < 3) {
            throw new IllegalArgumentException(getMessageHowUseCommand());
        }

        UserEntity user = authService.requireCurrentUser();
        String shortCode = extractShortCode(args[0]);

        handleTtlFlag(shortCode, user.getId(), args[1], args[2]);
    }

    @Override
    public String getDescription() {
        return "увеличить TTL ссылки - edit <url> -t <1h|1d>";
    }

    private void handleTtlFlag(String shortCode, UUID userId, String flag, String value) {
        if (!"-t".equals(flag)) {
            throw new IllegalArgumentException(getMessageHowUseCommand());
        }

        Integer hours = null;
        Integer days = null;
        Matcher matcher = TTL_PATTERN.matcher(value);

        if (!matcher.matches()) {
            throw new IllegalArgumentException("TTL нужно указывать в формате 1h или 1d");
        }

        int amount = Integer.parseInt(matcher.group(1));
        String unit = matcher.group(2).toLowerCase();

        if ("h".equals(unit)) {
            hours = amount;
        } else {
            days = amount;
        }

        LocalDateTime expiresDate = urlService.extendTtl(shortCode, userId, hours, days);
        System.out.printf("TTL ссылки увеличен. Новое время действия: %s%n", expiresDate);
    }

    private String extractShortCode(String input) {
        String value = input.trim();

        if (value.startsWith("https://")) {
            value = value.substring("https://".length());
        } else if (value.startsWith("http://")) {
            value = value.substring("http://".length());
        }

        if (value.startsWith(SHORT_URL_PREFIX)) {
            value = value.substring(SHORT_URL_PREFIX.length());
        }

        return value;
    }

    private String getMessageHowUseCommand() {
        return "Ошибка при вводе. Нужно использовать: edit <url> -t <1h|1d>";
    }
}
