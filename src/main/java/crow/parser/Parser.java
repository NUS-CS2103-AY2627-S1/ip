package crow.parser;

import crow.exception.CrowException;
import crow.task.Deadline;
import crow.task.Event;
import crow.task.Todo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Interprets user input and converts command arguments into program data.
 */
public class Parser {
    private static final Pattern DEADLINE_SEPARATOR = Pattern.compile(
            "\\s+/by\\s+", Pattern.CASE_INSENSITIVE);
    private static final Pattern EVENT_START_SEPARATOR = Pattern.compile(
            "\\s+/from\\s+", Pattern.CASE_INSENSITIVE);
    private static final Pattern EVENT_END_SEPARATOR = Pattern.compile(
            "\\s+/to\\s+", Pattern.CASE_INSENSITIVE);
    private static final DateTimeFormatter INPUT_DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("d/M/uuuu HHmm").withResolverStyle(ResolverStyle.STRICT);

    /**
     * Returns the type represented by the first word of the input.
     *
     * @param input Full user input.
     * @return Recognized command type, or {@link CommandType#UNKNOWN}.
     */
    public static CommandType parseCommandType(String input) {
        String[] inputParts = input.trim().split("\\s+", 2);
        return CommandType.parseCommandWord(inputParts[0]);
    }

    /**
     * Returns everything after the command word.
     *
     * @param input Full user input.
     * @return Trimmed command arguments, or an empty string if absent.
     */
    public static String parseArguments(String input) {
        String[] inputParts = input.trim().split("\\s+", 2);
        return inputParts.length > 1 ? inputParts[1].trim() : "";
    }

    /**
     * Checks whether an input is a valid command to exit the application.
     */
    public static boolean isExitCommand(String input) {
        return parseCommandType(input) == CommandType.BYE && parseArguments(input).isEmpty();
    }

    /**
     * Creates a todo from its command arguments.
     *
     * @param arguments Todo description.
     * @return Parsed todo.
     * @throws CrowException If the description is empty.
     */
    public static Todo parseTodo(String arguments) throws CrowException {
        return new Todo(parseDescription(arguments, "Todo"));
    }

    /**
     * Creates a deadline from its command arguments.
     *
     * @param arguments Deadline description and date-time.
     * @return Parsed deadline.
     * @throws CrowException If the arguments are incomplete or invalid.
     */
    public static Deadline parseDeadline(String arguments) throws CrowException {
        Matcher separatorMatcher = DEADLINE_SEPARATOR.matcher(arguments.trim());
        if (!separatorMatcher.find()) {
            throw new CrowException("Error: Use deadline DESCRIPTION /by d/M/yyyy HHmm.");
        }
        int descriptionEndIndex = separatorMatcher.start();
        int dateTimeStartIndex = separatorMatcher.end();
        if (separatorMatcher.find()) {
            throw new CrowException("Error: A deadline must contain exactly one /by parameter.");
        }

        String description = parseDescription(arguments.substring(0, descriptionEndIndex), "Deadline");
        String dateTimeInput = arguments.substring(dateTimeStartIndex).trim();
        if (dateTimeInput.isEmpty()) {
            throw new CrowException("Error: Use deadline DESCRIPTION /by d/M/yyyy HHmm.");
        }
        LocalDateTime deadlineDateTime = parseDateTime(dateTimeInput);
        return new Deadline(description, deadlineDateTime);
    }

