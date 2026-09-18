package eden;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import eden.command.Command;
import eden.command.ExitCommand;
import eden.command.FindCommand;
import eden.command.ListCommand;
import eden.exception.EdenException;
import eden.parser.CommandType;
import eden.storage.Storage;
import eden.task.Deadline;
import eden.task.Event;
import eden.task.Task;
import eden.task.TaskList;
import eden.task.Todo;
import eden.ui.Ui;

/**
 * Runs the Eden task manager and coordinates its user interface and storage.
 */
public class Eden {
    private static final String DEADLINE_DATE_ERROR =
            "OOPS!!! Please enter the deadline date as yyyy-MM-dd "
            + "(e.g., 2019-12-02).";
    private static final String EVENT_FORMAT_ERROR =
            "OOPS!!! Please enter an event as: "
            + "event DESCRIPTION /from START /to END.";
    private static final String TASK_NUMBER_ERROR =
            "OOPS!!! Please enter a valid task number.";
    private static final String UNKNOWN_COMMAND_ERROR =
            "OOPS!!! I'm sorry, but I don't know what that means :-(";

    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;

    /**
     * Records whether Eden loaded its data successfully and can process commands.
     */
    private final boolean isReady;
    private final String loadingError;

    private boolean isExit;
    private boolean isLastResponseError;

    /**
     * Creates Eden and attempts to load tasks from the given data file.
     * If loading fails, Eden reports the loading error and will not process commands.
     *
     * @param filePath path to the task data file.
     */
    public Eden(Path filePath) {
        ui = new Ui();
        storage = new Storage(filePath);

        TaskList loadedTasks = new TaskList();
        boolean isLoadedSuccessfully = false;
        String loadError = null;

        try {
            loadedTasks = new TaskList(storage.load());
            isLoadedSuccessfully = true;
        } catch (EdenException exception) {
            loadError = exception.getMessage();
        }

        tasks = loadedTasks;
        isReady = isLoadedSuccessfully;
        loadingError = loadError;
        isLastResponseError = !isReady;
        assert (isReady && loadingError == null) || (!isReady && loadingError != null)
                : "Eden must start either ready or with a loading error";
    }

    /**
     * Greets the user and processes commands until the user exits, provided that
     * the task data loaded successfully. Otherwise, this method reports the loading
     * error and returns.
     */
    public void run() {
        if (!isReady) {
            ui.showResponse(loadingError);
            return;
        }

        ui.showWelcome();
        while (!isExit) {
            ui.showResponse(getResponse(ui.readCommand()));
        }
    }

    /**
     * Returns Eden's startup message for a graphical or other non-console interface.
     *
     * @return greeting, or the loading error if stored data could not be loaded.
     */
    public String getWelcomeMessage() {
        return isReady ? ui.getWelcomeMessage() : loadingError;
    }

    /**
     * Processes one user command and returns the response without console decoration.
     *
     * @param fullCommand full command entered by the user.
     * @return response suitable for a console or graphical interface.
     */
    public String getResponse(String fullCommand) {
        if (!isReady) {
            isLastResponseError = true;
            return loadingError;
        }

        try {
            String response = processCommand(fullCommand.trim());
            isLastResponseError = false;
            return response;
        } catch (EdenException exception) {
            return recordError(exception.getMessage());
        } catch (NumberFormatException | IndexOutOfBoundsException exception) {
            return recordError(TASK_NUMBER_ERROR);
        }
    }

    /**
     * Returns whether the last response reports an error.
     *
     * @return true if the most recent response was an error message.
     */
    public boolean isLastResponseError() {
        return isLastResponseError;
    }

    /**
     * Returns whether the most recently processed command requested an exit.
     *
     * @return true after a successful bye command.
     */
    public boolean isExit() {
        return isExit;
    }

    /**
     * Routes a normalized command to the matching operation.
     */
    private String processCommand(String fullCommand) throws EdenException {
        CommandType commandType = CommandType.from(fullCommand);
        switch (commandType) {
            case BYE:
                requireNoArguments(fullCommand, "bye");
                return executeCommand(new ExitCommand());
            case LIST:
                requireNoArguments(fullCommand, "list");
                return executeCommand(new ListCommand(tasks));
            case FIND:
                String keyword = fullCommand.substring("find".length()).trim();
                return executeCommand(new FindCommand(keyword, tasks));
            case MARK:
                return markTask(fullCommand);
            case UNMARK:
                return unmarkTask(fullCommand);
            case TODO:
                return addTodo(fullCommand);
            case DEADLINE:
                return addDeadline(fullCommand);
            case EVENT:
                return addEvent(fullCommand);
            case DELETE:
                return deleteTask(fullCommand);
            default:
                throw new EdenException(UNKNOWN_COMMAND_ERROR);
        }
    }

    /**
     * Executes a command object and remembers whether it exits Eden.
     */
    private String executeCommand(Command command) throws EdenException {
        String response = command.execute(ui);
        assert response != null : "Every command must return a displayable response";
        isExit = command.isExit();
        return response;
    }

    /**
     * Marks the selected task and persists the change.
     */
    private String markTask(String fullCommand) throws EdenException {
        int taskNumber = parseTaskNumber(fullCommand, "mark");
        boolean wasMarked = tasks.asList().get(taskNumber - 1).isMarked();
        Task task = tasks.mark(taskNumber);
        assert task.isMarked() : "A task returned by mark must be marked";
        saveWithStatusRollback(task, wasMarked);
        return ui.formatTaskMarked(task);
    }

