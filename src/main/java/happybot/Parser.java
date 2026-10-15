package happybot;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

import happybot.task.Deadline;
import happybot.task.Event;
import happybot.task.ToDo;

/**
 * Deals with making sense of the user command.
 *
 * <p>Each method turns command text into something the rest of HappyBot can act on: a command
 * word, a task number, a date, or a task. Text that cannot be read is rejected here with a
 * HappyBotException carrying the message shown to the user, so no other class has to look at
 * what was typed.
 *
 * <p>A parser keeps nothing between calls, so every method is static.
 */
public class Parser {
    /** Usage messages that show where a date belongs in each command. */
    private static final String BYE_USAGE = "Use: bye.";
    private static final String LIST_USAGE = "Use: list.";
    private static final String DEADLINE_USAGE = "Use: deadline <description> /by <yyyy-MM-dd>.";
    private static final String EVENT_USAGE =
            "Use: event <description> /from <yyyy-MM-dd> /to <yyyy-MM-dd>.";

    /** Usage message for the command that lists the deadlines falling on one date. */
    private static final String DUE_USAGE = "Use: due <date>, such as due 2019-12-02.";

    /** Usage message for the command that finds tasks by keyword. */
    private static final String FIND_USAGE = "Use: find <keyword>, such as find book.";

    /** Usage message for the command that displays task statistics. */
    private static final String STATS_USAGE = "Use: stats.";

    /** Message shown when a command does not name one positive whole task number. */
    private static final String TASK_NUMBER_MESSAGE = "Please provide one positive whole task number.";

    /** Message shown when the user sends no command. */
    private static final String EMPTY_COMMAND_MESSAGE = "Please enter a command.";

    /** Text that separates a deadline description from its due date. */
    private static final String DEADLINE_MARKER = " /by ";

    /** Text that separates an event description from its start date. */
    private static final String EVENT_START_MARKER = " /from ";

    /** Text that separates an event start date from its end date. */
    private static final String EVENT_END_MARKER = " /to ";

    /**
     * Date patterns accepted from the user. The brackets mark each one as optional.
     *
     * <p>Strict resolving rejects a date that names no real day, such as 2019-11-31, instead of
     * quietly moving it back to the last day of the month. Strict resolving needs the year
     * written as uuuu rather than yyyy, because yyyy is a year within an era and an era is
     * never typed.
     */
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("[uuuu-MM-dd][d/M/uuuu]")
                    .withResolverStyle(ResolverStyle.STRICT);

    /**
     * The same patterns resolved leniently, used only to tell the two kinds of bad date apart.
     *
     * <p>A date this reads but DATE_FORMAT rejects was written correctly and simply names no
     * real day, which earns a clearer message than the one about the format.
     */
    private static final DateTimeFormatter LENIENT_DATE_FORMAT =
            DateTimeFormatter.ofPattern("[uuuu-MM-dd][d/M/uuuu]");

    /** Pattern accepted for the optional 24-hour time that may follow a date. */
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HHmm");

    /** Message shown when a date is written correctly but names no day on the calendar. */
    private static final String NO_SUCH_DATE_HINT =
            "There is no such date on the calendar. Please enter a valid date, checking the "
                    + "number of days the month has.";

    /** Message shown when a date and time cannot be read. */
    private static final String DATE_FORMAT_HINT =
            "Please write dates as yyyy-MM-dd or d/M/yyyy, with an optional 24-hour time that "
                    + "defaults to 0000, such as 2019-12-02 1800.";

    /**
     * Holds the parts of a parsed deadline command.
     *
     * <p>The due date keeps whether the user wrote a time, because a date-only deadline and a
     * deadline explicitly due at midnight have different expiry rules.
     */
    static final class DeadlineDetails {
        private final String description;
        private final ParsedDateTime dueDateTime;

        DeadlineDetails(String description, ParsedDateTime dueDateTime) {
            this.description = description;
            this.dueDateTime = dueDateTime;
        }

        String getDescription() {
            return description;
        }

        ParsedDateTime getDueDateTime() {
            return dueDateTime;
        }
    }

    /**
     * Holds the parts of a parsed event command.
     *
     * <p>The start and end retain whether each supplied date included a time, so HappyBot can
     * apply its current-time rule without mistaking a date-only input for midnight.
     */
    static final class EventDetails {
        private final String description;
        private final ParsedDateTime startTime;
        private final ParsedDateTime endTime;

