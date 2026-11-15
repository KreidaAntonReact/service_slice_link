# Service Slice Link

Консольный сервис коротких ссылок на Java. Принимает длинный URL и возвращает короткую ссылку вида `clck.ru/...`, идентифицирует пользователей по UUID (без пароля), поддерживает лимит переходов, время жизни (TTL), удаление и редактирование своих ссылок.

Данные пользователей и ссылок сохраняются в файл `data.json` в корне проекта: загружаются при старте и записываются при регистрации, создании/изменении/удалении ссылок, переходах и при выходе. Настройки TTL и лимита по умолчанию задаются в `src/main/resources/application.properties`.

## Запуск

**Требования:** Java 17+ и Gradle Wrapper (уже в репозитории).

Сборка:

```bash
./gradlew build
```

Запуск:

```bash
./gradlew classes
java -cp "build/classes/java/main:build/resources/main" ru.service.slice.link.Main
```

В IntelliJ IDEA: открыть проект → запустить класс `ru.service.slice.link.Main`.

После старта появится приглашение `>` — команды вводятся в консоль.

## Команды

| Команда | Описание |
|---|---|
| `shorten <url> [limit]` | Создать короткую ссылку. URL должен начинаться с `http://` или `https://`. Лимит опционален; итоговое значение не меньше `link.default.limit` из конфига |
| `open <https://clck.ru/code>` | Открыть исходный URL в браузере. Нужна полная короткая ссылка с протоколом |
| `edit <https://clck.ru/code> [-t <1h\|1d>] [-l <limit>]` | Изменить TTL и/или лимит своей ссылки. Нужна полная короткая ссылка с протоколом. Можно указать один флаг или оба |
| `delete <https://clck.ru/code>` | Удалить свою короткую ссылку. Нужна полная короткая ссылка с протоколом |
| `user` | Показать UUID текущего пользователя |
| `user new` | Создать нового пользователя |
| `login <uuid>` | Войти по сохранённому UUID |
| `logout` | Завершить текущую сессию |
| `help` | Список команд |
| `exit` | Выйти из программы |

### Примеры

```text
> shorten https://www.baeldung.com/java-9-http-client
Создан пользователь. Ваш UUID:  ba18e48c-075b-4fae-a174-0c631a8717ef
Короткая ссылка: https://clck.ru/3W0a8Bo

> shorten https://www.baeldung.com/java-9-http-client 5
Короткая ссылка: https://clck.ru/AbCdEfG

> open https://clck.ru/3W0a8Bo
Переход по ссылке: https://www.baeldung.com/java-9-http-client

> edit https://clck.ru/3W0a8Bo -t 1h
TTL ссылки увеличен. Новое время действия: 2026-11-16T19:00:00

> edit https://clck.ru/3W0a8Bo -l 20
Лимит переходов обновлен: 20

> edit https://clck.ru/3W0a8Bo -t 2d -l 15
TTL ссылки увеличен. Новое время действия: 2026-11-18T19:00:00
Лимит переходов обновлен: 15

> user
Текущий UUID: ba18e48c-075b-4fae-a174-0c631a8717ef

> logout
Пользователь ba18e48c-075b-4fae-a174-0c631a8717ef вышел из сессии

> login ba18e48c-075b-4fae-a174-0c631a8717ef
Вы вошли как ba18e48c-075b-4fae-a174-0c631a8717ef

> delete https://clck.ru/3W0a8Bo
Ссылка успешно удалена

> help
> exit
```

## Настройки

Файл `src/main/resources/application.properties`:

| Параметр | По умолчанию | Смысл |
|---|---|---|
| `link.ttl.hours` | `24` | Время жизни новой ссылки в часах |
| `link.default.limit` | `10` | Минимальный / дефолтный лимит переходов |
