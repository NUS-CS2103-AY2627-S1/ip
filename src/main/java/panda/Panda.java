package panda;

import java.nio.file.Path;
import java.util.List;

import panda.exception.DataLoadingException;
import panda.exception.DataSavingException;
import panda.exception.InvalidTaskNumberException;
import panda.exception.PandaException;
import panda.parser.Parser;
import panda.storage.Storage;
import panda.task.Task;
import panda.task.TaskList;
import panda.ui.Ui;

/**
 * Runs the Panda task manager and coordinates its user interfaces.
 *
 * Coordinates command parsing, task storage, and user-facing output.
 */
public class Panda {
    /**
     * Builds a relative default path from OS-independent components.
     */
    private static final Path DEFAULT_DATA_FILE_PATH =
            Path.of("data", "info.txt");

    private final Parser parser;
    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;
    private final List<PandaException> loadingErrors;

    /**
     * Creates Panda using the default data file in the {@code data} directory.
     */
    public Panda() {
        this(DEFAULT_DATA_FILE_PATH.toString());
    }

    /**
     * Creates Panda with a user interface and storage for the supplied file.
     *
     * @param filePath the task data file path.
     */
    public Panda(String filePath) {
        ui = new Ui();
        parser = new Parser();
        storage = new Storage(filePath);
        TaskList loadedTasks;
        List<PandaException> errors;
        try {
            Storage.LoadResult loadResult = storage.load();
            loadedTasks = new TaskList(loadResult.tasks());
            errors = loadResult.errors();
        } catch (DataLoadingException exception) {
            loadedTasks = new TaskList();
            errors = List.of(exception);
        }
        tasks = loadedTasks;
        loadingErrors = errors;
    }

    /**
     * Returns the default data file path.
     *
     * @return the default path relative to Panda's working directory.
     */
    static Path getDefaultDataFilePath() {
        return DEFAULT_DATA_FILE_PATH;
    }

    /**
     * Starts Panda using the default data file or an optional test file.
     *
     * @param args an optional first argument overriding the data file path.
     */
    public static void main(String[] args) {
        Panda panda = args.length > 0 ? new Panda(args[0]) : new Panda();
        panda.run();
    }

    /**
     * Returns Panda's response to one command.
     *
     * @param message the complete user command.
     * @return the response to display.
     */
    public String getResponse(String message) {
        if (isExitCommand(message)) {
            return ui.showGoodbye();
        }

        try {
            Parser.ParsedCommand parsedCommand = parser.parse(message);
            return executeCommand(parsedCommand);
        } catch (PandaException exception) {
            return ui.showError(exception);
        }
    }

    /**
     * Executes a parsed non-exit command and returns its user-facing response.
     *
     * @param parsedCommand the command and its applicable argument.
     * @return the response produced by the command.
     * @throws PandaException if the command cannot be completed.
     */
    private String executeCommand(Parser.ParsedCommand parsedCommand) throws PandaException {
        switch (parsedCommand.command()) {
            case LIST:
                List<TaskList.NumberedTask> displayedTasks =
                        parsedCommand.filterDate() == null
                                ? tasks.getTasks()
                                : tasks.getTasksOn(parsedCommand.filterDate());
                return ui.showTaskList(displayedTasks);
            case FIND:
                List<TaskList.NumberedTask> matchingTasks =
                        tasks.getTasksMatching(parsedCommand.searchTerm());
                return ui.showMatchingTaskList(matchingTasks);
            case MARK:
                return markTask(parsedCommand.taskNumber());
            case UNMARK:
                return unmarkTask(parsedCommand.taskNumber());
            case UPDATE:
                return updateTask(parsedCommand.taskNumber(), parsedCommand.updateDetails());
            case DELETE:
                return deleteTask(parsedCommand.taskNumber());
            case EVENT, DEADLINE, TODO:
                return addTask(parsedCommand.task());
            case BYE:
                throw new IllegalStateException("The bye command should exit before dispatch.");
            default:
                throw new IllegalStateException("This should not be reachable");
        }
    }

    private String markTask(int taskNumber)
            throws InvalidTaskNumberException, DataSavingException {
        Task markedTask = tasks.mark(taskNumber);
        String response = ui.showMarked(markedTask);
        saveTasks();
        return response;
    }

    private String unmarkTask(int taskNumber)
            throws InvalidTaskNumberException, DataSavingException {
        Task unmarkedTask = tasks.unmark(taskNumber);
        String response = ui.showUnmarked(unmarkedTask);
        saveTasks();
        return response;
    }

    private String updateTask(int taskNumber, Parser.UpdateDetails updateDetails)
            throws PandaException {
        Task updatedTask;
        boolean hasTimingUpdate = updateDetails.updatedDeadlineDateTime() != null
                || updateDetails.updatedStartDateTime() != null;
        if (updateDetails.updatedDeadlineDateTime() != null) {
            updatedTask = tasks.rescheduleDeadline(
                    taskNumber, updateDetails.updatedDeadlineDateTime());
        } else if (updateDetails.updatedStartDateTime() != null) {
            assert updateDetails.updatedStartDateTime() != null
                    && updateDetails.updatedEndDateTime() != null
                    : "An Event timing update must contain both endpoints.";
            updatedTask = tasks.rescheduleEvent(taskNumber,
                    updateDetails.updatedStartDateTime(), updateDetails.updatedEndDateTime());
        } else {
            assert updateDetails.updatedName() != null
                    : "An update must change a task's name or timing.";
            updatedTask = tasks.rename(taskNumber, updateDetails.updatedName());
        }

        if (hasTimingUpdate && updateDetails.updatedName() != null) {
            updatedTask = tasks.rename(taskNumber, updateDetails.updatedName());
        }
        String response = ui.showUpdated(updatedTask);
        saveTasks();
        return response;
    }

    private String deleteTask(int taskNumber)
            throws InvalidTaskNumberException, DataSavingException {
        Task removedTask = tasks.delete(taskNumber);
        String response = ui.showDeleted(removedTask, tasks.getTaskCount());
        saveTasks();
        return response;
    }

    private String addTask(Task task) throws DataSavingException {
        tasks.add(task);
        String response = ui.showAdded(task, tasks.getTaskCount());
        saveTasks();
        return response;
    }

    /**
     * Saves the current task-list state.
     *
     * @throws DataSavingException if the task list cannot be saved.
     */
    private void saveTasks() throws DataSavingException {
        storage.save(tasks.getTaskSnapshot());
    }

    /**
     * Checks whether a message is the exact command that ends the application.
     *
     * @param message the complete user command.
     * @return true only for an argument-free {@code bye} command.
     */
    public boolean isExitCommand(String message) {
        return parser.isExitCommand(message);
    }

    /**
     * Loads tasks and runs the command-processing loop.
     */
    public void run() {
        ui.showWelcome();

        if (!loadingErrors.isEmpty()) {
            ui.showLoadingErrors(loadingErrors);
        }
        // Treat a closed input stream as a graceful end to the session.
        while (ui.hasNextCommand()) {
            String message = ui.readCommand();
            if (parser.isExitCommand(message)) {
                break;
            }
            ui.showDivider();
            getResponse(message);
            ui.showDivider();
        }
        ui.showGoodbye();
    }
}
