package clarry;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;

import clarry.parser.CommandType;
import clarry.parser.Parser;
import clarry.storage.Storage;
import clarry.task.Task;
import clarry.task.TaskList;
import clarry.ui.Ui;

/**
 * Provides the command-line interface for the Clarry task manager.
 */
public class Clarry {
    private static final Path DATA_FILE = Paths.get("data", "clarry.txt");
    private final Ui ui;
    private final Storage storage;
    private final Parser parser;
    private TaskList tasks;
    private boolean isExitRequested;
    /** Blocks writes when loading could not safely recover the whole save file. */
    private boolean isStorageReadOnly;
    private String pendingStorageWarning;

    /** Creates Clarry with its console user interface. */
    public Clarry() {
        this(DATA_FILE);
    }

    /**
     * Creates Clarry with a specified save file, allowing isolated test data.
     *
     * @param dataFile path to the task file
     */
    public Clarry(Path dataFile) {
        ui = new Ui();
        storage = new Storage(dataFile);
        parser = new Parser();
        isExitRequested = false;
    }

    /** Starts Clarry's command loop. */
    public void run() {
        ui.showWelcome();
        initialiseTasks();

        while (!isExitRequested) {
            String input = ui.readCommand();
            ui.showResponse(getResponse(input));
        }
    }

    /**
     * Executes one command and returns the response for a console or GUI frontend.
     *
     * @param input command entered by the user
     * @return response produced by Clarry
     */
    public String getResponse(String input) {
        return getCommandResponse(input).text();
    }

    /**
     * Executes a command and includes its status for GUI presentation.
     *
     * @param input command entered by the user
     * @return reply text and whether the command failed
     */
    public Response getCommandResponse(String input) {
        initialiseTasks();
        Response response;
        try {
            response = new Response(executeCommand(parser.normalizeInput(input)), false);
        } catch (ClarryException e) {
            response = new Response(ui.getErrorMessage(e.getMessage()), true);
        } catch (NumberFormatException e) {
            response = new Response(ui.getErrorMessage("Please provide a valid task number."), true);
        }
        if (pendingStorageWarning != null) {
            response = new Response(" " + pendingStorageWarning + "\n" + response.text(), true);
            pendingStorageWarning = null;
        }
        return response;
    }

    public boolean isExitRequested() {
        return isExitRequested;
    }

    /** Starts Clarry from the command line. */
    public static void main(String[] args) {
        new Clarry().run();
    }

    /**
     * Saves all tasks to Clarry's relative data file.
     *
     * @param tasks tasks to save
     */
    private void saveTasks(TaskList tasks) throws ClarryException {
        if (isStorageReadOnly) {
            throw new ClarryException("Changes are blocked to protect your save file. Repair it and restart Clarry.");
        }
        try {
            storage.save(tasks);
        } catch (IOException | SecurityException e) {
            throw new ClarryException(
                    "Could not save tasks. No changes were applied. Check file access and try again.");
        }
    }

    /** Loads tasks once before either frontend processes its first command. */
    private void initialiseTasks() {
        if (tasks == null) {
            tasks = loadTasks();
        }
    }

    /**
     * Executes a validated command and returns its response text.
     *
     * @param input command entered by the user
     * @return successful command response
     * @throws ClarryException if the command is invalid
     */
    private String executeCommand(String input) throws ClarryException {
        assert tasks != null : "Tasks must be initialized before executing a command";
        CommandType commandType = parser.parseCommandType(input);
        switch (commandType) {
            case HELP:
                parser.requireExactCommand(input, "help");
                return ui.getHelpMessage();
            case BYE:
                parser.requireExactCommand(input, "bye");
                isExitRequested = true;
                return ui.getGoodbyeMessage();
            case LIST:
                parser.requireExactCommand(input, "list");
                return ui.getListMessage(tasks.getTasks());
            case ON:
                LocalDate date = parser.parseDate(input);
                return ui.getTasksOnDateMessage(date, tasks.getTasksOnDate(date));
            case FIND:
                String keyword = parser.parseFindKeyword(input);
                return ui.getFoundTasksMessage(tasks.find(keyword));
            case MARK:
                return markTask(input);
            case UNMARK:
                return unmarkTask(input);
            case DELETE:
                return deleteTask(input);
            case TODO:
                return addTask(parser.parseTodo(input));
            case DEADLINE:
                return addTask(parser.parseDeadline(input));
            case EVENT:
                return addTask(parser.parseEvent(input));
            case UNKNOWN:
                parser.throwUnknownCommand();
                throw new AssertionError("Unknown-command parser did not throw an exception");
            default:
                throw new AssertionError("Unhandled command type: " + commandType);
        }
    }

