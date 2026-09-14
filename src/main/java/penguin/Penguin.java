package penguin;

import java.util.List;

import penguin.command.Command;
import penguin.exception.PenguinException;
import penguin.parser.Parser;
import penguin.storage.Storage;
import penguin.task.TaskList;
import penguin.ui.Ui;

/**
 * Runs the Penguin chatbot.
 */
public class Penguin {
    /** User interface used for console interaction. */
    private final Ui ui;

    /** Storage used for persistent task data. */
    private final Storage storage;

    /** Task list owned by this chatbot. */
    private final TaskList taskList;

    /** Whether the most recently processed command requested application exit. */
    private boolean isExitRequested;

    /**
     * Creates Penguin using the default storage path.
     */
    public Penguin() {
        this("./data/penguin.txt");
    }

    /**
     * Creates Penguin using the specified storage path.
     *
     * @param filePath path of the task storage file.
     */
    public Penguin(String filePath) {
        this(filePath, true);
    }

    /**
     * Creates Penguin with configurable console output.
     *
     * @param filePath path of the task storage file.
     * @param isConsoleOutputEnabled whether responses should be printed.
     */
    public Penguin(String filePath, boolean isConsoleOutputEnabled) {
        ui = new Ui(isConsoleOutputEnabled);
        storage = new Storage(filePath);
        taskList = new TaskList();
        loadTasks();
    }

    /**
     * Loads valid saved tasks and reports invalid records individually.
     */
    private void loadTasks() {
        try {
            List<String> storageLines = storage.loadTaskLines();

            for (String line : storageLines) {
                try {
                    taskList.addLoadedTask(Parser.parseSavedTask(line));
                } catch (PenguinException e) {
                    ui.showError("Skipping invalid saved task! "
                            + e.getMessage());
                }
            }
        } catch (PenguinException e) {
            ui.showError(e.getMessage());
        }
    }

    /**
     * Returns warnings generated while loading saved tasks during startup.
     *
     * @return startup response, or an empty string when all tasks loaded successfully.
     */
    public String getStartupResponse() {
        return ui.getResponse();
    }

    /**
     * Processes one command entered through the GUI.
     *
     * @param input command entered by the user.
     * @return complete chatbot response.
     */
    public String getResponse(String input) {
        ui.clearResponse();
        isExitRequested = false;

        try {
            if (input == null || input.isBlank()) {
                throw new PenguinException("Please input a task.");
            }

            isExitRequested = executeCommand(input);
        } catch (PenguinException e) {
            ui.showError(e.getMessage());
        }

        return ui.getResponse();
    }

    /**
     * Checks whether the most recently processed command requested application exit.
     *
     * @return true if the application should exit.
     */
    public boolean isExitRequested() {
        return isExitRequested;
    }

    /**
     * Runs the chatbot command loop.
     */
    public void run() {
        ui.showWelcome();
        boolean isExit = false;

        while (!isExit) {
            isExit = processNextCommand();
        }
    }

    /**
     * Reads, parses, and executes one command from the console.
     *
     * @return true if input ended or the command requests application exit.
     */
    private boolean processNextCommand() {
        String fullCommand = ui.readCommand();
        if (fullCommand == null) {
            return true;
        }

        ui.showDivider();
        try {
            if (fullCommand.isBlank()) {
                throw new PenguinException("Please input a task.");
            }

            return executeCommand(fullCommand);
        } catch (PenguinException e) {
            ui.showError(e.getMessage());
            return false;
        } finally {
            ui.showDivider();
        }
    }

    /**
     * Parses and executes one validated command.
     *
     * @param input complete command entered by the user.
     * @return true if the command requests application exit.
     * @throws PenguinException if parsing or command execution fails.
     */
    private boolean executeCommand(String input) throws PenguinException {
        Command command = Parser.parseCommand(input);
        command.execute(taskList, ui, storage);
        return command.isExit();
    }

    /**
     * Starts Penguin with its default storage path.
     *
     * @param args command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        new Penguin().run();
    }
}
