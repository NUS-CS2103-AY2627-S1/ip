package cookie;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

import cookie.command.CookieException;
import cookie.command.Parser;
import cookie.storage.Storage;
import cookie.task.Deadline;
import cookie.task.Event;
import cookie.task.SortCriterion;
import cookie.task.SortDirection;
import cookie.task.Task;
import cookie.task.TaskList;
import cookie.task.Todo;
import cookie.ui.ConsoleOutput;
import cookie.ui.Output;
import cookie.ui.ReplyCollector;
import cookie.ui.Ui;

/** The main entry point for the Cookie command-line application. */
public class Cookie {
    /** A formatted response and whether the GUI should present it as an error. */
    public record CommandResult(String message, boolean isError) {
    }

    /** The default path used to store Cookie's task data. */
    private static final String DEFAULT_FILE_PATH = "./data/cookie.txt";

    /** The tasks currently managed by this Cookie instance. */
    private final TaskList tasks;

    /** Handles user-facing messages for this Cookie instance. */
    private final Ui ui;

    /** Handles persistence for this Cookie instance. */
    private final Storage storage;

    /** Interprets commands entered by the user. */
    private final Parser parser;

    /** Collects each command's messages for the JavaFX response API and tests. */
    private final ReplyCollector replyCollector;

    /** Malformed save-file lines skipped during startup. */
    private final List<Integer> malformedLineNumbers;

    /** Details of a file-reading failure encountered during startup, if any. */
    private final String loadErrorDetails;

    /** Whether the most recently processed command requested that Cookie exit. */
    private boolean isExitRequested;

    /** Whether the command currently being handled has produced an error. */
    private boolean isCurrentResponseError;

    /** Creates Cookie for the GUI using the default task file. */
    public Cookie() {
        this(DEFAULT_FILE_PATH, false);
    }

    /**
     * Creates Cookie with a task file at the specified path.
     *
     * @param filePath The path of the task file.
     */
    public Cookie(String filePath) {
        this(filePath, true);
    }

    /** Creates Cookie with the requested storage path and console output mode. */
    Cookie(String filePath, boolean isConsoleOutputEnabled) {
        this.replyCollector = new ReplyCollector();
        Output output = isConsoleOutputEnabled
                ? Output.combine(replyCollector, new ConsoleOutput())
                : replyCollector;
        this.ui = new Ui(output);
        this.storage = new Storage(filePath);
        this.parser = new Parser();
        TaskList loadedTasks;
        List<Integer> skippedLineNumbers;
        String loadingError;
        try {
            Storage.LoadResult loadResult = storage.load();
            loadedTasks = loadResult.tasks();
            skippedLineNumbers = loadResult.malformedLineNumbers();
            loadingError = null;
        } catch (IOException exception) {
            loadedTasks = new TaskList();
            skippedLineNumbers = List.of();
            loadingError = exception.getMessage();
        }
        this.tasks = loadedTasks;
        this.malformedLineNumbers = skippedLineNumbers;
        this.loadErrorDetails = loadingError;
    }

    /** Stores user input and prints the message indicating a successful addition. */
    private void addTask(Task task) throws CookieException {
        assert task != null : "A parsed task must not be null";
        requireWritableTaskData();
        if (tasks.containsSameDetails(task)) {
            throw new CookieException("That task already exists.");
        }
        int previousTaskCount = tasks.size();
        tasks.add(task);
        assert tasks.size() == previousTaskCount + 1
                : "Adding a task must increase the task count by one";
        if (!saveTasks()) {
            Task rolledBackTask = tasks.delete(tasks.size() - 1);
            assert rolledBackTask == task : "A failed addition must roll back the added task";
            assert tasks.size() == previousTaskCount
                    : "A failed addition must restore the previous task count";
            return;
        }
        ui.showTaskAdded(task, tasks.size());
    }

    /** Displays the list of items added by users when users enter {@code list}. */
    private void list() {
        ui.showTaskList(tasks);
    }