    /** Marks the task identified by the command as done. */
    private String markTask(String input) throws ClarryException {
        int taskIndex = parser.parseIndex(input, "mark", tasks.size());
        assert taskIndex >= 0 && taskIndex < tasks.size()
                : "Parser must return an index within the task list";
        Task task = tasks.get(taskIndex);
        updateCompletion(task, true);
        return ui.getMarkedMessage(task);
    }

    /** Marks the task identified by the command as not done. */
    private String unmarkTask(String input) throws ClarryException {
        int taskIndex = parser.parseIndex(input, "unmark", tasks.size());
        assert taskIndex >= 0 && taskIndex < tasks.size()
                : "Parser must return an index within the task list";
        Task task = tasks.get(taskIndex);
        updateCompletion(task, false);
        return ui.getUnmarkedMessage(task);
    }

    /** Deletes the task identified by the command. */
    private String deleteTask(String input) throws ClarryException {
        int taskIndex = parser.parseIndex(input, "delete", tasks.size());
        assert taskIndex >= 0 && taskIndex < tasks.size()
                : "Parser must return an index within the task list";
        TaskList updatedTasks = new TaskList(tasks.getTasks());
        Task deletedTask = updatedTasks.delete(taskIndex);
        saveTasks(updatedTasks);
        tasks = updatedTasks;
        return ui.getDeletedMessage(deletedTask, tasks.size());
    }

    /** Adds a parsed task to the task list. */
    private String addTask(Task task) throws ClarryException {
        if (tasks.getTasks().stream().anyMatch(task::hasSameDetails)) {
            throw new ClarryException("That task is already aboard! Type 'list' to find it.");
        }
        TaskList updatedTasks = new TaskList(tasks.getTasks());
        updatedTasks.add(task);
        saveTasks(updatedTasks);
        tasks = updatedTasks;
        return ui.getAddedMessage(task, tasks.size());
    }

    /** Restores the original completion status if persisting the change fails. */
    private void updateCompletion(Task task, boolean isDone) throws ClarryException {
        boolean wasDone = task.isDone();
        setCompletion(task, isDone);
        try {
            saveTasks(tasks);
        } catch (ClarryException e) {
            setCompletion(task, wasDone);
            throw e;
        }
    }

    /** Applies a completion flag using the task's existing operations. */
    private void setCompletion(Task task, boolean isDone) {
        if (isDone) {
            task.markAsDone();
        } else {
            task.markAsNotDone();
        }
    }

    /**
     * Loads the saved tasks, skipping any corrupted save-file lines.
     *
     * @return tasks reconstructed from the save file, or an empty list if it is absent
     */
    private TaskList loadTasks() {
        try {
            Storage.LoadResult loadResult = storage.load();
            if (loadResult.getCorruptedLineCount() > 0) {
                isStorageReadOnly = true;
                pendingStorageWarning = "Some saved tasks are invalid. Showing readable tasks only. "
                        + "Changes are blocked; repair the save file and restart Clarry.";
            }
            return new TaskList(loadResult.getTasks());
        } catch (IOException | SecurityException e) {
            isStorageReadOnly = true;
            pendingStorageWarning = "Could not load saved tasks. Changes are blocked; "
                    + "check the save file and restart Clarry.";
            return new TaskList();
        }
    }

}