        EventDetails(String description, ParsedDateTime startTime, ParsedDateTime endTime) {
            this.description = description;
            this.startTime = startTime;
            this.endTime = endTime;
        }

        String getDescription() {
            return description;
        }

        ParsedDateTime getStartTime() {
            return startTime;
        }

        ParsedDateTime getEndTime() {
            return endTime;
        }
    }

    /**
     * Holds a parsed date and time together with whether the user entered the time.
     */
    static final class ParsedDateTime {
        private final LocalDateTime dateTime;
        private final boolean hasTime;

        ParsedDateTime(LocalDateTime dateTime, boolean hasTime) {
            this.dateTime = dateTime;
            this.hasTime = hasTime;
        }

        LocalDateTime getDateTime() {
            return dateTime;
        }

        boolean hasTime() {
            return hasTime;
        }
    }

    /**
     * Verifies that a command contains at least one non-whitespace character.
     *
     * <p>Commands intentionally accept accidental leading, trailing and repeated whitespace.
     * Every parser method normalizes that whitespace before interpreting its argument.
     *
     * @param userInput The command line entered by the user.
     * @throws HappyBotException If the command is blank.
     */
    public static void validateCommandInput(String userInput) throws HappyBotException {
        if (userInput == null || normalizeWhitespace(userInput).isEmpty()) {
            throw new HappyBotException(EMPTY_COMMAND_MESSAGE);
        }
    }

    /**
     * Returns the first word of a command, which says what the user wants done.
     *
     * @param userInput The command line, exactly as it was typed.
     * @return The command word, or an empty string when the line holds no word.
     */
    public static String parseCommandWord(String userInput) {
        String normalizedInput = normalizeWhitespace(userInput);
        if (normalizedInput.isEmpty()) {
            return "";
        }

        return normalizedInput.split(" ", 2)[0];
    }

    /**
     * Returns everything a command holds after its command word.
     *
     * @param userInput The command line, exactly as it was typed.
     * @return The text after the first space, or an empty string when there is none.
     */
    public static String parseCommandBody(String userInput) {
        String normalizedInput = normalizeWhitespace(userInput);
        String[] commandParts = normalizedInput.split(" ", 2);
        return commandParts.length == 2 ? commandParts[1] : "";
    }

    /**
     * Rejects arguments supplied to the bye command.
     *
     * @param body The command text after the command word.
     * @throws HappyBotException If an argument was supplied.
     */
    public static void validateByeCommand(String body) throws HappyBotException {
        validateNoArguments(body, BYE_USAGE);
    }

    /**
     * Rejects arguments supplied to the list command.
     *
     * @param body The command text after the command word.
     * @throws HappyBotException If an argument was supplied.
     */
    public static void validateListCommand(String body) throws HappyBotException {
        validateNoArguments(body, LIST_USAGE);
    }

    /**
     * Returns the task number named by a mark, unmark or delete command.
     *
     * <p>Whether the number belongs to a task is not decided here, because that depends on the
     * task list rather than on the text.
     *
     * @param body The command text after the command word.
     * @return The number the user typed.
     * @throws HappyBotException If the text is not one positive whole number.
     */
    public static int parseTaskNumber(String body) throws HappyBotException {
        String normalizedBody = normalizeWhitespace(body);
        if (!normalizedBody.matches("[1-9]\\d*")) {
            throw new HappyBotException(TASK_NUMBER_MESSAGE);
        }

        try {
            return Integer.parseInt(normalizedBody);
        } catch (NumberFormatException e) {
            throw new HappyBotException(TASK_NUMBER_MESSAGE);
        }
    }

    /**
     * Returns the todo described by a todo command.
     *
     * @param body The command text after the command word.
     * @return The todo the command describes.
     * @throws HappyBotException If the description is unusable or missing.
     */
    public static ToDo parseToDo(String body) throws HappyBotException {
        String description = normalizeWhitespace(body);
        checkTaskText(description);

        if (description.isEmpty()) {
            throw new HappyBotException("The description of a todo cannot be empty.");
        }

        return new ToDo(description);
    }

