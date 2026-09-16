package bobby;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

/** Coordinates command parsing, task management, persistence, and responses for Bobby. */
public class Bobby {
    /** Storage used by the GUI-backed chatbot instance. */
    private final Storage storage;

    /** Parser used by the GUI-backed chatbot instance. */
    private final Parser parser;

    /** Tasks used by the GUI-backed chatbot instance. */
    private TaskList tasks;

    /** Storage error to include in the GUI welcome message, if loading failed. */
    private final String storageLoadError;

    /** Simple class name of the most recently executed command. */
    private String commandType;

    /** Whether the most recent GUI response reports a command error. */
    private boolean wasLastResponseError = false;

    /** Creates a Bobby application instance. */
    public Bobby() {
        this("data/bobby.txt");
    }

    /** Creates a Bobby chatbot instance backed by the given task file. */
    Bobby(String filePath) {
        storage = new Storage(filePath);
        parser = new Parser();
        String loadError = null;
        try {
            tasks = storage.load();
        } catch (BobbyException exception) {
            tasks = new TaskList();
            loadError = exception.getMessage();
        }
        storageLoadError = loadError;
    }

    /**
     * Processes one input using Bobby's existing parser, commands, task list, and storage.
     *
     * @param input user input to process
     * @return Bobby's response text
     */
    public String getResponse(String input) {
        ByteArrayOutputStream responseOutput = new ByteArrayOutputStream();
        PrintStream responseStream = new PrintStream(responseOutput, true, StandardCharsets.UTF_8);
        Ui responseUi = new Ui(responseStream);
        commandType = null;
        wasLastResponseError = false;
        try {
            Command command = parser.parse(input);
            assert command != null : "Parser must return a command for valid input";
            command.execute(tasks, responseUi, storage);
            commandType = command.getClass().getSimpleName();
        } catch (BobbyException exception) {
            wasLastResponseError = true;
            String errorWithUsage = exception.getMessage() + System.lineSeparator()
                    + System.lineSeparator() + getUsageGuidance(input);
            responseUi.showError(errorWithUsage);
        }
        responseStream.close();
        return responseOutput.toString(StandardCharsets.UTF_8)
                .replace("____________________________________________________________" + System.lineSeparator(), "");
    }

    /**
     * Returns the type of the most recently executed command.
     *
     * @return the command's simple class name, or {@code null} if execution failed
     */
    public String getCommandType() {
        return commandType;
    }

    /**
     * Returns whether the most recent GUI response reports a command error.
     *
     * @return {@code true} if the most recent command failed
     */
    public boolean wasLastResponseError() {
        return wasLastResponseError;
    }

    /**
     * Returns the GUI greeting, including a storage warning when saved tasks could not be loaded.
     *
     * @return startup text to display before the first command
     */
    public String getWelcomeMessage() {
        if (storageLoadError == null) {
            return Ui.WELCOME_MESSAGE;
        }
        return Ui.WELCOME_MESSAGE + System.lineSeparator() + System.lineSeparator()
                + storageLoadError;
    }

    /**
     * Returns concise command guidance for an invalid GUI input.
     *
     * @param input invalid command entered by the user
     * @return relevant format and example, or the list of available commands
     */
    private static String getUsageGuidance(String input) {
        if (input == null) {
            return getAvailableCommandsGuidance();
        }
        String trimmedInput = input.trim();
        if (trimmedInput.isEmpty()) {
            return getAvailableCommandsGuidance();
        }

        String commandName = trimmedInput.split("\\s+", 2)[0];
        switch (commandName) {
            case "todo":
                return String.join(System.lineSeparator(),
                        "Format: todo DESCRIPTION",
                        "Example: todo read book");
            case "deadline":
                return String.join(System.lineSeparator(),
                        "Format: deadline DESCRIPTION /by DATE",
                        "Example: deadline return book /by 2026-09-20",
                        "With time: deadline call client /by 2026-09-20 1800");
            case "event":
                return String.join(System.lineSeparator(),
                        "Format: event DESCRIPTION /from DATE /to DATE",
                        "Example: event meeting /from 2026-09-20 /to 2026-09-21",
                        "Dates: yyyy-MM-dd");
            case "mark":
                return String.join(System.lineSeparator(),
                        "Format: mark TASK_NUMBER",
                        "Example: mark 1");
            case "unmark":
                return String.join(System.lineSeparator(),
                        "Format: unmark TASK_NUMBER",
                        "Example: unmark 1");
            case "delete":
                return String.join(System.lineSeparator(),
                        "Format: delete TASK_NUMBER",
                        "Example: delete 1");
            case "find":
                return String.join(System.lineSeparator(),
                        "Format: find KEYWORD",
                        "Example: find book");
            case "priority":
                return String.join(System.lineSeparator(),
                        "Format: priority TASK_NUMBER LEVEL",
                        "Example: priority 1 high",
                        "Levels: high, medium, low, none, 1, 2, or 3");
            case "list":
                return "Format: list";
            case "bye":
                return "Format: bye";
            default:
                return getAvailableCommandsGuidance();
        }
    }

    /** Returns a compact list of commands for empty or unknown input. */
    private static String getAvailableCommandsGuidance() {
        return "Available commands: todo, deadline, event, list, mark, unmark,"
                + System.lineSeparator() + "delete, find, priority, bye";
    }

    /**
     * Prints Bobby's welcome message, stores tasks, changes task completion states, deletes tasks,
     * lists tasks, and exits on {@code bye}.
     *
     * @param args command-line arguments, which are not used
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        Storage storage = new Storage("data/bobby.txt");
        Parser parser = new Parser();
        ui.showWelcome();

        TaskList tasks;
        try {
            tasks = storage.load();
        } catch (BobbyException exception) {
            tasks = new TaskList();
            ui.showError(exception.getMessage());
        }
        while (true) {
            String command = ui.readCommand();
            if (command == null) {
                ui.showSeparator();
                ExitCommand exitCommand = new ExitCommand();
                exitCommand.execute(tasks, ui, storage);
                break;
            }

            ui.showSeparator();
            try {
                if (command.isEmpty()) {
                    throw new BobbyException("Error! The command cannot be empty!");
                } else {
                    Command parsedCommand = parser.parse(command);
                    parsedCommand.execute(tasks, ui, storage);
                    if (parsedCommand.isExit()) {
                        break;
                    }
                }
            } catch (BobbyException exception) {
                ui.showError(exception.getMessage());
            }
            ui.showSeparator();
        }
    }

}
