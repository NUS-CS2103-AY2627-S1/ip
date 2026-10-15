package potato;

import potato.command.Command;
import potato.exception.PotatoException;
import potato.parser.Parser;
import potato.storage.Storage;
import potato.tasklist.TaskList;
import potato.ui.Ui;

/**
 * Main logic class for the Potato task manager application.
 */
public class Potato {
    private final Storage storage;
    private TaskList tasks;
    private final Ui ui;
    private boolean lastResponseWasError;
    private boolean lastResponseWasExit;

    /**
     * Initializes Potato with a storage file path.
     *
     * @param filePath File path for task data storage.
     */
    public Potato(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        try {
            tasks = new TaskList(storage.load());
        } catch (Exception e) {
            tasks = new TaskList();
        }
    }

    /**
     * Generates a response string for the JavaFX GUI given user input.
     *
     * @param input Raw user command line.
     * @return String response output from executing the command.
     */
    public String getResponse(String input) {
        lastResponseWasError = false;
        lastResponseWasExit = false;
        if (input == null || input.trim().isEmpty()) {
            lastResponseWasError = true;
            return "MON DIEU! Do not stand there silently! Order a task!";
        }
        try {
            Command command = Parser.parse(input);
            command.execute(tasks, ui, storage);
            lastResponseWasExit = command.isExit();
            return ui.getAndClearResponse();
        } catch (PotatoException e) {
            lastResponseWasError = true;
            return e.getMessage();
        }
    }

    /**
     * Returns whether the latest GUI response was an error response.
     *
     * @return {@code true} if the most recent response came from invalid input; {@code false} otherwise.
     */
    public boolean wasLastResponseError() {
        return lastResponseWasError;
    }

    /**
     * Returns whether the latest GUI response should end the application.
     *
     * @return {@code true} if the most recent command was an exit command; {@code false} otherwise.
     */
    public boolean wasLastResponseExit() {
        return lastResponseWasExit;
    }

    /**
     * Runs the command-line interface version of the application.
     */
    public void run() {
        ui.showWelcome();
        boolean isExit = false;

        while (!isExit) {
            String fullCommand = ui.readCommand();
            if (fullCommand.isEmpty()) {
                continue;
            }

            ui.showLine();
            try {
                Command c = Parser.parse(fullCommand);
                c.execute(tasks, ui, storage);
                isExit = c.isExit();
            } catch (PotatoException e) {
                ui.showError(e.getMessage());
            } finally {
                ui.showLine();
            }
        }
        ui.closeScanner();
    }

    /**
     * Generates the welcome message string for the GUI initialization.
     *
     * @return Formatted welcome greeting.
     */
    public String getWelcomeMessage() {
        ui.showWelcome();
        return ui.getAndClearResponse();
    }

    public static void main(String[] args) {
        new Potato("./data/potato.txt").run();
    }
}
