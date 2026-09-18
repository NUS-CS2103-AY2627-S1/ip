package sandrone;

import java.nio.file.Path;
import java.util.List;

import sandrone.command.Command;
import sandrone.parser.Parser;
import sandrone.storage.Storage;
import sandrone.task.TaskList;
import sandrone.ui.Ui;

/** Coordinates the user interface, command parser, task list, and storage. */
public class Sandrone {
    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;
    private final Parser parser;
    private boolean isExitRequested;
    private boolean lastResponseWasError;

    /**
     * Creates the chatbot and restores any previously saved tasks.
     *
     * @param filePath path of the task save file
     */
    public Sandrone(String filePath) {
        ui = new Ui();
        storage = new Storage(Path.of(filePath), ui);
        parser = new Parser();
        tasks = loadTasks(storage.load());
    }

    /** Runs the command loop until the user enters {@code bye}. */
    public void run() {
        showWelcome();
        while (!isExitRequested && ui.hasNextCommand()) {
            getResponse(ui.readCommand());
        }
        ui.close();
    }

    /** Returns Sandrone's welcome message for the selected user interface. */
    public String showWelcome() {
        ui.showWelcome();
        return ui.consumeOutput();
    }

    /**
     * Processes one user command through the existing parser and command logic.
     *
     * @param input command entered by the user
     * @return text produced while processing the command
     */
    public String getResponse(String input) {
        lastResponseWasError = false;
        try {
            Command command = parser.parse(input.trim());
            command.execute(tasks, ui, storage);
            isExitRequested = command.isExit();
            if (isExitRequested) {
                ui.showMessage("Your records have been preserved. Do not lose them.");
            }
        } catch (SandroneException e) {
            lastResponseWasError = true;
            ui.showMessage("That input does not form a valid instruction. Correct it: " + e.getMessage());
        }
        return ui.consumeOutput();
    }

    /** Returns whether the most recently processed command was an exit command. */
    public boolean isExitRequested() {
        return isExitRequested;
    }

    /** Returns whether processing the most recent command produced an error response. */
    public boolean wasLastResponseError() {
        return lastResponseWasError;
    }

    /** Recreates the task list from saved task records. */
    private TaskList loadTasks(List<String> taskLines) {
        TaskList loadedTasks = new TaskList();
        for (String taskLine : taskLines) {
            if (taskLine.isBlank()) {
                continue;
            }
            if (loadedTasks.isFull()) {
                ui.showMessage("Warning: Skipped saved tasks beyond the maximum of 10");
                break;
            }
            try {
                loadedTasks.addTask(parser.parseTaskFromFile(taskLine));
            } catch (SandroneException e) {
                ui.showMessage("Warning: Skipped invalid saved task: " + e.getMessage());
            }
        }
        return loadedTasks;
    }

    /** Starts the chatbot using its default save-file location. */
    public static void main(String[] args) {
        new Sandrone("data/tasks.txt").run();
    }
}
