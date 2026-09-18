package chausistant.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import chausistant.command.AddCommand;
import chausistant.command.Command;
import chausistant.command.DeleteCommand;
import chausistant.command.ExitCommand;
import chausistant.command.FindCommand;
import chausistant.command.ListCommand;
import chausistant.command.MarkCommand;
import chausistant.command.ReminderCommand;
import chausistant.command.UnmarkCommand;
import chausistant.command.WhatsOnCommand;
import chausistant.exception.ChausistantException;
import chausistant.task.DeadlineTask;
import chausistant.task.EventTask;
import chausistant.task.Task;
import chausistant.task.TodoTask;

/**
 * Converts a user-entered command line into an executable command object.
 */
public final class Parser {
    private static final LocalTime START_OF_DAY = LocalTime.MIDNIGHT;
    private static final LocalTime END_OF_DAY = LocalTime.of(23, 59);
    private static final DateTimeFormatter INPUT_DATE_TIME_FORMATTER = new DateTimeFormatterBuilder()
            .appendPattern("d/M/uuuu HHmm")
            .toFormatter(Locale.ROOT)
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter INPUT_DATE_FORMATTER = new DateTimeFormatterBuilder()
            .appendPattern("d/M/uuuu")
            .toFormatter(Locale.ROOT)
            .withResolverStyle(ResolverStyle.STRICT);
    private static final Pattern DATE_TIME_SHAPE = Pattern.compile(
            "^\\d{1,2}/\\d{1,2}/\\d{4} \\d{4}$");
    private static final Pattern DATE_SHAPE = Pattern.compile(
            "^\\d{1,2}/\\d{1,2}/\\d{4}$");
    private static final Pattern DEADLINE_PATTERN = Pattern.compile(
            "^(.+?)\\s+/by\\s+(.+)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern EVENT_PATTERN = Pattern.compile(
            "^(.+?)\\s+/from\\s+(.+?)\\s+/to\\s+(.+)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern WHAT_IS_ON_PATTERN = Pattern.compile(
            "^what's\\s+on:\\s*(.*)$", Pattern.CASE_INSENSITIVE);
    private static final String DATE_TIME_ERROR =
            "Use date format DD/MM/YYYY, optionally followed by HHmm, with a valid calendar date.";
    private static final String DATE_ERROR =
            "Use date format DD/MM/YYYY with a valid calendar date.";
    private static final String WHAT_IS_ON_ERROR = "Use: what's on: <date>.";
    private static final String FIND_USAGE = "Use: find <keyword>.";
    private static final String TODO_USAGE = "Use: todo <task>.";
    private static final String DEADLINE_USAGE = "Use: deadline <task> /by <date> [HHmm].";
    private static final String EVENT_USAGE =
            "Use: event <task> /from <date> [HHmm] /to <date> [HHmm].";
    private static final String EVENT_RANGE_ERROR = "The event end must be after its start.";

    private Parser() {
        // Utility class.
    }

    /**
     * Parses one trimmed command line into an executable command.
     *
     * @param fullCommand the full user-entered command
     * @return a command ready to execute
     * @throws ChausistantException if the command is unknown or invalid
     */
    public static Command parse(String fullCommand) throws ChausistantException {
        Matcher whatsOnMatcher = WHAT_IS_ON_PATTERN.matcher(fullCommand);
        if (whatsOnMatcher.matches()) {
            String dateText = whatsOnMatcher.group(1).strip();
            if (dateText.isBlank()) {
                throw new ChausistantException(WHAT_IS_ON_ERROR);
            }
            return new WhatsOnCommand(parseInputDate(dateText));
        }
        if (fullCommand.length() >= 6 && fullCommand.regionMatches(true, 0, "what's", 0, 6)
                && (fullCommand.length() == 6 || Character.isWhitespace(fullCommand.charAt(6)))) {
            throw new ChausistantException(WHAT_IS_ON_ERROR);
        }

        String[] parts = fullCommand.split("\\s+", 2);
        String actionText = parts[0].toLowerCase(Locale.ROOT);
        String details = parts.length == 2 ? parts[1].strip() : "";
        CommandType action = fromInput(actionText);

        return switch (action) {
            case BYE -> {
                validateNoDetails(action, details);
                yield new ExitCommand();
            }
            case LIST -> {
                validateNoDetails(action, details);
                yield new ListCommand();
            }
            case REMIND -> {
                validateNoDetails(action, details);
                yield new ReminderCommand();
            }
            case MARK -> new MarkCommand(details);
            case UNMARK -> new UnmarkCommand(details);
            case DELETE -> new DeleteCommand(details);
            case FIND -> createFindCommand(details);
            case TODO, DEADLINE, EVENT -> new AddCommand(createTask(action, details));
        };
    }

