package mary;

import mary.command.Command;
import mary.exception.MaryException;
import mary.parser.Parser;
import mary.storage.Storage;
import mary.task.TaskList;
import mary.ui.Ui;

/**
 * Owns one chatbot session shared by the console and JavaFX interfaces.
 */
public class Mary {
    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;
    private String loadingError;
    private boolean isExited;

    /**
     * Loads saved tasks without creating a file when none exists.
     *
     * @param filePath task data path, relative to the working directory in production.
     * @param ui destination for all command replies.
     */
    public Mary(String filePath, Ui ui) {
        this.ui = ui;
        storage = new Storage(filePath);
        TaskList loadedTasks;
        try {
            loadedTasks = new TaskList(storage.load());
        } catch (MaryException exception) {
            loadedTasks = new TaskList();
            loadingError = "could not load saved tasks: " + exception.getMessage()
                    + " Repair or move the data file and restart MARY; saving is disabled to protect it.";
        }
        tasks = loadedTasks;
    }

    /**
     * Returns a startup error for the interface to display, or null on success.
     *
     * @return explanation of any loading failure.
     */
    public String getLoadingError() {
        return loadingError;
    }

    /**
     * Executes one command and delivers its reply through the UI.
     * Commands after bye are ignored so an ended session cannot change tasks.
     *
     * @param input complete, non-null command text.
     * @return whether the session has ended.
     */
    public boolean execute(String input) {
        if (!isExited) {
            Command command = Parser.parse(input);
            // Even invalid user input must produce an UnknownCommand, never null.
            assert command != null : "Parser must return a command for every input";
            command.execute(tasks, ui, storage);
            isExited = command.isExit();
        }
        return isExited;
    }

    /**
     * Runs the original console interaction with its existing separators and greeting.
     */
    public void run() {
        ui.showLine();
        if (loadingError != null) {
            ui.showLoadingError(loadingError);
        }
        ui.showWelcome();
        String input;
        while ((input = ui.readCommand()) != null) {
            ui.showLine();
            if (execute(input)) {
                break;
            }
            ui.showLine();
        }
    }

    /**
     * Starts the optional console interface using data in the working directory.
     *
     * @param args command-line arguments (unused).
     */
    public static void main(String[] args) {
        new Mary("mary-data.txt", new Ui()).run();
    }
}
