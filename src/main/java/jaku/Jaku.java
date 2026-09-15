package jaku;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jaku.parser.Command;
import jaku.parser.Parser;
import jaku.storage.Storage;
import jaku.task.Deadline;
import jaku.task.Event;
import jaku.task.Recurrence;
import jaku.task.RecurringEvent;
import jaku.task.RecurringTodo;
import jaku.task.Task;
import jaku.task.TaskList;
import jaku.task.Todo;
import jaku.ui.Ui;

/**
 * Coordinates Jaku's parser, task list, storage, and console user interface.
 * <p>
 * A chat session loads saved tasks, accepts commands until {@code bye}, and
 * delegates persistence and presentation to the appropriate collaborators.
 */
public class Jaku {
    /** Marker between a deadline's description and its due date. */
    private static final String BY_SEPARATOR = "/by";

    /** Marker between an event's description and its start date or time. */
    private static final String FROM_SEPARATOR = "/from";

    /** Marker between an event's start and end dates or times. */
    private static final String TO_SEPARATOR = "/to";

    /** Marker between a recurring task's start and recurrence interval. */
    private static final String EVERY_SEPARATOR = "/every";

    /** Formatter accepted for recurring event date-times. */
    private static final DateTimeFormatter RECURRING_EVENT_DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /** Usage text for creating recurring todos. */
    private static final String RECURRING_TODO_USAGE =
            "Use: repeat todo <description> /from yyyy-MM-dd /every daily|weekly.";

    /** Usage text for creating recurring events. */
    private static final String RECURRING_EVENT_USAGE =
            "Use: repeat event <description> /from yyyy-MM-dd HH:mm /to yyyy-MM-dd HH:mm /every daily|weekly.";

    /** Message shown when saved tasks cannot be loaded without risking data loss. */
    private static final String RECOVERY_NOTICE =
            "Jaku couldn't read your saved tasks. Your saved file was left untouched. Repair it, then restart Jaku.";

    /** Message shown when a command would write while Jaku is in recovery mode. */
    private static final String RECOVERY_MODE_MESSAGE =
            "Jaku is in recovery mode and cannot change tasks until you repair the saved file and restart.";

    /** Saves and loads Jaku's tasks. */
    private final Storage storage;

    /** Stores the tasks in the current chat session. */
    private final TaskList tasks;

    /** Reads user commands and displays Jaku's replies. */
    private final Ui ui;

    /** Whether saved tasks loaded successfully and may safely be replaced. */
    private final boolean canSaveTasks;

    /** Error to show once at startup when Jaku protects an unreadable task file. */
    private final String startupNotice;

    /** Whether the last command requested application exit. */
    private boolean exitRequested;

    /** Creates Jaku with its default relative data-file location. */
    public Jaku() {
        this(new Storage(Path.of(System.getProperty("jaku.dataFile", "data/jaku.txt"))));
    }

    /**
     * Creates Jaku using the supplied storage mechanism.
     *
     * @param storage persistence mechanism for tasks
     */
    public Jaku(Storage storage) {
        this.storage = storage;
        this.ui = new Ui();
        LoadedTasks loadedTasks = loadTasks();
        this.tasks = loadedTasks.tasks();
        this.canSaveTasks = loadedTasks.canSaveTasks();
        this.startupNotice = loadedTasks.startupNotice();
    }

    /**
     * Starts a new Jaku chat session.
     *
     * @param args command-line arguments, which Jaku does not use
     */
    public static void main(String[] args) {
        new Jaku().run();
    }

    /** Runs one chat session, from greeting to farewell. */
    private void run() {
        ui.showWelcome();
        getStartupNotice().ifPresent(notice -> ui.showResponse(notice.text()));
        readCommandsUntilBye();
        if (!exitRequested) {
            ui.showGoodbye();
        }
    }

    /**
     * Loads saved tasks before accepting user commands.
     *
     * @return loaded tasks, or an empty list when loading fails
     */
    private LoadedTasks loadTasks() {
        try {
            return new LoadedTasks(new TaskList(storage.load()), true, null);
        } catch (JakuException exception) {
            return new LoadedTasks(new TaskList(), false, RECOVERY_NOTICE);
        }
    }

    /** Reads user commands line by line until the user enters {@code bye}. */
    private void readCommandsUntilBye() {
        while (ui.hasNextCommand()) {
            String input = ui.readCommand();
            String response = getResponse(input);
            ui.showRaw(response);
            if (exitRequested) {
                return;
            }
        }
    }

