package crystal;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import crystal.command.Command;
import crystal.exception.CrystalException;
import crystal.parser.Parser;
import crystal.storage.Storage;
import crystal.task.TaskList;
import crystal.ui.Ui;

/**
 * Coordinates Crystal's command loop, task operations, storage, and console UI.
 */
public class Crystal {
    /** Console interface used to interact with the user. */
    private final Ui ui;

    /** Persistence service for the task list. */
    private final Storage storage;

    /** Tasks available during the current chatbot session. */
    private TaskList tasks;

    /** Whether the persisted task list has been loaded for this session. */
    private boolean hasLoadedTasks;

    /**
     * Creates a Crystal chatbot backed by the supplied data file.
     *
     * @param dataFilePath relative path to the task data file.
     */
    public Crystal(Path dataFilePath) {
        ui = new Ui();
        storage = new Storage(dataFilePath);
    }

    /**
     * Loads saved tasks and runs the chatbot's command loop.
     */
    public void run() {
        ui.showWelcome();
        loadTasks(ui);
        boolean isExit = false;
        while (!isExit) {
            try {
                String fullCommand = ui.readCommand();
                ui.showDivider();
                Command command = Parser.parse(fullCommand);
                command.execute(tasks, ui, storage);
                isExit = command.isExit();
            } catch (CrystalException exception) {
                ui.showError(exception);
            } finally {
                ui.showDivider();
            }
        }
    }

    /**
     * Executes one user command and returns the complete user-facing response.
     * This entry point lets graphical interfaces reuse Crystal's command logic
     * without redirecting the process-wide standard output stream.
     *
     * @param fullCommand command entered by the user.
     * @return response generated while loading or executing the command.
     */
    public String getResponse(String fullCommand) {
        ByteArrayOutputStream responseBuffer = new ByteArrayOutputStream();
        try (PrintStream responseOutput = new PrintStream(
                responseBuffer, true, StandardCharsets.UTF_8)) {
            Ui responseUi = Ui.createForOutput(responseOutput);
            loadTasks(responseUi);
            executeCommand(fullCommand, responseUi);
        }
        return responseBuffer.toString(StandardCharsets.UTF_8).stripTrailing();
    }

    /**
     * Loads persisted tasks once and reports recoverable data errors through the active UI.
     *
     * @param responseUi interface that should present any loading error.
     */
    private void loadTasks(Ui responseUi) {
        if (hasLoadedTasks) {
            return;
        }

        try {
            tasks = new TaskList(storage.loadTasks());
        } catch (CrystalException exception) {
            responseUi.showError(exception);
            tasks = new TaskList();
        }
        hasLoadedTasks = true;
    }

    /**
     * Parses and executes one command, reporting expected failures through the active UI.
     *
     * @param fullCommand command entered by the user.
     * @param responseUi interface that should present the command result.
     */
    private void executeCommand(String fullCommand, Ui responseUi) {
        try {
            Command command = Parser.parse(fullCommand);
            command.execute(tasks, responseUi, storage);
        } catch (CrystalException exception) {
            responseUi.showError(exception);
        }
    }

    /**
     * Starts Crystal using its OS-independent relative data path.
     *
     * @param args command-line arguments; not used.
     */
    public static void main(String[] args) {
        new Crystal(Path.of("data", "crystal.txt")).run();
    }
}
