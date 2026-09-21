package janet;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import janet.exception.JanetException;
import janet.logic.Command;
import janet.logic.Parser;
import janet.storage.Storage;
import janet.task.Task;
import janet.task.TaskList;
import janet.ui.Ui;

/**
 * Runs Janet, a command-line task manager.
 */
public class Janet {
    /** Location of Janet's data file relative to the working directory. */
    private static final String DATA_FILE_PATH = "data/janet.txt";

    /** Handles persisted task data. */
    private final Storage storage;

    /** Holds the tasks managed during this run. */
    private final TaskList tasks;

    /** Warnings found while loading saved tasks. */
    private final List<String> startupWarnings;

    /** Handles console input and output. */
    private final Ui ui;

    /**
     * Creates Janet and loads tasks saved during a previous run.
     */
    public Janet() {
        this(DATA_FILE_PATH);
    }

    /**
     * Creates Janet with a specific task data file.
     *
     * @param dataFilePath path of the file used to store tasks.
     */
    Janet(String dataFilePath) {
        ui = new Ui();
        storage = new Storage(dataFilePath);
        List<Task> loadedTasks = storage.load();
        tasks = new TaskList(loadedTasks);
        startupWarnings = storage.getLoadWarnings();
    }

    /**
     * Starts Janet's command-processing loop.
     */
    public void run() {
        ui.showWelcome();
        for (String warning : startupWarnings) {
            ui.showError(warning);
        }
        if (!startupWarnings.isEmpty()) {
            ui.showLine();
        }
        boolean isExit = false;
        while (!isExit && ui.hasNextCommand()) {
            try {
                Command command = Parser.parse(ui.readCommand());
                command.execute(tasks, ui, storage);
                isExit = command.isExit();
            } catch (JanetException exception) {
                ui.showError(exception.getMessage());
            } finally {
                ui.showLine();
            }
        }
    }

    /**
     * Processes one command and returns its response for the graphical interface.
     *
     * @param userInput command entered by the user.
     * @return Janet's response without console divider lines
     */
    public String getResponse(String userInput) {
        return getCommandResult(userInput).message();
    }

    /**
     * Processes one command and describes how its response should be presented.
     *
     * @param userInput command entered by the user.
     * @return response text together with error and exit state
     */
    public CommandResult getCommandResult(String userInput) {
        ByteArrayOutputStream responseBytes = new ByteArrayOutputStream();
        boolean isError = false;
        boolean isExit = false;
        try (PrintStream responseOutput = new PrintStream(responseBytes, true, StandardCharsets.UTF_8)) {
            Ui responseUi = new Ui(responseOutput);
            try {
                Command command = Parser.parse(userInput);
                command.execute(tasks, responseUi, storage);
                isExit = command.isExit();
            } catch (JanetException exception) {
                isError = true;
                responseUi.showError(exception.getMessage());
            }
        }

        String message = responseBytes.toString(StandardCharsets.UTF_8).lines()
                .filter(line -> !line.matches("_+"))
                .map(String::stripLeading)
                .collect(Collectors.joining(System.lineSeparator()))
                .stripTrailing();
        return new CommandResult(message, isError, isExit);
    }

    /**
     * Returns warnings found while loading saved tasks.
     *
     * @return read-only startup warning list
     */
    public List<String> getStartupWarnings() {
        return startupWarnings;
    }

    /**
     * Starts Janet from the command line.
     *
     * @param args command-line arguments, which Janet does not use.
     */
    public static void main(String[] args) {
        new Janet().run();
    }
}
