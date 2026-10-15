package shai.parser;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import shai.command.AddCommand;
import shai.command.Command;
import shai.command.DeleteCommand;
import shai.command.ExitCommand;
import shai.command.FindCommand;
import shai.command.ListCommand;
import shai.command.MarkCommand;
import shai.command.ReminderCommand;
import shai.command.UnmarkCommand;
import shai.exception.ShaiException;
import shai.task.Deadline;
import shai.task.Event;
import shai.task.Reminder;
import shai.task.ToDo;

/**
 * Converts raw user input into executable commands for Shai.
 *
 * <p>Parsing and validation errors are reported as {@link ShaiException}s so
 * that the caller can display them without knowing the parsing details.</p>
 */
public class Parser {
    private static final String BYE_COMMAND = "bye";
    private static final String LIST_COMMAND = "list";
    private static final String FIND_COMMAND = "find";
    private static final String MARK_COMMAND = "mark";
    private static final String UNMARK_COMMAND = "unmark";
    private static final String TODO_COMMAND = "todo";
    private static final String DEADLINE_COMMAND = "deadline";
    private static final String EVENT_COMMAND = "event";
    private static final String DELETE_COMMAND = "delete";
    private static final String REMIND_COMMAND = "remind";
    private static final String BY_MARKER = "/by";
    private static final String FROM_MARKER = "/from";
    private static final String TO_MARKER = "/to";
    private static final String BEFORE_MARKER = "/before";
    private static final String OFF_MARKER = "/off";

    private final Clock clock;

    /** Creates a parser that uses the system clock for reminder validation. */
    public Parser() {
        this(Clock.systemDefaultZone());
    }

    /** Creates a parser that uses the supplied clock for reminder validation. */
    public Parser(Clock clock) {
        if (clock == null) {
            throw new IllegalArgumentException("The parser clock must not be null.");
        }
        this.clock = clock;
    }

    /**
     * Parses and validates a raw command.
     *
     * @param input raw user input
     * @param taskCount current number of tasks, used to validate task numbers
     * @return an executable command
     * @throws ShaiException if the command is unknown or malformed
     * @throws IllegalArgumentException if the task count is negative
     */
    public Command parse(String input, int taskCount) throws ShaiException {
        if (input == null) {
            throw new ShaiException("Please enter a command, King.");
        }
        if (taskCount < 0) {
            throw new IllegalArgumentException("The task count must not be negative.");
        }
        String command = input.trim();
        validateSpacing(command);
        if (command.isEmpty()) {
            throw new ShaiException("Please enter a command, King.");
        }
        if (command.equals(BYE_COMMAND)) {
            return new ExitCommand();
        } else if (command.equals(LIST_COMMAND)) {
            return new ListCommand();
        } else if (isCommand(command, FIND_COMMAND)) {
            return parseFind(command);
        } else if (isCommand(command, MARK_COMMAND)) {
            return new MarkCommand(parseTaskIndex(command, MARK_COMMAND, taskCount));
        } else if (isCommand(command, UNMARK_COMMAND)) {
            return new UnmarkCommand(parseTaskIndex(command, UNMARK_COMMAND, taskCount));
        } else if (isCommand(command, TODO_COMMAND)) {
            return parseToDo(command);
        } else if (isCommand(command, DEADLINE_COMMAND)) {
            return parseDeadline(command);
        } else if (isCommand(command, EVENT_COMMAND)) {
            return parseEvent(command);
        } else if (isCommand(command, DELETE_COMMAND)) {
            return new DeleteCommand(parseTaskIndex(command, DELETE_COMMAND, taskCount));
        } else if (isCommand(command, REMIND_COMMAND)) {
            return parseReminder(command, taskCount);
        }
        throw new ShaiException("Turnover. Check your command, King.");
    }

    /** Parses a find command and extracts its keyword. */
    private static Command parseFind(String command) throws ShaiException {
        String keyword = command.substring(FIND_COMMAND.length()).trim();
        requireNonEmpty(keyword, "Please provide a keyword after " + FIND_COMMAND + ".");
        return new FindCommand(keyword);
    }

    /** Parses a ToDo command and extracts its description. */
    private static Command parseToDo(String command) throws ShaiException {
        String description = command.substring(TODO_COMMAND.length()).trim();
        requireNonEmpty(description, "Hold up - I need a description for that " + TODO_COMMAND + ".");
        return new AddCommand(new ToDo(description));
    }

