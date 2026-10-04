package happy;

import java.nio.file.Paths;

import happy.exception.HappyException;
import happy.parser.Parser;
import happy.storage.Storage;
import happy.task.TaskList;
import happy.ui.Ui;

/**
 * Main entry point for the Happy chatbot application.
 * Orchestrates Ui, Storage, TaskList, and Parser components to execute chatbot operations.
 */
public class Happy {
    private final Storage storage;
    private TaskList tasks;
    private final Ui ui;

    /**
     * Constructs a Happy chatbot instance configured with the specified file path.
     *
     * @param filePath Relative or absolute path to the data storage file.
     */
    public Happy(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        try {
            tasks = new TaskList(storage.load());
        } catch (HappyException e) {
            ui.showLoadingError();
            tasks = new TaskList();
        }
    }

    /**
     * Starts the chatbot application loop.
     */
    public void run() {
        ui.showWelcome();
        boolean isExit = false;
        while (!isExit) {
            String fullCommand = ui.readCommand();
            if (fullCommand.isEmpty()) {
                continue;
            }
            try {
                isExit = Parser.parseAndExecute(fullCommand, tasks, ui, storage);
            } catch (HappyException e) {
                ui.showError(e.getMessage());
            }
        }
        ui.close();
    }

    /**
     * Launches the Happy chatbot application.
     *
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        new Happy(Paths.get(".", "data", "happy.txt").toString()).run();
    }

    /**
     * Generates a response for the user's chat input.
     *
     * @param input User input string from GUI.
     * @return Response message string from Happy.
     */
    public String getResponse(String input) {
        try {
            // Execute command and return response text
            return Parser.parseAndExecuteForGui(input, tasks, storage);
        } catch (HappyException e) {
            return e.getMessage();
        }
    }
}
