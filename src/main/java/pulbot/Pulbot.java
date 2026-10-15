package pulbot;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

import pulbot.command.Command;
import pulbot.command.Parser;
import pulbot.storage.Storage;
import pulbot.task.TaskList;
import pulbot.ui.Ui;

/**
 * Starts Pulbot and stores tasks entered by the user.
 */
public class Pulbot {
    private static final String ANSI_ESCAPE_SEQUENCE = "\\u001B\\[[;\\d]*m";
    private static final String DEFAULT_FILE_PATH = "data/pulbot.txt";
    private static final DateTimeFormatter INPUT_DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("d/M/uuuu HHmm").withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter DISPLAY_DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd uuuu h:mm a", Locale.ENGLISH);

    private final Parser parser;
    private final Storage storage;
    private final TaskList tasks;
    private final String startupWarning;
    private boolean isExitRequested;

    /** Creates Pulbot and loads its saved tasks. */
    public Pulbot() {
        this(new Storage(DEFAULT_FILE_PATH));
    }

    /** Creates Pulbot with the supplied storage, primarily to support isolated tests. */
    Pulbot(Storage storage) {
        parser = new Parser();
        this.storage = storage;
        TaskList loadedTasks;
        String loadingWarning = "";
        try {
            loadedTasks = storage.load();
        } catch (PulbotException e) {
            loadedTasks = new TaskList();
            loadingWarning = "Unable to load saved tasks. Starting with an empty list. "
                    + e.getMessage();
        }
        tasks = loadedTasks;
        startupWarning = loadingWarning;
    }

    /** Returns a startup warning, or an empty string when saved tasks loaded successfully. */
    String getStartupWarning() {
        return startupWarning;
    }

    /** Runs Pulbot until the user enters the {@code bye} command. */
    public static void main(String[] args) {
        Ui ui = new Ui();
        Parser parser = new Parser();
        Storage storage = new Storage(DEFAULT_FILE_PATH);
        ui.showWelcome();
        TaskList tasks = loadTasks(storage, ui);
        processCommands(parser, tasks, ui, storage);
        ui.close();
    }

    /** Loads saved tasks and reports recoverable storage errors to the console. */
    private static TaskList loadTasks(Storage storage, Ui ui) {
        try {
            return storage.load();
        } catch (PulbotException e) {
            ui.showError(e.getMessage());
            return new TaskList();
        }
    }

    /** Reads and executes commands until input ends or the user exits. */
    private static void processCommands(Parser parser, TaskList tasks, Ui ui, Storage storage) {
        boolean isExit = false;
        while (ui.hasNextLine() && !isExit) {
            String input = ui.nextLine();
            ui.showSeparator();
            isExit = executeCommand(input, parser, tasks, ui, storage);
            ui.showSeparator();
            ui.showBlankLine();
        }
    }

    /** Executes one console command and returns whether it requests application exit. */
    private static boolean executeCommand(String input, Parser parser, TaskList tasks,
            Ui ui, Storage storage) {
        try {
            Command command = parser.parse(input);
            command.execute(tasks, ui, storage);
            return command.isExit();
        } catch (PulbotException | IllegalArgumentException e) {
            ui.showError(e.getMessage());
            return false;
        }
    }

    /** Generates a response for the user's chat message. */
    public String getResponse(String input) {
        isExitRequested = false;
        ByteArrayOutputStream responseBuffer = new ByteArrayOutputStream();
        try (PrintStream output = new PrintStream(responseBuffer, true, StandardCharsets.UTF_8)) {
            Ui ui = new Ui(InputStream.nullInputStream(), output);
            try {
                Command command = parser.parse(input);
                command.execute(tasks, ui, storage);
                isExitRequested = command.isExit();
            } catch (PulbotException | IllegalArgumentException e) {
                ui.showError(e.getMessage());
            } finally {
                ui.close();
            }
        }
        return responseBuffer.toString(StandardCharsets.UTF_8)
                .replaceAll(ANSI_ESCAPE_SEQUENCE, "")
                .strip();
    }

    /** Returns whether the last successfully executed chat command requested exit. */
    public boolean isExitRequested() {
        return isExitRequested;
    }

    /** Parses a date and time entered using Pulbot's command format. */
    public static LocalDateTime parseDateTime(String value) throws IllegalArgumentException {
        try {
            return LocalDateTime.parse(value.trim(), INPUT_DATE_TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Use d/M/yyyy HHmm, for example 2/12/2019 1800.");
        }
    }

    /** Formats a date and time for display to the user. */
    public static String formatDateTime(LocalDateTime value) {
        return value.format(DISPLAY_DATE_TIME_FORMATTER);
    }
}