    /** Processes one command and returns its complete user-facing response.
     *
     * @param input command entered by the user
     * @return formatted response, or an empty string for blank input
     */
    public String getResponse(String input) {
        return processCommand(input).text();
    }

    /**
     * Processes one command and returns its text together with its presentation category.
     *
     * @param input command entered by the user
     * @return formatted response and whether it represents an error
     */
    public CommandResult processCommand(String input) {
        String normalizedInput = input.trim();
        if (normalizedInput.isEmpty()) {
            return new CommandResult("", MessageKind.REPLY);
        }
        ui.startCapturing();
        Command command = Parser.parseCommand(normalizedInput);
        if (command == Command.BYE && Parser.getArguments(normalizedInput, command).isEmpty()) {
            exitRequested = true;
            ui.showGoodbye();
            return new CommandResult(ui.stopCapturing(), MessageKind.REPLY);
        }
        try {
            handleCommand(command, normalizedInput);
        } catch (JakuException exception) {
            ui.showResponse(exception.getMessage());
            return new CommandResult(ui.stopCapturing(), MessageKind.ERROR);
        }
        return new CommandResult(ui.stopCapturing(), MessageKind.REPLY);
    }

    /**
     * Returns whether the last command requested application exit.
     *
     * @return true after a valid {@code bye} command
     */
    public boolean isExitRequested() {
        return exitRequested;
    }

    /**
     * Returns the startup recovery warning when saved tasks could not be loaded.
     *
     * @return recovery warning for the active UI, if one is needed
     */
    public Optional<CommandResult> getStartupNotice() {
        if (startupNotice == null) {
            return Optional.empty();
        }
        return Optional.of(new CommandResult(startupNotice, MessageKind.ERROR));
    }

    /**
     * Carries out a single command from the user.
     *
     * @param command the recognized command type
     * @param input the trimmed, non-empty line the user entered
     * @throws JakuException if the command is not recognized or its arguments are invalid
     */
    private void handleCommand(Command command, String input) throws JakuException {
        switch (command) {
        case LIST:
            if (!Parser.getArguments(input, command).isEmpty()) {
                throw unknownCommandException();
            }
            ui.showTaskList(tasks);
            break;
        case MARK:
            markTask(input);
            break;
        case UNMARK:
            unmarkTask(input);
            break;
        case TODO:
            addTodo(input);
            break;
        case DEADLINE:
            addDeadline(input);
            break;
        case EVENT:
            addEvent(input);
            break;
        case DELETE:
            deleteTask(input);
            break;
        case FIND:
            findTasks(input);
            break;
        case REPEAT:
            addRecurringTask(input);
            break;
        case BYE:
        case UNKNOWN:
            throw unknownCommandException();
        default:
            throw new AssertionError("Unhandled command: " + command);
        }
    }

    /**
     * Creates the error shown when no supported command matches the user's input.
     *
     * @return an exception containing the supported-command guidance
     */
    private JakuException unknownCommandException() {
        return new JakuException(
                "I don't recognize that command. Try todo, deadline, event, repeat, list, mark, unmark, delete, find,"
                        + " or bye."
        );
    }

    /**
     * Shows tasks whose descriptions contain the keyword supplied by the user.
     *
     * @param input a find command followed by a keyword
     * @throws JakuException if the find keyword is empty
     */
    private void findTasks(String input) throws JakuException {
        String keyword = Parser.getArguments(input, Command.FIND);
        if (keyword.isEmpty()) {
            throw new JakuException("I need a keyword after \"find\".");
        }
        ui.showMatchingTasks(tasks.find(keyword));
    }

    /**
     * Creates a todo from its command and adds it to the task list.
     *
     * @param input a todo command followed by a description
     * @throws JakuException if the todo description is empty
     */
    private void addTodo(String input) throws JakuException {
        String description = Parser.getArguments(input, Command.TODO);
        if (description.isEmpty()) {
            throw new JakuException("I need a description after \"todo\".");
        }
        addTask(new Todo(description));
    }

