package xerxes.parser;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import xerxes.storage.TaskStorage;
import xerxes.task.Deadline;
import xerxes.task.Event;
import xerxes.task.Task;
import xerxes.task.TaskList;
import xerxes.task.ToDo;

/**
 * Interprets user commands and performs the corresponding task-list operations.
 */
public class Parser {
    /** Commands that do not require additional arguments. */
    private static final String BYE_COMMAND = "bye";
    private static final String LIST_COMMAND = "list";
    private static final String SAVE_COMMAND = "save";
    private static final String FIND_COMMAND = "find";

    /** Command prefixes used to identify commands with arguments. */
    private static final String TODO_COMMAND = "todo ";
    private static final String TODO_COMMAND_NAME = "todo";
    private static final String DEADLINE_COMMAND = "deadline ";
    private static final String DEADLINE_COMMAND_NAME = "deadline";
    private static final String EVENT_COMMAND = "event ";
    private static final String EVENT_COMMAND_NAME = "event";
    private static final String DELETE_COMMAND = "delete ";
    private static final String DELETE_COMMAND_NAME = "delete";
    private static final String MARK_COMMAND = "mark ";
    private static final String MARK_COMMAND_NAME = "mark";
    private static final String UNMARK_COMMAND = "unmark ";
    private static final String UNMARK_COMMAND_NAME = "unmark";
    private static final String MARK_COMMAND_PATTERN = "mark -?\\d+";
    private static final String UNMARK_COMMAND_PATTERN = "unmark -?\\d+";

    /** Command aliases. */
    private static final Map<String, String> COMMAND_ALIASES = Map.of(
            "b", "bye",
            "l", "list",
            "s", "save",
            "f", "find",
            "t", "todo",
            "d", "deadline",
            "e", "event",
            "del", "delete",
            "m", "mark",
            "um", "unmark"
    );
    /** Markers separating task descriptions from their date arguments. */
    private static final String DEADLINE_MARKER = " /by ";
    private static final String EVENT_FROM_MARKER = " /from ";
    private static final String EVENT_TO_MARKER = " /to ";

    /** Strict date format accepted for deadline and event commands. */
    private static final DateTimeFormatter INPUT_FORMAT =
            DateTimeFormatter.ofPattern("d/M/uuuu")
                    .withResolverStyle(ResolverStyle.STRICT);

    /** Storage used when the user requests that tasks be saved. */
    private final TaskStorage taskStorage;

    /** Creates a parser that saves tasks through the given storage. */
    public Parser(TaskStorage taskStorage) {
        this.taskStorage = taskStorage;
    }

    /**
     * Processes one user command.
     *
     * @param input Command entered by the user.
     * @param tasks Task list to query or modify.
     * @return Result containing the response, error status, and exit status.
     */
    public CommandResult handleCommand(String input, TaskList tasks) {
        assert input != null : "The parser expects a command string";
        assert tasks != null : "The parser expects a task list to operate on";

        input = normaliseInput(input);

        if (input.startsWith(BYE_COMMAND + " ") || input.startsWith(LIST_COMMAND + " ")
                || input.startsWith(SAVE_COMMAND + " ")) {
            return error("Eh, bye, list, and save do not take extra arguments ah.");
        }
        if (input.matches(MARK_COMMAND + "-?\\d+\\s+.+")
                || input.matches(UNMARK_COMMAND + "-?\\d+\\s+.+")) {
            return error("Eh, mark and unmark only take one task number ah.");
        }

        if (input.equals(BYE_COMMAND)) {
            return success("Ciao, cya again", true);
        }
        if (input.equals(LIST_COMMAND)) {
            return success("Yo your tasks are:\n" + tasks.toString());
        }
        if (input.equals(SAVE_COMMAND)) {
            return handleSaveTasks(tasks);
        }
        if (input.equals(FIND_COMMAND) || input.startsWith(FIND_COMMAND + " ")) {
            return handleFindTasks(input, tasks);
        }
        if (input.matches(MARK_COMMAND_PATTERN)) {
            return handleTaskStatus(input, tasks, true);
        }
        if (input.matches(UNMARK_COMMAND_PATTERN)) {
            return handleTaskStatus(input, tasks, false);
        }
        if (input.matches(DELETE_COMMAND + "-?\\d+")) {
            return handleDeleteTask(input, tasks);
        }
        if (input.equals(MARK_COMMAND_NAME) || input.equals(UNMARK_COMMAND_NAME)
                || input.equals(DELETE_COMMAND_NAME)) {
            return error("Eh, you need to provide a task number ah.");
        }
        if (input.equals(TODO_COMMAND_NAME)) {
            return error("Yoo the task name cannot be empty man.");
        }
        if (input.equals(DEADLINE_COMMAND_NAME)) {
            return error("Yoo yr format cmi must use : deadline <description> /by <time>");
        }
        if (input.equals(EVENT_COMMAND_NAME)) {
            return error("Yo this format cannot ah, it must be: event <description> /from <start> /to <end>");
        }
        if (input.startsWith(MARK_COMMAND) || input.startsWith(UNMARK_COMMAND)
                || input.startsWith(DELETE_COMMAND)) {
            return error("Eh, the task number must be a whole number ah.");
        }
        if (input.startsWith(TODO_COMMAND)) {
            return handleAddTodo(input, tasks);
        }
        if (input.startsWith(DEADLINE_COMMAND)) {
            return handleAddDeadline(input, tasks);
        }
        if (input.startsWith(EVENT_COMMAND)) {
            return handleAddEvent(input, tasks);
        }
        return error("I dont gets, not going to do anth.");
    }

