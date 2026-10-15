package nob.parser;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.Optional;

import nob.exception.NobException;
import nob.task.Deadline;
import nob.task.Event;
import nob.task.Task;
import nob.task.Todo;
import nob.util.DateTimeUtil;

/**
 * Interprets command arguments and creates the corresponding task objects.
 */
public class Parser {
    private static final String TODO_COMMAND = "todo";
    private static final String DEADLINE_COMMAND = "deadline";
    private static final String EVENT_COMMAND = "event";
    private static final String DEADLINE_DELIMITER = " /by ";
    private static final String EVENT_START_DELIMITER = " /from ";
    private static final String EVENT_END_DELIMITER = " /to ";

    private static final String TODO_USAGE = "Use: todo DESCRIPTION\n(eg., todo borrow book)";
    private static final String DEADLINE_USAGE = "Use: deadline DESCRIPTION /by DATE_OR_TIME";
    private static final String DEADLINE_EXAMPLE = "(eg., deadline return book /by Sunday)";
    private static final String EVENT_USAGE = "Use: event DESCRIPTION /from START /to END";
    private static final String EVENT_EXAMPLE = "(eg., event project meeting /from Mon 2pm /to 4pm)";

    private static final DateTimeFormatter[] TIME_ONLY_FORMATTERS = new DateTimeFormatter[] {
            new DateTimeFormatterBuilder()
                    .parseCaseInsensitive()
                    .appendPattern("h[:mm]a")
                    .toFormatter(Locale.ENGLISH),
            DateTimeFormatter.ofPattern("H:mm", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("HHmm", Locale.ENGLISH)
    };

    /**
     * Parses a {@code todo DESCRIPTION} command into a to-do task.
     *
     * @param command The complete command entered by the user.
     * @return The to-do task described by the command.
     * @throws NobException If the description is missing.
     */
    public static Task parseTodo(String command) throws NobException {
        assert command != null : "The dispatcher must provide a command";
        assert command.equals(TODO_COMMAND) || command.startsWith(TODO_COMMAND + " ")
                : "parseTodo must only receive todo commands";
        String description = command.substring(TODO_COMMAND.length()).trim();
        if (description.isEmpty()) {
            throw new NobException(TODO_USAGE);
        }
        return new Todo(description);
    }

    /**
     * Parses a {@code deadline DESCRIPTION /by TIME} command into a deadline task.
     *
     * @param command The complete command entered by the user.
     * @return The deadline task described by the command.
     * @throws NobException If the command syntax is invalid.
     */
    public static Task parseDeadline(String command) throws NobException {
        assert command != null : "The dispatcher must provide a command";
        assert command.equals(DEADLINE_COMMAND) || command.startsWith(DEADLINE_COMMAND + " ")
                : "parseDeadline must only receive deadline commands";
        String details = command.substring(DEADLINE_COMMAND.length()).trim();
        if (details.startsWith("/by")) {
            throw new NobException("Description should not be empty.\n"
                    + DEADLINE_USAGE + "\n" + DEADLINE_EXAMPLE);
        }

        int byIndex = details.indexOf(DEADLINE_DELIMITER);
        if (byIndex < 1 || byIndex + DEADLINE_DELIMITER.length() == details.length()) {
            if (details.contains("/by")) {
                throw new NobException("Check that there is a space before and after '/by'.\n"
                        + DEADLINE_USAGE + "\n" + DEADLINE_EXAMPLE);
            }
            throw new NobException(DEADLINE_USAGE + "\n" + DEADLINE_EXAMPLE);
        }

        String description = details.substring(0, byIndex).trim();
        if (description.isEmpty()) {
            throw new NobException("Description should not be empty.\n"
                    + DEADLINE_USAGE + "\n" + DEADLINE_EXAMPLE);
        }

        String by = details.substring(byIndex + DEADLINE_DELIMITER.length()).trim();
        if (by.isEmpty()) {
            throw new NobException("Deadline time should not be empty.\n" + DEADLINE_USAGE);
        }
        return new Deadline(description, by);
    }

    /**
     * Parses an {@code event DESCRIPTION /from START /to END} command into an event task.
     *
     * @param command The complete command entered by the user.
     * @return The event task described by the command.
     * @throws NobException If the command syntax is invalid.
     */
    public static Task parseEvent(String command) throws NobException {
        assert command != null : "The dispatcher must provide a command";
        assert command.equals(EVENT_COMMAND) || command.startsWith(EVENT_COMMAND + " ")
                : "parseEvent must only receive event commands";
        String details = command.substring(EVENT_COMMAND.length()).trim();
        if (details.startsWith("/from") || details.startsWith("/to")) {
            throw new NobException("Description should not be empty.\n"
                    + EVENT_USAGE + "\n" + EVENT_EXAMPLE);
        }

        int fromIndex = details.indexOf(EVENT_START_DELIMITER);
        int toIndex = details.indexOf(EVENT_END_DELIMITER);
        if (fromIndex < 1 || toIndex <= fromIndex + EVENT_START_DELIMITER.length()
                || toIndex + EVENT_END_DELIMITER.length() == details.length()) {
            if (details.contains("/from") || details.contains("/to")) {
                throw new NobException("Check that there is a space before and after '/from' and '/to'.\n"
                        + EVENT_USAGE + "\n" + EVENT_EXAMPLE);
            }
            throw new NobException(EVENT_USAGE + "\n" + EVENT_EXAMPLE);
        }

        String description = details.substring(0, fromIndex).trim();
        if (description.isEmpty()) {
            throw new NobException("Description should not be empty.\n"
                    + EVENT_USAGE + "\n" + EVENT_EXAMPLE);
        }

        String from = details.substring(fromIndex + EVENT_START_DELIMITER.length(), toIndex).trim();
        String to = details.substring(toIndex + EVENT_END_DELIMITER.length()).trim();
        if (from.isEmpty() || to.isEmpty()) {
            throw new NobException("Event times should not be empty.\n" + EVENT_USAGE);
        }
        if (isEndBeforeStart(from, to)) {
            throw new NobException("Event end time should not be before its start time.\n"
                    + EVENT_USAGE);
        }
        return new Event(description, from, to);
    }

    /** Returns whether two recognized event endpoints are in reverse chronological order. */
    private static boolean isEndBeforeStart(String from, String to) {
        Optional<LocalDateTime> fromDateTime = DateTimeUtil.parseDateTime(from);
        Optional<LocalDateTime> toDateTime = DateTimeUtil.parseDateTime(to);
        if (fromDateTime.isPresent() && toDateTime.isPresent()) {
            return toDateTime.get().isBefore(fromDateTime.get());
        }

        Optional<LocalTime> fromTime = parseTimeOnly(from);
        Optional<LocalTime> toTime = parseTimeOnly(to);
        return fromTime.isPresent() && toTime.isPresent() && toTime.get().isBefore(fromTime.get());
    }

    /** Parses a time without attaching an artificial date. */
    private static Optional<LocalTime> parseTimeOnly(String value) {
        for (DateTimeFormatter formatter : TIME_ONLY_FORMATTERS) {
            try {
                return Optional.of(LocalTime.parse(value, formatter));
            } catch (DateTimeParseException exception) {
                // Try the next supported time format.
            }
        }
        return Optional.empty();
    }

    /**
     * Parses the task number from a mark, unmark, or delete command.
     *
     * @param command The complete command entered by the user.
     * @return The task number.
     * @throws NobException If the task number is not an integer.
     */
    public static int parseTaskNumber(String command) throws NobException {
        assert command != null : "The dispatcher must provide a command";
        assert command.startsWith("mark ") || command.startsWith("unmark ")
                || command.startsWith("delete ")
                : "Task-number parsing requires a mark, unmark, or delete command";
        String numberText = command.substring(command.indexOf(' ') + 1).trim();
        try {
            return Integer.parseInt(numberText);
        } catch (NumberFormatException exception) {
            throw new NobException("Please enter a valid task number.");
        }
    }
}
