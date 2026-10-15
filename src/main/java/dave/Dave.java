package dave;

import java.util.ArrayList;

import dave.command.Command;
import dave.exception.DaveCommandException;
import dave.parser.Parser;
import dave.parser.SnoozeRequest;
import dave.storage.Storage;
import dave.task.IndexedTask;
import dave.task.Task;
import dave.task.TaskList;
import dave.ui.Ui;

/**
 * Represents the main entry point for the Dave chatbot application.
 * Manages user interactions, command execution, and task persistence.
 */
public class Dave {

    /** Default path to the data file used for persistent storage. */
    private static final String DEFAULT_FILE_PATH = "data/dave.txt";

    /** User interface handler responsible for all input and output. */
    private final Ui ui;
    /** Storage handler responsible for loading and saving tasks on disk. */
    private final Storage storage;
    /** List of tasks currently managed by the chatbot. */
    private TaskList tasks;
    /** Flag indicating whether the chatbot application should terminate. */
    private boolean isExit;
    /** Warning message generated during application startup or task loading. */
    private String startupNotice;

    /**
     * Constructs a new Dave chatbot application instance with the default data file path.
     */
    public Dave() {
        this(DEFAULT_FILE_PATH);
    }

    /**
     * Constructs a new Dave chatbot application instance with the specified data file path.
     *
     * @param filePath Relative or absolute path to the data file.
     */
    public Dave(String filePath) {
        assert filePath != null && !filePath.trim().isEmpty() : "File path for Dave storage cannot be null or empty";

        this.ui = new Ui();
        this.storage = new Storage(filePath);
        this.isExit = false;
        try {
            this.tasks = new TaskList(this.storage.load());
            int corrupted = this.storage.getCorruptedLineCount();
            if (corrupted > 0) {
                this.startupNotice = String.format(
                        "Warning: %d corrupted task entry(ies) in data file could not be read and were skipped.",
                        corrupted);
            }
        } catch (DaveCommandException e) {
            this.ui.showLoadingError(e.getMessage());
            this.startupNotice = "Warning: " + e.getMessage();
            this.tasks = new TaskList();
        }

        assert this.tasks != null : "TaskList must be initialized after constructor execution";
    }

    /**
     * Returns whether the chatbot application has received an exit command.
     *
     * @return True if Dave has been signaled to exit, false otherwise.
     */
    public boolean isExit() {
        return this.isExit;
    }

    /**
     * Returns the welcome greeting message from the chatbot, including any startup notices.
     *
     * @return Welcome greeting string.
     */
    public String getWelcome() {
        String welcome = this.ui.formatWelcome();
        if (this.startupNotice != null && !this.startupNotice.isEmpty()) {
            return welcome + "\n\n" + this.startupNotice;
        }
        return welcome;
    }

    /**
     * Determines whether the provided chatbot response represents an error message.
     *
     * @param response Chatbot response string to inspect.
     * @return True if the response indicates an error or warning, false otherwise.
     */
    public boolean isErrorResponse(String response) {
        if (response == null || response.trim().isEmpty()) {
            return false;
        }

        return response.startsWith("Pardon, Sire!")
                || response.startsWith("Hold, Sire!")
                || response.startsWith("Forgive me")
                || response.startsWith("Warning:");
    }

    /**
     * Generates a response string for a given user command input.
     *
     * @param input Raw command input from the user.
     * @return Formatted response string to display to the user.
     */
    public String getResponse(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "";
        }

        assert this.tasks != null : "TaskList must be initialized before processing any command";

