package henry.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import henry.exception.HenryException;
import henry.exception.InvalidDateException;
import henry.task.Deadline;
import henry.task.Event;
import henry.task.Task;
import henry.task.Todo;

/**
 * Interprets user input and converts command arguments into application values.
 */
public class Parser {
    private static final String DEADLINE_SEPARATOR = "/by";
    private static final String EVENT_START_SEPARATOR = "/from";
    private static final String EVENT_END_SEPARATOR = "/to";
    private static final DateTimeFormatter SLASH_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("d/M/uuuu").withResolverStyle(ResolverStyle.STRICT);
    private static final Pattern SLASH_DATE_TIME_PATTERN =
            Pattern.compile("(\\d{1,2}/\\d{1,2}/\\d{4}) \\d{4}");
    private static final Pattern ISO_DATE_PATTERN = Pattern.compile("(\\d{4}-\\d{2}-\\d{2})");

    private Parser() {
    }

    /**
     * Identifies the command represented by the given input.
     *
     * @param input complete user input.
     * @return matching command type, or {@link CommandType#UNKNOWN} when none matches.
     */
    public static CommandType parseCommandType(String input) {
        return CommandType.from(input);
    }

    /**
     * Extracts and validates the task number supplied to a command.
     *
     * @param input complete user input.
     * @param commandType command whose task number should be read.
     * @param taskCount current number of tasks.
     * @return zero-based index of the selected task.
     * @throws HenryException if the task number is absent, invalid, or out of range.
     */
    public static int parseTaskIndex(String input, CommandType commandType, int taskCount)
            throws HenryException {
        String commandWord = commandType.getCommandWord();
        String argument = extractArguments(input);
        if (argument.isEmpty()) {
            throw new HenryException(
                    "I'll need a task number for that. For example: " + commandWord + " 1");
        }

        final int taskNumber;
        try {
            taskNumber = Integer.parseInt(argument);
        } catch (NumberFormatException e) {
            throw new HenryException("'" + argument + "' isn't a valid task number.");
        }

        if (taskCount == 0) {
            throw new HenryException("There aren't any tasks to " + commandWord + " yet.");
        }
        if (taskNumber < 1 || taskNumber > taskCount) {
            throw new HenryException(
                    "I can't find task " + taskNumber + ". Choose a number from 1 to "
                            + taskCount + ".");
        }
        return taskNumber - 1;
    }

    /**
     * Extracts the keyword supplied to a find command.
     *
     * @param input complete user input.
     * @return keyword to search for.
     * @throws HenryException if no keyword was supplied.
     */
    public static String parseKeyword(String input) throws HenryException {
        String keyword = extractArguments(input);
        if (keyword.isEmpty()) {
            throw new HenryException("I'll need a keyword for that. For example: find book");
        }
        return keyword;
    }

    /**
     * Parses a task-creation command into its corresponding task.
     *
     * @param input complete user input.
     * @param commandType task command to parse.
     * @return parsed task.
     * @throws HenryException if required task details are missing or malformed.
     * @throws InvalidDateException if a deadline contains an impossible calendar date.
     */
    public static Task parseTask(String input, CommandType commandType)
            throws HenryException, InvalidDateException {
        return switch (commandType) {
            case TODO -> parseTodo(input);
            case DEADLINE -> parseDeadline(input);
            case EVENT -> parseEvent(input);
            default -> throw new IllegalArgumentException("Not a task command: " + commandType);
        };
    }

    private static Todo parseTodo(String input) throws HenryException {
        String description = extractArguments(input);
        if (description.isEmpty()) {
            throw new HenryException(
                    "I'll need a description for that. For example: todo borrow a book");
        }
        return new Todo(description);
    }