    /**
     * Unmarks the selected task and persists the change.
     */
    private String unmarkTask(String fullCommand) throws EdenException {
        int taskNumber = parseTaskNumber(fullCommand, "unmark");
        boolean wasMarked = tasks.asList().get(taskNumber - 1).isMarked();
        Task task = tasks.unmark(taskNumber);
        assert !task.isMarked() : "A task returned by unmark must be unmarked";
        saveWithStatusRollback(task, wasMarked);
        return ui.formatTaskUnmarked(task);
    }

    /**
     * Creates a todo from its command text.
     */
    private String addTodo(String fullCommand) throws EdenException {
        String description = fullCommand.substring("todo".length()).trim();
        if (description.isEmpty()) {
            throw new EdenException("OOPS!!! The description of a todo cannot be empty.");
        }
        return addTask(new Todo(description));
    }

    /**
     * Creates a deadline from its description and ISO date.
     */
    private String addDeadline(String fullCommand) throws EdenException {
        String details = fullCommand.substring("deadline".length()).trim();
        String[] parts = details.split("(?i)(?:^|\\s+)/by\\s+", 2);
        String description = parts[0].trim();
        if (description.isEmpty()) {
            throw new EdenException("OOPS!!! The description of a deadline cannot be empty.");
        }
        if (parts.length < 2 || parts[1].isBlank()) {
            throw new EdenException(DEADLINE_DATE_ERROR);
        }

        LocalDate dueDate = parseDeadlineDate(parts[1].trim());
        return addTask(new Deadline(description, dueDate));
    }

    /**
     * Creates an event from its description, start, and end text.
     */
    private String addEvent(String fullCommand) throws EdenException {
        String details = fullCommand.substring("event".length()).trim();
        String[] fromParts = details.split("(?i)\\s+/from\\s+", 2);
        if (fromParts.length < 2) {
            throw new EdenException(EVENT_FORMAT_ERROR);
        }

        String[] toParts = fromParts[1].split("(?i)\\s+/to\\s+", 2);
        String description = fromParts[0].trim();
        if (description.isEmpty()) {
            throw new EdenException("OOPS!!! The description of an event cannot be empty.");
        }
        if (toParts.length < 2 || toParts[0].isBlank() || toParts[1].isBlank()) {
            throw new EdenException(EVENT_FORMAT_ERROR);
        }

        return addTask(new Event(description, toParts[0].trim(), toParts[1].trim()));
    }

    /**
     * Adds and persists one newly parsed task.
     */
    private String addTask(Task task) throws EdenException {
        int previousTaskCount = tasks.size();
        List<Task> updatedTasks = new ArrayList<>(tasks.asList());
        updatedTasks.add(task);
        storage.save(updatedTasks);

        tasks.add(task);
        assert tasks.size() == previousTaskCount + 1
                : "Adding one task must increase the task count by one";
        return ui.formatTaskAdded(task, tasks.size());
    }

    /**
     * Deletes the selected task and persists the change.
     */
    private String deleteTask(String fullCommand) throws EdenException {
        int taskNumber = parseTaskNumber(fullCommand, "delete");
        int previousTaskCount = tasks.size();
        List<Task> updatedTasks = new ArrayList<>(tasks.asList());
        Task task = updatedTasks.remove(taskNumber - 1);
        storage.save(updatedTasks);

        Task deletedTask = tasks.delete(taskNumber);
        assert deletedTask == task : "The persisted and deleted tasks must be identical";
        assert tasks.size() == previousTaskCount - 1
                : "Deleting one task must decrease the task count by one";
        return ui.formatTaskDeleted(task, tasks.size());
    }

    /**
     * Saves a status change and restores the previous status if saving fails.
     */
    private void saveWithStatusRollback(Task task, boolean wasMarked) throws EdenException {
        try {
            storage.save(tasks.asList());
        } catch (EdenException exception) {
            if (wasMarked) {
                task.mark();
            } else {
                task.unmark();
            }
            throw exception;
        }
    }

    /**
     * Rejects extra text supplied to a command that accepts no arguments.
     */
    private void requireNoArguments(String fullCommand, String commandWord)
            throws EdenException {
        if (!fullCommand.equalsIgnoreCase(commandWord)) {
            throw new EdenException("OOPS!!! The " + commandWord
                    + " command does not accept extra details.");
        }
    }

    /**
     * Records and returns a user-facing error response.
     */
    private String recordError(String message) {
        isLastResponseError = true;
        return message;
    }

    /**
     * Parses the one-based task number after a command word.
     */
    private int parseTaskNumber(String fullCommand, String commandWord) throws EdenException {
        String numberText = fullCommand.substring(commandWord.length()).trim();
        if (numberText.isEmpty()) {
            throw new EdenException(TASK_NUMBER_ERROR);
        }
        return Integer.parseInt(numberText);
    }

    /**
     * Parses a deadline date written in the ISO {@code yyyy-MM-dd} format.
     *
     * @param dateText deadline date entered by the user.
     * @return parsed date.
     * @throws EdenException if the text is not a valid ISO date.
     */
    private LocalDate parseDeadlineDate(String dateText) throws EdenException {
        try {
            return LocalDate.parse(dateText, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException exception) {
            throw new EdenException(DEADLINE_DATE_ERROR, exception);
        }
    }

    /**
     * Starts Eden using {@code data/eden.txt}, resolved relative to the process's
     * working directory.
     *
     * @param args command-line arguments, which Eden does not use.
     */
    public static void main(String[] args) {
        new Eden(Path.of("data", "eden.txt")).run();
    }
}
