package johnny.command;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import johnny.JohnnyException;
import johnny.task.Deadline;
import johnny.task.Event;
import johnny.task.Todo;

/**
 * Parses user input into commands and task objects.
 * All methods are static since Parser holds no state.
 */
public class Parser {

    private static final String DEADLINE_PARAMETER = "/by";
    private static final Pattern DEADLINE_PARAMETER_PATTERN = Pattern.compile("(?<!\\S)/by(?!\\S)");
    private static final String EVENT_START_PARAMETER = "/from";
    private static final Pattern EVENT_START_PARAMETER_PATTERN = Pattern.compile("(?<!\\S)/from(?!\\S)");
    private static final String EVENT_END_PARAMETER = "/to";
    private static final Pattern EVENT_END_PARAMETER_PATTERN = Pattern.compile("(?<!\\S)/to(?!\\S)");

    /**
     * Extracts the command keyword from the user's input.
     * Returns Command.UNKNOWN for unrecognized keywords.
     */
    public static Command parseCommand(String input) {
        String trimmedInput = input.trim();
        if (trimmedInput.isEmpty()) {
            return Command.UNKNOWN;
        }
        String keyword = trimmedInput.split("\\s+", 2)[0].toUpperCase(Locale.ROOT);
        try {
            return Command.valueOf(keyword);
        } catch (IllegalArgumentException e) {
            return Command.UNKNOWN;
        }
    }

    /**
     * Extracts the argument portion of the user's input (everything after
     * the first word). Returns an empty string if there are no arguments.
     */
    public static String parseArguments(String input) {
        String trimmedInput = input.trim();
        String[] inputParts = trimmedInput.split("\\s+", 2);
        return inputParts.length > 1 ? inputParts[1].trim() : "";
    }

    /**
     * Parses a 1-based task index from the user's argument string.
     * Throws JohnnyException if the argument is missing, non-numeric, or out of range.
     */
    public static int parseTaskIndex(String arguments, int taskCount) throws JohnnyException {
        if (arguments.trim().isEmpty()) {
            throw new JohnnyException("Please provide a task number.");
        }
        int index;
        try {
            index = Integer.parseInt(arguments.trim()) - 1;
        } catch (NumberFormatException e) {
            throw new JohnnyException("'" + arguments.trim() + "' is not a valid task number.");
        }
        if (index < 0 || index >= taskCount) {
            throw new JohnnyException("Task number " + (index + 1) + " is out of range. "
                    + "You have " + taskCount + (taskCount == 1 ? " task." : " tasks."));
        }
        assert index >= 0 && index < taskCount : "Validated task index must be within range";
        return index;
    }

    /**
     * Parses the arguments of a "todo" command into a Todo task.
     */
    public static Todo parseTodo(String arguments) throws JohnnyException {
        if (arguments.trim().isEmpty()) {
            throw new JohnnyException("The description of a todo cannot be empty.");
        }
        String description = arguments.trim();
        rejectStorageDelimiter(description);
        return new Todo(description);
    }

    /**
     * Parses the arguments of a "deadline" command into a Deadline task.
     * Expected format: {@code <description> /by <yyyy-MM-dd>}
     */
    public static Deadline parseDeadline(String arguments) throws JohnnyException {
        String formatError = "Invalid deadline format. Use: deadline <description> /by <date>";
        int byIndex = getSingleParameterIndex(arguments, DEADLINE_PARAMETER_PATTERN,
                DEADLINE_PARAMETER, formatError);
        String description = arguments.substring(0, byIndex).trim();
        String dueDateText = arguments.substring(byIndex + DEADLINE_PARAMETER.length()).trim();
        if (description.isEmpty()) {
            throw new JohnnyException("The description of a deadline cannot be empty.");
        }
        rejectStorageDelimiter(description);
        if (dueDateText.isEmpty()) {
            throw new JohnnyException("The deadline date cannot be empty.");
        }
        LocalDate dueDate = parseDate(dueDateText,
                "Invalid date format. Please use yyyy-MM-dd (e.g., 2019-10-15).");
        return new Deadline(description, dueDate);
    }

    /**
     * Parses the arguments of an "event" command into an Event task.
     * Expected format: {@code <description> /from <yyyy-MM-dd> /to <yyyy-MM-dd>}
     */
    public static Event parseEvent(String arguments) throws JohnnyException {
        String formatError = "Invalid event format. Use: event <description> /from <date> /to <date>";
        int fromIndex = getSingleParameterIndex(arguments, EVENT_START_PARAMETER_PATTERN,
                EVENT_START_PARAMETER, formatError);
        int toIndex = getSingleParameterIndex(arguments, EVENT_END_PARAMETER_PATTERN,
                EVENT_END_PARAMETER, formatError);
        if (fromIndex > toIndex) {
            throw new JohnnyException(
                    "Invalid event format. /from must come before /to.");
        }
        String description = arguments.substring(0, fromIndex).trim();
        String startDateText = arguments.substring(
                fromIndex + EVENT_START_PARAMETER.length(), toIndex).trim();
        String endDateText = arguments.substring(toIndex + EVENT_END_PARAMETER.length()).trim();
        if (description.isEmpty()) {
            throw new JohnnyException("The description of an event cannot be empty.");
        }
        rejectStorageDelimiter(description);
        if (startDateText.isEmpty()) {
            throw new JohnnyException("The start date of an event cannot be empty.");
        }
        if (endDateText.isEmpty()) {
            throw new JohnnyException("The end date of an event cannot be empty.");
        }
        LocalDate startDate = parseDate(startDateText,
                "Invalid start date format. Please use yyyy-MM-dd (e.g., 2019-10-15).");
        LocalDate endDate = parseDate(endDateText,
                "Invalid end date format. Please use yyyy-MM-dd (e.g., 2019-10-15).");
        if (!startDate.isBefore(endDate)) {
            throw new JohnnyException("The event start date must be before the end date.");
        }
        return new Event(description, startDate, endDate);
    }

    /** Returns the position of a required parameter that occurs exactly once. */
    private static int getSingleParameterIndex(String arguments, Pattern parameterPattern,
            String parameter, String formatError) throws JohnnyException {
        Matcher matcher = parameterPattern.matcher(arguments);
        if (!matcher.find()) {
            throw new JohnnyException(formatError);
        }
        int parameterIndex = matcher.start();
        if (matcher.find()) {
            throw new JohnnyException("The " + parameter + " parameter must be specified only once.");
        }
        return parameterIndex;
    }

    /** Rejects descriptions that cannot be represented safely in the storage format. */
    private static void rejectStorageDelimiter(String description) throws JohnnyException {
        if (description.contains("|")) {
            throw new JohnnyException("Task descriptions cannot contain the '|' character.");
        }
    }

    /**
     * Parses an ISO date and translates formatting failures into a user-facing error.
     *
     * @param dateText date in {@code yyyy-MM-dd} format
     * @param errorMessage message to show when the date is invalid
     * @return parsed date
     * @throws JohnnyException if the date is invalid
     */
    private static LocalDate parseDate(String dateText, String errorMessage) throws JohnnyException {
        try {
            return LocalDate.parse(dateText);
        } catch (DateTimeParseException e) {
            throw new JohnnyException(errorMessage);
        }
    }
}