    private static Deadline parseDeadline(String input)
            throws HenryException, InvalidDateException {
        String taskDetails = extractArguments(input);
        int bySeparatorIndex = taskDetails.indexOf(DEADLINE_SEPARATOR);
        if (bySeparatorIndex < 0) {
            throw new HenryException(
                    "A deadline needs '/by'. For example: deadline submit report /by 2019-12-02");
        }

        String description = taskDetails.substring(0, bySeparatorIndex).trim();
        String by = taskDetails.substring(
                bySeparatorIndex + DEADLINE_SEPARATOR.length()).trim();
        if (description.isEmpty()) {
            throw new HenryException("A deadline needs a description before '/by'.");
        }
        if (by.isEmpty()) {
            throw new HenryException("A deadline needs a date or time after '/by'.");
        }

        try {
            LocalDateTime deadline = Deadline.parseBy(by);
            return new Deadline(description, deadline);
        } catch (DateTimeParseException e) {
            if (hasInvalidCalendarDate(by)) {
                throw new InvalidDateException();
            }
            throw new HenryException(
                    "Please use a deadline date like 2/12/2019 1800 or 2019-12-02.");
        }
    }

    private static boolean hasInvalidCalendarDate(String input) {
        Matcher slashDateTimeMatcher = SLASH_DATE_TIME_PATTERN.matcher(input);
        if (slashDateTimeMatcher.matches()) {
            return cannotParseDate(slashDateTimeMatcher.group(1), SLASH_DATE_FORMATTER);
        }

        Matcher isoDateMatcher = ISO_DATE_PATTERN.matcher(input);
        return isoDateMatcher.matches()
                && cannotParseDate(isoDateMatcher.group(1), DateTimeFormatter.ISO_LOCAL_DATE);
    }

    private static boolean cannotParseDate(String input, DateTimeFormatter formatter) {
        try {
            LocalDate.parse(input, formatter);
            return false;
        } catch (DateTimeParseException e) {
            return true;
        }
    }

    private static Event parseEvent(String input) throws HenryException, InvalidDateException {
        String taskDetails = extractArguments(input);
        int fromSeparatorIndex = taskDetails.indexOf(EVENT_START_SEPARATOR);
        if (fromSeparatorIndex < 0) {
            throw new HenryException(
                    "An event needs '/from' and '/to'. "
                            + "For example: event meeting /from 2/12/2019 1400 "
                            + "/to 2/12/2019 1500");
        }

        int fromValueIndex = fromSeparatorIndex + EVENT_START_SEPARATOR.length();
        int toSeparatorIndex = taskDetails.indexOf(EVENT_END_SEPARATOR, fromValueIndex);
        if (toSeparatorIndex < 0) {
            throw new HenryException("An event needs an ending time introduced by '/to'.");
        }

        String description = taskDetails.substring(0, fromSeparatorIndex).trim();
        String from = taskDetails.substring(fromValueIndex, toSeparatorIndex).trim();
        String to = taskDetails.substring(
                toSeparatorIndex + EVENT_END_SEPARATOR.length()).trim();
        if (description.isEmpty()) {
            throw new HenryException("An event needs a description before '/from'.");
        }
        if (from.isEmpty()) {
            throw new HenryException("An event needs a starting time after '/from'.");
        }
        if (to.isEmpty()) {
            throw new HenryException("An event needs an ending time after '/to'.");
        }
        LocalDateTime startTime = parseEventDateTime(from);
        LocalDateTime endTime = parseEventDateTime(to);
        if (!endTime.isAfter(startTime)) {
            throw new HenryException("An event's ending time must be after its starting time.");
        }
        return new Event(description, startTime, endTime);
    }

    private static LocalDateTime parseEventDateTime(String input)
            throws HenryException, InvalidDateException {
        try {
            return Deadline.parseBy(input);
        } catch (DateTimeParseException e) {
            if (hasInvalidCalendarDate(input)) {
                throw new InvalidDateException();
            }
            throw new HenryException(
                    "Please use an event date like 2/12/2019 1400 or 2019-12-02.");
        }
    }

    private static String extractArguments(String input) {
        int commandEndIndex = input.indexOf(' ');
        if (commandEndIndex < 0) {
            return "";
        }
        return input.substring(commandEndIndex + 1).trim();
    }
}
