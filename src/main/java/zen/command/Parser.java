package zen.command;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import zen.ZenException;
import zen.task.Deadline;
import zen.task.Event;
import zen.task.Priority;
import zen.task.Todo;

/** Converts user input into commands and validates command arguments. */
public class Parser {
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String DATE_TIME_FORMAT = DATE_FORMAT + " HH:mm:ss";
    private static final String DEADLINE_BY_DELIMITER = "/by";
    private static final String EVENT_FROM_DELIMITER = "/from";
    private static final String EVENT_TO_DELIMITER = "/to";
    private static final String PRIORITY_FORMAT = "Please choose high, medium, or low for priority."
            + "\nPriority format: /priority <high|medium|low>";
    private static final String DEADLINE_FORMAT = "\nDeadline format: deadline <description> /by " + DATE_TIME_FORMAT;
    private static final String EVENT_FORMAT = String.format("\nEvent format: event <description> %s %s %s %s",
            EVENT_FROM_DELIMITER, DATE_TIME_FORMAT, EVENT_TO_DELIMITER, DATE_TIME_FORMAT);
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter
            .ofPattern("uuuu-MM-dd HH:mm:ss")
            .withResolverStyle(ResolverStyle.STRICT);
    private static final Pattern DEADLINE_BY_TOKEN_PATTERN = Pattern.compile("(?<!\\S)/by(?!\\S)");
    private static final Pattern EVENT_FROM_TOKEN_PATTERN = Pattern.compile("(?<!\\S)/from(?!\\S)");
    private static final Pattern EVENT_TO_TOKEN_PATTERN = Pattern.compile("(?<!\\S)/to(?!\\S)");
    private static final Pattern PRIORITY_TOKEN_PATTERN = Pattern.compile("(?<!\\S)/priority(?!\\S)");
    private static final Pattern TERMINAL_PRIORITY_PATTERN = Pattern.compile("(?s)^(.*)\\s+/priority\\s+(\\S+)\\s*$");

    /**
     * Converts one line of user input into the command that will handle it.
     *
     * @param fullCommand raw input entered by the user
     * @return the matching command, or an {@link UnknownCommand} for an unsupported keyword
     */
    public static Command parse(String fullCommand) {
        if (fullCommand.isEmpty()) {
            return new UnknownCommand();
        }

        String[] parts = fullCommand.trim().split("\\s+", 2);
        String command = parts[0].toLowerCase();
        String arguments = parts.length == 2 ? parts[1] : "";

        return switch (command) {
            case "list" -> new ListCommand(arguments);
            case "mark" -> new MarkCommand(arguments);
            case "unmark" -> new UnmarkCommand(arguments);
            case "occur" -> new OccurCommand(arguments);
            case "find" -> new FindCommand(arguments);
            case "delete" -> new DeleteCommand(arguments);
            case "todo" -> new TodoCommand(arguments);
            case "deadline" -> new DeadlineCommand(arguments);
            case "event" -> new EventCommand(arguments);
            case "bye" -> new ExitCommand(arguments);
            default -> new UnknownCommand();
        };
    }

    /**
     * Validates that no arguments were supplied for a command that expects none.
     *
     * @param args    the argument string following the command keyword.
     * @param command the name of the command, used in the error message if validation fails.
     * @throws ZenException if {@code args} is non-empty.
     */
    public static void requireNoArguments(String args, String command) throws ZenException {
        if (!args.isEmpty()) {
            throw new ZenException("The " + command + " command does not take arguments.");
        }
    }

    /**
     * Validates that a command argument contains non-whitespace text.
     *
     * @param args the argument string to validate
     * @param command the command name used in the error message
     * @param argumentName the required argument name used in the error message
     * @throws ZenException if {@code args} is blank
     */
    public static void requireNonBlankArgument(String args, String command, String argumentName) throws ZenException {
        if (args.isBlank()) {
            throw new ZenException("The " + command + " command requires a " + argumentName + ".");
        }
    }

    /**
     * Parses an ISO-8601 date from command arguments.
     *
     * @param arguments the text expected in {@code yyyy-MM-dd} format.
     * @return the parsed date.
     * @throws ZenException if the text is not a valid ISO-8601 date.
     */
    public static LocalDate parseDate(String arguments) throws ZenException {
        try {
            return LocalDate.parse(arguments);
        } catch (DateTimeParseException e) {
            throw new ZenException("The date is not in the correct format.\nCorrect format: "
                    + DATE_FORMAT);
        }
    }

