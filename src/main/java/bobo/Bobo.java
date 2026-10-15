package bobo;

import bobo.exception.BoboException;
import bobo.parser.Parser;
import bobo.storage.Storage;
import bobo.task.TaskList;
import bobo.ui.Ui;

/**
 * Main class for the Bobo task chatbot application.
 * Orchestrates Ui, Storage, TaskList, and Parser components.
 */
public class Bobo {

    private final Storage storage;
    private final Ui ui;
    private TaskList tasks;
    private boolean isExit = false;

    /**
     * Constructs a Bobo chatbot instance with default storage location.
     */
    public Bobo() {
        this("store.txt");
    }

    /**
     * Constructs a Bobo chatbot instance with storage at the given file path.
     *
     * @param filePath Path to the storage text file.
     */
    public Bobo(String filePath) {
        assert filePath != null && !filePath.trim().isEmpty() : "Storage file path must not be null or empty";
        ui = new Ui();
        storage = new Storage(filePath);
        try {
            tasks = new TaskList(storage.load());
        } catch (BoboException e) {
            ui.showLoadingError();
            tasks = new TaskList();
        }
        assert ui != null : "Ui subsystem must be initialized";
        assert storage != null : "Storage subsystem must be initialized";
        assert tasks != null : "TaskList subsystem must be initialized";
    }

    /**
     * Generates a response string for the user input.
     *
     * @param input Raw user input command.
     * @return Response message from Bobo.
     */
    public String getResponse(String input) {
        assert tasks != null : "TaskList should be initialized before processing responses";
        assert ui != null : "Ui should be initialized before processing responses";
        assert storage != null : "Storage should be initialized before processing responses";
        ui.clearResponseBuffer();
        try {
            isExit = Parser.executeCommand(input, tasks, ui, storage);
        } catch (BoboException e) {
            ui.showError(e.getMessage());
        }
        return ui.getResponseBuffer();
    }

    /**
     * Returns whether the last processed command was an exit command.
     *
     * @return True if command was 'bye', false otherwise.
     */
    public boolean isExit() {
        return isExit;
    }

    /**
     * Returns the welcome message for Bobo.
     *
     * @return Welcome greeting string.
     */
    public String getWelcomeMessage() {
        ui.clearResponseBuffer();
        ui.showWelcome(tasks != null ? tasks.size() : 0);
        return ui.getResponseBuffer();
    }
}
