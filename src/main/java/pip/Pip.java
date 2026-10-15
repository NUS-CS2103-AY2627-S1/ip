package pip;

import java.io.IOException;

import pip.command.Command;
import pip.parser.Parser;
import pip.storage.Storage;
import pip.task.TaskList;
import pip.ui.Ui;


/**
 * Entry point and main control loop for the Pip task management app.
 */
public class Pip {
    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;
    /** Status of the latest getResponse call; separate from the response wording used by the GUI. */
    private boolean responseError;
    /** Loading warning retained for the GUI, which is connected after storage is loaded. */
    private String startupWarning = "";

    /**
     * Creates a Pip instance backed by the given storage file, loading
     * any previously saved tasks. If loading fails, an error is shown and
     * the app starts with an empty task list instead of failing to start.
     *
     * @param filePath The path of the file used to load and save tasks.
     */
    public Pip(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        TaskList loadedTasks;
        try {
            loadedTasks = new TaskList(storage.load());
        } catch (Exception e) {
            startupWarning = "Error loading tasks from file. Starting with an empty list.";
            ui.showError(startupWarning);
            loadedTasks = new TaskList();
        }
        tasks = loadedTasks;
    }

    /**
     * Runs Pip, using {@code ./data/Pip.txt} (relative to the current
     * working directory) as the storage file.
     *
     * @param args Command-line arguments (unused).
     */
    public static void main(String... args) {
        new Pip("./data/Pip.txt").run();
    }

    /**
     * Runs the main command loop: greets the user, then repeatedly reads,
     * parses, and executes commands until an exit command is given or
     * input ends. Errors from parsing or execution are shown to the user
     * without terminating the loop.
     */
    public void run() {
        ui.showWelcome();
        boolean shouldExit = false;
        while (!shouldExit) {
            try {
                String fullCommand = ui.readCommand();
                if (fullCommand == null) {
                    break;
                }
                Command command = Parser.parse(fullCommand);
                command.execute(tasks, ui, storage);
                shouldExit = command.isExit();
            } catch (Exception e) {
                ui.showError(e.getMessage());
            }
        }
    }

    /**
     * Processes one command, returning the message also printed by the console UI.
     * Parsing and execution errors become response messages rather than escaping to the caller.
     * Updates {@link #isResponseError()} on every call, including successful commands after errors.
     *
     * @param input The command text to parse and execute.
     * @return The command's confirmation, task list, or error message.
     */
    public String getResponse(String input) {
        responseError = false;
        try {
            Command command = Parser.parse(input);
            command.execute(tasks, ui, storage);
            return ui.getLastResponse();
        } catch (Exception e) {
            responseError = true;
            ui.showError(e instanceof IOException
                    ? "Aw, nuts! I couldn't save your tasks. Check the data folder and write permissions. "
                            + "Your change is in memory only; keep Pip open until you can save it."
                    : e.getMessage());
            return ui.getLastResponse();
        }
    }

    /**
     * Indicates whether the latest {@link #getResponse(String)} call failed.
     * This status does not describe commands executed by the console's {@link #run()} loop.
     *
     * @return True if the latest response reports an error; false before the first call or after success.
     */
    public boolean isResponseError() {
        return responseError;
    }

    /**
     * Returns any warning produced while loading saved tasks.
     *
     * @return The warning, or an empty string when loading succeeded or no file existed.
     */
    public String getStartupWarning() {
        return startupWarning;
    }
}
