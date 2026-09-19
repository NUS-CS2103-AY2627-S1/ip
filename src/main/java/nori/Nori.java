package nori;

import nori.command.Command;
import nori.command.Parser;
import nori.storage.Storage;
import nori.task.TaskList;
import nori.ui.Ui;

/**
 * Coordinates Nori's user interface, task list, command parser, and storage.
 */
public class Nori {
    /** The final response shown when a user ends a Nori session. */
    private static final String GOODBYE_MESSAGE = "Noot noot! Time to waddle off. Stay frosty!";

    /** Reads and writes the saved task list. */
    private final Storage storage;
    /** The tasks for this session, restored from storage at startup. */
    private final TaskList tasks;
    /** Reads commands from and writes responses to the console. */
    private final Ui ui;
    /** Why the saved tasks could not be loaded, or {@code null} if they were. */
    private final String loadingError;
    /** A migration or recovery message to show at startup, if any. */
    private final String loadingNotice;

    /**
     * Creates Nori with its standard console user interface and disk storage.
     */
    public Nori() {
        this(new Ui());
    }

    /**
     * Creates Nori with a supplied user interface and standard disk storage.
     *
     * @param ui the user interface that receives command responses.
     */
    public Nori(Ui ui) {
        this.ui = ui;
        storage = new Storage();

        TaskList loadedTasks;
        String savedLoadingError = null;
        String savedLoadingNotice = null;
        try {
            loadedTasks = new TaskList(storage.loadTasks());
            savedLoadingNotice = storage.getLoadingNotice();
        } catch (NoriException exception) {
            savedLoadingError = exception.getMessage();
            loadedTasks = new TaskList();
        }
        tasks = loadedTasks;
        loadingError = savedLoadingError;
        loadingNotice = savedLoadingNotice;
        assert loadingError == null || loadingNotice == null
                : "A failed load produces an error and no notice, so one startup message is shown.";
    }

    /**
     * Runs Nori until the user exits or standard input ends.
     */
    public void run() {
        ui.showWelcome();
        showLoadingMessage();

        boolean isExitRequested = false;
        String input;
        while ((input = ui.readCommand()) != null) {
            if (executeCommand(input)) {
                isExitRequested = true;
                break;
            }
        }
        if (!isExitRequested) {
            ui.showResponse(GOODBYE_MESSAGE);
        }
        ui.close();
    }

    /**
     * Executes one command and sends its response to the configured user interface.
     *
     * @param input the command entered by the user.
     * @return {@code true} when the command requests that Nori exits.
     */
    public boolean executeCommand(String input) {
        assert input != null : "A user interface reads a command line before asking for it to run.";

        try {
            Command command = Parser.parse(input);
            command.execute(tasks, ui, storage);
            if (command.isExit()) {
                ui.showResponse(GOODBYE_MESSAGE);
                return true;
            }
        } catch (NoriException exception) {
            ui.showError(exception.getMessage());
        }
        return false;
    }

    /**
     * Returns the notice or error produced while loading saved tasks.
     *
     * @return the loading message, or {@code null} when loading was uneventful.
     */
    public String getLoadingMessage() {
        return loadingError != null ? loadingError : loadingNotice;
    }

    /**
     * Returns whether the message produced while loading saved tasks reported a failure.
     *
     * @return {@code true} when saved tasks could not be loaded.
     */
    public boolean hasLoadingError() {
        return loadingError != null;
    }

    /**
     * Displays the result of loading saved tasks, if one was produced.
     */
    private void showLoadingMessage() {
        if (loadingError != null) {
            ui.showLoadingError(loadingError);
        } else if (loadingNotice != null) {
            ui.showLoadingNotice(loadingNotice);
        }
    }

    /**
     * Starts Nori with its standard configuration.
     *
     * @param args unused command-line arguments.
     */
    public static void main(String[] args) {
        new Nori().run();
    }
}
