package caesar;

import caesar.command.Command;
import caesar.exception.CaesarException;
import caesar.parser.Parser;
import caesar.storage.Storage;
import caesar.task.Task;
import caesar.task.TaskList;
import caesar.ui.Ui;

import java.util.ArrayList;

/**
 * Runs the Caesar command-line task assistant and coordinates task persistence.
 */
public class Caesar {
    /** Provides access to the task file. */
    private final Storage storage;
    /** Handles all console input and output. */
    private final Ui ui;
    /** Interprets commands entered by the user. */
    private final Parser parser;
    /** Holds the tasks currently managed by the application. */
    private final TaskList tasks;
    /** Records whether storage created the task file during construction. */
    private final boolean fileWasCreated;
    /** Stores a loading error so it can be displayed when {@link #run()} starts. */
    private final CaesarException loadingError;

    /** Supported command keywords. Kept here for compatibility with earlier levels. */
    public enum CommandType {
        TODO,
        DEADLINE,
        EVENT,
        LIST,
        FIND,
        HELP,
        REMINDER,
        CLEAR,
        MARK,
        UNMARK,
        DELETE,
        RESCHEDULE,
        BYE,
        UNKNOWN;

        /** Converts a command keyword into its corresponding command type. */
        public static CommandType fromString(String command) {
            if (command == null || command.isBlank()) {
                return UNKNOWN;
            }

            try {
                return CommandType.valueOf(command.trim().toUpperCase());
            } catch (IllegalArgumentException exception) {
                return UNKNOWN;
            }
        }
    }

    /** Creates Caesar with the supplied task-file path. */
    public Caesar(String filePath) {
        this(filePath, new Ui());
    }

    /** Creates Caesar with the supplied task-file path and user interface. */
    public Caesar(String filePath, Ui ui) {
        storage = new Storage(filePath);
        this.ui = ui;
        parser = new Parser();

        TaskList loadedTasks;
        CaesarException loadFailure = null;
        boolean createdFile = false;
        try {
            loadedTasks = new TaskList(storage.load());
            createdFile = storage.wasFileCreated();
        } catch (CaesarException exception) {
            loadFailure = exception;
            loadedTasks = new TaskList();
        }
        tasks = loadedTasks;
        fileWasCreated = createdFile;
        loadingError = loadFailure;
    }

    /** Processes one command and returns whether the application should exit. */
    public boolean processCommand(String command) throws CaesarException {
        Command commandObject = parser.parseCommand(command);
        commandObject.execute(tasks, ui, storage);
        return commandObject.isExit();
    }

    /** Runs the command loop until the user enters {@code bye} or input ends. */
    public void run() {
        ui.showWelcome();
        if (loadingError != null) {
            ui.showLoadingError(loadingError);
        } else if (fileWasCreated) {
            ui.showFileCreated(storage.getFilePath());
        }

        try {
            while (ui.hasNextCommand()) {
                String command = ui.readCommand();
                ui.showDivider();

                try {
                    if (processCommand(command)) {
                        return;
                    }
                } catch (CaesarException exception) {
                    ui.showError(exception);
                }
            }
        } finally {
            ui.close();
        }
    }

    /** Starts Caesar with its default task-file location. */
    public static void main(String[] args) {
        new Caesar("data/tasks.txt").run();
    }

    /** Loads tasks from a string path for callers from earlier levels. */
    static ArrayList<Task> loadTasksFromFile(String filePath) throws CaesarException {
        ArrayList<Task> tasks = new Storage(filePath).load();
        // Reuse TaskList's capacity validation for callers of the old helper.
        new TaskList(tasks);
        return tasks;
    }

}