    /**
     * Normalises Inputs by replacing aliases with long form
     *
     * @param input Command entered by the user.
     * @return Command that has been normalised, where short form has been processed.
     */
    private String normaliseInput(String input) {
        assert input != null : "Input command must not be null.";

        String trimmedInput = input.trim();
        if (trimmedInput.isEmpty()) {
            return trimmedInput;
        }

        String[] parts = trimmedInput.split("\\s+", 2);
        String command = parts[0].toLowerCase(Locale.ROOT);
        String fullCommand = COMMAND_ALIASES.get(command);
        if (fullCommand == null) {
            if (parts.length == 1) {
                return command;
            }
            return command + " " + parts[1];
        }

        if (parts.length == 1) {
            return fullCommand;
        }

        return fullCommand + " " + parts[1];
    }

    /** Handles a find command. */
    private CommandResult handleFindTasks(String input, TaskList tasks) {
        String keyword = input.substring(FIND_COMMAND.length()).trim();
        if (keyword.isEmpty()) {
            return error("Eh, please provide a keyword to search for eh.");
        }

        List<Task> matchingTasks = tasks.findMatchingTasks(keyword);
        if (matchingTasks.isEmpty()) {
            return success("wah dont have any matching tasks.");
        }

        StringBuilder message = new StringBuilder("Gotcha, here are the matching tasks:\n");
        for (int i = 0; i < matchingTasks.size(); i++) {
            message.append(i + 1).append(". ").append(matchingTasks.get(i));
            if (i < matchingTasks.size() - 1) {
                message.append("\n");
            }
        }
        return success(message.toString());
    }

    /** Handles a mark or unmark command. */
    private CommandResult handleTaskStatus(String input, TaskList tasks, boolean isCompleted) {
        try {
            int taskNumber = Integer.parseInt(input.split(" ")[1]);
            if (taskNumber <= 0) {
                return error("Eh, the task number must be positive ah.");
            }
            int taskIndex = taskNumber - 1;
            Task task = tasks.handleCompletionStatus(taskIndex, isCompleted);
            if (isCompleted) {
                return success("Yippy! " + (taskIndex + 1) + ": " + task
                        + " has been mark completed.");
            }
            return success("Awww " + (taskIndex + 1) + ": " + task
                    + " has been mark uncompleted. :(");
        } catch (NumberFormatException e) {
            return error("W theee, your number is way too big!");
        } catch (IllegalArgumentException e) {
            return error(e.getMessage());
        }
    }

    /** Handles a save command. */
    private CommandResult handleSaveTasks(TaskList tasks) {
        try {
            taskStorage.save(tasks);
            return success("Yr tasks have been saved!");
        } catch (IOException e) {
            return error("Eh an error has occurred while saving!");
        }
    }

    /** Handles a todo command. */
    private CommandResult handleAddTodo(String input, TaskList tasks) {
        String description = input.substring(TODO_COMMAND.length()).trim();
        if (description.isEmpty()) {
            return error("Yoo the task name cannot be empty man.");
        }

        Task task = new ToDo(description);
        try {
            tasks.addTask(task);
        } catch (IllegalArgumentException e) {
            return error(e.getMessage());
        }
        return success("Gotcha boss, the task: " + task + " has been added!");
    }

