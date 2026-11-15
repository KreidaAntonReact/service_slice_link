package ru.service.slice.link.app.command;

import ru.service.slice.link.entity.UserEntity;
import ru.service.slice.link.service.AuthService;
import ru.service.slice.link.service.UrlService;
import ru.service.slice.link.util.UrlValidator;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditCommand implements Command {
    private static final Pattern TTL_PATTERN = Pattern.compile("^(\\d+)([hd])$", Pattern.CASE_INSENSITIVE);

    private final AuthService authService;
    private final UrlService urlService;

    public EditCommand(AuthService authService, UrlService urlService) {
        this.authService = authService;
        this.urlService = urlService;
    }

    @Override
    public void execute(String... args) {
        if (args.length < 2) {
            throw new IllegalArgumentException(getMessageHowUseCommand());
        }

        UserEntity user = authService.requireCurrentUser();
        String shortCode = UrlValidator.extractShortCode(args[0]);
        String[] flags = Arrays.copyOfRange(args, 1, args.length);

        TtlChange ttlChange = parseTtlFlag(flags);
        Integer limit = parseLimitFlag(flags);

        if (ttlChange == null && limit == null) {
            throw new IllegalArgumentException(getMessageHowUseCommand());
        }

        if (ttlChange != null) {
            LocalDateTime expiresDate = urlService.extendTtl(
                    shortCode,
                    user.getId(),
                    ttlChange.hours,
                    ttlChange.days
            );
            System.out.printf("TTL ссылки увеличен. Новое время действия: %s%n", expiresDate);
        }

        if (limit != null) {
            int newLimit = urlService.updateLimit(shortCode, user.getId(), limit);
            System.out.printf("Лимит переходов обновлен: %d%n", newLimit);
        }
    }

    @Override
    public String getDescription() {
        return "изменить TTL или лимит ссылки - edit <https://"
                + UrlValidator.SHORT_URL_PREFIX
                + "code> [-t <1h|1d>] [-l <limit>]";
    }

    private TtlChange parseTtlFlag(String... flags) {
        for (int i = 0; i < flags.length; i++) {
            if (!"-t".equals(flags[i])) {
                continue;
            }

            if (i + 1 >= flags.length) {
                throw new IllegalArgumentException(getMessageHowUseCommand());
            }

            Matcher matcher = TTL_PATTERN.matcher(flags[i + 1]);

            if (!matcher.matches()) {
                throw new IllegalArgumentException("TTL нужно указывать в формате 1h или 1d");
            }

            int amount = Integer.parseInt(matcher.group(1));
            String unit = matcher.group(2).toLowerCase();
            TtlChange ttlChange = new TtlChange();

            if ("h".equals(unit)) {
                ttlChange.hours = amount;
            } else {
                ttlChange.days = amount;
            }

            return ttlChange;
        }

        return null;
    }

    private Integer parseLimitFlag(String... flags) {
        for (int i = 0; i < flags.length; i++) {
            if (!"-l".equals(flags[i])) {
                continue;
            }

            if (i + 1 >= flags.length) {
                throw new IllegalArgumentException(getMessageHowUseCommand());
            }

            return parseLimit(flags[i + 1]);
        }

        return null;
    }

    private int parseLimit(String value) {
        int limit;

        try {
            limit = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Лимит переходов должен быть числом");
        }

        if (limit <= 0) {
            throw new IllegalArgumentException("Лимит переходов должен быть больше 0");
        }

        return limit;
    }

    private String getMessageHowUseCommand() {
        return "Ошибка при вводе. Нужно использовать: edit <https://"
                + UrlValidator.SHORT_URL_PREFIX
                + "code> [-t <1h|1d>] [-l <limit>]";
    }

    private static class TtlChange {
        private Integer hours;
        private Integer days;
    }
}
