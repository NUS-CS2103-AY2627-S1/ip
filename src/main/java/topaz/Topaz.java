package topaz;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

import topaz.command.Command;
import topaz.parser.Parser;
import topaz.storage.Storage;
import topaz.task.TaskList;
import topaz.ui.Ui;

/**
 * Runs the Topaz command-line chatbot and coordinates its collaborators.
 */
public class Topaz {
    private static final Path DEFAULT_SAVE_FILE = Paths.get("data", "Topaz.txt");
    private final Ui ui;
    private final String saveFileName;
    private Storage storage;
    private final Parser parser;
    private TaskList tasks;
    private boolean exitRequested;
    private boolean responseError;

    /**
     * Creates Topaz with a console user interface.
     */
    public Topaz() {
        this(System.getProperty("topaz.dataFile", DEFAULT_SAVE_FILE.toString()));
    }

    /**
     * Creates Topaz with the specified save file.
     *
     * @param saveFile the file used to store tasks
     */
    Topaz(Path saveFile) {
        this(saveFile.toString());
    }

    /**
     * Defers path validation so configuration errors can be shown in either interface.
     */
    Topaz(String saveFileName) {
        ui = new Ui();
        this.saveFileName = saveFileName;
        parser = new Parser();
    }

    /**
     * Runs the chatbot until the user enters the bye command.
     */
    public void run() {
        try {
            loadTasks();
        } catch (TopazException exception) {
            ui.showLoadingError(exception);
            return;
        }

        ui.showWelcome();

        while (ui.hasNextCommand()) {
            String command = ui.readCommand();

            try {
                Command parsedCommand = parser.parse(command, tasks.size());
                parsedCommand.execute(tasks, ui, storage);
                if (parsedCommand.isExit()) {
                    break;
                }
            } catch (TopazException exception) {
                ui.showError(exception);
            }

            ui.showSeparator();
        }
    }

    /**
     * Processes one command and returns Topaz's response for the graphical interface.
     *
     * @param input the command entered by the user
     * @return the response generated for the command
     */
    public String getResponse(String input) {
        responseError = false;
        StringBuilder response = new StringBuilder();
        Ui responseUi = new Ui(response);
        try {
            loadTasks();
            Command command = parser.parse(input, tasks.size());
            command.execute(tasks, responseUi, storage);
            exitRequested = command.isExit();
        } catch (TopazException exception) {
            responseError = true;
            responseUi.showError(exception);
        }
        return response.toString().stripTrailing();
    }

    /**
     * Returns whether the most recent graphical response reports a failure.
     *
     * @return true when loading, parsing, or executing the last command failed
     */
    public boolean isResponseError() {
        return responseError;
    }

    /**
     * Returns whether the user has ended the current graphical session.
     *
     * @return true if the bye command was processed
     */
    public boolean isExitRequested() {
        return exitRequested;
    }

    /**
     * Loads the saved tasks the first time Topaz needs them.
     *
     * @throws TopazException if the saved task list cannot be loaded
     */
    private void loadTasks() throws TopazException {
        if (storage == null) {
            try {
                if (saveFileName.isBlank()) {
                    throw new InvalidPathException(saveFileName, "The path is empty.");
                }
                storage = new Storage(Paths.get(saveFileName));
            } catch (InvalidPathException | SecurityException exception) {
                throw new TopazException("The save-file path is invalid or inaccessible. Check topaz.dataFile.");
            }
        }
        if (tasks == null) {
            tasks = new TaskList(storage.load());
        }
    }

    /**
     * Starts the Topaz chatbot.
     *
     * @param args command-line arguments, which are not used
     */
    public static void main(String[] args) {
        new Topaz().run();
    }
}