    /** Creates a command that searches for one non-empty phrase in task descriptions. */
    private static FindCommand createFindCommand(String details) throws ChausistantException {
        if (details.isBlank()) {
            throw new ChausistantException(FIND_USAGE);
        }
        return new FindCommand(details);
    }

    /** Creates a task after validating the details supplied for its task type. */
    private static Task createTask(CommandType action, String details) throws ChausistantException {
        if (details.isBlank()) {
            throw new ChausistantException(getTaskUsage(action));
        }

        return switch (action) {
            case TODO -> new TodoTask(details);
            case DEADLINE -> createDeadlineTask(details);
            case EVENT -> createEventTask(details);
            default -> throw new IllegalArgumentException("Not a task command: " + action);
        };
    }

    /** Creates a deadline task after validating its description and date details. */
    private static DeadlineTask createDeadlineTask(String details) throws ChausistantException {
        Matcher matcher = DEADLINE_PATTERN.matcher(details);
        if (!matcher.matches()) {
            throw new ChausistantException(DEADLINE_USAGE);
        }
        DateTimeDetails deadline = parseInputDateTime(matcher.group(2).strip(), END_OF_DAY);
        return new DeadlineTask(matcher.group(1).strip(), deadline.dateTime, deadline.hasTime);
    }

    /** Creates an event task after validating its description and date details. */
    private static EventTask createEventTask(String details) throws ChausistantException {
        Matcher matcher = EVENT_PATTERN.matcher(details);
        if (!matcher.matches()) {
            throw new ChausistantException(EVENT_USAGE);
        }
        DateTimeDetails from = parseInputDateTime(matcher.group(2).strip(), START_OF_DAY);
        DateTimeDetails to = parseInputDateTime(matcher.group(3).strip(), END_OF_DAY);
        if (!to.dateTime.isAfter(from.dateTime)) {
            throw new ChausistantException(EVENT_RANGE_ERROR);
        }
        return new EventTask(matcher.group(1).strip(), from.dateTime, from.hasTime,
                to.dateTime, to.hasTime);
    }

    /** Parses a user-entered date with an optional 24-hour time. */
    private static DateTimeDetails parseInputDateTime(String text, LocalTime defaultTime)
            throws ChausistantException {
        try {
            if (DATE_SHAPE.matcher(text).matches()) {
                return new DateTimeDetails(LocalDate.parse(text, INPUT_DATE_FORMATTER)
                        .atTime(defaultTime), false);
            }
            if (DATE_TIME_SHAPE.matcher(text).matches()) {
                return new DateTimeDetails(LocalDateTime.parse(text, INPUT_DATE_TIME_FORMATTER), true);
            }
        } catch (DateTimeParseException error) {
            throw new ChausistantException(DATE_TIME_ERROR);
        }
        throw new ChausistantException(DATE_TIME_ERROR);
    }

    /** Parses the date supplied to {@code what's on:}. */
    private static LocalDate parseInputDate(String text) throws ChausistantException {
        if (!DATE_SHAPE.matcher(text).matches()) {
            throw new ChausistantException(DATE_ERROR);
        }

        try {
            return LocalDate.parse(text, INPUT_DATE_FORMATTER);
        } catch (DateTimeParseException error) {
            throw new ChausistantException(DATE_ERROR);
        }
    }

    /** Returns the command template for a task-creation action. */
    private static String getTaskUsage(CommandType action) {
        return switch (action) {
            case TODO -> TODO_USAGE;
            case DEADLINE -> DEADLINE_USAGE;
            case EVENT -> EVENT_USAGE;
            default -> throw new IllegalArgumentException("Unknown task action: " + action);
        };
    }

    /** Checks that a command without details was entered on its own. */
    private static void validateNoDetails(CommandType action, String details) throws ChausistantException {
        if (!details.isBlank()) {
            throw new ChausistantException("Use: " + action.keyword + ".");
        }
    }

    /** Returns the command type represented by the entered keyword. */
    private static CommandType fromInput(String input) throws ChausistantException {
        for (CommandType command : CommandType.values()) {
            if (command.keyword.equals(input)) {
                return command;
            }
        }
        throw new ChausistantException("Unknown command: " + input);
    }

    /** Identifies the command keyword supplied at the input boundary. */
    private enum CommandType {
        BYE("bye"),
        LIST("list"),
        MARK("mark"),
        UNMARK("unmark"),
        DELETE("delete"),
        FIND("find"),
        REMIND("remind"),
        TODO("todo"),
        DEADLINE("deadline"),
        EVENT("event");

        private final String keyword;

        CommandType(String keyword) {
            this.keyword = keyword;
        }
    }

    /** Holds a parsed date/time and whether the user supplied the time. */
    private static class DateTimeDetails {
        private final LocalDateTime dateTime;
        private final boolean hasTime;

        DateTimeDetails(LocalDateTime dateTime, boolean hasTime) {
            this.dateTime = dateTime;
            this.hasTime = hasTime;
        }
    }
}
