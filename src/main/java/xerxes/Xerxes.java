package xerxes;

import java.io.IOException;

import xerxes.parser.CommandResult;
import xerxes.parser.Parser;
import xerxes.storage.TaskStorage;
import xerxes.task.TaskList;
import xerxes.ui.Ui;

/**
 * Coordinates the user interface, command parser, task list, and persistent storage
 * for the Xerxes task manager.
 */
public class Xerxes {
    /** Default location used to persist tasks when the application starts normally. */
    private static final String SAVE_FILE_PATH = "data/Xerxes.txt";

    /** Stores and retrieves the application's tasks. */
    private final TaskStorage taskStorage;

    /** Contains the tasks managed during this application session. */
    private final TaskList tasks;

    /** Handles console input and output. */
    private final Ui ui;

    /** Interprets commands entered by the user. */
    private final Parser parser;

    /**
     * Creates an application instance and loads tasks from the specified file.
     * If the file cannot be read or contains malformed task data, the application
     * starts with an empty task list instead.
     */
    public Xerxes() {
        this.ui = new Ui();
        this.taskStorage = new TaskStorage(SAVE_FILE_PATH);
        TaskList loadedTasks;

        try {
            loadedTasks = new TaskList(taskStorage.load());
            ui.showTopMessage("Yo your save file has been loaded");
        } catch (IOException e) {
            ui.showTopError("Oops yr save file didnt load: " + e.getMessage());
            loadedTasks = new TaskList();
        } catch (IllegalArgumentException e) {
            ui.showTopError("Wah, your save file has been corrupted: " + e.getMessage());
            loadedTasks = new TaskList();
        }
        this.tasks = loadedTasks;
        this.parser = new Parser(taskStorage);
    }

    /**
     * Displays the welcome message and repeatedly processes commands until the user exits.
     */
    public void run() {
        ui.showInitialMessage();
        boolean isActive = true;
        while (isActive) {
            String input = ui.readCommand();
            CommandResult result = executeCommand(input);
            if (result.isError()) {
                ui.showError(result.getMessage());
            } else {
                ui.showMessage(result.getMessage());
            }
            isActive = !result.shouldExit();
        }
        ui.close();
    }

    /**
     * Processes one command using the application's task list and storage.
     *
     * @param input Command entered by the user.
     * @return Result containing the command response and status.
     */
    public CommandResult executeCommand(String input) {
        return parser.handleCommand(input, tasks);
    }

    /**
     * Starts the Xerxes application using the default save-file location.
     *
     * @param args Command-line arguments, which are not currently used.
     */
    public static void main(String[] args) {
        new Xerxes().run();
    }
}
