package tardt;

import java.nio.file.Path;
import java.nio.file.Paths;

import tardt.exception.TardTException;
import tardt.parser.Parser;
import tardt.storage.Storage;
import tardt.task.TaskList;
import tardt.ui.Ui;


/** Provides the interactive command-line interface for the TardT task manager. */
public class TardT {
    private static final Path SAVE_FILE = Paths.get("data", "tasks.txt");

    private final Ui ui;
    private final Storage storage;
    private final TaskList tasks;

    /**
     * Constructor for TardT object
     */
    public TardT() {
        ui = new Ui();
        storage = new Storage(SAVE_FILE);
        TaskList loadedTasks;
        try {
            loadedTasks = new TaskList(storage.load());
        } catch (TardTException e) {
            ui.showError(e.getMessage());
            loadedTasks = new TaskList();
        }

        tasks = loadedTasks;
    }

    /**
     * Processes a user command and returns the response.
     * This is the method that the GUI calls.
     */
    public String getResponse(String input) {
        try {
            return Parser.parseForResponse(input, tasks, storage);
        } catch (TardTException e) {
            return e.getMessage();
        }
    }

    /**
     * Saves tasks to storage (useful for GUI shutdown).
     */
    public void save() {
        try {
            storage.save(tasks.getTasks());
        } catch (TardTException e) {
            ui.showError(e.getMessage());
        }
    }

    public TaskList getTasks() {
        return tasks;
    }

    /**
     * Run method for CLI
     */
    public void run() {
        ui.showWelcome();
        boolean isExit = false;
        while (!isExit) {
            if (!ui.hasNextCommand()) {
                ui.showBye();
                break;
            }
            String userInput = ui.readCommand();
            ui.showLine();
            isExit = Parser.parse(userInput, tasks, ui, storage);
            ui.showLine();
        }
        ui.close();
    }

    public static void main(String[] args) {
        new TardT().run();
    }
}
