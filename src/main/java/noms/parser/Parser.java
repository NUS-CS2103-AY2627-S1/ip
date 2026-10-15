package noms.parser;

import java.time.LocalDate;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import noms.command.AddCommand;
import noms.command.Command;
import noms.command.CommandType;
import noms.command.DeleteCommand;
import noms.command.ExitCommand;
import noms.command.FindCommand;
import noms.command.ListCommand;
import noms.command.MarkCommand;
import noms.command.OnCommand;
import noms.command.SnoozeCommand;
import noms.command.UnmarkCommand;
import noms.exception.EmptyCommandException;
import noms.exception.EmptyDescriptionException;
import noms.exception.EmptyKeywordException;
import noms.exception.InvalidDateException;
import noms.exception.InvalidDeadlineException;
import noms.exception.InvalidEventDateRangeException;
import noms.exception.InvalidEventException;
import noms.exception.InvalidSnoozeException;
import noms.exception.InvalidTaskNumberException;
import noms.exception.NomsException;
import noms.exception.UnexpectedArgumentException;
import noms.exception.UnknownCommandException;
import noms.task.Deadline;
import noms.task.Event;
import noms.task.Task;
import noms.task.ToDo;
import noms.util.DateUtil;

/**
 * Makes sense of the raw command lines the user types: it works out which
 * command was given and turns the arguments into the values the rest of the
 * program needs (a {@link Task}, a task number, or a date).
 *
 * The methods are static because parsing depends only on the input string,
 * not on any stored state; grouping them here keeps all command-format
 * knowledge in one place. An instance-based parser would work too, but adds
 * no value while there is nothing to remember between calls.
 */
public class Parser {
    private static final String TODO_COMMAND = "todo";
    private static final String DEADLINE_COMMAND = "deadline";
    private static final String EVENT_COMMAND = "event";
    private static final String TODO_FORMAT = TODO_COMMAND + " <description>";
    private static final String DEADLINE_FORMAT =
            DEADLINE_COMMAND + " <description> /by yyyy-mm-dd";
    private static final String EVENT_FORMAT =
            EVENT_COMMAND + " <description> /from yyyy-mm-dd /to yyyy-mm-dd";
    private static final String FIND_FORMAT = "find <keyword>";
    private static final String DEADLINE_SNOOZE_FORMAT =
            "snooze <task number> /by yyyy-mm-dd";
    private static final String EVENT_SNOOZE_FORMAT =
            "snooze <task number> /from yyyy-mm-dd [/to yyyy-mm-dd]";

    private static final Pattern DEADLINE_SNOOZE_PATTERN = Pattern.compile(
            "^\\s*(?i:snooze)\\s+-?\\d+\\s+/by\\s+([^\\s/]+)\\s*$");
    private static final Pattern EVENT_SNOOZE_PATTERN = Pattern.compile(
            "^\\s*(?i:snooze)\\s+-?\\d+\\s+/from\\s+([^\\s/]+)"
                    + "(?:\\s+/to\\s+([^\\s/]+))?\\s*$");
    private static final Pattern DEADLINE_DATE_SEPARATOR_PATTERN = Pattern.compile("\\s+/by\\s+");
    private static final Pattern EVENT_START_DATE_SEPARATOR_PATTERN = Pattern.compile("\\s+/from\\s+");
    private static final Pattern EVENT_END_DATE_SEPARATOR_PATTERN = Pattern.compile("\\s+/to\\s+");

