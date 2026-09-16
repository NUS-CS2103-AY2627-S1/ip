package grower;

import java.io.IOException;
import java.util.List;

import grower.commands.Command;
import grower.exceptions.GrowerException;
import grower.parser.Parser;
import grower.storage.Storage;
import grower.tasks.Task;
import grower.tasks.TaskList;
import grower.ui.ResponseType;
import grower.ui.Ui;

/**
 * Runs the Grower task-management application.
 */
public class Grower {
    /** Default location used to persist tasks between application sessions. */
    private static final String DEFAULT_FILE_PATH = "./data/grower.txt";

    /** Formats command results for the CLI and captures them for the GUI. */
    private final Ui ui;

    /** Tasks maintained during the current application session. */
    private TaskList taskList;

    /** Loads and saves the task list using the configured data file. */
    private final Storage storage;

    /** Whether the application should continue accepting commands. */
    private boolean shouldContinueRunning;

    /** Prevents overwriting a file that could not be completely restored. */
    private boolean isStorageReady = true;

    /** Startup problems retained for display in both interfaces. */
    private String startupMessage = "";

    /**
     * Creates a Grower application backed by the default data file.
     */
    public Grower() {
        this(DEFAULT_FILE_PATH);
    }

    /**
     * Creates a Grower application backed by the specified data file.
     *
     * @param filePath Path used to load and save tasks.
     */
    public Grower(String filePath) {
        ui = new Ui();
        taskList = new TaskList();
        storage = new Storage(filePath);
        shouldContinueRunning = true;

        loadTasks();
    }

    /**
     * Starts the command-line interface.
     *
     * @param args Command-line arguments; currently unused.
     */
    public static void main(String[] args) {
        new Grower().runCli();
    }

    /**
     * Processes one command using the same logic for both the CLI and GUI.
     *
     * @param input Command entered by the user.
     * @return Response produced by the command.
     */
    public String getResponse(String input) {
        ui.clearOutput();

        try {
            Command command = Parser.parse(input);
            assert command != null : "Parser must return a command when parsing succeeds";
            TaskList candidate = copyTasks();
            Ui commandUi = new Ui(false);
            boolean shouldContinue = command.execute(candidate, commandUi);
            saveChangesIfNeeded(candidate);
            taskList = candidate;
            shouldContinueRunning = shouldContinue;
            ui.showResponse(commandUi.getOutput(), commandUi.getResponseType());
        } catch (GrowerException e) {
            ui.showError(e.getMessage());
        } catch (IOException e) {
            ui.showError("Hoom! Could not save tasks. Your change was not applied. "
                    + "Check the data folder, write permissions and free space, then try again.");
        }

        return ui.getOutput();
    }

    /**
     * Rebuilds independent task objects so commands cannot change the active list
     * until the new state has been saved successfully.
     */
    private TaskList copyTasks() throws GrowerException {
        TaskList copy = new TaskList();
        for (String taskData : taskList.getTaskData()) {
            copy.addTask(storage.parseTask(taskData));
        }
        return copy;
    }

    /**
     * Saves changed task data before it becomes active, protecting unreadable or corrupt storage.
     */
    private void saveChangesIfNeeded(TaskList candidate) throws GrowerException, IOException {
        List<String> candidateData = candidate.getTaskData();
        if (candidateData.equals(taskList.getTaskData())) {
            return;
        }
        if (!isStorageReady) {
            throw new GrowerException("Hoom! Changes are disabled to protect saved tasks. "
                    + "Repair the data file and restart Grower. " + startupMessage);
        }
        storage.saveTasks(candidateData);
    }

    /**
     * Returns whether Grower should continue accepting commands.
     *
     * @return {@code false} after the bye command, and {@code true} otherwise.
     */
    public boolean isRunning() {
        return shouldContinueRunning;
    }

    /**
     * Returns the attention level of the most recent response.
     *
     * @return Type used to style the response in the GUI.
     */
    public ResponseType getResponseType() {
        return ui.getResponseType();
    }

    /**
     * Restores tasks from storage when the application starts.
     */
    private void loadTasks() {
        try {
            List<String> savedTasks = storage.loadTasks();

            for (String taskData : savedTasks) {
                try {
                    Task task = storage.parseTask(taskData);
                    taskList.addTask(task);
                } catch (GrowerException e) {
                    isStorageReady = false;
                    ui.showError(e.getMessage());
                }
            }
        } catch (IOException e) {
            isStorageReady = false;
            ui.showError("Could not load saved tasks. Check the data file and read permissions.");
        }
        if (!isStorageReady) {
            ui.showError("Changes are disabled to protect saved tasks. Repair the data file and restart Grower.");
            startupMessage = ui.getOutput();
        }
    }

    /**
     * Returns loading errors for display when the GUI first opens.
     *
     * @return Startup errors, or an empty string when all tasks loaded successfully.
     */
    public String getStartupMessage() {
        return startupMessage;
    }

    /**
     * Runs the original command-line input loop for debugging and CLI use.
     */
    private void runCli() {
        ui.showWelcome();

        while (shouldContinueRunning) {
            String input = ui.readCommand();
            if (input == null) {
                shouldContinueRunning = false;
                break;
            }
            ui.showSeparator();
            getResponse(input);
            ui.showSeparator();
        }

        ui.close();
    }
}