    /** Parses a Deadline command and extracts its description and due date. */
    private static Command parseDeadline(String command) throws ShaiException {
        int indexBy = findMarker(command, BY_MARKER, 0);
        if (indexBy < 0) {
            throw new ShaiException("A deadline needs a date after " + BY_MARKER + ". Try: "
                    + DEADLINE_COMMAND + " submit report " + BY_MARKER + " 2019-12-01.");
        }
        if (findMarker(command, BY_MARKER, indexBy + BY_MARKER.length()) >= 0) {
            throw new ShaiException("A deadline must contain exactly one " + BY_MARKER + " parameter.");
        }
        String description = command.substring(DEADLINE_COMMAND.length(), indexBy).trim();
        String byText = command.substring(indexBy + BY_MARKER.length()).trim();
        requireNonEmpty(description, "Hold up - I need a description for that " + DEADLINE_COMMAND + ".");
        requireNonEmpty(byText, "Hold up - I need a date after " + BY_MARKER + ".");
        return new AddCommand(new Deadline(description, parseDateTime(byText)));
    }

    /** Parses an Event command and extracts its description and time range. */
    private static Command parseEvent(String command) throws ShaiException {
        int indexFrom = findMarker(command, FROM_MARKER, 0);
        int indexTo = findMarker(command, TO_MARKER, 0);
        if (indexFrom < 0 || indexTo < 0 || indexFrom >= indexTo) {
            throw new ShaiException("An event needs " + FROM_MARKER + " and " + TO_MARKER
                    + " times. Try: " + EVENT_COMMAND + " meeting " + FROM_MARKER
                    + " 2019-12-01 1400 " + TO_MARKER + " 2019-12-01 1600.");
        }
        if (findMarker(command, FROM_MARKER, indexFrom + FROM_MARKER.length()) >= 0
                || findMarker(command, TO_MARKER, indexTo + TO_MARKER.length()) >= 0) {
            throw new ShaiException("An event must contain exactly one " + FROM_MARKER
                    + " and one " + TO_MARKER + " parameter.");
        }
        String description = command.substring(EVENT_COMMAND.length(), indexFrom).trim();
        String fromText = command.substring(indexFrom + FROM_MARKER.length(), indexTo).trim();
        String toText = command.substring(indexTo + TO_MARKER.length()).trim();
        requireNonEmpty(description, "Hold up - I need a description for that " + EVENT_COMMAND + ".");
        requireNonEmpty(fromText, "Hold up - I need a starting time after " + FROM_MARKER + ".");
        requireNonEmpty(toText, "Hold up - I need an ending time after " + TO_MARKER + ".");
        LocalDateTime from = parseDateTime(fromText);
        LocalDateTime to = parseDateTime(toText);
        if (!from.isBefore(to)) {
            throw new ShaiException("An event must end after it starts.");
        }
        return new AddCommand(new Event(description, from, to));
    }

    /** Parses a reminder listing, configuration, or disabling command. */
    private Command parseReminder(String command, int taskCount) throws ShaiException {
        if (command.equals(REMIND_COMMAND)) {
            return new ReminderCommand(clock);
        }

        String arguments = command.substring(REMIND_COMMAND.length()).trim();
        int separatorIndex = arguments.indexOf(' ');
        String taskNumber = separatorIndex < 0 ? arguments : arguments.substring(0, separatorIndex);
        int taskIndex = parseTaskIndexValue(taskNumber, REMIND_COMMAND, taskCount);
        if (separatorIndex < 0) {
            throw invalidReminderSyntax();
        }

        String action = arguments.substring(separatorIndex + 1).trim();
        if (action.equals(OFF_MARKER)) {
            return new ReminderCommand(taskIndex, clock);
        }
        if (action.equals(BEFORE_MARKER)) {
            requireNonEmpty("", "That reminder play needs a duration, King. Try: remind 1 /before 2h.");
        }
        if (!action.startsWith(BEFORE_MARKER + " ")) {
            throw invalidReminderSyntax();
        }

        String duration = action.substring(BEFORE_MARKER.length()).trim();
        requireNonEmpty(duration, "That reminder play needs a duration, King. Try: remind 1 /before 2h.");
        return new ReminderCommand(taskIndex, parseReminderMinutes(duration), clock);
    }

