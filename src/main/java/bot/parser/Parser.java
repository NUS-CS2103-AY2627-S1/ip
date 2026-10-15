package bot.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bot.exception.BotException;
import bot.task.Deadline;
import bot.task.Event;
import bot.task.Todo;

/**
 * Converts user input into commands and validates command arguments.
 */
public final class Parser {
    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s+");
    private static final Pattern DEADLINE_MARKER_PATTERN = Pattern.compile("(?<!\\S)/by(?!\\S)");
    private static final Pattern EVENT_START_MARKER_PATTERN = Pattern.compile("(?<!\\S)/from(?!\\S)");
    private static final Pattern EVENT_END_MARKER_PATTERN = Pattern.compile("(?<!\\S)/to(?!\\S)");

    private static final String DEADLINE_MARKER = "/by";
    private static final String EVENT_START_MARKER = "/from";
    private static final String EVENT_END_MARKER = "/to";
    private static final String STORAGE_FIELD_SEPARATOR = "|";

    private static final DateTimeFormatter DEADLINE_INPUT_FORMAT =
            DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter EVENT_INPUT_FORMAT =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm").withResolverStyle(ResolverStyle.STRICT);

    private Parser() {
    }

    /**
     * Splits one line of input into its command word and arguments.
     *
     * @param input line entered by the user.
     * @return parsed command information
     */
    public static ParsedCommand parse(String input) {
        String normalizedInput = normalizeWhitespace(input);
        String[] parts = normalizedInput.split("\\s+", 2);
        String commandWord = parts[0];
        String arguments = parts.length == 2 ? parts[1] : "";
        return new ParsedCommand(CommandType.from(commandWord), commandWord, arguments);
    }

    /**
     * Creates a todo after checking that it has a description.
     *
     * @param arguments text following the todo command.
     * @return parsed todo
     * @throws BotException if the description is empty
     */
    public static Todo parseTodo(String arguments) throws BotException {
        arguments = normalizeWhitespace(arguments);
        if (arguments.isEmpty()) {
            throw new BotException("The description of a todo cannot be empty.");
        }
        ensureStorableDescription(arguments);
        return new Todo(arguments);
    }

    /**
     * Creates a deadline after checking its description and date.
     *
     * @param arguments text following the deadline command.
     * @return parsed deadline
     * @throws BotException if required deadline information is missing or invalid
     */
    public static Deadline parseDeadline(String arguments) throws BotException {
        arguments = normalizeWhitespace(arguments);
        if (arguments.isEmpty()) {
            throw new BotException("The description of a deadline cannot be empty.");
        }

        int byMarkerCount = countMatches(DEADLINE_MARKER_PATTERN, arguments);
        if (byMarkerCount == 0) {
            throw new BotException("A deadline must include /by followed by its date.");
        }
        if (byMarkerCount > 1) {
            throw new BotException("A deadline can contain only one /by marker.");
        }

        int byMarkerPosition = findMarkerPosition(DEADLINE_MARKER_PATTERN, arguments);
        String description = arguments.substring(0, byMarkerPosition).trim();
        String dateText = arguments.substring(byMarkerPosition + DEADLINE_MARKER.length()).trim();
        if (description.isEmpty()) {
            throw new BotException("The description of a deadline cannot be empty.");
        }
        if (dateText.isEmpty()) {
            throw new BotException("The date of a deadline cannot be empty.");
        }
        ensureStorableDescription(description);
        try {
            LocalDate dueDate = LocalDate.parse(dateText, DEADLINE_INPUT_FORMAT);
            return new Deadline(description, dueDate);
        } catch (DateTimeParseException error) {
            throw new BotException(
                    "Use yyyy-MM-dd for deadline dates, e.g. 2019-12-02.");
        }
    }

    /**
     * Creates an event after checking its description, start, and end.
     *
     * @param arguments text following the event command.
     * @return parsed event
     * @throws BotException if required event information is missing or invalid
     */
    public static Event parseEvent(String arguments) throws BotException {
        arguments = normalizeWhitespace(arguments);
        if (arguments.isEmpty()) {
            throw new BotException("The description of an event cannot be empty.");
        }

        int fromMarkerCount = countMatches(EVENT_START_MARKER_PATTERN, arguments);
        if (fromMarkerCount == 0) {
            throw new BotException("An event must include /from followed by its start time.");
        }
        if (fromMarkerCount > 1) {
            throw new BotException("An event can contain only one /from marker.");
        }

        int toMarkerCount = countMatches(EVENT_END_MARKER_PATTERN, arguments);
        if (toMarkerCount == 0) {
            throw new BotException("An event must include /to followed by its end time.");
        }
        if (toMarkerCount > 1) {
            throw new BotException("An event can contain only one /to marker.");
        }

        int fromMarkerPosition = findMarkerPosition(EVENT_START_MARKER_PATTERN, arguments);
        int toMarkerPosition = findMarkerPosition(EVENT_END_MARKER_PATTERN, arguments);
        if (toMarkerPosition < fromMarkerPosition) {
            throw new BotException("The /from marker must appear before the /to marker.");
        }
        String description = arguments.substring(0, fromMarkerPosition).trim();
        String startDateTimeText = arguments.substring(
                fromMarkerPosition + EVENT_START_MARKER.length(), toMarkerPosition).trim();
        String endDateTimeText = arguments.substring(toMarkerPosition + EVENT_END_MARKER.length()).trim();
        if (description.isEmpty()) {
            throw new BotException("The description of an event cannot be empty.");
        }
        if (startDateTimeText.isEmpty()) {
            throw new BotException("The start time of an event cannot be empty.");
        }
        if (endDateTimeText.isEmpty()) {
            throw new BotException("The end time of an event cannot be empty.");
        }
        ensureStorableDescription(description);
        try {
            LocalDateTime startDateTime = LocalDateTime.parse(startDateTimeText, EVENT_INPUT_FORMAT);
            LocalDateTime endDateTime = LocalDateTime.parse(endDateTimeText, EVENT_INPUT_FORMAT);
            if (!endDateTime.isAfter(startDateTime)) {
                throw new BotException(
                        "The event end date and time must be after its start date and time.");
            }
            return new Event(description, startDateTime, endDateTime);
        } catch (DateTimeParseException error) {
            throw new BotException(
                    "Use yyyy-MM-dd HHmm for event dates and times, e.g. 2019-12-02 1400.");
        }
    }