    /** Handles a deadline command. */
    private CommandResult handleAddDeadline(String input, TaskList tasks) {
        String taskNameAndDeadline = input.substring(DEADLINE_COMMAND.length()).trim();
        int byIndex = taskNameAndDeadline.indexOf(DEADLINE_MARKER);
        if (byIndex < 0) {
            return error("Yoo yr format cmi must use : deadline <description> /by <time>");
        }
        if (taskNameAndDeadline.indexOf(DEADLINE_MARKER,
                byIndex + DEADLINE_MARKER.length()) >= 0) {
            return error("Eh, a deadline can only have one /by parameter ah.");
        }

        String taskName = taskNameAndDeadline.substring(0, byIndex).trim();
        if (taskName.isEmpty()) {
            return error("Yoo the task name cannot be empty man.");
        }

        try {
            LocalDate deadline = formatDate(taskNameAndDeadline
                    .substring(byIndex + DEADLINE_MARKER.length()).trim());
            Deadline task = new Deadline(taskName, deadline);
            tasks.addTask(task);
            return success("Gotcha boss, the task: " + task + " has been added!");
        } catch (IllegalArgumentException e) {
            return error(e.getMessage());
        }
    }

    /** Handles an event command. */
    private CommandResult handleAddEvent(String input, TaskList tasks) {
        String eventAndDuration = input.substring(EVENT_COMMAND.length()).trim();
        int fromIndex = eventAndDuration.indexOf(EVENT_FROM_MARKER);
        int toIndex = eventAndDuration.indexOf(EVENT_TO_MARKER,
                fromIndex + EVENT_FROM_MARKER.length());
        if (fromIndex < 0 || toIndex < 0 || toIndex <= fromIndex + EVENT_FROM_MARKER.length()) {
            return error("Yo this format cannot ah, it must be: event <description> /from <start> /to <end>");
        }
        boolean hasMultipleFromMarkers = eventAndDuration.indexOf(EVENT_FROM_MARKER,
                fromIndex + EVENT_FROM_MARKER.length()) >= 0;
        boolean hasMultipleToMarkers = eventAndDuration.indexOf(EVENT_TO_MARKER,
                toIndex + EVENT_TO_MARKER.length()) >= 0;
        if (hasMultipleFromMarkers || hasMultipleToMarkers) {
            return error("Eh, an event can only have one /from and one /to parameter ah.");
        }

        String taskName = eventAndDuration.substring(0, fromIndex).trim();
        if (taskName.isEmpty()) {
            return error("Yoo the task name cannot be empty man.");
        }

        try {
            LocalDate startTime = formatDate(eventAndDuration
                    .substring(fromIndex + EVENT_FROM_MARKER.length(), toIndex).trim());
            LocalDate endTime = formatDate(eventAndDuration
                    .substring(toIndex + EVENT_TO_MARKER.length()).trim());
            Event task = new Event(taskName, startTime, endTime);
            tasks.addTask(task);
            return success("Gotcha boss, the task: " + task + " has been added!");
        } catch (IllegalArgumentException e) {
            return error(e.getMessage());
        }
    }

    /** Handles a delete command. */
    private CommandResult handleDeleteTask(String input, TaskList tasks) {
        try {
            int taskNumber = Integer.parseInt(input.substring(DELETE_COMMAND.length()).trim());
            if (taskNumber <= 0) {
                return error("Eh, the task number must be positive ah.");
            }
            int index = taskNumber - 1;
            Task task = tasks.deleteTask(index);
            return success("Got it ah, I removed your task: " + task);
        } catch (IllegalArgumentException e) {
            return error(e.getMessage());
        }
    }

    /** Creates a successful result that keeps the application running. */
    private CommandResult success(String message) {
        return success(message, false);
    }

    /** Creates a successful result with the specified exit status. */
    private CommandResult success(String message, boolean shouldExit) {
        return new CommandResult(message, shouldExit, false);
    }

    /** Creates an error result that keeps the application running. */
    private CommandResult error(String message) {
        return new CommandResult(message, false, true);
    }

    /**
     * Parses a user-entered date in {@code d/M/yyyy} format using strict calendar validation.
     *
     * @param rawDate Date text entered by the user.
     * @return Parsed date.
     * @throws IllegalArgumentException If the date is invalid or uses the wrong format.
     */
    public static LocalDate formatDate(String rawDate) {
        try {
            return LocalDate.parse(rawDate, INPUT_FORMAT);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Eh your date/time format cannot ah. Use: d/M/yyyy");
        }
    }
}