    /** Parses a duration such as {@code 30m}, {@code 2h}, or {@code 1d}. */
    private static long parseReminderMinutes(String value) throws ShaiException {
        if (!value.matches("\\d+[mhd]")) {
            throw new ShaiException("That reminder duration is out of bounds, King. Use 30m, 1h, or 1d.");
        }

        long amount;
        try {
            amount = Long.parseLong(value.substring(0, value.length() - 1));
        } catch (NumberFormatException e) {
            throw new ShaiException("That reminder duration is out of bounds, King. Use 30m, 1h, or 1d.");
        }

        char unit = value.charAt(value.length() - 1);
        long multiplier = switch (unit) {
            case 'm' -> 1;
            case 'h' -> 60;
            case 'd' -> 24 * 60;
            default -> 0;
        };
        try {
            long minutes = Math.multiplyExact(amount, multiplier);
            if (!Reminder.isValidMinutesBefore(minutes) || minutes == Reminder.DISABLED) {
                throw new ArithmeticException();
            }
            return minutes;
        } catch (ArithmeticException e) {
            throw new ShaiException("That reminder duration is too large, King.");
        }
    }

    /** Returns the standard error for an incomplete reminder command. */
    private static ShaiException invalidReminderSyntax() {
        return new ShaiException("Call the reminder play like this: remind; remind <task number> "
                + "/before <duration>; or remind <task number> /off, King.");
    }

    /** Parses a task date and converts parser errors into a user-friendly command error. */
    private static LocalDateTime parseDateTime(String value) throws ShaiException {
        try {
            return DateTimeParser.parse(value);
        } catch (DateTimeParseException e) {
            throw new ShaiException("Invalid date/time. Use yyyy-MM-dd HHmm, for example 2019-12-02 1800.");
        }
    }

    /** Returns whether the input is a command or a command followed by arguments. */
    private static boolean isCommand(String input, String command) {
        return input.equals(command) || input.startsWith(command + " ");
    }

    /** Rejects ambiguous command spacing before command-specific parsing. */
    private static void validateSpacing(String command) throws ShaiException {
        if (command.matches(".*\\s{2,}.*") || command.indexOf('\t') >= 0) {
            throw new ShaiException("Use a single space between command parameters, King.");
        }
    }

    /** Finds a marker that is separated from surrounding text by whitespace. */
    private static int findMarker(String command, String marker, int fromIndex) {
        int index = command.indexOf(marker, fromIndex);
        while (index >= 0) {
            boolean hasLeadingBoundary = index > 0 && Character.isWhitespace(command.charAt(index - 1));
            int markerEnd = index + marker.length();
            boolean hasTrailingBoundary = markerEnd == command.length()
                    || Character.isWhitespace(command.charAt(markerEnd));
            if (hasLeadingBoundary && hasTrailingBoundary) {
                return index;
            }
            index = command.indexOf(marker, markerEnd);
        }
        return -1;
    }

    /** Ensures that a required command field contains useful text. */
    private static void requireNonEmpty(String value, String message) throws ShaiException {
        if (value == null || value.isBlank()) {
            throw new ShaiException(message);
        }
    }

    /** Parses and validates a one-based task number into a zero-based index. */
    private static int parseTaskIndex(String command, String commandName, int taskCount)
            throws ShaiException {
        String argument = command.substring(commandName.length()).trim();
        requireNonEmpty(argument, "Please provide a task number after " + commandName + ".");

        return parseTaskIndexValue(argument, commandName, taskCount);
    }

    /** Parses and validates a task-number token. */
    private static int parseTaskIndexValue(String argument, String commandName, int taskCount)
            throws ShaiException {
        requireNonEmpty(argument, "Please provide a task number after " + commandName + ".");

        final int oneBasedIndex;
        if (!argument.matches("\\d+")) {
            throw new ShaiException("The task number after " + commandName + " must be a whole number.");
        }
        try {
            oneBasedIndex = Integer.parseInt(argument);
        } catch (NumberFormatException e) {
            throw new ShaiException("The task number after " + commandName + " must be a whole number.");
        }

        if (oneBasedIndex < 1 || oneBasedIndex > taskCount) {
            throw new ShaiException("That task number is not in your list yet.");
        }
        int zeroBasedIndex = oneBasedIndex - 1;
        assert zeroBasedIndex >= 0 && zeroBasedIndex < taskCount
                : "A validated task number must convert to a valid zero-based index.";
        return zeroBasedIndex;
    }
}
