package zinc.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import zinc.ui.Ui;

/**
 * Parses and executes commands that operate on tasks.
 */
public class TaskCommandHandler {
    /** The maximum number of characters accepted in a task description. */
    private static final int MAX_DESCRIPTION_LENGTH = 300;

    /** Accepts a date with a compact 24-hour time, such as 31/08/26 1800. */
    private static final DateTimeFormatter COMPACT_DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/uu HHmm")
                    .withResolverStyle(ResolverStyle.STRICT);

    /** Accepts a date with a colon-separated 24-hour time, such as 31/08/26 18:00. */
    private static final DateTimeFormatter COLON_DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/uu HH:mm")
                    .withResolverStyle(ResolverStyle.STRICT);

    /** Accepts the calendar date used to filter deadlines and events. */
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/uu")
            .withResolverStyle(ResolverStyle.STRICT);

    /** Identifies the separator between a deadline description and its date. */
    private static final Pattern DEADLINE_SEPARATOR_PATTERN = Pattern.compile("\\s+/by\\s+",
            Pattern.CASE_INSENSITIVE);

    /** Identifies event start and end field prefixes. */
    private static final Pattern EVENT_SEPARATOR_PATTERN = Pattern.compile("\\s+/(from|to)\\s+",
            Pattern.CASE_INSENSITIVE);

    /** The task list affected by commands. */
    private final TaskList taskList;

    /** The UI used to display validation messages. */
    private final Ui ui;

    /** Task commands indexed by their user-facing names. */
    private final Map<String, Consumer<String>> commands;

    /**
     * Creates a handler for commands that affect the supplied task list.
     *
     * @param taskList The task list to update.
     * @param ui The UI used to display validation messages.
     */
    public TaskCommandHandler(TaskList taskList, Ui ui) {
        assert taskList != null && ui != null : "Task command dependencies must not be null";
        this.taskList = taskList;
        this.ui = ui;
        this.commands = createCommands();
    }

    /**
     * Executes a task command when its name is recognized.
     *
     * @param command The task command name.
     * @param parameters The parameters following the command name.
     * @return {@code true} if the command was recognized; otherwise, {@code false}.
     */
    public boolean execute(String command, String parameters) {
        assert command != null && parameters != null : "Task command input must not be null";
        Consumer<String> selectedCommand = commands.get(command.toLowerCase(Locale.ROOT));
        if (selectedCommand == null) {
            return false;
        }

        selectedCommand.accept(parameters);
        return true;
    }

    /** Creates the command registry used to dispatch task operations. */
    private Map<String, Consumer<String>> createCommands() {
        return Map.of(
                "list", this::listTasks,
                "ls", this::listTasksUsingAlias,
                "find", this::findTasks,
                "mark", parameters -> changeTaskCompletion(parameters, true),
                "unmark", parameters -> changeTaskCompletion(parameters, false),
                "todo", this::addTodo,
                "deadline", this::addDeadline,
                "event", this::addEvent,
                "delete", this::deleteTask);
    }

    /** Lists every task or only tasks ending on a supplied date. */
    private void listTasks(String parameters) {
        if (parameters.isEmpty()) {
            taskList.printTasks();
            return;
        }

        try {
            taskList.printTasksEndingOn(LocalDate.parse(parameters, DATE_FORMAT));
        } catch (DateTimeParseException exception) {
            ui.printListDateError();
        }
    }

    /** Lists every task through the short alias when no arguments are supplied. */
    private void listTasksUsingAlias(String parameters) {
        if (!parameters.isEmpty()) {
            ui.printUnexpectedArguments("ls");
            return;
        }
        taskList.printTasks();
    }

    /** Adds a todo when it has a description. */
    private void addTodo(String description) {
        if (description.isEmpty()) {
            ui.printTodoUsage();
            return;
        }
        if (description.length() > MAX_DESCRIPTION_LENGTH) {
            ui.printFieldTooLong("Task description", MAX_DESCRIPTION_LENGTH);
            return;
        }

        taskList.addTask(new Todo(description));
    }

