package bubu;

import java.io.UncheckedIOException;

import bubu.command.Command;
import bubu.command.CommandType;
import bubu.exception.BubuException;
import bubu.exception.InvalidIndexException;
import bubu.exception.MissingArgumentException;
import bubu.parser.Parser;
import bubu.storage.Storage;
import bubu.task.Task;
import bubu.task.TaskList;
import bubu.ui.Ui;

/**
 * Coordinates command processing and the chatbot's collaborators.
 */
public class Bubu {
    private static final String REGEX_WHITESPACE = "\\s+";
    private static final int ONE_BASED_INDEX_OFFSET = 1;
    private static final int ARGUMENT_SPLIT_LIMIT = 2;
    private static final int COMMAND_ARGUMENT_INDEX = 1;
    private static final int MINIMUM_COMMAND_PARTS = 2;

    private static final String COMMAND_NAME_MARK = "mark";
    private static final String COMMAND_NAME_UNMARK = "unmark";
    private static final String COMMAND_NAME_DELETE = "delete";
    private static final String STARTUP_WARNING_MESSAGE =
            "Meow! I could not load some saved tasks. Please check the data file and try again.";
    private static final String COMMAND_FAILURE_MESSAGE =
            "Meow! I could not complete that command. Please try again.";
    private static final String UNHANDLED_COMMAND_MESSAGE = "Unhandled command type: ";

    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui = new Ui();
    private String startupWarning;
    /** Whether the most recent response was caused by invalid user input. */
    private boolean lastResponseWasError;
    /** Whether the most recent command requested application exit. */
    private boolean lastResponseWasExit;

    /** Creates Bubu and loads saved tasks without allowing bad data to crash startup. */
    public Bubu() {
        this(new Storage());
    }

    /**
     * Creates Bubu with a supplied storage source.
     *
     * @param storage storage source for the chatbot's tasks.
     */
    public Bubu(Storage storage) {
        assert storage != null : "Storage cannot be null";
        this.storage = storage;
        TaskList loadedTasks;
        try {
            loadedTasks = new TaskList(storage.load());
        } catch (UncheckedIOException | IllegalArgumentException exception) {
            loadedTasks = new TaskList();
            startupWarning = STARTUP_WARNING_MESSAGE;
        }
        tasks = loadedTasks;
    }

    /**
     * Marks the task identified by a user-provided one-based index as complete.
     *
     * @param input Full mark command.
     * @throws BubuException If the index is missing or invalid.
     */
    private void commandMark(String input) throws BubuException {
        assert input != null : "Input cannot be null";
        int index = extractValidIndex(input, COMMAND_NAME_MARK);
        Task task = this.tasks.get(index);
        boolean wasDone = task.isDone();
        task.markAsDone();
        saveTasksOrRestore(task, wasDone);
        ui.showTaskMarked(task, true);
    }

    /**
     * Marks the task identified by a user-provided one-based index as incomplete.
     *
     * @param input Full unmark command.
     * @throws BubuException If the index is missing or invalid.
     */
    private void commandUnmark(String input) throws BubuException {
        assert input != null : "Input cannot be null";
        int index = extractValidIndex(input, COMMAND_NAME_UNMARK);
        Task task = this.tasks.get(index);
        boolean wasDone = task.isDone();
        task.markAsUndone();
        saveTasksOrRestore(task, wasDone);
        ui.showTaskMarked(task, false);
    }

    /**
     * Deletes the task identified by a user-provided one-based index.
     *
     * @param input Full delete command.
     * @throws BubuException If the index is missing or invalid.
     */
    private void commandDelete(String input) throws BubuException {
        assert input != null : "Input cannot be null";
        int index = extractValidIndex(input, COMMAND_NAME_DELETE);
        Task removedTask = this.tasks.remove(index);
        try {
            this.storage.saveTasks(tasks.asList());
        } catch (UncheckedIOException exception) {
            this.tasks.insert(index, removedTask);
            throw exception;
        }
        ui.showTaskDeleted(removedTask, tasks.size());
    }

    /**
     * Saves a changed task and restores its previous status if saving fails.
     *
     * @param task task whose completion status changed.
     * @param previousStatus status before the change.
     */
    private void saveTasksOrRestore(Task task, boolean previousStatus) {
        try {
            this.storage.saveTasks(tasks.asList());
        } catch (UncheckedIOException exception) {
            if (previousStatus) {
                task.markAsDone();
            } else {
                task.markAsUndone();
            }
            throw exception;
        }
    }

