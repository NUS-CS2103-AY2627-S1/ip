package tasktracker;

import java.util.List;

import tasktracker.command.CommandType;
import tasktracker.exception.TaskTrackerException;
import tasktracker.parser.Parser;
import tasktracker.storage.Storage;
import tasktracker.task.Deadline;
import tasktracker.task.Event;
import tasktracker.task.Fixed;
import tasktracker.task.TaskList;
import tasktracker.task.ToDo;
import tasktracker.ui.Message;
import tasktracker.ui.UserInterface;

/**
 * Serves as the entry point and main controller for the application.
 * Manages task workflows, input routing, file persistence, and conversational state.
 */
public class TaskTracker {
    private static final String DEFAULT_FILE_PATH = "./data/tasks.txt";
    private final UserInterface ui;
    private final Storage storage;
    private TaskList taskList;
    private CommandType lastCommandType;
    private boolean isErrorResponse = false;

    /**
     * Initializes a new {@code TaskTracker} instance with default UI, storage, and task list.
     */
    public TaskTracker() {
        this.ui = new UserInterface();
        this.storage = new Storage(DEFAULT_FILE_PATH);
        try {
            this.taskList = new TaskList(storage.load());
        } catch (TaskTrackerException e) {
            this.taskList = new TaskList();
        }
    }

    /**
     * Retrieves the welcome message for the user interface.
     *
     * @return The formatted welcome message string.
     */
    public String getWelcomeMessage() {
        return ui.getWelcomeMessage();
    }

    /**
     * Checks if any corrupted lines were encountered and skipped during startup storage loading.
     *
     * @return {@code true} if there are startup load warnings, {@code false} otherwise.
     */
    public boolean hasStartupWarnings() {
        return !storage.getLoadWarnings().isEmpty();
    }

    /**
     * Formats and returns the warning messages from startup storage loading.
     *
     * @return A formatted warning message string detailing skipped records.
     */
    public String getStartupWarnings() {
        List<String> warnings = storage.getLoadWarnings();
        return Message.getLoadWarningMessage(warnings);
    }

    /**
     * Returns the command type of the most recently processed command.
     *
     * @return The last executed {@link CommandType}.
     */
    public CommandType getLastCommandType() {
        return lastCommandType;
    }

    /**
     * Checks if the most recent command execution resulted in an exception.
     *
     * @return {@code true} if the last response was an error, {@code false} otherwise.
     */
    public boolean isErrorResponse() {
        return isErrorResponse;
    }

    /**
     * Processes input from the GUI and returns the bot's response message.
     *
     * @param input User input string from the GUI.
     * @return Response string to display in the chat dialog.
     */
    public String getResponse(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "";
        }

