package meka;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import meka.command.Command;
import meka.exception.DataFileException;
import meka.exception.MekaException;
import meka.parser.Parser;
import meka.storage.Storage;
import meka.task.TaskList;
import meka.ui.Ui;

/**
 * Coordinates MEKA's user interface, task list, command parser, and storage.
 */
public class Meka {
    private static final String DEFAULT_FILE_PATH = "data/meka.txt";

    /** Storage component used to load and save tasks. */
    private final Storage storage;

    /** Tasks available in the current MEKA session. */
    private final TaskList tasks;

    /** Console interface used to interact with the user. */
    private final Ui ui;

    /** Whether startup encountered an error while loading saved tasks. */
    private final boolean hasLoadingError;

    /** Whether the most recent command requested that the application exit. */
    private boolean isExitRequested;

    /** Whether the most recent command produced an error response. */
    private boolean isLastResponseError;

    /**
     * Creates MEKA using its default task data file.
     */
    public Meka() {
        this(DEFAULT_FILE_PATH);
    }

    /**
     * Creates MEKA and loads tasks from the specified data file.
     *
     * If loading fails, MEKA starts with an empty in-memory task list and
     * avoids overwriting the unreadable data file.
     *
     * @param filePath location of the task data file.
     */
    public Meka(String filePath) {
        ui = new Ui();
        storage = new Storage(Path.of(filePath));

        TaskList loadedTasks;
        boolean hasLoadFailed = false;
        try {
            loadedTasks = storage.load();
        } catch (DataFileException | IOException | SecurityException exception) {
            loadedTasks = new TaskList();
            hasLoadFailed = true;
            storage.markUnavailable();
        }
        tasks = loadedTasks;
        hasLoadingError = hasLoadFailed;
    }

    /**
     * Processes commands until input ends or an exit command is executed.
     */
    public void run() {
        ui.showWelcome();
        if (hasLoadingError) {
            ui.showLoadingError();
        }
        ui.showLine();

        boolean isExit = false;
        while (!isExit && ui.hasNextCommand()) {
            String fullCommand = ui.readCommand();
            ui.showLine();
            processCommand(fullCommand, ui);
            isExit = isExitRequested;
            ui.showLine();
        }
    }

    /**
     * Returns MEKA's response to a command entered through the graphical interface.
     *
     * @param input command entered by the user.
     * @return user-visible response produced by the command.
     */
    public String getResponse(String input) {
        ByteArrayOutputStream responseBytes = new ByteArrayOutputStream();
        try (PrintStream responseOutput = new PrintStream(responseBytes, true, StandardCharsets.UTF_8)) {
            processCommand(input.trim(), new Ui(responseOutput));
        }
        return responseBytes.toString(StandardCharsets.UTF_8).strip();
    }

    /**
     * Returns whether the most recent command requested that MEKA exit.
     *
     * @return true after a successful {@code bye} command.
     */
    public boolean isExitRequested() {
        return isExitRequested;
    }

    /**
     * Returns whether the most recent command produced an error response.
     *
     * @return true when the command was invalid or its changes could not be saved.
     */
    public boolean isLastResponseError() {
        return isLastResponseError;
    }

    /**
     * Parses and executes one command, reporting recoverable failures through the supplied UI.
     *
     * @param fullCommand complete command text.
     * @param targetUi interface that receives the command result.
     */
    private void processCommand(String fullCommand, Ui targetUi) {
        isExitRequested = false;
        isLastResponseError = false;
        try {
            Command command = Parser.parse(fullCommand);
            command.execute(tasks, targetUi, storage);
            isExitRequested = command.isExit();
        } catch (MekaException exception) {
            isLastResponseError = true;
            targetUi.showError(exception.getMessage());
        } catch (IOException | SecurityException exception) {
            isLastResponseError = true;
            storage.markUnavailable();
            targetUi.showSavingError();
        }
    }

    /**
     * Starts MEKA using its default task data file.
     *
     * @param args command-line arguments; not used.
     */
    public static void main(String[] args) {
        new Meka().run();
    }
}
