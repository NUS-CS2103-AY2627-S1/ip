package kibo;

import java.time.LocalDate;

import kibo.exception.KiboException;
import kibo.exception.StorageException;
import kibo.parser.CommandType;
import kibo.parser.Parser;
import kibo.storage.Storage;
import kibo.task.Task;
import kibo.task.TaskList;
import kibo.ui.Ui;

/**
 * Runs the Kibo chatbot.
 */
public class Kibo {
    private final Parser parser;
    private final TaskList tasks;
    private final Ui ui;
    private final StorageException loadingError;
    private boolean isExitRequested;

    /**
     * Creates Kibo and loads tasks saved during earlier sessions.
     */
    public Kibo() {
        parser = new Parser();
        ui = new Ui();
        isExitRequested = false;

        TaskList loadedTasks;
        StorageException error = null;
        try {
            loadedTasks = new TaskList(Storage.load());
        } catch (StorageException exception) {
            loadedTasks = new TaskList();
            error = exception;
        }
        tasks = loadedTasks;
        loadingError = error;
    }

    /**
     * Starts Kibo and processes commands until the user exits or input ends.
     *
     * @param args command-line arguments; not used by Kibo.
     */
    public static void main(String[] args) {
        new Kibo().run();
    }

    /**
     * Starts the command-line interface and processes commands until the user exits.
     */
    public void run() {
        ui.showWelcome();

        if (loadingError != null) {
            ui.showMessage(ui.getErrorMessage(loadingError));
            ui.showSeparator();
            return;
        }

        while (ui.hasNextCommand()) {
            String input = ui.readCommand();
            ui.showSeparator();
            ui.showMessage(getResponse(input));
            ui.showSeparator();

            if (isExitRequested) {
                return;
            }
        }
    }

    /**
     * Returns Kibo's welcome message for display by a user interface.
     *
     * @return welcome message.
     */
    public String getWelcomeMessage() {
        return ui.getWelcomeMessage();
    }

    /**
     * Returns a loading error to display when saved tasks could not be loaded.
     *
     * @return loading error message, or an empty string when loading succeeded.
     */
    public String getLoadingErrorMessage() {
        return loadingError == null ? "" : ui.getErrorMessage(loadingError);
    }

    /**
     * Processes one command and returns Kibo's response.
     *
     * @param input command entered by the user.
     * @return response to display to the user.
     */
    public String getResponse(String input) {
        return getCommandResponse(input).getMessage();
    }

    /**
     * Processes a command and returns its message together with an explicit error status.
     *
     * @param input command entered by the user.
     * @return command response for interfaces that distinguish errors visually.
     */
    public CommandResponse getCommandResponse(String input) {
        try {
            CommandType commandType = parser.parseCommandType(input);
            return new CommandResponse(executeCommand(input, commandType), false);
        } catch (KiboException exception) {
            return new CommandResponse(ui.getErrorMessage(exception), true);
        }
    }

    /**
     * Returns whether the most recent command asked Kibo to exit.
     *
     * @return {@code true} after a valid bye command.
     */
    public boolean isExitRequested() {
        return isExitRequested;
    }

    /**
     * Executes a parsed command and returns its response.
     *
     * @param input full command entered by the user.
     * @param commandType parsed command type.
     * @return response for the command.
     * @throws KiboException if the command is invalid or storage cannot be updated.
     */
    private String executeCommand(String input, CommandType commandType) throws KiboException {
        assert commandType != null : "A parsed command type must not be null";
        assert commandType != CommandType.UNKNOWN
                : "Unknown commands must be rejected before execution";

        switch (commandType) {
            case BYE:
                parser.ensureNoArguments(input, commandType);
                isExitRequested = true;
                return ui.getGoodbyeMessage();
            case LIST:
                parser.ensureNoArguments(input, commandType);
                return ui.getTaskListMessage(tasks);
            case FIND:
                String keyword = parser.parseFindKeyword(input);
                return ui.getMatchingTasksMessage(tasks.find(keyword));
            case SCHEDULE:
                LocalDate date = parser.parseScheduleDate(input);
                return ui.getScheduleMessage(tasks.findScheduledOn(date), date);
            case MARK:
                return markTask(input, commandType);
            case UNMARK:
                return unmarkTask(input, commandType);
            case DELETE:
                return deleteTask(input, commandType);
            case TODO:
                return addTask(parser.parseTodo(input));
            case DEADLINE:
                return addTask(parser.parseDeadline(input));
            case EVENT:
                return addTask(parser.parseEvent(input));
            default:
                throw new IllegalStateException("Unexpected command type");
        }
    }

    /**
     * Marks one task as done and saves the updated list.
     *
     * @param input full mark command.
     * @param commandType mark command type.
     * @return confirmation message.
     * @throws KiboException if the task number is invalid or storage cannot be updated.
     */
    private String markTask(String input, CommandType commandType) throws KiboException {
        int taskIndex = parser.parseTaskIndex(input, commandType, tasks.size());
        Task task = tasks.get(taskIndex);
        boolean wasDone = task.isDone();
        task.markAsDone();
        try {
            Storage.save(tasks);
        } catch (StorageException exception) {
            if (!wasDone) {
                task.markAsNotDone();
            }
            throw exception;
        }
        return ui.getTaskMarkedMessage(task);
    }

    /**
     * Marks one task as not done and saves the updated list.
     *
     * @param input full unmark command.
     * @param commandType unmark command type.
     * @return confirmation message.
     * @throws KiboException if the task number is invalid or storage cannot be updated.
     */
    private String unmarkTask(String input, CommandType commandType) throws KiboException {
        int taskIndex = parser.parseTaskIndex(input, commandType, tasks.size());
        Task task = tasks.get(taskIndex);
        boolean wasDone = task.isDone();
        task.markAsNotDone();
        try {
            Storage.save(tasks);
        } catch (StorageException exception) {
            if (wasDone) {
                task.markAsDone();
            }
            throw exception;
        }
        return ui.getTaskUnmarkedMessage(task);
    }

    /**
     * Removes one task and saves the updated list.
     *
     * @param input full delete command.
     * @param commandType delete command type.
     * @return confirmation message.
     * @throws KiboException if the task number is invalid or storage cannot be updated.
     */
    private String deleteTask(String input, CommandType commandType) throws KiboException {
        int taskIndex = parser.parseTaskIndex(input, commandType, tasks.size());
        Task removedTask = tasks.remove(taskIndex);
        try {
            Storage.save(tasks);
        } catch (StorageException exception) {
            tasks.add(taskIndex, removedTask);
            throw exception;
        }
        return ui.getTaskDeletedMessage(removedTask, tasks.size());
    }

    /**
     * Stores a task and returns the standard confirmation message.
     *
     * @param task task to add.
     * @return confirmation message.
     * @throws StorageException if the updated task list cannot be saved.
     */
    private String addTask(Task task) throws StorageException {
        tasks.add(task);
        try {
            Storage.save(tasks);
        } catch (StorageException exception) {
            tasks.remove(tasks.size() - 1);
            throw exception;
        }
        return ui.getTaskAddedMessage(task, tasks.size());
    }
}