    /** Marks a task as done and prints a message indicating the successful update. */
    private void markTask(int taskIndex) throws CookieException {
        assert taskIndex >= 0 && taskIndex < tasks.size() : "A parsed task index must be valid";
        requireWritableTaskData();
        Task task = tasks.get(taskIndex);
        boolean wasDone = task.isDone();
        tasks.mark(taskIndex);
        assert task.isDone() : "Marking a task must set its completion state";
        if (!saveTasks()) {
            if (wasDone) {
                tasks.mark(taskIndex);
            } else {
                tasks.unmark(taskIndex);
            }
            assert task.isDone() == wasDone
                    : "A failed mark must restore the previous completion state";
            return;
        }
        ui.showTaskMarked(task);
    }

    /** Marks a task as not done and prints a message indicating the successful update. */
    private void unmarkTask(int taskIndex) throws CookieException {
        assert taskIndex >= 0 && taskIndex < tasks.size() : "A parsed task index must be valid";
        requireWritableTaskData();
        Task task = tasks.get(taskIndex);
        boolean wasDone = task.isDone();
        tasks.unmark(taskIndex);
        assert !task.isDone() : "Unmarking a task must clear its completion state";
        if (!saveTasks()) {
            if (wasDone) {
                tasks.mark(taskIndex);
            } else {
                tasks.unmark(taskIndex);
            }
            assert task.isDone() == wasDone
                    : "A failed unmark must restore the previous completion state";
            return;
        }
        ui.showTaskUnmarked(task);
    }

    /** Deletes the selected task and reports the removed task and remaining task count. */
    private void deleteTask(int taskIndex) throws CookieException {
        assert taskIndex >= 0 && taskIndex < tasks.size() : "A parsed task index must be valid";
        requireWritableTaskData();
        int previousTaskCount = tasks.size();
        Task task = tasks.delete(taskIndex);
        assert tasks.size() == previousTaskCount - 1
                : "Deleting a task must reduce the task count by one";
        if (!saveTasks()) {
            tasks.add(taskIndex, task);
            assert tasks.get(taskIndex) == task : "A failed deletion must restore the deleted task";
            assert tasks.size() == previousTaskCount
                    : "A failed deletion must restore the previous task count";
            return;
        }
        ui.showTaskDeleted(task, tasks.size());
    }

    /** Adds a deadline after the parser validates its description and date or time. */
    private void addDeadline(String description) throws CookieException {
        Parser.ParsedDeadline parsedDeadline = parser.parseDeadline(description);
        addTask(new Deadline(parsedDeadline.description(), parsedDeadline.dateTime()));
    }

    /** Adds an event after the parser validates its description and temporal values. */
    private void addEvent(String description) throws CookieException {
        Parser.ParsedEvent parsedEvent = parser.parseEvent(description);
        addTask(new Event(parsedEvent.description(), parsedEvent.start(), parsedEvent.end()));
    }

    /** Displays deadlines and events that occur on the requested calendar date. */
    private void listOnDate(String value) throws CookieException {
        LocalDate date = parser.parseDate(value);
        ui.showTasksOnDate(date, tasks.findOn(date));
    }

    /** Displays tasks whose descriptions contain the requested keyword. */
    private void findTasks(String keyword) {
        ui.showMatchingTasks(tasks.find(keyword));
    }

    /** Displays all tasks in a temporary sorted view without changing their stored order. */
    private void sortTasks(SortCriterion criterion, SortDirection direction) {
        ui.showSortedTasks(tasks.getSortedView(criterion, direction), criterion, direction);
    }

    /** Saves the current task list to the data file and reports whether it succeeded. */
    private boolean saveTasks() {
        try {
            storage.save(tasks);
            return true;
        } catch (IOException exception) {
            isCurrentResponseError = true;
            ui.showSaveError(exception.getMessage());
            return false;
        }
    }