    /** Adds a deadline when it has a description and a due date. */
    private void addDeadline(String parameters) {
        Matcher separatorMatcher = DEADLINE_SEPARATOR_PATTERN.matcher(parameters);
        if (!separatorMatcher.find()) {
            ui.printDeadlineUsage();
            return;
        }

        String description = parameters.substring(0, separatorMatcher.start()).strip();
        String dateTimeText = parameters.substring(separatorMatcher.end()).strip();
        if (description.isEmpty() || dateTimeText.isEmpty() || separatorMatcher.find()) {
            ui.printDeadlineUsage();
            return;
        }
        if (description.length() > MAX_DESCRIPTION_LENGTH) {
            ui.printFieldTooLong("Task description", MAX_DESCRIPTION_LENGTH);
            return;
        }

        try {
            taskList.addTask(new Deadline(description, parseDateTime(dateTimeText)));
        } catch (DateTimeParseException exception) {
            ui.printDateTimeError();
        }
    }

    /** Adds an event when it has a description, start time, and end time. */
    private void addEvent(String parameters) {
        String[] eventParts = parseEventParts(parameters);
        if (eventParts == null) {
            ui.printEventUsage();
            return;
        }
        if (eventParts[0].length() > MAX_DESCRIPTION_LENGTH) {
            ui.printFieldTooLong("Task description", MAX_DESCRIPTION_LENGTH);
            return;
        }

        try {
            LocalDateTime start = parseDateTime(eventParts[1]);
            LocalDateTime end = parseDateTime(eventParts[2]);
            if (end.isBefore(start)) {
                ui.printEventChronologyError();
                return;
            }

            taskList.addTask(new Event(eventParts[0].trim(), start, end));
        } catch (DateTimeParseException exception) {
            ui.printDateTimeError();
        }
    }

    /** Converts a command date to a date-time, using midnight when no time is given. */
    private LocalDateTime parseDateTime(String dateTimeText) {
        String normalizedDateTime = dateTimeText.strip().replaceAll("\\s+", " ");
        if (!normalizedDateTime.contains(" ")) {
            normalizedDateTime += " 0000";
        }

        try {
            return LocalDateTime.parse(normalizedDateTime, COMPACT_DATE_TIME_FORMAT);
        } catch (DateTimeParseException compactFormatException) {
            return LocalDateTime.parse(normalizedDateTime, COLON_DATE_TIME_FORMAT);
        }
    }

    /** Extracts an event description, start, and end when the field order is valid. */
    private String[] parseEventParts(String parameters) {
        Matcher separatorMatcher = EVENT_SEPARATOR_PATTERN.matcher(parameters);
        if (!separatorMatcher.find() || !separatorMatcher.group(1).equalsIgnoreCase("from")) {
            return null;
        }

        String description = parameters.substring(0, separatorMatcher.start()).strip();
        int startValueIndex = separatorMatcher.end();
        if (!separatorMatcher.find() || !separatorMatcher.group(1).equalsIgnoreCase("to")) {
            return null;
        }

        String startText = parameters.substring(startValueIndex, separatorMatcher.start()).strip();
        String endText = parameters.substring(separatorMatcher.end()).strip();
        if (description.isEmpty() || startText.isEmpty() || endText.isEmpty() || separatorMatcher.find()) {
            return null;
        }
        return new String[]{description, startText, endText};
    }

    /** Finds tasks whose descriptions contain the supplied keyword. */
    private void findTasks(String keyword) {
        if (keyword.isBlank()) {
            ui.printFindUsage();
            return;
        }
        taskList.printTasksContaining(keyword);
    }

    /** Changes the completion state of the task at the supplied user-facing task number. */
    private void changeTaskCompletion(String parameters, boolean shouldMarkAsCompleted) {
        String command = shouldMarkAsCompleted ? "mark" : "unmark";
        Integer taskNumber = parseTaskNumber(parameters, command);
        if (taskNumber == null) {
            return;
        }

        if (shouldMarkAsCompleted) {
            taskList.markTaskAsCompleted(taskNumber);
        } else {
            taskList.markTaskAsIncomplete(taskNumber);
        }
    }

    /** Parses a positive task number, printing usage guidance when it is invalid. */
    private Integer parseTaskNumber(String parameters, String command) {
        if (!parameters.matches("[1-9]\\d*")) {
            ui.printTaskNumberError(command);
            return null;
        }

        try {
            return Integer.parseInt(parameters);
        } catch (NumberFormatException exception) {
            ui.printTaskNumberTooLarge(command);
            return null;
        }
    }

    /** Deletes the task at the supplied user-facing task number. */
    private void deleteTask(String parameters) {
        Integer taskNumber = parseTaskNumber(parameters, "delete");
        if (taskNumber == null) {
            return;
        }
        taskList.deleteTask(taskNumber);
    }
}
