package ru.service.slice.link.app;

import ru.service.slice.link.app.command.Command;
import ru.service.slice.link.app.command.DeleteCommand;
import ru.service.slice.link.app.command.EditCommand;
import ru.service.slice.link.app.command.ExitCommand;
import ru.service.slice.link.app.command.HelpCommand;
import ru.service.slice.link.app.command.LoginCommand;
import ru.service.slice.link.app.command.LogoutCommand;
import ru.service.slice.link.app.command.OpenCommand;
import ru.service.slice.link.app.command.ShortenCommand;
import ru.service.slice.link.app.command.UserCommand;
import ru.service.slice.link.config.AppConfig;
import ru.service.slice.link.repository.UrlRepository;
import ru.service.slice.link.repository.UserRepository;
import ru.service.slice.link.service.AuthService;
import ru.service.slice.link.service.BaseConversion;
import ru.service.slice.link.service.PersistService;
import ru.service.slice.link.service.UrlService;
import ru.service.slice.link.service.UserService;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Stream;

public class App {
    private static final Set<String> PERSIST_COMMANDS = Set.of(
            "shorten",
            "open",
            "delete",
            "edit",
            "user"
    );

    private final Scanner scanner = new Scanner(System.in);
    private final LinkedHashMap<String, Command> commands = new LinkedHashMap<>();
    private final PersistService persistService;
    private boolean isRun = true;

    public App() {
        AppConfig appConfig = new AppConfig();
        UrlRepository urlRepository = new UrlRepository();
        UserRepository userRepository = new UserRepository();
        this.persistService = new PersistService(userRepository, urlRepository);

        BaseConversion baseConversion = new BaseConversion();
        UrlService urlService = new UrlService(urlRepository, baseConversion, appConfig);
        UserService userService = new UserService(userRepository);
        AuthService authService = new AuthService(userService);

        commands.put("shorten", new ShortenCommand(authService, urlService));
        commands.put("open", new OpenCommand(urlService));
        commands.put("delete", new DeleteCommand(authService, urlService));
        commands.put("edit", new EditCommand(authService, urlService));
        commands.put("user", new UserCommand(authService));
        commands.put("login", new LoginCommand(authService));
        commands.put("logout", new LogoutCommand(authService));
        commands.put("exit", new ExitCommand(this::stop));
        commands.put("help", new HelpCommand(commands));
    }

    public void stop() {
        try {
            persistService.save();
        } catch (Exception e) {
            logError(e);
        }

        this.isRun = false;
    }

    public void run() {
        this.persistService.load();

        System.out.println("Привет! Для того чтобы начать использовать приложение \n"
                + "ознакомся с перечнем команд.");
        commands.get("help").execute();

        while (isRun) {
            System.out.print("> ");
            System.out.flush();

            String input = scanner.nextLine();

            if (input.isEmpty()) {
                continue;
            }

            String commandText = getCommandFromInput(input);
            Command command = commands.get(commandText);

            if (command == null) {
                System.out.println("Вы ввели неизвестную команду");
                continue;
            }

            String[] args = getArgFromInput(input);

            try {
                command.execute(args);
            } catch (Exception e) {
                logError(e);
            } finally {
                if (PERSIST_COMMANDS.contains(commandText)) {
                    try {
                        persistService.save();
                    } catch (Exception persistError) {
                        logError(persistError);
                    }
                }
            }
        }
    }

    private String getCommandFromInput(String input) {
        return input.trim().toLowerCase().split("\\s+")[0];
    }

    private String[] getArgFromInput(String input) {
        String[] args = input.trim().split("\\s+");

        if (args.length <= 1) {
            return new String[0];
        }

        Stream<String> stream = Arrays.stream(args);

        return stream.skip(1).toArray(String[]::new);
    }

    private void logError(Exception error) {
        System.err.println(error.getMessage());
        System.err.flush();
    }
}