    /**
     * Creates a deadline from its command and adds it to the task list.
     *
     * @param input a deadline command containing a description and {@code /by} value
     * @throws JakuException if the description, separator, or due date is invalid
     */
    private void addDeadline(String input) throws JakuException {
        String arguments = Parser.getArguments(input, Command.DEADLINE);
        int separatorIndex = findSingleMarker(arguments, BY_SEPARATOR,
                "Use: deadline <description> /by <date or time>.");
        String description = arguments.substring(0, separatorIndex).trim();
        String by = arguments.substring(separatorIndex + BY_SEPARATOR.length()).trim();
        if (description.isEmpty() || by.isEmpty()) {
            throw new JakuException("Use: deadline <description> /by <date or time>.");
        }
        try {
            addTask(new Deadline(description, LocalDate.parse(by)));
        } catch (DateTimeParseException exception) {
            throw new JakuException("Use: deadline <description> /by yyyy-MM-dd.");
        }
    }

    /**
     * Creates an event from its command and adds it to the task list.
     *
     * @param input an event command containing a description, {@code /from}, and {@code /to} values
     * @throws JakuException if the description, separators, start text, or end text is missing
     */
    private void addEvent(String input) throws JakuException {
        String arguments = Parser.getArguments(input, Command.EVENT);
        String usage = "Use: event <description> /from <start> /to <end>.";
        int fromIndex = findSingleMarker(arguments, FROM_SEPARATOR, usage);
        int toIndex = findSingleMarker(arguments, TO_SEPARATOR, usage);
        if (toIndex < fromIndex) {
            throw new JakuException(usage);
        }
        String description = arguments.substring(0, fromIndex).trim();
        String from = arguments.substring(fromIndex + FROM_SEPARATOR.length(), toIndex).trim();
        String to = arguments.substring(toIndex + TO_SEPARATOR.length()).trim();
        if (description.isEmpty() || from.isEmpty() || to.isEmpty()) {
            throw new JakuException("Use: event <description> /from <start> /to <end>.");
        }
        addTask(new Event(description, from, to));
    }

    /**
     * Creates a recurring todo or event from a {@code repeat} command.
     *
     * @param input a repeat command followed by a task type and recurrence details
     * @throws JakuException if the task type or recurring schedule is invalid
     */
    private void addRecurringTask(String input) throws JakuException {
        String arguments = Parser.getArguments(input, Command.REPEAT);
        Command taskType = Parser.parseCommand(arguments);
        if (taskType == Command.TODO) {
            addRecurringTodo(Parser.getArguments(arguments, taskType));
            return;
        }
        if (taskType == Command.EVENT) {
            addRecurringEvent(Parser.getArguments(arguments, taskType));
            return;
        }
        throw new JakuException("Use: repeat todo ... or repeat event ...");
    }

    /**
     * Creates a recurring todo from its details after the {@code todo} keyword.
     *
     * @param arguments todo description, first date, and recurrence interval
     * @throws JakuException if the recurring todo details are invalid
     */
    private void addRecurringTodo(String arguments) throws JakuException {
        int fromIndex = findSingleMarker(arguments, FROM_SEPARATOR, RECURRING_TODO_USAGE);
        int everyIndex = findSingleMarker(arguments, EVERY_SEPARATOR, RECURRING_TODO_USAGE);
        if (everyIndex < fromIndex) {
            throw new JakuException(RECURRING_TODO_USAGE);
        }
        String description = arguments.substring(0, fromIndex).trim();
        String from = arguments.substring(fromIndex + FROM_SEPARATOR.length(), everyIndex).trim();
        String recurrence = arguments.substring(everyIndex + EVERY_SEPARATOR.length()).trim();
        if (description.isEmpty() || from.isEmpty() || recurrence.isEmpty()) {
            throw new JakuException(RECURRING_TODO_USAGE);
        }
        try {
            addTask(new RecurringTodo(description, LocalDate.parse(from), Recurrence.fromLabel(recurrence)));
        } catch (DateTimeParseException | IllegalArgumentException exception) {
            throw new JakuException(RECURRING_TODO_USAGE);
        }
    }

