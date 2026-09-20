package woofer;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import woofer.exception.WooferException;
import woofer.parser.Parser;
import woofer.storage.Storage;
import woofer.task.Task;
import woofer.task.TaskList;

/**
 * Provides the application logic shared by Woofer's text and graphical user interfaces.
 */
public class WooferService {
    private final Storage storage;
    private final Parser parser;
    private TaskList taskList;
    private boolean loadingError;
    private UndoAction undoAction;
    /** Whether the latest save failed, so interfaces can highlight its warning. */
    private boolean savingError;

    /**
     * Creates a service and loads the saved tasks from disk.
     */
    public WooferService() {
        this(new Storage());
    }

    /**
     * Creates a service with a chosen storage location, including isolated files for tests.
     *
     * @param storage storage used to load and save tasks.
     */
    public WooferService(Storage storage) {
        this.storage = storage;
        parser = new Parser();
        taskList = loadTaskList();
    }

    /**
     * Returns whether the saved task file could not be loaded.
     *
     * @return true when Woofer started with an empty list because loading failed.
     */
    public boolean hasLoadingError() {
        return loadingError;
    }

    /**
     * Explains startup failure and how to protect and recover the saved tasks.
     *
     * @return warning shown by both interfaces when loading fails.
     */
    public String getLoadingWarning() {
        return "Could not load saved tasks. Check data/woofer.txt for invalid records or access problems. "
                + "This session starts empty; saving is disabled to protect the original file. "
                + "Repair the file or its permissions and restart Woofer.";
    }

    /**
     * Processes one command and returns the text that should be shown to the user.
     *
     * @param command command entered by the user.
     * @return the command response and whether the application should exit.
     * @throws WooferException when the command is invalid.
     */
    public Response execute(String command) throws WooferException {
        command = parser.normalizeCommand(command);
        Parser.CommandType commandType = parser.parseCommandType(command);
        return switch (commandType) {
            case EXIT -> new Response("Woof woof! Time for a nap. See you on our next adventure!", true);
            case UNDO -> taskResponse(undoTask());
            case LIST -> new Response(listTasks(), false);
            case FIND -> new Response(findTasks(command), false);
            case DELETE -> taskResponse(deleteTask(command));
            case MARK -> taskResponse(markTask(command, true));
            case UNMARK -> taskResponse(markTask(command, false));
            case ADD -> taskResponse(addTask(command));
            default -> throw new IllegalStateException("Unsupported command type: " + commandType);
        };
    }

    /**
     * Loads saved tasks, falling back to an empty list when storage cannot be read.
     *
     * @return the loaded tasks, or an empty list when loading fails.
     */
    private TaskList loadTaskList() {
        try {
            return storage.load();
        } catch (IOException | SecurityException exception) {
            loadingError = true;
            return new TaskList();
        }
    }

    /**
     * Formats all tasks for display.
     *
     * @return a formatted list response.
     */
    private String listTasks() {
        return formatTasks(
                "Woof! I fetched your task list (" + taskList.size() + "):",
                taskList.getTasks(),
                "Nothing on the list yet! Try: todo walk the dog");
    }

    /**
     * Finds tasks matching a keyword and formats the result.
     *
     * @param command find command containing the keyword.
     * @return a formatted search response.
     * @throws WooferException when no keyword is provided.
     */
    private String findTasks(String command) throws WooferException {
        String keyword = parser.parseFindKeyword(command);
        return formatTasks(
                "Sniff sniff! Here are the matching tasks:",
                taskList.findTasks(keyword),
                "No matching tasks sniffed out. Try another keyword!");
    }

    /**
     * Adds a task parsed from a command.
     *
     * @param command add command describing the task.
     * @return a confirmation response.
     * @throws WooferException when the task is malformed or the list is full.
     */
    private String addTask(String command) throws WooferException {
        Task task = parser.parseTask(command);
        if (!taskList.addTask(task)) {
            throw new WooferException("My task basket is full! Delete a task before adding another.");
        }

        int addedTaskNumber = taskList.size();
        undoAction = () -> taskList.deleteTask(addedTaskNumber);

        String response = "Woof! I'll keep an eye on this task:\n"
                + "  " + task.getDisplayText() + "\n"
                + "Now you have " + taskList.size() + " tasks in the list.";
        return withSavingWarning(response);
    }

