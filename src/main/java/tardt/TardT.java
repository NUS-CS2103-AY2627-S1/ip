package tardt;

import java.nio.file.Path;
import java.nio.file.Paths;

import tardt.exception.TardTException;
import tardt.parser.Parser;
import tardt.storage.Storage;
import tardt.task.TaskList;
import tardt.ui.Ui;


/** Provides the interactive command-line interface for the TardT task manager. */
public class TardT {
    private static final Path SAVE_FILE = Paths.get("data", "tasks.txt");

    private final Ui ui;
    private final Storage storage;
    private final TaskList tasks;
    /** Startup storage error to show through the active interface, or an empty string when loading succeeded. */
    private final String startupError;

    /**
     * Constructor for TardT object
     */
    public TardT() {
        ui = new Ui();
        storage = new Storage(SAVE_FILE);

        // Loading can fail before either interface is ready to show a message.
        // Retain that message so the CLI and GUI can present it through their own output paths.
        TaskList loadedTasks;
        String loadError = "";
        try {
            loadedTasks = new TaskList(storage.load());
        } catch (TardTException e) {
            loadError = e.getMessage();
            loadedTasks = new TaskList();
        }

        tasks = loadedTasks;
        startupError = loadError;
    }

    /**
     * Processes a user command and returns the response.
     * This is the method that the GUI calls.
     */
    public String getResponse(String input) {
        try {
            return Parser.parseForResponse(input, tasks, storage);
        } catch (TardTException e) {
            return e.getMessage();
        }
    }

    /**
     * Saves tasks to storage (useful for GUI shutdown).
     */
    public void save() {
        try {
            storage.save(tasks.getTasks());
        } catch (TardTException e) {
            ui.showError(e.getMessage());
        }
    }

    public TaskList getTasks() {
        return tasks;
    }

    /**
     * Returns the error encountered while loading saved tasks, if any.
     * The caller displays this through its own interface so GUI errors do not go only to the terminal.
     *
     * @return the startup error, or an empty string when saved tasks loaded successfully
     */
    public String getStartupError() {
        return startupError;
    }

    /**
     * Run method for CLI
     */
    public void run() {
        // Preserve the CLI's startup-error-before-welcome ordering while keeping the GUI free to show it in a dialog.
        if (!startupError.isEmpty()) {
            ui.showError(startupError);
        }
        ui.showWelcome();
        boolean isExit = false;
        while (!isExit) {
            if (!ui.hasNextCommand()) {
                ui.showBye();
                break;
            }
            String userInput = ui.readCommand();
            ui.showLine();
            isExit = Parser.parse(userInput, tasks, ui, storage);
            ui.showLine();
        }
        ui.close();
    }

    public static void main(String[] args) {
        new TardT().run();
    }
}