    /**
     * Extracts and validates a zero-based task index from user input.
     *
     * @param input Full user command.
     * @param commandName Name of the command requesting the index.
     * @return Validated zero-based task index.
     * @throws BubuException If the argument is missing, non-numeric, or out of range.
     */
    private int extractValidIndex(String input, String commandName) throws BubuException {
        assert input != null : "Input cannot be null";
        assert commandName != null : "Command name cannot be null";
        String[] parts = input.trim().split(REGEX_WHITESPACE, ARGUMENT_SPLIT_LIMIT);
        if (parts.length < MINIMUM_COMMAND_PARTS
                || parts[COMMAND_ARGUMENT_INDEX].trim().isEmpty()) {
            throw new MissingArgumentException(commandName);
        }

        String rawIndex = parts[COMMAND_ARGUMENT_INDEX].trim();
        try {
            int index = Integer.parseInt(rawIndex) - ONE_BASED_INDEX_OFFSET;
            if (!tasks.hasIndex(index)) {
                throw new InvalidIndexException(tasks.size());
            }
            return index;
        } catch (NumberFormatException e) {
            throw new InvalidIndexException(rawIndex);
        }
    }

    /**
     * Generates a response for the user's chat message.
     *
     * @param input The user's chat message.
     * @return The chatbot's response.
     */
    public String getResponse(String input) {
        assert input != null : "Input cannot be null";
        ui.clearResponse();
        lastResponseWasError = false;
        lastResponseWasExit = false;

        try {
            CommandType commandType = Parser.parseCommandType(input);
            assert commandType != null : "CommandType must not be null after parsing";
            executeCommand(commandType, input);
        } catch (BubuException e) {
            lastResponseWasError = true;
            ui.showError(e.getMessage());
        } catch (UncheckedIOException e) {
            lastResponseWasError = true;
            ui.showError(COMMAND_FAILURE_MESSAGE);
        }

        return ui.getResponse();
    }

    /**
     * Returns whether the most recent command requested application exit.
     *
     * @return true when the most recent command was {@code bye}
     */
    public boolean wasLastResponseAnExit() {
        return lastResponseWasExit;
    }

    /**
     * Returns the welcome message shown when the application starts.
     *
     * @return Welcome message generated by the UI formatter.
     */
    public String getWelcomeMessage() {
        ui.clearResponse();
        ui.showWelcome();
        if (startupWarning != null) {
            ui.showError(startupWarning);
        }
        return ui.getResponse();
    }

    /**
     * Returns whether the most recently generated response is an error.
     *
     * @return true when the last command could not be processed
     */
    public boolean wasLastResponseAnError() {
        return lastResponseWasError;
    }

    /**
     * Dispatches command execution based on the parsed command type.
     */
    private void executeCommand(CommandType commandType, String input) throws BubuException {
        assert commandType != null : "CommandType cannot be null";
        assert input != null : "Input cannot be null";
        switch (commandType) {
            case BYE:
            case LIST:
            case TODO:
            case DEADLINE:
            case EVENT:
            case FIND:
            case REMIND:
                executeParsedCommand(commandType, input);
                break;
            case MARK:
                commandMark(input);
                break;
            case UNMARK:
                commandUnmark(input);
                break;
            case DELETE:
                commandDelete(input);
                break;
            default:
                throw createUnhandledCommandException(commandType);
        }
    }

    /** Creates and executes a command that is fully handled by the parser. */
    private void executeParsedCommand(CommandType commandType, String input) throws BubuException {
        Command command = Parser.createCommand(commandType, input);
        assert command != null : "Parser must create a valid Command object";
        assert tasks != null : "TaskList must not be null before execution";
        assert ui != null : "Ui must not be null before execution";
        assert storage != null : "Storage must not be null before execution";
        command.execute(tasks, ui, storage);
        lastResponseWasExit = command.isExit();
    }

    /** Creates an exception for a command type that has no execution path. */
    private IllegalArgumentException createUnhandledCommandException(CommandType commandType) {
        assert false : UNHANDLED_COMMAND_MESSAGE + commandType;
        return new IllegalArgumentException(UNHANDLED_COMMAND_MESSAGE + commandType);
    }
}