    /** Prevents changes when startup failed to load the existing task data safely. */
    private void requireWritableTaskData() throws CookieException {
        if (loadErrorDetails != null) {
            throw new CookieException(
                    "I can't change tasks because the task file could not be loaded. "
                            + "Fix the file problem and restart Cookie first.");
        }
    }

    /** Reads and responds to commands until the user enters {@code bye}. */
    public void run() {
        replyCollector.clear();
        ui.greet();
        showStartupIssues();

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            getResponse(scanner.nextLine());
            if (isExitRequested) {
                return;
            }
        }
    }

    /**
     * Processes one user command and returns Cookie's response.
     *
     * @param input The command entered by the user.
     * @return Cookie's response to the command.
     */
    public String getResponse(String input) {
        return getCommandResult(input).message();
    }

    /**
     * Processes one user command and returns its formatted response and presentation type.
     *
     * @param input The command entered by the user.
     * @return The response and whether it represents an error.
     */
    public CommandResult getCommandResult(String input) {
        replyCollector.clear();
        isExitRequested = false;
        isCurrentResponseError = false;
        try {
            execute(parser.parse(input));
        } catch (CookieException exception) {
            isCurrentResponseError = true;
            ui.showError(exception);
        }
        return new CommandResult(replyCollector.getReply(), isCurrentResponseError);
    }

    /**
     * Returns any warning or error produced while loading saved tasks.
     *
     * @return The startup issue message, or an empty string when loading succeeded cleanly.
     */
    public String getStartupResponse() {
        return getStartupResult().message();
    }

    /**
     * Returns startup feedback and whether it should be visually highlighted by the GUI.
     *
     * @return The startup response and whether loading encountered a problem.
     */
    public CommandResult getStartupResult() {
        replyCollector.clear();
        showStartupIssues();
        boolean hasStartupIssue = loadErrorDetails != null || !malformedLineNumbers.isEmpty();
        return new CommandResult(replyCollector.getReply(), hasStartupIssue);
    }

    /** Reports file-reading failures or malformed records detected during startup. */
    private void showStartupIssues() {
        if (loadErrorDetails != null) {
            ui.showLoadError(loadErrorDetails);
        } else if (!malformedLineNumbers.isEmpty()) {
            ui.showMalformedRecords(malformedLineNumbers);
        }
    }

    /** Executes a parsed command against Cookie's current task list. */
    private void execute(Parser.ParsedCommand parsedCommand) throws CookieException {
        switch (parsedCommand.command()) {
            case BYE -> {
                parser.requireNoArguments(parsedCommand);
                ui.exit();
                isExitRequested = true;
            }
            case LIST -> {
                parser.requireNoArguments(parsedCommand);
                list();
            }
            case MARK -> markTask(parser.parseTaskIndex(parsedCommand, tasks.size()));
            case UNMARK -> unmarkTask(parser.parseTaskIndex(parsedCommand, tasks.size()));
            case DELETE -> deleteTask(parser.parseTaskIndex(parsedCommand, tasks.size()));
            case ON -> {
                parser.requireSingleArgument(parsedCommand, "on <date>");
                listOnDate(parsedCommand.description());
            }
            case FIND -> {
                parser.requireSingleArgument(parsedCommand, "find <keyword>");
                findTasks(parsedCommand.argument(0));
            }
            case SORT -> {
                Parser.ParsedSort parsedSort = parser.parseSort(parsedCommand);
                sortTasks(parsedSort.criterion(), parsedSort.direction());
            }
            case TODO -> addTask(new Todo(
                    parser.requireFileSafe(parser.requireDescription(parsedCommand))));
            case DEADLINE -> addDeadline(parsedCommand.description());
            case EVENT -> addEvent(parsedCommand.description());
            default -> throw new AssertionError("Unhandled command: " + parsedCommand.command());
        }
    }

    /**
     * Starts Cookie with its default task file.
     *
     * @param args Command-line arguments, which are ignored.
     */
    public static void main(String[] args) {
        new Cookie(DEFAULT_FILE_PATH).run();
    }
}