    /**
     * Returns the details described by a deadline command.
     *
     * @param body The command text after the command word.
     * @return The deadline description and due date details.
     * @throws HappyBotException If a part of the command is unusable, missing or unreadable.
     */
    static DeadlineDetails parseDeadlineDetails(String body) throws HappyBotException {
        String normalizedBody = normalizeWhitespace(body);
        checkTaskText(normalizedBody);

        if (countMarkerOccurrences(normalizedBody, DEADLINE_MARKER) != 1) {
            throw new HappyBotException(DEADLINE_USAGE);
        }

        int markerIndex = normalizedBody.indexOf(DEADLINE_MARKER);
        String description = normalizedBody.substring(0, markerIndex);
        String dueDateText = normalizedBody.substring(markerIndex + DEADLINE_MARKER.length());
        if (description.isEmpty() || dueDateText.isEmpty()) {
            throw new HappyBotException(DEADLINE_USAGE);
        }

        return new DeadlineDetails(description, parseDateTime(dueDateText));
    }

    /**
     * Returns the deadline described by a deadline command.
     *
     * <p>HappyBot uses {@link #parseDeadlineDetails(String)} when it needs to know whether a
     * time was typed. This method remains for callers that only need the resulting task.
     *
     * @param body The command text after the command word.
     * @return The deadline the command describes.
     * @throws HappyBotException If a part of the command is unusable, missing or unreadable.
     */
    public static Deadline parseDeadline(String body) throws HappyBotException {
        DeadlineDetails deadlineDetails = parseDeadlineDetails(body);
        return new Deadline(deadlineDetails.getDescription(), deadlineDetails.getDueDateTime().getDateTime());
    }

    /**
     * Returns the details described by an event command.
     *
     * @param body The command text after the command word.
     * @return The event description, start details and end details.
     * @throws HappyBotException If a part of the command is unusable, missing or unreadable.
     */
    static EventDetails parseEventDetails(String body) throws HappyBotException {
        String normalizedBody = normalizeWhitespace(body);
        checkTaskText(normalizedBody);

        int startMarkerIndex = normalizedBody.indexOf(EVENT_START_MARKER);
        int endMarkerIndex = normalizedBody.indexOf(EVENT_END_MARKER);
        boolean hasExactlyOneStartMarker = countMarkerOccurrences(normalizedBody, EVENT_START_MARKER) == 1;
        boolean hasExactlyOneEndMarker = countMarkerOccurrences(normalizedBody, EVENT_END_MARKER) == 1;
        boolean hasMarkersInOrder = startMarkerIndex >= 0
                && endMarkerIndex >= startMarkerIndex + EVENT_START_MARKER.length();
        if (!hasExactlyOneStartMarker || !hasExactlyOneEndMarker || !hasMarkersInOrder) {
            throw new HappyBotException(EVENT_USAGE);
        }

        String description = normalizedBody.substring(0, startMarkerIndex);
        String startText =
                normalizedBody.substring(startMarkerIndex + EVENT_START_MARKER.length(), endMarkerIndex);
        String endText = normalizedBody.substring(endMarkerIndex + EVENT_END_MARKER.length());
        if (description.isEmpty() || startText.isEmpty() || endText.isEmpty()) {
            throw new HappyBotException(EVENT_USAGE);
        }

        return new EventDetails(description, parseDateTime(startText), parseDateTime(endText));
    }

    /**
     * Returns the event described by an event command.
     *
     * <p>HappyBot uses {@link #parseEventDetails(String)} when it needs to know whether the end
     * time was typed. Event remains responsible for deciding whether the start is before the end.
     *
     * @param body The command text after the command word.
     * @return The event the command describes.
     * @throws HappyBotException If a part of the command is unusable, missing or unreadable, or
     *         if the event does not start before it ends.
     */
    public static Event parseEvent(String body) throws HappyBotException {
        EventDetails eventDetails = parseEventDetails(body);
        try {
            return new Event(eventDetails.getStartTime().getDateTime(),
                    eventDetails.getEndTime().getDateTime(), eventDetails.getDescription());
        } catch (IllegalArgumentException e) {
            throw new HappyBotException(e.getMessage());
        }
    }

    /**
     * Returns the date named by a due command.
     *
     * <p>A due command asks about a whole day, so a supplied time is rejected rather than being
     * silently ignored.
     *
     * @param body The command text after the command word.
     * @return The date the deadlines are wanted for.
     * @throws HappyBotException If no date was given or the date cannot be read.
     */
    public static LocalDate parseDueDate(String body) throws HappyBotException {
        String normalizedBody = normalizeWhitespace(body);
        if (normalizedBody.isEmpty() || normalizedBody.contains(" ")) {
            throw new HappyBotException(DUE_USAGE);
        }

        return parseDateTime(normalizedBody).getDateTime().toLocalDate();
    }

