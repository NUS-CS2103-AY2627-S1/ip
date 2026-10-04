package penny;

import penny.command.Command;
import penny.common.PennyException;
import penny.parser.Parser;
import penny.storage.Storage;
import penny.task.TaskList;
import penny.ui.Ui;

/**
 * Coordinates user interaction, command interpretation, task management,
 * and data persistence across the application lifecycle.
 */
public class Penny {

    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;
    private boolean isExit;

    /**
     * Constructs a Penny chatbot instance with a specified storage file path.
     *
     * @param filePath The file path string to the storage file (e.g., "data/penny.txt").
     */
    public Penny(String filePath) {
        this(filePath, new String[0]);
    }

    /**
     * Constructs a Penny chatbot instance with OS-independent path segments.
     *
     * @param first The primary directory or path segment.
     * @param more Additional path segments if any.
     */
    public Penny(String first, String... more) {
        this.ui = new Ui();
        this.storage = new Storage(first, more);
        TaskList loadedTasks;
        try {
            loadedTasks = new TaskList(storage.load());
        } catch (PennyException e) {
            ui.showLoadingError(e.getMessage());
            loadedTasks = new TaskList();
        }
        this.tasks = loadedTasks;
        assert this.storage != null : "Storage component must be initialized";
        assert this.tasks != null : "TaskList component must be initialized";
        assert this.ui != null : "Ui component must be initialized";
    }

    /**
     * Executes the main command processing loop of the chatbot using the Command pattern.
     * Reads user commands continuously until an exit command is encountered.
     */
    public void run() {
        ui.showWelcome();
        this.isExit = false;
        while (!this.isExit) {
            try {
                String fullCommand = ui.readCommand();
                ui.showLine();
                Command command = Parser.parse(fullCommand);
                command.execute(tasks, ui, storage);
                this.isExit = command.isExit();
            } catch (PennyException e) {
                ui.showError(e.getMessage());
            } finally {
                ui.showLine();
            }
        }
        ui.close();
    }

    /**
     * Generates a response for user input in the graphical user interface.
     *
     * @param input The raw command string entered by the user.
     * @return The response string produced by executing the command, or the error message.
     */
    public String getResponse(String input) {
        assert input != null : "Input text cannot be null";
        assert this.tasks != null : "TaskList must be initialized";
        assert this.ui != null : "Ui must be initialized";
        assert this.storage != null : "Storage must be initialized";

        String response;
        try {
            Command command = Parser.parse(input);
            response = command.execute(tasks, ui, storage);
            this.isExit = command.isExit();
        } catch (PennyException e) {
            response = ui.showError(e.getMessage());
        }

        assert response != null : "Penny response must never be null";
        return response;
    }

    /**
     * Indicates whether the last executed command signals the application to exit.
     *
     * @return True if the application should terminate, false otherwise.
     */
    public boolean isExit() {
        return isExit;
    }

    /**
     * Returns the welcome greeting string for the application.
     *
     * @return The welcome greeting message.
     */
    public String getWelcomeMessage() {
        return ui.showWelcome();
    }

    /**
     * Starts the Penny application from the command line.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        new Penny("data", "penny.txt").run();
    }
}