    /**
     * Creates a recurring event from its details after the {@code event} keyword.
     *
     * @param arguments event description, date-times, and recurrence interval
     * @throws JakuException if the recurring event details are invalid
     */
    private void addRecurringEvent(String arguments) throws JakuException {
        int fromIndex = findSingleMarker(arguments, FROM_SEPARATOR, RECURRING_EVENT_USAGE);
        int toIndex = findSingleMarker(arguments, TO_SEPARATOR, RECURRING_EVENT_USAGE);
        int everyIndex = findSingleMarker(arguments, EVERY_SEPARATOR, RECURRING_EVENT_USAGE);
        if (toIndex < fromIndex || everyIndex < toIndex) {
            throw new JakuException(RECURRING_EVENT_USAGE);
        }
        String description = arguments.substring(0, fromIndex).trim();
        String from = arguments.substring(fromIndex + FROM_SEPARATOR.length(), toIndex).trim();
        String to = arguments.substring(toIndex + TO_SEPARATOR.length(), everyIndex).trim();
        String recurrence = arguments.substring(everyIndex + EVERY_SEPARATOR.length()).trim();
        if (description.isEmpty() || from.isEmpty() || to.isEmpty() || recurrence.isEmpty()) {
            throw new JakuException(RECURRING_EVENT_USAGE);
        }
        try {
            LocalDateTime start = LocalDateTime.parse(from, RECURRING_EVENT_DATE_TIME_FORMAT);
            LocalDateTime end = LocalDateTime.parse(to, RECURRING_EVENT_DATE_TIME_FORMAT);
            if (!end.isAfter(start)) {
                throw new JakuException("A recurring event must end after it starts.");
            }
            addTask(new RecurringEvent(description, start, end, Recurrence.fromLabel(recurrence)));
        } catch (DateTimeParseException | IllegalArgumentException exception) {
            throw new JakuException(RECURRING_EVENT_USAGE);
        }
    }

    /**
     * Stores a new task and confirms its addition and the updated task count.
     *
     * @param task the task to remember
     * @throws JakuException if the task list cannot be saved
     */
    private void addTask(Task task) throws JakuException {
        ensureTaskChangesCanBeSaved();
        tasks.add(task);
        assert tasks.get(tasks.size() - 1) == task
                : "A newly added task must be the last task in the list.";
        try {
            saveTasks();
        } catch (JakuException exception) {
            assert tasks.get(tasks.size() - 1) == task
                    : "A failed save must not alter the task that needs to be rolled back.";
            tasks.remove(tasks.size() - 1);
            throw exception;
        }
        ui.showResponse(List.of(
                "Got it. I've added this task:",
                "  " + task,
                "Now you have " + tasks.size() + " tasks in the list."
        ));
    }

    /**
     * Marks the task selected by a one-based task number as done.
     *
     * @param input a mark command followed by a task number
     * @throws JakuException if the task number is invalid or outside the list
     */
    private void markTask(String input) throws JakuException {
        ensureTaskChangesCanBeSaved();
        int taskIndex = tasks.getIndex(Parser.parseTaskNumber(input, Command.MARK));
        assert taskIndex >= 0 && taskIndex < tasks.size()
                : "A validated task number must identify an existing task.";
        Task task = tasks.get(taskIndex);
        if (task instanceof RecurringTodo recurringTodo) {
            recurringTodo.advanceOccurrence();
            saveRecurringTaskUpdate(recurringTodo, true);
            ui.showResponse("Nice! I've completed this occurrence. The next one is:", "  " + recurringTodo);
            return;
        }
        if (task instanceof RecurringEvent recurringEvent) {
            recurringEvent.advanceOccurrence();
            saveRecurringTaskUpdate(recurringEvent, true);
            ui.showResponse("Nice! I've completed this occurrence. The next one is:", "  " + recurringEvent);
            return;
        }
        boolean wasDone = task.isDone();
        task.markAsDone();
        try {
            saveTasks();
        } catch (JakuException exception) {
            restoreTaskStatus(task, wasDone);
            throw exception;
        }
        ui.showResponse(List.of("Nice! I've marked this task as done:", "  " + task));
    }

    /**
     * Marks the task selected by a one-based task number as not done.
     *
     * @param input an unmark command followed by a task number
     * @throws JakuException if the task number is invalid or outside the list
     */
    private void unmarkTask(String input) throws JakuException {
        ensureTaskChangesCanBeSaved();
        int taskIndex = tasks.getIndex(Parser.parseTaskNumber(input, Command.UNMARK));
        assert taskIndex >= 0 && taskIndex < tasks.size()
                : "A validated task number must identify an existing task.";
        Task task = tasks.get(taskIndex);
        if (task instanceof RecurringTodo recurringTodo) {
            recurringTodo.reverseOccurrence();
            saveRecurringTaskUpdate(recurringTodo, false);
            ui.showResponse("OK, I've restored the previous occurrence:", "  " + recurringTodo);
            return;
        }
        if (task instanceof RecurringEvent recurringEvent) {
            recurringEvent.reverseOccurrence();
            saveRecurringTaskUpdate(recurringEvent, false);
            ui.showResponse("OK, I've restored the previous occurrence:", "  " + recurringEvent);
            return;
        }
        boolean wasDone = task.isDone();
        task.markAsNotDone();
        try {
            saveTasks();
        } catch (JakuException exception) {
            restoreTaskStatus(task, wasDone);
            throw exception;
        }
        ui.showResponse(List.of("OK, I've marked this task as not done yet:", "  " + task));
    }

