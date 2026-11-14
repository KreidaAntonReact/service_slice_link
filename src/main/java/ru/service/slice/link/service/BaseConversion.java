package ru.service.slice.link.service;

public class BaseConversion {

    private static final String allowedString = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private final char[] allowedCharacters = allowedString.toCharArray();
    private final int base = allowedCharacters.length;

    public String encode(long input) {
        StringBuilder encodedString = new StringBuilder();

        if (input == 0) {
            return String.valueOf(allowedCharacters[0]);
        }

        while (input > 0) {
            encodedString.append(allowedCharacters[(int) (input % base)]);
            input = input / base;
        }

        return encodedString.reverse().toString();
    }

    public long decode(String input) {
        long decoded = 0;

        for (char character : input.toCharArray()) {
            int index = allowedString.indexOf(character);

            if (index < 0) {
                throw new IllegalArgumentException("Некорректная короткая ссылка");
            }

            decoded = decoded * base + index;
        }

        return decoded;
    }
}