    /**
     * Turns a full command line into the {@link Command} that carries it
     * out. Parsing that needs no task list (task descriptions, dates) is
     * done here; validation that depends on the current list size (task
     * numbers) is deferred to the command's {@code execute}.
     *
     * @param fullCommand the raw line typed by the user
     * @return the command to execute
     * @throws NomsException if the command is blank, unrecognized, or malformed
     */
    public static Command parse(String fullCommand) throws NomsException {
        String normalizedCommand = normalizeCommand(fullCommand);
        CommandType commandType = getCommandType(normalizedCommand);
        if (commandType == null) {
            throw new UnknownCommandException();
        }

        switch (commandType) {
            case BYE:
                validateNoArguments(normalizedCommand, "bye");
                return new ExitCommand();
            case LIST:
                validateNoArguments(normalizedCommand, "list");
                return new ListCommand();
            case MARK:
                return new MarkCommand(normalizedCommand);
            case UNMARK:
                return new UnmarkCommand(normalizedCommand);
            case DELETE:
                return new DeleteCommand(normalizedCommand);
            case SNOOZE:
                return new SnoozeCommand(normalizedCommand);
            case ON:
                return new OnCommand(parseOnDate(normalizedCommand));
            case FIND:
                return new FindCommand(parseKeyword(normalizedCommand));
            case TODO:
            case DEADLINE:
            case EVENT:
                return new AddCommand(parseTask(normalizedCommand));
            default:
                assert false : "Unhandled command type";
                throw new UnknownCommandException();
        }
    }