    /**
     * Removes the task selected by a one-based task number.
     *
     * @param input a delete command followed by a task number
     * @throws JakuException if the task number is invalid or outside the list
     */
    private void deleteTask(String input) throws JakuException {
        ensureTaskChangesCanBeSaved();
        int taskIndex = tasks.getIndex(Parser.parseTaskNumber(input, Command.DELETE));
        assert taskIndex >= 0 && taskIndex < tasks.size()
                : "A validated task number must identify an existing task.";
        Task removedTask = tasks.remove(taskIndex);
        try {
            saveTasks();
        } catch (JakuException exception) {
            tasks.add(taskIndex, removedTask);
            throw exception;
        }
        ui.showResponse(List.of(
                "Noted. I've removed this task:",
                "  " + removedTask,
                "Now you have " + tasks.size() + " tasks in the list."
        ));
    }

    /**
     * Writes the current task list to persistent storage.
     *
     * @throws JakuException if the task list cannot be saved
     */
    private void saveTasks() throws JakuException {
        storage.save(tasks.asList());
    }

    /**
     * Rejects changes when an existing task file could not be read safely.
     *
     * @throws JakuException if a save could overwrite unreadable saved data
     */
    private void ensureTaskChangesCanBeSaved() throws JakuException {
        if (!canSaveTasks) {
            throw new JakuException(RECOVERY_MODE_MESSAGE);
        }
    }

    /**
     * Locates one whitespace-delimited command parameter and rejects missing or duplicate markers.
     *
     * @param arguments complete command arguments
     * @param marker parameter marker to locate
     * @param usage message shown when the marker is invalid
     * @return index at which the marker starts
     * @throws JakuException if the marker is missing or appears more than once
     */
    private int findSingleMarker(String arguments, String marker, String usage) throws JakuException {
        Pattern pattern = Pattern.compile("(?<!\\S)" + Pattern.quote(marker) + "(?!\\S)");
        Matcher matcher = pattern.matcher(arguments);
        if (!matcher.find()) {
            throw new JakuException(usage);
        }
        int markerIndex = matcher.start();
        if (matcher.find()) {
            throw new JakuException(usage);
        }
        return markerIndex;
    }

    /**
     * Saves a recurring-task date change, reversing it if persistence fails.
     *
     * @param task recurring todo whose occurrence was changed
     * @param advanced whether the update advanced rather than reversed the occurrence
     * @throws JakuException if the task list cannot be saved
     */
    private void saveRecurringTaskUpdate(RecurringTodo task, boolean advanced) throws JakuException {
        try {
            saveTasks();
        } catch (JakuException exception) {
            if (advanced) {
                task.reverseOccurrence();
            } else {
                task.advanceOccurrence();
            }
            throw exception;
        }
    }

    /**
     * Saves a recurring-event date change, reversing it if persistence fails.
     *
     * @param task recurring event whose occurrence was changed
     * @param advanced whether the update advanced rather than reversed the occurrence
     * @throws JakuException if the task list cannot be saved
     */
    private void saveRecurringTaskUpdate(RecurringEvent task, boolean advanced) throws JakuException {
        try {
            saveTasks();
        } catch (JakuException exception) {
            if (advanced) {
                task.reverseOccurrence();
            } else {
                task.advanceOccurrence();
            }
            throw exception;
        }
    }

    /**
     * Restores a task's completion status after an unsuccessful save.
     *
     * @param task task whose status should be restored
     * @param wasDone completion status before the attempted mutation
     */
    private void restoreTaskStatus(Task task, boolean wasDone) {
        if (wasDone) {
            task.markAsDone();
        } else {
            task.markAsNotDone();
        }
        assert task.isDone() == wasDone
                : "Restoring a task after a failed save must recover its original completion status.";
    }

    /** Result of loading persisted tasks before a chat session begins. */
    private record LoadedTasks(TaskList tasks, boolean canSaveTasks, String startupNotice) {
    }
}