    /**
     * Parses the date supplied to the on command.
     *
     * @param arguments date text following the on command.
     * @return parsed search date
     * @throws BotException if the date is missing or invalid
     */
    public static LocalDate parseDateQuery(String arguments) throws BotException {
        arguments = normalizeWhitespace(arguments);
        if (arguments.isEmpty()) {
            throw new BotException("Tell me which date to search using yyyy-MM-dd.");
        }
        try {
            return LocalDate.parse(arguments, DEADLINE_INPUT_FORMAT);
        } catch (DateTimeParseException error) {
            throw new BotException(
                    "Use yyyy-MM-dd when searching by date, e.g. 2019-12-02.");
        }
    }

    /**
     * Parses the keyword supplied to the find command.
     *
     * @param arguments keyword text following the find command.
     * @return validated search keyword
     * @throws BotException if the keyword is empty
     */
    public static String parseFindKeyword(String arguments) throws BotException {
        arguments = normalizeWhitespace(arguments);
        if (arguments.isEmpty()) {
            throw new BotException("Tell me what keyword to find.");
        }
        return arguments;
    }

    /**
     * Converts a one-based task number into a valid zero-based index.
     *
     * @param arguments task number entered by the user.
     * @param taskCount number of tasks currently stored.
     * @param command command being processed.
     * @return zero-based index of the selected task
     * @throws BotException if the task number is missing or invalid
     */
    public static int parseTaskIndex(String arguments, int taskCount, String command) throws BotException {
        assert taskCount >= 0 : "Task count must not be negative";
        assert command != null && !command.isBlank() : "Command name must not be blank";

        arguments = normalizeWhitespace(arguments);
        if (arguments.isEmpty()) {
            throw new BotException("Tell me which task number to " + command + ".");
        }
        if (arguments.contains(" ")) {
            throw new BotException("Enter only one task number for the " + command + " command.");
        }

        int taskNumber;
        try {
            taskNumber = Integer.parseInt(arguments);
        } catch (NumberFormatException error) {
            throw new BotException("The task number must be a whole number.");
        }

        if (taskCount == 0) {
            throw new BotException("The task list is empty.");
        }
        if (taskNumber < 1 || taskNumber > taskCount) {
            throw new BotException("Task number " + taskNumber + " does not exist. Choose a number from 1 to "
                    + taskCount + ".");
        }
        return taskNumber - 1;
    }

    /**
     * Checks that a command which takes no arguments was entered correctly.
     *
     * @param arguments text following the command.
     * @param command command being checked.
     * @throws BotException if extra text was supplied
     */
    public static void ensureNoArguments(String arguments, String command) throws BotException {
        arguments = normalizeWhitespace(arguments);
        if (!arguments.isEmpty()) {
            throw new BotException("The " + command + " command does not take extra information.");
        }
    }

    /**
     * Rejects descriptions containing the separator reserved by the storage format.
     *
     * @param description task description to validate.
     * @throws BotException if the description contains the reserved separator
     */
    private static void ensureStorableDescription(String description) throws BotException {
        if (description.contains(STORAGE_FIELD_SEPARATOR)) {
            throw new BotException("Task descriptions cannot contain the | character.");
        }
    }

    /**
     * Returns the first position of a command marker known to be present.
     *
     * @param markerPattern pattern identifying the command marker.
     * @param arguments normalized command arguments.
     * @return zero-based marker position
     */
    private static int findMarkerPosition(Pattern markerPattern, String arguments) {
        Matcher matcher = markerPattern.matcher(arguments);
        boolean hasMarker = matcher.find();
        assert hasMarker : "Command marker must be present before locating it";
        return matcher.start();
    }

    /**
     * Counts occurrences of a command marker in normalized arguments.
     *
     * @param markerPattern pattern identifying the command marker.
     * @param arguments normalized command arguments.
     * @return number of marker occurrences
     */
    private static int countMatches(Pattern markerPattern, String arguments) {
        Matcher matcher = markerPattern.matcher(arguments);
        int matchCount = 0;
        while (matcher.find()) {
            matchCount++;
        }
        return matchCount;
    }

    /**
     * Trims text and reduces each run of whitespace to one space.
     * A null value is treated as empty user input.
     *
     * @param text text to normalize.
     * @return normalized text
     */
    private static String normalizeWhitespace(String text) {
        if (text == null) {
            return "";
        }
        return WHITESPACE_PATTERN.matcher(text.trim()).replaceAll(" ");
    }
}
