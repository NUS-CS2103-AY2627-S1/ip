package friday;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import friday.command.Command;
import friday.parser.Parser;
import friday.storage.Storage;
import friday.task.TaskList;
import friday.ui.Ui;

/**
 * Coordinates Friday's commands, tasks, storage, and user interfaces.
 */
public class Friday {
    private static final String STORAGE_PATH_PROPERTY = "friday.storage.path";
    private static final String USER_HOME_STORAGE_PROPERTY = "friday.storage.inUserHome";

    private final Storage storage;
    private final TaskList tasks;
    private String loadWarning;

    /**
     * Creates Friday using the default task storage file.
     */
    public Friday() {
        this(getDefaultStoragePath());
    }

    /**
     * Creates Friday using the specified task storage file.
     *
     * @param storagePath Path used to load and save tasks.
     */
    public Friday(Path storagePath) {
        storage = new Storage(storagePath);
        TaskList loadedTasks;
        try {
            loadedTasks = new TaskList(storage.load());
            if (storage.getRejectedRecordCount() > 0) {
                loadWarning = "Skipped " + storage.getRejectedRecordCount()
                        + " malformed or duplicate records. Saving is disabled to protect the original file.";
            }
        } catch (IOException exception) {
            loadedTasks = new TaskList();
            loadWarning = "Could not load tasks: " + exception.getMessage();
        }
        tasks = loadedTasks;
    }

    /**
     * Executes one user command and returns Friday's response.
     *
     * @param input Command entered by the user.
     * @return Friday's response to the command.
     */
    public String getResponse(String input) {
        ByteArrayOutputStream responseBytes = new ByteArrayOutputStream();
        try (PrintStream responseOutput = new PrintStream(responseBytes, true, StandardCharsets.UTF_8)) {
            Ui responseUi = new Ui(responseOutput);
            showLoadWarning(responseUi);
            Command command = Parser.parse(input == null ? "" : input);
            if (command.isExit()) {
                responseUi.showLine("Bye. See you next time lah!");
            } else {
                command.execute(tasks, responseUi);
                saveTasks(responseUi);
            }
        }
        return responseBytes.toString(StandardCharsets.UTF_8).stripTrailing();
    }

    /**
     * Starts Friday's original console interface.
     *
     * @param args Command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        Friday friday = new Friday();
        friday.runConsole();
    }

    private void runConsole() {
        Ui ui = new Ui();
        ui.showWelcome();
        showLoadWarning(ui);
        Command command = Parser.parse(ui.readCommand());
        while (!command.isExit()) {
            ui.showSeparator();
            command.execute(tasks, ui);
            ui.showSeparator();
            command = Parser.parse(ui.readCommand());
        }
        ui.close();
        ui.showGoodbye();
        saveTasks(ui);
    }

    private void showLoadWarning(Ui ui) {
        if (loadWarning != null) {
            ui.showLine(loadWarning);
            loadWarning = null;
        }
    }

    private void saveTasks(Ui ui) {
        try {
            storage.save(tasks);
        } catch (IOException exception) {
            ui.showLine("Could not save tasks: " + exception.getMessage());
        }
    }

    private static Path getDefaultStoragePath() {
        String configuredPath = System.getProperty(STORAGE_PATH_PROPERTY);
        if (configuredPath != null && !configuredPath.isBlank()) {
            return Path.of(configuredPath);
        }
        if (Boolean.getBoolean(USER_HOME_STORAGE_PROPERTY)) {
            return Path.of(System.getProperty("user.home"), ".friday", "tasks.txt");
        }
        return Path.of("data", "tasks.txt");
    }
}