    /**
     * Marks or unmarks a task parsed from a command.
     *
     * @param command mark or unmark command containing a task number.
     * @param isDone whether the task should be marked as done.
     * @return a confirmation response.
     * @throws WooferException when the task number is invalid or out of range.
     */
    private String markTask(String command, boolean isDone) throws WooferException {
        Parser.CommandType commandType = parser.parseCommandType(command);
        int taskNumber = parser.parseTaskNumber(command, commandType);
        Task task = getTaskOrThrow(taskNumber);
        boolean previousDoneState = task.isDone();

        if (isDone) {
            task.markAsDone();
        } else {
            task.markAsNotDone();
        }

        undoAction = () -> {
            if (previousDoneState) {
                task.markAsDone();
            } else {
                task.markAsNotDone();
            }
        };

        String response = isDone
                ? "High paw! This task is done:\n  [X] " + task.getDescription()
                : "Back on the trail! This task is not done yet:\n  [ ] " + task.getDescription();
        return withSavingWarning(response);
    }

    /**
     * Deletes a task parsed from a command.
     *
     * @param command delete command containing a task number.
     * @return a confirmation response.
     * @throws WooferException when the task number is invalid or out of range.
     */
    private String deleteTask(String command) throws WooferException {
        int taskNumber = parser.parseTaskNumber(command, Parser.CommandType.DELETE);
        Task task = getTaskOrThrow(taskNumber);
        taskList.deleteTask(taskNumber);
        undoAction = () -> taskList.insertTask(taskNumber, task);

        String response = "All clear! I've removed this task:\n"
                + "  " + task.getDisplayText() + "\n"
                + "Now you have " + taskList.size() + " tasks in the list.";
        return withSavingWarning(response);
    }

    /**
     * Reverses the latest successful task-changing command.
     *
     * @return a confirmation response.
     * @throws WooferException when there is no command to undo.
     */
    private String undoTask() throws WooferException {
        if (undoAction == null) {
            throw new WooferException("No previous task change to undo. Our trail starts here!");
        }

        UndoAction action = undoAction;
        undoAction = null;
        action.undo();
        return withSavingWarning("Back we go! I undid the previous command.");
    }

    /**
     * Returns a task or reports the user-facing error for an invalid number.
     *
     * @param taskNumber one-based task number.
     * @return the task at the requested position.
     * @throws WooferException when the task number is outside the list.
     */
    private Task getTaskOrThrow(int taskNumber) throws WooferException {
        Task task = taskList.getTask(taskNumber);
        if (task == null) {
            throw new WooferException("I couldn't sniff out that task number. Use list to see your tasks.");
        }
        return task;
    }

    /**
     * Saves the current task list and appends a warning if saving fails.
     *
     * @param response response to return when saving succeeds.
     * @return the response, possibly with a saving warning.
     */
    private String withSavingWarning(String response) {
        savingError = !saveTaskList();
        if (!savingError) {
            return response;
        }
        return response + "\nWarning: Could not save your tasks. Changes are only in memory. "
                + "Check the data file and folder permissions before closing Woofer.";
    }

    /**
     * Saves the current task list.
     *
     * @return true when saving succeeds.
     */
    private boolean saveTaskList() {
        try {
            storage.save(taskList);
            return true;
        } catch (IOException | SecurityException exception) {
            return false;
        }
    }

    /**
     * Formats a heading followed by numbered tasks.
     *
     * @param heading heading shown before the tasks.
     * @param tasks tasks to format.
     * @param emptyMessage message shown when there are no tasks.
     * @return the formatted task output.
     */
    private String formatTasks(String heading, List<Task> tasks, String emptyMessage) {
        if (tasks.isEmpty()) {
            return heading + "\n" + emptyMessage;
        }

        String numberedTasks = IntStream.range(0, tasks.size())
                .mapToObj(index -> (index + 1) + "." + tasks.get(index).getDisplayText())
                .collect(Collectors.joining("\n"));
        return heading + "\n" + numberedTasks;
    }

    /**
     * Represents the result of processing one command.
     *
     * @param message text to show in the conversation.
     * @param exits whether the command requests application exit.
     * @param warning whether a save failure needs attention.
     */
    public record Response(String message, boolean exits, boolean warning) {
        /**
         * Creates an ordinary response without a storage warning.
         *
         * @param message response text.
         * @param exits whether to stop accepting commands.
         */
        public Response(String message, boolean exits) {
            this(message, exits, false);
        }
    }

    /**
     * Attaches the latest saving status to a task-changing response.
     *
     * @param message result of the task change and save attempt.
     * @return response with explicit warning status for the GUI.
     */
    private Response taskResponse(String message) {
        return new Response(message, false, savingError);
    }

    /** Reverses one previously executed task-changing command. */
    @FunctionalInterface
    private interface UndoAction {
        void undo();
    }
}