    /**
     * Parses a task number from a user-supplied argument string.
     *
     * @param arguments the raw argument string expected to contain a positive integer.
     * @return the parsed index as an integer.
     * @throws ZenException if {@code arguments} is not a positive integer.
     */
    public static int parseTaskNumber(String arguments) throws ZenException {
        if (!arguments.matches("\\d+")) {
            throw new ZenException("The task number must be a positive integer.");
        }
        try {
            int taskNumber = Integer.parseInt(arguments);
            if (taskNumber <= 0) {
                throw new ZenException("The task number must be a positive integer.");
            }
            return taskNumber;
        } catch (NumberFormatException exception) {
            throw new ZenException("The task number must be a positive integer.");
        }
    }

    /**
     * Parses a todo description and its optional terminal priority clause.
     *
     * @param userInput the todo command arguments
     * @return a todo with the parsed description and priority
     * @throws ZenException if the description or priority clause is invalid
     */
    public static Todo parseTodo(String userInput) throws ZenException {
        PriorityArguments priorityArguments = extractPriorityArguments(userInput);
        if (priorityArguments.details().isEmpty()) {
            throw new ZenException("The to-do description cannot be empty. Please try again.");
        }
        validateTaskDescription(priorityArguments.details());
        return new Todo(priorityArguments.details(), priorityArguments.priority());
    }

    /**
     * Parses a {@link Deadline} task from raw user input of the form
     * {@code <description> /by <due by>}.
     *
     * @param userInput the full user input string for the deadline command,
     *                  excluding the leading "deadline" keyword.
     * @return a new {@code Deadline} constructed from the parsed description and due date.
     * @throws ZenException if {@code /by} is missing, appears more than once,
     *                       or if the description or due-by value is empty.
     */
    public static Deadline parseDeadline(String userInput) throws ZenException {
        PriorityArguments priorityArguments = extractPriorityArguments(userInput);
        userInput = priorityArguments.details();
        Matcher byMatcher = DEADLINE_BY_TOKEN_PATTERN.matcher(userInput);
        int byIdx = byMatcher.find() ? byMatcher.start() : -1;

        if (byIdx == -1) {
            throw new ZenException("Please include " + DEADLINE_BY_DELIMITER
                    + " in your deadline to separate the description "
                    + "from the due date and time."
                    + DEADLINE_FORMAT);
        }

        String description = userInput.substring(0, byIdx).trim();
        if (description.isEmpty()) {
            throw new ZenException("The deadline description cannot be empty. Please try again."
                    + DEADLINE_FORMAT);
        }
        validateTaskDescription(description);

        if (byMatcher.find()) {
            throw new ZenException("Please include only one " + DEADLINE_BY_DELIMITER
                    + " in your deadline to separate the description "
                    + "from the due date and time."
                    + DEADLINE_FORMAT);
        }

        String dueBy = userInput.substring(byIdx + DEADLINE_BY_DELIMITER.length()).trim();
        if (dueBy.isEmpty()) {
            throw new ZenException("The due date and time cannot be empty. Please try again."
                    + DEADLINE_FORMAT);
        }

        try {
            LocalDateTime dateTime = LocalDateTime.parse(dueBy, DATE_TIME_FORMATTER);
            return new Deadline(description, dateTime, priorityArguments.priority());
        } catch (DateTimeParseException e) {
            throw new ZenException("The due date and time must follow the required format."
                    + DEADLINE_FORMAT);
        }
    }

    /**
     * Parses an {@link Event} task from raw user input of the form
     * {@code <description> /from <start> /to <end>}.
     *
     * @param userInput the full user input string for the event command,
     *                  excluding the leading "event" keyword.
     * @return a new {@code Event} constructed from the parsed description, start, and end times.
     * @throws ZenException if {@code /from} or {@code /to} is missing, either appears more than
     *                       once, {@code /from} does not precede {@code /to}, or if the
     *                       description, start, or end value is empty.
     */
    public static Event parseEvent(String userInput) throws ZenException {
        PriorityArguments priorityArguments = extractPriorityArguments(userInput);
        userInput = priorityArguments.details();
        Matcher fromMatcher = EVENT_FROM_TOKEN_PATTERN.matcher(userInput);
        Matcher toMatcher = EVENT_TO_TOKEN_PATTERN.matcher(userInput);
        int fromIdx = fromMatcher.find() ? fromMatcher.start() : -1;
        int toIdx = toMatcher.find() ? toMatcher.start() : -1;

        validateEventDelimiters(fromMatcher, toMatcher, fromIdx, toIdx);

        String description = userInput.substring(0, fromIdx).trim();
        String start = userInput.substring(fromIdx + EVENT_FROM_DELIMITER.length(), toIdx).trim();
        String end = userInput.substring(toIdx + EVENT_TO_DELIMITER.length()).trim();

        validateEventParts(description, start, end);
        validateTaskDescription(description);
        return createEvent(description, start, end, priorityArguments.priority());
    }

