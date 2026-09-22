package yappa;

import java.nio.file.Path;
import java.util.List;

import yappa.command.Command;
import yappa.exception.YappaException;
import yappa.parser.Parser;
import yappa.storage.LoadResult;
import yappa.storage.Storage;
import yappa.task.TaskList;
import yappa.ui.Ui;

/**
 * Runs the Yappa task manager and coordinates user input, storage, and output.
 */

public class Yappa {

    private final Storage storage = new Storage(Path.of("data", "yappa.txt"));
    private TaskList tasks = new TaskList();
    private final Ui ui = new Ui();
    private boolean isExitRequested;
    private boolean isLastError = false;
    private List<String> loadWarnings = List.of();

    /**
     * Creates Yappa and loads previously saved tasks.
     */
    public Yappa() {
        loadTasks();
    }

    /**
     * Returns the greeting shown when the application starts.
     *
     * @return User-facing greeting.
     */
    public String getGreeting() {
        return ui.showGreeting();
    }

    /**
     * Loads saved tasks, or displays an error if the storage file cannot be read.
     */
    private void loadTasks() {
        try {
            LoadResult result = storage.loadTasks();
            tasks = result.tasks();
            loadWarnings = result.warnings();
        } catch (YappaException e) {
            tasks = new TaskList();
            loadWarnings = List.of(e.getMessage());
        }
    }

    /**
     * Returns warnings produced while loading saved tasks.
     *
     * @return Warnings from the most recent load operation.
     */
    public List<String> getLoadWarnings() {
        return loadWarnings;
    }

    /**
     * Executes a user command and returns the corresponding response.
     *
     * @param input User command.
     * @return User-facing response.
     */
    public String getResponse(String input) {
        isExitRequested = false;

        try {
            Command command = Parser.parse(input);
            assert command != null;

            String response = command.execute(tasks, ui);
            isLastError = false;

            if (command.modifiesTasks()) {
                storage.saveTasks(tasks);
            }

            isExitRequested = command.isExit();

            return response;
        } catch (YappaException e) {
            isLastError = true;
            return e.getMessage();
        }
    }

    /**
     * Returns whether the most recently executed command requested application
     * exit.
     *
     * @return True when the application should exit.
     */
    public boolean isExitRequested() {
        return isExitRequested;
    }

    public boolean isLastError() {
        return isLastError;
    }

}