    /**
     * Creates an event from its command arguments.
     *
     * @param arguments Event description, start, and end date-times.
     * @return Parsed event.
     * @throws CrowException If the arguments are incomplete or invalid.
     */
    public static Event parseEvent(String arguments) throws CrowException {
        String trimmedArguments = arguments.trim();
        Matcher startMatcher = EVENT_START_SEPARATOR.matcher(trimmedArguments);
        if (!startMatcher.find()) {
            throw createInvalidEventFormatException();
        }
        int descriptionEndIndex = startMatcher.start();
        int startDateTimeIndex = startMatcher.end();
        if (startMatcher.find()) {
            throw new CrowException("Error: An event must contain exactly one /from parameter.");
        }

        Matcher endMatcher = EVENT_END_SEPARATOR.matcher(trimmedArguments);
        if (!endMatcher.find() || endMatcher.start() < startDateTimeIndex) {
            throw createInvalidEventFormatException();
        }
        int startDateTimeEndIndex = endMatcher.start();
        int endDateTimeStartIndex = endMatcher.end();
        if (endMatcher.find()) {
            throw new CrowException("Error: An event must contain exactly one /to parameter.");
        }

        String description = parseDescription(trimmedArguments.substring(0, descriptionEndIndex), "Event");
        String startDateTimeInput = trimmedArguments
                .substring(startDateTimeIndex, startDateTimeEndIndex)
                .trim();
        String endDateTimeInput = trimmedArguments.substring(endDateTimeStartIndex).trim();
        if (startDateTimeInput.isEmpty() || endDateTimeInput.isEmpty()) {
            throw createInvalidEventFormatException();
        }
        LocalDateTime startDateTime = parseDateTime(startDateTimeInput);
        LocalDateTime endDateTime = parseDateTime(endDateTimeInput);
        if (!startDateTime.isBefore(endDateTime)) {
            throw new CrowException("Error: Event start must be before its end.");
        }
        return new Event(description, startDateTime, endDateTime);
    }

    /**
     * Validates and returns a keyword used to search for tasks.
     *
     * @param arguments Search keyword.
     * @return Validated search keyword.
     * @throws CrowException If the keyword is empty.
     */
    public static String parseFindKeyword(String arguments) throws CrowException {
        String keyword = normalizeWhitespace(arguments);
        if (keyword.isEmpty()) {
            throw new CrowException("Error: Search keyword cannot be empty.");
        }
        return keyword;
    }

    /**
     * Ensures that a command that takes no parameters has none.
     *
     * @param arguments Text following the command word.
     * @param commandName Name shown in an error message.
     * @throws CrowException If unexpected parameters are present.
     */
    public static void validateNoArguments(String arguments, String commandName) throws CrowException {
        if (!arguments.isBlank()) {
            throw new CrowException(
                    "Error: " + commandName + " does not accept parameters.");
        }
    }

    /**
     * Converts a user-provided task number into a valid list index.
     *
     * @param input User-provided task number.
     * @param taskCount Number of tasks currently stored.
     * @return Zero-based list index of the task.
     * @throws CrowException If the input is not a number of an existing task.
     */
    public static int parseTaskIndex(String input, int taskCount) throws CrowException {
        try {
            int taskIndex = Integer.parseInt(input.trim()) - 1;
            if (taskIndex < 0 || taskIndex >= taskCount) {
                throw new CrowException("Error: Enter a valid task number.");
            }
            return taskIndex;
        } catch (NumberFormatException e) {
            throw new CrowException("Error: Enter a valid task number.");
        }
    }

    /**
     * Parses a date and time written in the format {@code d/M/yyyy HHmm}.
     */
    private static LocalDateTime parseDateTime(String input) throws CrowException {
        try {
            return LocalDateTime.parse(normalizeWhitespace(input), INPUT_DATE_TIME_FORMAT);
        } catch (DateTimeParseException e) {
            throw new CrowException("Error: Date and time must use d/M/yyyy HHmm format.");
        }
    }

    /**
     * Creates the standard error for malformed event arguments.
     */
    private static CrowException createInvalidEventFormatException() {
        return new CrowException("Error: Use event DESCRIPTION /from d/M/yyyy HHmm /to d/M/yyyy HHmm.");
    }

    /**
     * Validates and normalizes a task description.
     */
    private static String parseDescription(String input, String taskType) throws CrowException {
        String description = normalizeWhitespace(input);
        if (description.isEmpty()) {
            throw new CrowException("Error: " + taskType + " description cannot be empty.");
        }
        if (description.contains("|")) {
            throw new CrowException("Error: Task descriptions cannot contain '|'.");
        }
        return description;
    }

    /**
     * Trims text and replaces consecutive whitespace with one space.
     */
    private static String normalizeWhitespace(String input) {
        return input.trim().replaceAll("\\s+", " ");
    }
}