        try {
            Command command = Parser.parseCommand(input);
            String arguments = Parser.parseArguments(input);

            assert command != null : "Parser.parseCommand must always return a valid Command enum";

            switch (command) {
                case BYE:
                    this.isExit = true;
                    return this.ui.formatGoodbye();
                case LIST:
                    return this.ui.formatTaskList(this.tasks.asList());
                case FIND:
                    return findTasks(arguments);
                case MARK:
                    return updateTaskStatus(arguments, true);
                case UNMARK:
                    return updateTaskStatus(arguments, false);
                case TODO:
                    return addTask(Parser.parseTodo(arguments));
                case DEADLINE:
                    return addTask(Parser.parseDeadline(arguments));
                case EVENT:
                    return addTask(Parser.parseEvent(arguments));
                case DELETE:
                    return deleteTask(arguments);
                case SNOOZE:
                    return snoozeTask(arguments);
                case UNKNOWN:
                    // Fallthrough
                default:
                    return "Forgive me, my Liege, but that command is foreign to my training.";
            }
        } catch (DaveCommandException e) {
            return e.getMessage();
        }
    }

    /**
     * Runs the Dave chatbot application, reading and processing user commands until exit.
     */
    public void run() {
        this.ui.showWelcome();

        while (!this.isExit) {
            String userIn = this.ui.readCommand();

            if (userIn.isEmpty()) {
                continue;
            }

            String response = getResponse(userIn);
            this.ui.showMessage(response);
        }
    }

    /**
     * Finds tasks matching the specified keyword and returns the formatted result.
     *
     * @param arguments User input arguments containing the search keyword.
     * @return Formatted string containing matching tasks.
     * @throws DaveCommandException If parsing the keyword fails.
     */
    private String findTasks(String arguments) throws DaveCommandException {
        String keyword = Parser.parseFind(arguments);
        assert keyword != null && !keyword.isEmpty() : "Parser.parseFind must return non-empty keyword";
        ArrayList<IndexedTask> matchingTasks = this.tasks.find(keyword);
        return this.ui.formatMatchingTasks(matchingTasks);
    }

    /**
     * Deletes a task from the task list according to the specified 1-based index string.
     *
     * @param arguments User input arguments containing the index of the task to be removed.
     * @return Formatted string confirming task removal.
     * @throws DaveCommandException If parsing the index or deleting the task fails.
     */
    private String deleteTask(String arguments) throws DaveCommandException {
        int index = Parser.parseIndex(arguments);
        assert index >= 0 : "Parser.parseIndex must yield a non-negative index value";
        Task removedTask = this.tasks.delete(index);
        assert removedTask != null : "TaskList.delete must return the removed task";
        saveTasks();
        return this.ui.formatTaskDeleted(removedTask);
    }

    /**
     * Snoozes or reschedules a task according to the provided argument specifications.
     *
     * @param arguments User input arguments containing the task index and snooze specifications.
     * @return Formatted string confirming task snooze.
     * @throws DaveCommandException If parsing fails, the task is invalid, or the task cannot be snoozed.
     */
    private String snoozeTask(String arguments) throws DaveCommandException {
        SnoozeRequest snoozeRequest = Parser.parseSnooze(arguments);
        assert snoozeRequest != null : "Parser.parseSnooze must return a non-null SnoozeRequest";

        Task task = this.tasks.get(snoozeRequest.getIndex());
        assert task != null : "TaskList.get must return a valid Task instance";

        snoozeRequest.applyTo(task);
        saveTasks();
        return this.ui.formatTaskSnoozed(task);
    }

    /**
     * Updates the completion status of a task based on the specified 1-based index string.
     *
     * @param arguments User input arguments containing the index of the task to be updated.
     * @param isComplete True if the task should be marked as completed, false otherwise.
     * @return Formatted string confirming task status update.
     * @throws DaveCommandException If parsing the index or updating status fails.
     */
    private String updateTaskStatus(String arguments, boolean isComplete) throws DaveCommandException {
        int index = Parser.parseIndex(arguments);
        assert index >= 0 : "Parser.parseIndex must yield a non-negative index value";
        Task task = this.tasks.setDone(index, isComplete);
        assert task != null : "TaskList.setDone must return the updated task";
        assert task.isDone() == isComplete : "Task completion status must match the requested status";
        saveTasks();
        return this.ui.formatTaskStatusUpdated(task, isComplete);
    }

    /**
     * Adds the specified task to the task list, saves the updated list to disk, and returns confirmation.
     *
     * @param task Task to be added.
     * @return Formatted string confirming task addition.
     * @throws DaveCommandException If the task is a duplicate or saving tasks fails.
     */
    private String addTask(Task task) throws DaveCommandException {
        assert task != null : "Cannot add a null task to TaskList";
        if (this.tasks.hasDuplicate(task)) {
            Task duplicate = this.tasks.findDuplicate(task);
            throw new DaveCommandException(
                    "Hold, Sire! This quest already rests upon your scroll:\n  " + duplicate);
        }
        int initialSize = this.tasks.size();
        this.tasks.add(task);
        assert this.tasks.size() == initialSize + 1 : "TaskList size must increment by 1 after adding task";
        saveTasks();
        return this.ui.formatTaskAdded(task);
    }

    /**
     * Saves the current list of tasks to persistent storage via the storage handler.
     *
     * @throws DaveCommandException If saving tasks fails.
     */
    private void saveTasks() throws DaveCommandException {
        assert this.storage != null : "Storage handler must be initialized to save tasks";
        assert this.tasks != null : "TaskList must be initialized to save tasks";
        this.storage.save(this.tasks.asList());
    }

    /**
     * Starts the Dave chatbot application in terminal mode.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        new Dave(DEFAULT_FILE_PATH).run();
    }
}
