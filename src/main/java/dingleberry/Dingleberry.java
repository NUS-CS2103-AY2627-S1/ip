package dingleberry;

import java.io.IOException;

import dingleberry.command.Command;
import dingleberry.exception.DingleberryException;
import dingleberry.model.TaskList;
import dingleberry.parser.Parser;
import dingleberry.persistence.Storage;
import dingleberry.ui.Ui;

/**
 * Entry point and orchestrator for the Dingleberry task-tracking chatbot.
 * Wires together the {@link Ui}, {@link Storage}, and {@link TaskList}
 * collaborators and drives the read-parse-execute loop in {@link #run()}.
 */
public class Dingleberry {
    /** Fallback path used when the configured storage path is invalid. */
    private static final String FALLBACK_DATA_FILE_PATH =
            "./data/dingleberry.txt";
    /** Handles console input and output. */
    private final Ui ui;
    /** Loads and saves task data. */
    private final Storage storage;
    /** Stores the current in-memory tasks. */
    private TaskList tasks;

    /**
    * Creates a new app instance that loads saved tasks from the given file
    * path.
     *
     * @param filePath the path to the task data file on disk
     */
    public Dingleberry(final String filePath) {
        ui = new Ui();
        Storage configuredStorage;
        try {
            configuredStorage = new Storage(filePath);
        } catch (IllegalArgumentException e) {
            ui.showLoadingError(e.getMessage());
            configuredStorage = new Storage(FALLBACK_DATA_FILE_PATH);
        }
        storage = configuredStorage;
        try {
            Storage.LoadResult loadResult = storage.loadWithReport();
            tasks = new TaskList(loadResult.getTasks());
            for (String warning : loadResult.getWarnings()) {
                ui.showLoadingWarning(warning);
            }
        } catch (IOException e) {
            ui.showLoadingError(e.getMessage());
            tasks = new TaskList();
        }
    }

    /** Runs the lifecycle until the user closes input or issues "bye". */
    public void run() {
        ui.showWelcome();

        while (ui.hasNextCommand()) {
            final String input = ui.readCommand();

            try {
                final Command command = Parser.parse(input);
                assert command != null
                        : "Successful parsing must produce a command.";
                command.execute(tasks, ui, storage);
                if (command.isExit()) {
                    break;
                }
            } catch (DingleberryException e) {
                if (e.isWrongCommand()) {
                    ui.showWrongCommandError(e.getMessage());
                } else {
                    ui.showIncorrectParametersError(e.getMessage());
                }
            }
        }
        ui.close();

        ui.showGoodbye();
    }

    /**
    * Launches the application with the default data file in the project
    * directory.
     *
     * @param args command-line arguments, currently ignored by the app
     */
    public static void main(final String[] args) {
        new Dingleberry("./data/dingleberry.txt").run();
    }
}