    /**
     * Returns the keyword named by a find command.
     *
     * @param body The command text after the command word.
     * @return The keyword to look for, without the spaces around it.
     * @throws HappyBotException If no keyword was given.
     */
    public static String parseKeyword(String body) throws HappyBotException {
        String keyword = normalizeWhitespace(body);
        if (keyword.isEmpty()) {
            throw new HappyBotException(FIND_USAGE);
        }

        return keyword;
    }

    /**
     * Rejects arguments supplied to the stats command.
     *
     * @param body The command text after the command word.
     * @throws HappyBotException If any non-whitespace argument was supplied.
     */
    public static void validateStatsCommand(String body) throws HappyBotException {
        validateNoArguments(body, STATS_USAGE);
    }

    /**
     * Converts the date text of a command into a date and time.
     *
     * <p>The time is optional: text without a space holds a date alone and starts at midnight,
     * while text with one must have a 24-hour time after the space.
     *
     * @param dateTimeText The date, and optional time, typed by the user.
     * @return The date and time the text describes.
     * @throws HappyBotException If the date, or the time given after it, cannot be read.
     */
    private static ParsedDateTime parseDateTime(String dateTimeText) throws HappyBotException {
        String[] dateTimeParts = normalizeWhitespace(dateTimeText).split(" ", 2);

        // The date and the time are read separately so that each failure earns its own message.
        LocalDate date;
        try {
            date = LocalDate.parse(dateTimeParts[0], DATE_FORMAT);
        } catch (DateTimeParseException e) {
            // The hint replaces the exception's own message, which names an index in the text.
            throw new HappyBotException(describeBadDate(dateTimeParts[0]));
        }

        if (dateTimeParts.length == 1) {
            return new ParsedDateTime(LocalDateTime.of(date, LocalTime.MIDNIGHT), false);
        }

        try {
            LocalDateTime dateTime = LocalDateTime.of(date, LocalTime.parse(dateTimeParts[1], TIME_FORMAT));
            return new ParsedDateTime(dateTime, true);
        } catch (DateTimeParseException e) {
            throw new HappyBotException(DATE_FORMAT_HINT);
        }
    }

    /**
     * Returns the message explaining why a date could not be used.
     *
     * <p>A date the lenient format reads is written correctly and only names a day the month
     * does not have, so it earns the clearer message of the two.
     *
     * @param dateText The date part of what the user typed, without any time after it.
     * @return The message to show the user.
     */
    private static String describeBadDate(String dateText) {
        try {
            LocalDate.parse(dateText, LENIENT_DATE_FORMAT);
            return NO_SUCH_DATE_HINT;
        } catch (DateTimeParseException e) {
            return DATE_FORMAT_HINT;
        }
    }

    /**
     * Rejects task text that could not be stored and read back correctly.
     *
     * <p>The data file separates fields with a vertical bar, so a task containing one would be
     * split into the wrong fields the next time HappyBot starts.
     *
     * @param taskText The command text describing the task.
     * @throws HappyBotException If the text contains a vertical bar.
     */
    private static void checkTaskText(String taskText) throws HappyBotException {
        if (taskText.contains("|")) {
            throw new HappyBotException("A task cannot contain the '|' character.");
        }
    }

    /**
     * Returns command text with runs of whitespace represented by one ordinary space.
     */
    private static String normalizeWhitespace(String text) {
        if (text == null) {
            return "";
        }

        return text.strip().replaceAll("\\s+", " ");
    }

    /**
     * Rejects unexpected command arguments using the supplied usage message.
     */
    private static void validateNoArguments(String body, String usage) throws HappyBotException {
        if (!normalizeWhitespace(body).isEmpty()) {
            throw new HappyBotException(usage);
        }
    }

    /**
     * Counts the non-overlapping occurrences of a parameter marker in a command body.
     */
    private static int countMarkerOccurrences(String text, String marker) {
        int markerCount = 0;
        int searchStartIndex = 0;
        int markerIndex = text.indexOf(marker);

        while (markerIndex >= 0) {
            markerCount++;
            searchStartIndex = markerIndex + marker.length();
            markerIndex = text.indexOf(marker, searchStartIndex);
        }

        return markerCount;
    }
}
