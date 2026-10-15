package jiji;

import jiji.command.Command;
import jiji.exception.JijiException;
import jiji.exception.JijiStorageException;
import jiji.gui.JijiResponse;
import jiji.parser.Parser;
import jiji.storage.Storage;
import jiji.task.TaskList;
import jiji.ui.PersonalityBank;
import jiji.ui.Ui;

/**
 * Main entry point for the Jiji personal assistant chatbot.
 * Coordinates the Ui, Storage, TaskList, and Parser components to execute user commands.
 */
public class Jiji {

    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;

    /**
     * Constructs a Jiji chatbot instance with default persistent storage at "data/jiji.txt".
     */
    public Jiji() {
        this("data/jiji.txt");
    }

    /**
     * Constructs a Jiji chatbot instance with the specified file path for persistent data storage.
     *
     * @param filePath The file path for data persistence (e.g. "data/jiji.txt").
     */
    public Jiji(String filePath) {
        this.ui = new Ui();
        this.storage = new Storage(filePath);
        TaskList loadedTasks;
        try {
            loadedTasks = new TaskList(storage.load());
        } catch (JijiStorageException e) {
            ui.showLoadingError();
            loadedTasks = new TaskList();
        }
        this.tasks = loadedTasks;
        assert this.ui != null : "Ui component must be initialized";
        assert this.storage != null : "Storage component must be initialized";
        assert this.tasks != null : "TaskList component must be initialized";
    }

    /**
     * Returns the welcome greeting string for the chatbot.
     *
     * @return Initial greeting message.
     */
    public String getGreeting() {
        return PersonalityBank.getGreeting();
    }

    /**
     * Generates a detailed response object containing the message text and error status
     * for the given user input command.
     *
     * @param input The raw input command string entered by the user.
     * @return A {@code JijiResponse} containing the response message and error flag.
     */
    public JijiResponse getResponseDetails(String input) {
        if (input == null || input.trim().isEmpty()) {
            return new JijiResponse("Please enter a valid command!", true);
        }
        assert input != null && !input.trim().isEmpty() : "Input must be non-empty after check";
        assert tasks != null && ui != null && storage != null : "Components must be initialized";
        try {
            Command command = Parser.parse(input);
            String response = command.execute(tasks, ui, storage);
            assert response != null : "Response should never be null";
            return new JijiResponse(response, false);
        } catch (JijiException e) {
            return new JijiResponse(e.getMessage(), true);
        }
    }

    /**
     * Generates a response string for the given user input command.
     *
     * @param input The raw input command string entered by the user.
     * @return The response text produced by executing the command, or an error message.
     */
    public String getResponse(String input) {
        return getResponseDetails(input).getMessage();
    }

    /**
     * Starts the main command processing loop of Jiji.
     */
    public void run() {
        ui.showWelcome();
        boolean isExit = false;
        while (!isExit && ui.hasNextCommand()) {
            String fullCommand = ui.readCommand();
            if (fullCommand.trim().isEmpty()) {
                continue;
            }
            try {
                Command command = Parser.parse(fullCommand);
                command.execute(tasks, ui, storage);
                isExit = command.isExit();
            } catch (JijiException e) {
                ui.showError(e.getMessage());
            }
        }
    }

    /**
     * Application entry point.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        new Jiji("data/jiji.txt").run();
    }
}
