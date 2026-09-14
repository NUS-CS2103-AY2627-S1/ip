package toot;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Scanner;

import toot.parser.CommandType;
import toot.parser.ParsedCommand;
import toot.parser.Parser;
import toot.storage.Storage;
import toot.task.Task;
import toot.task.TaskList;
import toot.ui.Ui;

/**
 * Coordinates Toot's user interface, command parsing, task list, and storage.
 */
public class Toot {
    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;
    private final String loadingError;

    /**
     * Creates a Toot instance using the configured default task data file.
     */
    public Toot() {
        this(getDataFilePath());
    }

    /**
     * Creates a Toot instance backed by the given task data file.
     *
     * @param filePath Path of the task data file.
     */
    public Toot(Path filePath) {
        this.ui = new Ui();
        this.storage = new Storage(filePath);

        TaskList loadedTasks;
        String error = null;
        try {
            loadedTasks = new TaskList(storage.load());
        } catch (TootException exception) {
            loadedTasks = new TaskList();
            error = exception.getMessage() + " Toot will start with an empty task list.";
        }
        this.tasks = loadedTasks;
        this.loadingError = error;
    }

    /**
     * Starts the command loop and processes input until the user exits or input ends.
     */
    public void run() {
        ui.showWelcome();
        if (loadingError != null) {
            ui.showError(loadingError);
        }
        ui.showLineWithBlankLine();

        while (ui.hasNextCommand()) {
            String commandText = ui.readCommand();
            ui.showLine();

            boolean shouldExit = executeCommand(commandText, ui);
            if (shouldExit) {
                ui.showLine();
                return;
            }

            ui.showLineWithBlankLine();
        }
    }

    /**
     * Returns Toot's startup message for a graphical interface.
     *
     * @return Greeting, or a task-loading error when saved data could not be loaded.
     */
    public String getStartupMessage() {
        if (loadingError != null) {
            return "Oh crumbs! " + loadingError;
        }
        return "Hewwo!! I'm Toot, ur teeny-tiny computey baby! ૮₍ ˶•⤙•˶ ₎ა\n"
                + "Gib me a command... Toot do a BIG twy!! (•̀ᴗ•́)و";
    }

    /**
     * Processes one command and returns its user-facing response.
     *
     * @param commandText Raw command entered by the user.
     * @return Response text without the console-only divider lines.
     */
    public String getResponse(String commandText) {
        ByteArrayOutputStream responseBytes = new ByteArrayOutputStream();
        try (Scanner unusedInput = new Scanner("");
                PrintStream responseOutput = new PrintStream(responseBytes, true, StandardCharsets.UTF_8)) {
            Ui responseUi = new Ui(unusedInput, responseOutput);
            executeCommand(commandText, responseUi);
        }
        return responseBytes.toString(StandardCharsets.UTF_8)
                .replace("\r\n", "\n")
                .stripTrailing();
    }

    /**
     * Parses and executes one command through the supplied UI.
     *
     * @param commandText Raw command entered by the user.
     * @param activeUi UI that should receive the response.
     * @return {@code true} when the command requests application exit.
     */
    private boolean executeCommand(String commandText, Ui activeUi) {
        try {
            ParsedCommand command = Parser.parse(commandText);
            if (command.type() == CommandType.BYE) {
                activeUi.showGoodbye();
                return true;
            }
            execute(command, activeUi);
        } catch (TootException exception) {
            activeUi.showError(exception.getMessage());
        }
        return false;
    }

    /**
     * Executes one parsed command and persists any resulting task-list change.
     *
     * @param command Parsed user command.
     * @param activeUi UI that should receive the response.
     * @throws TootException If the command arguments or storage operation are invalid.
     */
    private void execute(ParsedCommand command, Ui activeUi) throws TootException {
        switch (command.type()) {
            case HELP:
                activeUi.showHelp();
                break;
            case LIST:
                activeUi.showTasks(tasks.asList());
                break;
            case FIND:
                String keyword = Parser.parseFindKeyword(command);
                activeUi.showMatchingTasks(tasks.find(keyword));
                break;
            case MARK:
            case UNMARK:
            case DELETE:
                executeIndexedCommand(command, activeUi);
                break;
            case TODO:
            case DEADLINE:
            case EVENT:
                addTask(command, activeUi);
                break;
            case BYE:
                throw new AssertionError("The exit command must be handled before execution.");
            default:
                throw new AssertionError("Unhandled command type: " + command.type());
        }
    }

    /**
     * Executes a command that targets an existing task after validating its index.
     */
    private void executeIndexedCommand(ParsedCommand command, Ui activeUi) throws TootException {
        int index = Parser.parseTaskIndex(command, tasks.size());
        ensureStorageAvailable();
        switch (command.type()) {
            case MARK:
                activeUi.showMarkedTask(tasks.mark(index));
                break;
            case UNMARK:
                activeUi.showUnmarkedTask(tasks.unmark(index));
                break;
            case DELETE:
                Task deletedTask = tasks.delete(index);
                activeUi.showDeletedTask(deletedTask, tasks.size());
                break;
            default:
                throw new AssertionError("Not an indexed command: " + command.type());
        }
        storage.save(tasks.asList());
    }

    /**
     * Creates, displays, and saves a new task described by an add command.
     */
    private void addTask(ParsedCommand command, Ui activeUi) throws TootException {
        Task addedTask = Parser.parseTask(command);
        ensureStorageAvailable();
        tasks.add(addedTask);
        activeUi.showAddedTask(addedTask, tasks.size());
        storage.save(tasks.asList());
    }

    /**
     * Prevents a malformed or unreadable data file from being overwritten by a new task list.
     */
    private void ensureStorageAvailable() throws TootException {
        if (loadingError != null) {
            throw new TootException("Saved tasks could not be loaded. Fix or back up the data file and restart "
                    + "Toot before changing tasks.");
        }
    }

    /**
     * Returns the task data path. Tests can override it without touching real user data.
     *
     * @return Configured data file path, or {@code ./data/toot.txt} by default.
     */
    private static Path getDataFilePath() {
        String customPath = System.getProperty("toot.data.path");
        return customPath == null ? Path.of("data", "toot.txt") : Path.of(customPath);
    }

    /**
     * Launches Toot using its default data-file location.
     *
     * @param args Command-line arguments; currently unused.
     */
    public static void main(String[] args) {
        new Toot().run();
    }
}