        try {
            isErrorResponse = false;
            return processCommand(input.trim());
        } catch (TaskTrackerException e) {
            isErrorResponse = true;
            return e.getMessage();
        }
    }

    /**
     * Processes a single user input command, performs task mutations, and returns output.
     *
     * @param input Cleaned user input string.
     * @return The response message to show to the user.
     * @throws TaskTrackerException If input parsing fails or command is unrecognized.
     */
    private String processCommand(String input) throws TaskTrackerException {
        String[] parts = input.split(" ", 2);
        String commandWord = parts[0].toLowerCase();
        String argument = parts.length > 1 ? parts[1].trim() : "";

        CommandType command = CommandType.from(commandWord);
        this.lastCommandType = command;

        manageUndoState(command);

        String response = executeCommand(command, argument);
        saveIfMutated(command);

        return response;
    }

    /**
     * Invalidates the cached deletion history when an intervening action is executed.
     * <p>
     * Clears any restorable task if the executed command is neither {@code UNDO} nor {@code DELETE}.
     * Note that subsequent {@code DELETE} commands overwrite the previously cached deletion.
     *
     * @param command The command type currently executing.
     */
    private void manageUndoState(CommandType command) {
        if (command != CommandType.UNDO && command != CommandType.DELETE) {
            taskList.clearUndoHistory();
        }
    }

    /**
     * Executes the specific operation associated with the parsed command type.
     *
     * @param command The type of command to execute.
     * @param argument The argument string passed alongside the command keyword.
     * @return The resulting output message after execution.
     * @throws TaskTrackerException If argument validation fails or task execution errors occur.
     */
    private String executeCommand(CommandType command, String argument) throws TaskTrackerException {
        switch (command) {
            case BYE:
                return ui.getGoodbyeMessage();
            case LIST:
                return taskList.getFormattedList();
            case MARK:
                return handleTaskStatusChange(argument, true);
            case UNMARK:
                return handleTaskStatusChange(argument, false);
            case TODO:
                return handleAddToDo(argument);
            case DEADLINE:
                return handleAddDeadline(argument);
            case EVENT:
                return handleAddEvent(argument);
            case FIXED:
                return handleAddFixedDurationTask(argument);
            case DELETE:
                return handleDeleteTask(argument);
            case UNDO:
                return handleUndoTask();
            case FIND:
                return taskList.findTasks(Parser.parseFind(argument));
            case HELP:
                return Message.MSG_HELP;
            default:
                throw new TaskTrackerException(Message.ERR_UNKNOWN_COMMAND);
        }
    }

    /**
     * Updates the status of a task to done or undone.
     *
     * @param argument Raw task index string from input.
     * @param isDone {@code true} to mark complete, {@code false} to unmark.
     * @return Confirmation message of the task status update.
     * @throws TaskTrackerException If the index is invalid or out of bounds.
     */
    private String handleTaskStatusChange(String argument, boolean isDone) throws TaskTrackerException {
        int index = Parser.parseIndex(argument);
        return taskList.setTaskStatus(index, isDone);
    }

    /**
     * Creates and adds a new {@code ToDo} task.
     *
     * @param argument Raw task argument string.
     * @return Confirmation message of the added task.
     * @throws TaskTrackerException If argument validation fails.
     */
    private String handleAddToDo(String argument) throws TaskTrackerException {
        ToDo toDo = Parser.parseToDo(argument);
        return taskList.add(toDo);
    }

    /**
     * Creates and adds a new {@code Deadline} task.
     *
     * @param argument Raw task argument string with due date.
     * @return Confirmation message of the added task.
     * @throws TaskTrackerException If argument or date validation fails.
     */
    private String handleAddDeadline(String argument) throws TaskTrackerException {
        Deadline deadline = Parser.parseDeadline(argument);
        return taskList.add(deadline);
    }

    /**
     * Creates and adds a new {@code Event} task.
     *
     * @param argument Raw task argument string with time range.
     * @return Confirmation message of the added task.
     * @throws TaskTrackerException If argument or date-time range validation fails.
     */
    private String handleAddEvent(String argument) throws TaskTrackerException {
        Event event = Parser.parseEvent(argument);
        return taskList.add(event);
    }

    /**
     * Creates and adds a new {@code Fixed} duration task.
     *
     * @param argument Raw task argument string with duration details.
     * @return Confirmation message of the added task.
     * @throws TaskTrackerException If argument validation fails.
     */
    private String handleAddFixedDurationTask(String argument) throws TaskTrackerException {
        Fixed fixedTask = Parser.parseFixedDurationTask(argument);
        return taskList.add(fixedTask);
    }

    /**
     * Deletes a task from the list by its index.
     *
     * @param argument Raw task index string from input.
     * @return Confirmation message of the removed task.
     * @throws TaskTrackerException If index is invalid or out of bounds.
     */
    private String handleDeleteTask(String argument) throws TaskTrackerException {
        int index = Parser.parseIndex(argument);
        return taskList.deleteTask(index);
    }

    /**
     * Restores the most recently deleted task.
     *
     * @return Confirmation message of the restored task.
     * @throws TaskTrackerException If there is no task available to restore.
     */
    private String handleUndoTask() throws TaskTrackerException {
        return taskList.undoDelete();
    }

    /**
     * Persists tasks to disk if the command alters the task list state.
     *
     * @param command The command type that was executed.
     * @throws TaskTrackerException If disk persistence fails.
     */
    private void saveIfMutated(CommandType command) throws TaskTrackerException {
        switch (command) {
            case MARK:
            case UNMARK:
            case TODO:
            case DEADLINE:
            case EVENT:
            case FIXED:
            case DELETE:
            case UNDO:
                storage.save(taskList.getTasks());
                break;
            default:
                break;
        }
    }

    /**
     * Runs the legacy CLI main command processing loop until an exit command is received.
     */
    public void run() {
        ui.showWelcome();

        if (hasStartupWarnings()) {
            ui.showMessage(getStartupWarnings());
        }

        while (true) {
            String input = ui.readCommand().trim();
            if (input.equalsIgnoreCase("bye")) {
                ui.showGoodbye();
                break;
            }
            String response = getResponse(input);
            ui.showMessage(response);
        }
    }

    /**
     * Starts the Task Tracker application.
     *
     * @param args Command-line arguments passed during startup.
     */
    public static void main(String[] args) {
        new TaskTracker().run();
    }
}
