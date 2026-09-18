package aether;

import java.nio.file.Path;
import java.util.List;

import aether.exception.AetherException;
import aether.parser.Command;
import aether.parser.CommandType;
import aether.parser.Parser;
import aether.storage.Storage;
import aether.task.Task;
import aether.task.TaskList;
import aether.task.TaskStatus;
import aether.ui.Ui;

/**
 * Coordinates the chatbot's user interface, command parsing, task list, and persistent storage.
 */
public class Aether {
    /** Storage location used by the desktop application. */
    private static final Path DEFAULT_STORAGE_PATH = Path.of("data", "aether.txt");

    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;
    private final Parser parser;
    /** Prevents unreadable saved data from being overwritten by a recovered empty task list. */
    private boolean canSaveTasks = true;

    /** Creates an Aether chatbot that uses the standard project-relative data file. */
    public Aether() {
        this(DEFAULT_STORAGE_PATH);
    }

    /**
     * Creates an Aether chatbot that saves tasks at the supplied path.
     * If saved data cannot be loaded, the chatbot reports the problem and starts with a read-only empty list.
     *
     * @param filePath the path used to save tasks between sessions
     */
    public Aether(Path filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        parser = new Parser();
        tasks = loadTasks();
    }

    /** Runs the chatbot until the user enters {@code bye} or the input stream ends. */
    public void run() {
        ui.showWelcome();
        while (ui.hasNextCommand()) {
            try {
                Command command = parser.parse(ui.readCommand());
                if (command.getType() == CommandType.BYE) {
                    break;
                }
                ui.showResponse(processCommand(command));
            } catch (AetherException e) {
                ui.showError(e.getMessage());
            }
        }
        ui.showGoodbye();
        ui.close();
    }

    /**
     * Returns the greeting shown when a new GUI conversation starts.
     *
     * @return Aether's welcome message
     */
    public String getWelcomeMessage() {
        return ui.getWelcomeMessage();
    }

    /**
     * Processes one GUI command and returns the user-facing response without ending the application on invalid input.
     *
     * @param input the complete command entered by the user
     * @return Aether's response, including a helpful error for malformed input
     */
    public String getResponse(String input) {
        return getCommandResult(input).getMessage();
    }

    /**
     * Processes one GUI command and identifies whether its reply represents invalid input.
     *
     * @param input the complete command entered by the user
     * @return the response text together with its error state
     */
    public CommandResult getCommandResult(String input) {
        try {
            Command command = parser.parse(input);
            if (command.getType() == CommandType.BYE) {
                return CommandResult.success(ui.getGoodbyeMessage());
            }
            return CommandResult.success(processCommand(command));
        } catch (AetherException e) {
            return CommandResult.error(e.getMessage());
        }
    }

    /** Starts Aether with its standard project-relative data file. */
    public static void main(String[] args) {
        new Aether(DEFAULT_STORAGE_PATH).run();
    }

    /** Executes one validated, non-exit command and returns its user-facing response. */
    private String processCommand(Command command) throws AetherException {
        switch (command.getType()) {
            case LIST:
                return tasks.formatTaskList();
            case FIND:
                return tasks.formatMatchingTasks(command.getArguments());
            case SORT:
                return sortTasks();
            case TODO:
                // Fallthrough
            case DEADLINE:
                // Fallthrough
            case EVENT:
                return addTask(parser.createTask(command));
            case MARK:
                return markTask(command);
            case UNMARK:
                return unmarkTask(command);
            case DELETE:
                return deleteTask(command);
            default:
                throw new IllegalStateException("Unexpected command type: " + command.getType());
        }
    }

    /** Adds a task, saving it before returning the confirmation. */
    private String addTask(Task task) throws AetherException {
        if (tasks.containsEquivalentTask(task)) {
            throw new AetherException("That task is already in your list. Use a different description or date.");
        }
        tasks.addTask(task);
        try {
            saveTasks();
        } catch (AetherException e) {
            tasks.removeLastTask();
            throw e;
        }
        return ui.getTaskAddedMessage(task, tasks.getTaskCount());
    }

    /** Marks a task as done and restores its previous state if saving fails. */
    private String markTask(Command command) throws AetherException {
        int index = parser.parseTaskIndex(command, tasks.getTaskCount());
        TaskStatus previousStatus = tasks.getTaskStatus(index);
        Task task = tasks.markTask(index);
        try {
            saveTasks();
        } catch (AetherException e) {
            tasks.setTaskStatus(index, previousStatus);
            throw e;
        }
        return ui.getTaskMarkedMessage(task);
    }

    /** Marks a task as not done and restores its previous state if saving fails. */
    private String unmarkTask(Command command) throws AetherException {
        int index = parser.parseTaskIndex(command, tasks.getTaskCount());
        TaskStatus previousStatus = tasks.getTaskStatus(index);
        Task task = tasks.unmarkTask(index);
        try {
            saveTasks();
        } catch (AetherException e) {
            tasks.setTaskStatus(index, previousStatus);
            throw e;
        }
        return ui.getTaskUnmarkedMessage(task);
    }

    /** Deletes a task and restores it at its original position if saving fails. */
    private String deleteTask(Command command) throws AetherException {
        int index = parser.parseTaskIndex(command, tasks.getTaskCount());
        Task task = tasks.deleteTask(index);
        try {
            saveTasks();
        } catch (AetherException e) {
            tasks.restoreTask(index, task);
            throw e;
        }
        return ui.getTaskDeletedMessage(task, tasks.getTaskCount());
    }

    /** Sorts tasks by date and restores their prior order if saving the new order fails. */
    private String sortTasks() throws AetherException {
        List<Task> previousOrder = tasks.sortByDate();
        try {
            saveTasks();
        } catch (AetherException e) {
            tasks.restoreOrder(previousOrder);
            throw e;
        }
        return ui.getTasksSortedMessage(tasks.formatTaskList());
    }

    /** Loads saved tasks, reporting loading errors through the UI. */
    private TaskList loadTasks() {
        try {
            return new TaskList(storage.load());
        } catch (AetherException e) {
            canSaveTasks = false;
            ui.showError(e.getMessage());
            return new TaskList();
        }
    }

    /** Saves the current task list through the storage component. */
    private void saveTasks() throws AetherException {
        if (!canSaveTasks) {
            throw new AetherException("I cannot save changes because the saved tasks could not be loaded. "
                    + "Repair or remove data/aether.txt, then restart Aether.");
        }
        storage.save(tasks.asList());
    }

    /** Contains one GUI response and whether it should be presented as an error. */
    public static class CommandResult {
        private final String message;
        private final boolean error;

        private CommandResult(String message, boolean error) {
            this.message = message;
            this.error = error;
        }

        /** Returns a successful command result. */
        private static CommandResult success(String message) {
            return new CommandResult(message, false);
        }

        /** Returns an invalid-input result. */
        private static CommandResult error(String message) {
            return new CommandResult(message, true);
        }

        /** Returns the response text to show in the chat. */
        public String getMessage() {
            return message;
        }

        /** Returns whether the response represents invalid input. */
        public boolean isError() {
            return error;
        }
    }
}