    /**
     * Validates that an event command contains one correctly ordered pair of delimiters.
     *
     * @param fromMatcher matcher that has found the first {@code /from} delimiter, if present
     * @param toMatcher matcher that has found the first {@code /to} delimiter, if present
     * @param fromIdx position of the {@code /from} delimiter
     * @param toIdx position of the {@code /to} delimiter
     * @throws ZenException if either delimiter is missing, duplicated, or out of order
     */
    private static void validateEventDelimiters(Matcher fromMatcher, Matcher toMatcher, int fromIdx, int toIdx)
            throws ZenException {
        if (fromIdx == -1) {
            throw new ZenException("Please include " + EVENT_FROM_DELIMITER
                    + " in your event to separate the description from the timings." + EVENT_FORMAT);
        }
        if (toIdx == -1) {
            throw new ZenException("Please include " + EVENT_TO_DELIMITER + " in your event." + EVENT_FORMAT);
        }
        if (fromIdx > toIdx) {
            throw new ZenException(EVENT_FROM_DELIMITER + " must come before " + EVENT_TO_DELIMITER + "."
                    + EVENT_FORMAT);
        }

        if (fromMatcher.find()) {
            throw new ZenException("Please include only one " + EVENT_FROM_DELIMITER + " in your event."
                    + EVENT_FORMAT);
        }
        if (toMatcher.find()) {
            throw new ZenException("Please include only one " + EVENT_TO_DELIMITER + " in your event."
                    + EVENT_FORMAT);
        }
    }

    /**
     * Validates the description and timing text extracted from an event command.
     *
     * @param description event description
     * @param start event start date and time text
     * @param end event end date and time text
     * @throws ZenException if a required event value is empty
     */
    private static void validateEventParts(String description, String start, String end) throws ZenException {
        if (description.isEmpty()) {
            throw new ZenException("The event description cannot be empty. Please try again." + EVENT_FORMAT);
        }
        if (start.isEmpty()) {
            throw new ZenException("The event start time cannot be empty. Please try again." + EVENT_FORMAT);
        }
        if (end.isEmpty()) {
            throw new ZenException("The event end time cannot be empty. Please try again." + EVENT_FORMAT);
        }
    }

    /**
     * Validates that task text is compatible with the pipe-delimited storage format.
     *
     * @param description task description to validate
     * @throws ZenException if the description contains the storage delimiter
     */
    private static void validateTaskDescription(String description) throws ZenException {
        if (description.contains("|")) {
            throw new ZenException("Task descriptions cannot contain the | character.");
        }
    }

    /**
     * Parses validated event values and creates the corresponding event.
     *
     * @param description event description
     * @param start event start date and time text
     * @param end event end date and time text
     * @return an event with the supplied description and timing
     * @throws ZenException if a time is malformed or the event ends before it starts
     */
    private static Event createEvent(String description, String start, String end, Priority priority)
            throws ZenException {
        try {
            LocalDateTime startDateTime = LocalDateTime.parse(start, DATE_TIME_FORMATTER);
            LocalDateTime endDateTime = LocalDateTime.parse(end, DATE_TIME_FORMATTER);

            if (startDateTime.isAfter(endDateTime)) {
                throw new ZenException("The start date and time is after the end date and time." + EVENT_FORMAT);
            }

            assert !startDateTime.isAfter(endDateTime) : "An event's start must not be after its end";

            return new Event(description, startDateTime, endDateTime, priority);
        } catch (DateTimeParseException e) {
            throw new ZenException("The start and end date and time must follow the required format."
                    + EVENT_FORMAT);
        }
    }

    /**
     * Separates an optional terminal priority clause from task command arguments.
     *
     * @param userInput raw task command arguments
     * @return the details without the priority clause and the selected priority
     * @throws ZenException if a priority clause is missing a value, is not terminal, is duplicated,
     *                      or uses an unsupported value
     */
    private static PriorityArguments extractPriorityArguments(String userInput) throws ZenException {
        Matcher terminalPriorityMatcher = TERMINAL_PRIORITY_PATTERN.matcher(userInput);
        if (!terminalPriorityMatcher.matches()) {
            if (PRIORITY_TOKEN_PATTERN.matcher(userInput).find()) {
                throw new ZenException(PRIORITY_FORMAT);
            }
            return new PriorityArguments(userInput.trim(), Priority.NONE);
        }

        String taskDetails = terminalPriorityMatcher.group(1).trim();
        if (PRIORITY_TOKEN_PATTERN.matcher(taskDetails).find()) {
            throw new ZenException(PRIORITY_FORMAT);
        }

        try {
            Priority priority = Priority.fromCommandValue(terminalPriorityMatcher.group(2));
            return new PriorityArguments(taskDetails, priority);
        } catch (IllegalArgumentException exception) {
            throw new ZenException(PRIORITY_FORMAT);
        }
    }

    /** Stores task details separated from an optional priority clause. */
    private record PriorityArguments(String details, Priority priority) {
    }
}
