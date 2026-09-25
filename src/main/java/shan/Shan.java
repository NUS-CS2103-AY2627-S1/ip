package shan;

import java.nio.file.Path;

import shan.command.Command;
import shan.exception.DataFileException;
import shan.exception.ShanException;
import shan.parser.Parser;
import shan.storage.Storage;
import shan.task.TaskList;
import shan.ui.Ui;

/**
 * Runs the Shan chatbot.
 */
public class Shan {
    private static final Path DEFAULT_DATA_FILE = Path.of("data", "shan.txt");

    private final Storage storage;
    private final TaskList tasks;
    private boolean isInitialized;

    /**
     * Constructs Shan with collaborators that use the specified data file.
     *
     * @param dataFile Relative path to the task data file.
     */
    public Shan(Path dataFile) {
        this.storage = new Storage(dataFile);
        this.tasks = new TaskList();
    }

    /**
     * Constructs Shan with the default task data file.
     */
    public Shan() {
        this(DEFAULT_DATA_FILE);
    }

    /**
     * Starts Shan using the default task data file.
     *
     * @param args Command-line arguments; unused.
     */
    public static void main(String[] args) {
        new Shan().run();
    }

    /**
     * Loads saved tasks once and returns a startup warning, if any.
     *
     * @return Startup warning, or {@code null} when loading succeeds completely.
     */
    public String initialize() {
        if (this.isInitialized) {
            return null;
        }

        this.isInitialized = true;
        return loadTasks();
    }

    /**
     * Parses and executes one user command.
     *
     * @param input User command to execute.
     * @return Message to show the user and whether the application should exit.
     */
    public CommandResult executeCommand(String input) {
        try {
            Command command = Parser.parse(input);
            TaskList.State stateBeforeCommand = this.tasks.createState();
            String response = command.execute(this.tasks, this.storage);
            if (command.isUndoable()) {
                this.tasks.recordUndoState(stateBeforeCommand);
            }
            return new CommandResult(response, command.isExit());
        } catch (ShanException exception) {
            return new CommandResult(exception.getMessage(), false);
        }
    }

    /**
     * Loads saved tasks and processes commands from standard input.
     */
    public void run() {
        Ui ui = new Ui();
        String startupWarning = initialize();
        ui.showWelcome();
        if (startupWarning != null) {
            ui.showMessage(startupWarning);
        }

        boolean isExit = false;
        while (!isExit && ui.hasNextCommand()) {
            String inputLine = ui.readCommand();
            CommandResult result = executeCommand(inputLine);
            ui.showMessage(result.message());
            isExit = result.shouldExit();
        }

        ui.close();
    }

    /**
     * Loads tasks and returns a warning when any saved data cannot be used.
     *
     * @return Startup warning, or {@code null} when loading succeeds completely.
     */
    private String loadTasks() {
        this.tasks.clear();
        try {
            Storage.LoadResult loadResult = this.storage.load();
            this.tasks.replaceAll(loadResult.tasks());
            int skippedTasks = loadResult.skippedTasks();
            if (skippedTasks > 0) {
                return String.format(
                        "Warning: I skipped %d invalid task %s in %s.",
                        skippedTasks, skippedTasks == 1 ? "entry" : "entries",
                        this.storage.getFilePath());
            }
            return null;
        } catch (DataFileException exception) {
            return exception.getMessage();
        }
    }
}