    /**
     * Returns the command type named by the first word of the command, or
     * {@code null} if that word is not a recognized command.
     *
     * @param command the raw line typed by the user
     * @throws EmptyCommandException if the command is blank
     */
    public static CommandType getCommandType(String command) throws EmptyCommandException {
        String normalizedCommand = normalizeCommand(command);
        if (normalizedCommand.isEmpty()) {
            throw new EmptyCommandException();
        }

        String commandWord = normalizedCommand.split("\\s+")[0];
        try {
            return CommandType.valueOf(commandWord.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Parses a task-adding command (todo, deadline, or event) into the
     * corresponding {@link Task}.
     *
     * @param command the raw line typed by the user
     * @return the task described by the command
     * @throws NomsException if the command is unknown or missing required parts
     */
    public static Task parseTask(String command) throws NomsException {
        String normalizedCommand = normalizeCommand(command);
        CommandType commandType = getCommandType(normalizedCommand);

        if (commandType == null) {
            throw new UnknownCommandException();
        }

        switch (commandType) {
            case TODO:
                return parseTodo(normalizedCommand);
            case DEADLINE:
                return parseDeadline(normalizedCommand);
            case EVENT:
                return parseEvent(normalizedCommand);
            default:
                throw new UnknownCommandException();
        }
    }

    /**
     * Parses a todo command after its command type has been identified.
     *
     * @param command the raw todo command
     * @return the parsed todo task
     * @throws EmptyDescriptionException if no description is provided
     */
    private static Task parseTodo(String command) throws EmptyDescriptionException {
        String description = command.substring(TODO_COMMAND.length()).trim();
        if (description.isEmpty()) {
            throw new EmptyDescriptionException(TODO_COMMAND, TODO_FORMAT);
        }
        return new ToDo(description);
    }

    /**
     * Parses a deadline command after its command type has been identified.
     *
     * @param command the raw deadline command
     * @return the parsed deadline task
     * @throws NomsException if the description or deadline date is invalid
     */
    private static Task parseDeadline(String command) throws NomsException {
        if (command.equals(DEADLINE_COMMAND)) {
            throw new EmptyDescriptionException(DEADLINE_COMMAND, DEADLINE_FORMAT);
        }
        String remainder = command.substring(DEADLINE_COMMAND.length());
        Matcher separatorMatcher = DEADLINE_DATE_SEPARATOR_PATTERN.matcher(remainder);
        if (!separatorMatcher.find()) {
            throw new InvalidDeadlineException(DEADLINE_FORMAT);
        }
        String description = remainder.substring(0, separatorMatcher.start()).trim();
        String dueDate = remainder.substring(separatorMatcher.end()).trim();
        if (description.isEmpty()) {
            throw new EmptyDescriptionException(DEADLINE_COMMAND, DEADLINE_FORMAT);
        }
        if (dueDate.isEmpty()) {
            throw new InvalidDeadlineException(DEADLINE_FORMAT);
        }
        return new Deadline(description, DateUtil.parse(dueDate));
    }

    /**
     * Parses an event command after its command type has been identified.
     *
     * @param command the raw event command
     * @return the parsed event task
     * @throws NomsException if the description or event dates are invalid
     */
    private static Task parseEvent(String command) throws NomsException {
        if (command.equals(EVENT_COMMAND)) {
            throw new EmptyDescriptionException(EVENT_COMMAND, EVENT_FORMAT);
        }
        String remainder = command.substring(EVENT_COMMAND.length());
        Matcher fromMatcher = EVENT_START_DATE_SEPARATOR_PATTERN.matcher(remainder);
        if (!fromMatcher.find()) {
            throw new InvalidEventException(EVENT_FORMAT);
        }
        Matcher toMatcher = EVENT_END_DATE_SEPARATOR_PATTERN.matcher(remainder);
        if (!toMatcher.find(fromMatcher.end())) {
            throw new InvalidEventException(EVENT_FORMAT);
        }
        String description = remainder.substring(0, fromMatcher.start()).trim();
        String startDate = remainder.substring(fromMatcher.end(), toMatcher.start()).trim();
        String endDate = remainder.substring(toMatcher.end()).trim();
        if (description.isEmpty()) {
            throw new EmptyDescriptionException(EVENT_COMMAND, EVENT_FORMAT);
        }
        if (startDate.isEmpty() || endDate.isEmpty()) {
            throw new InvalidEventException(EVENT_FORMAT);
        }
        LocalDate parsedStartDate = DateUtil.parse(startDate);
        LocalDate parsedEndDate = DateUtil.parse(endDate);
        validateEventDateRange(parsedStartDate, parsedEndDate);
        return new Event(description, parsedStartDate, parsedEndDate);
    }

    /**
     * Parses and validates the task number argument of a mark, unmark, or
     * delete command, returning it as a 1-based number within range.
     *
     * @param command the raw line typed by the user
     * @param action the command word, used in the error messages
     * @param taskCount the current number of tasks, used for the range check
     * @return the validated 1-based task number
     * @throws InvalidTaskNumberException if the number is missing, extra,
     *         non-numeric, or out of range
     */
    public static int parseTaskNumber(String command, String action, int taskCount)
            throws InvalidTaskNumberException {
        String[] parts = command.trim().split("\\s+");

        if (taskCount == 0) {
            throw new InvalidTaskNumberException(
                    "Noms has no tasks to " + action + " yet.\n"
                            + "Add a task first, then try again.");
        }

        if (parts.length == 1) {
            throw new InvalidTaskNumberException(
                    "Noms needs to know which task to " + action + ".\n"
                            + "Try: " + action + " <task number>");
        }

        if (parts.length > 2) {
            throw new InvalidTaskNumberException(
                    "Noms can only " + action + " one task at a time.\n"
                            + "Try: " + action + " <task number>");
        }

        return parseTaskNumberText(parts[1], action, taskCount);
    }

    /**
     * Parses the task number from a snooze command while leaving its date
     * arguments for the task-specific snooze parser.
     *
     * @param command the raw snooze command
     * @param taskCount the current number of tasks
     * @return the validated 1-based task number
     * @throws InvalidTaskNumberException if the number is missing, invalid,
     *         or out of range
     */
    public static int parseSnoozeTaskNumber(String command, int taskCount)
            throws InvalidTaskNumberException {
        String[] parts = command.trim().split("\\s+", 3);

        if (taskCount == 0) {
            throw new InvalidTaskNumberException(
                    "Noms has no tasks to snooze yet.\n"
                            + "Add a task first, then try again.");
        }

        if (parts.length == 1) {
            throw new InvalidTaskNumberException(
                    "Noms needs to know which task to snooze.\n"
                            + "Try: snooze <task number>");
        }

        return parseTaskNumberText(parts[1], "snooze", taskCount);
    }

    /**
     * Parses the replacement date from a deadline snooze command.
     *
     * @param command the raw snooze command
     * @return the deadline's replacement due date
     * @throws NomsException if the syntax or date is invalid
     */
    public static LocalDate parseDeadlineSnoozeDate(String command) throws NomsException {
        Matcher matcher = DEADLINE_SNOOZE_PATTERN.matcher(command);
        if (!matcher.matches()) {
            throw new InvalidSnoozeException(
                    "This deadline snooze recipe is incomplete.\n"
                            + "Try: " + DEADLINE_SNOOZE_FORMAT);
        }
        return DateUtil.parse(matcher.group(1));
    }

    /**
     * Parses the replacement dates from an event snooze command. The end
     * date is empty when the user supplies only {@code /from}.
     *
     * @param command the raw snooze command
     * @return the event's replacement start and optional end date
     * @throws NomsException if the syntax or either date is invalid
     */
    public static EventSnoozeDates parseEventSnoozeDates(String command) throws NomsException {
        Matcher matcher = EVENT_SNOOZE_PATTERN.matcher(command);
        if (!matcher.matches()) {
            throw new InvalidSnoozeException(
                    "This event snooze recipe is incomplete.\n"
                            + "Try: " + EVENT_SNOOZE_FORMAT);
        }

        LocalDate startDate = DateUtil.parse(matcher.group(1));
        String endDateText = matcher.group(2);
        Optional<LocalDate> endDate = endDateText == null
                ? Optional.empty()
                : Optional.of(DateUtil.parse(endDateText));
        if (endDate.isPresent()) {
            validateEventDateRange(startDate, endDate.get());
        }
        return new EventSnoozeDates(startDate, endDate);
    }

    /** Rejects an event date range that does not have a positive duration. */
    private static void validateEventDateRange(LocalDate startDate, LocalDate endDate)
            throws InvalidEventDateRangeException {
        if (!Event.isValidDateRange(startDate, endDate)) {
            throw new InvalidEventDateRangeException();
        }
    }

    /** Removes whitespace surrounding a command without altering its contents. */
    private static String normalizeCommand(String command) {
        return command.strip();
    }

    /** Rejects arguments supplied to a command that does not accept any. */
    private static void validateNoArguments(String command, String commandWord)
            throws UnexpectedArgumentException {
        if (command.split("\\s+").length > 1) {
            throw new UnexpectedArgumentException(commandWord);
        }
    }

    /** Holds the parsed dates for an event snooze command. */
    public record EventSnoozeDates(LocalDate startDate, Optional<LocalDate> endDate) {
    }

    /**
     * Validates one task-number token against the current task list.
     */
    private static int parseTaskNumberText(String taskNumberText, String action, int taskCount)
            throws InvalidTaskNumberException {
        if (!taskNumberText.matches("-?\\d+")) {
            throw new InvalidTaskNumberException(
                    "Noms needs a whole task number to find the right menu item.\n"
                            + "Try: " + action + " 1");
        }

        try {
            int taskNumber = Integer.parseInt(taskNumberText);
            if (taskNumber < 1 || taskNumber > taskCount) {
                throw new InvalidTaskNumberException(
                        "Task number " + taskNumber + " is out of range.\n"
                                + "Choose a task number from 1 to " + taskCount + ".");
            }
            return taskNumber;
        } catch (NumberFormatException e) {
            throw new InvalidTaskNumberException(
                    "That task number is too large for Noms.\n"
                            + "Choose a task number from 1 to " + taskCount + ".");
        }
    }

    /**
     * Extracts and parses the date from an {@code on <date>} command, using
     * the same rules used when adding tasks so that an invalid date surfaces
     * the same friendly error message.
     *
     * @param command the raw line typed by the user
     * @return the date named after {@code on}
     * @throws NomsException if the date is missing or unparseable
     */
    public static LocalDate parseOnDate(String command) throws NomsException {
        String[] parts = command.trim().split("\\s+", 2);
        if (parts.length < 2 || parts[1].isBlank()) {
            throw new InvalidDateException("");
        }
        return DateUtil.parse(parts[1].trim());
    }

    /**
     * Extracts the search keyword from a {@code find <keyword>} command. The
     * whole remainder of the line (after the {@code find} word) is treated as
     * the keyword, so multi-word keywords such as {@code find read book} work.
     *
     * @param command the raw line typed by the user
     * @return the keyword to search for
     * @throws EmptyKeywordException if no keyword is given
     */
    public static String parseKeyword(String command) throws EmptyKeywordException {
        String[] parts = command.trim().split("\\s+", 2);
        if (parts.length < 2 || parts[1].isBlank()) {
            throw new EmptyKeywordException(FIND_FORMAT);
        }
        return parts[1].trim();
    }
}
