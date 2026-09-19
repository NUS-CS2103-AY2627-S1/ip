package fox.parser;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;

import fox.exception.FoxException;
import fox.task.Deadline;
import fox.task.Event;
import fox.task.FoxDate;
import fox.task.Task;
import fox.task.Todo;

/** Parses task-creation commands without knowing about Fox's UI or storage. */
public class TaskParser {
    private static final List<DateTimeFormatter> EVENT_TIME_FORMATS = List.of(
            createTimeFormat("ha"),
            createTimeFormat("h:mma"),
            createTimeFormat("H:mm"));

    /** Creates a parser for Fox task-creation commands. */
    public TaskParser() {
    }

    /**
     * Creates a task from a complete, trimmed task-creation command.
     *
     * @param commandName the first word of {@code command}, such as {@code todo} or {@code deadline}
     * @param command the complete task-creation command
     * @return the task represented by the command
     * @throws FoxException if the command is unknown or has missing or malformed task details
     */
    public Task parse(String commandName, String command) throws FoxException {
        String details = command.substring(commandName.length()).trim();
        if (commandName.equalsIgnoreCase("todo")) {
            return parseTodo(details);
        }
        if (commandName.equalsIgnoreCase("deadline")) {
            return parseDeadline(details);
        }
        if (commandName.equalsIgnoreCase("event")) {
            return parseEvent(details);
        }
        throw new FoxException("☹ OOPS!!! I don't recognize '" + commandName
                + "'. Try list, find, todo, deadline, event, mark, unmark, delete, or bye.");
    }

    /** Creates a to-do task from its description. */
    private Todo parseTodo(String details) throws FoxException {
        if (details.isEmpty()) {
            throw new FoxException("☹ OOPS!!! The description of a todo cannot be empty.");
        }
        return new Todo(normalizeWhitespace(details));
    }

    /** Creates a deadline task from its description and date. */
    private Deadline parseDeadline(String details) throws FoxException {
        rejectRepeatedParameter(details, "/by");
        String[] parts = details.split("(?i)\\s+/by(?:\\s+|$)", -1);
        if (details.isEmpty() || (parts.length == 2 && parts[0].isBlank())) {
            throw new FoxException("☹ OOPS!!! The description of a deadline cannot be empty.");
        }
        if (parts.length != 2 || parts[1].isBlank()) {
            throw new FoxException("☹ OOPS!!! The deadline time cannot be empty.");
        }
        return new Deadline(normalizeWhitespace(parts[0]), FoxDate.parse(parts[1].trim()));
    }

    /** Creates an event task from its description, start time, and end time. */
    private Event parseEvent(String details) throws FoxException {
        rejectRepeatedParameter(details, "/from");
        rejectRepeatedParameter(details, "/to");
        String[] descriptionAndTimes = details.split("(?i)\\s+/from(?:\\s+|$)", -1);
        if (details.isEmpty() || (descriptionAndTimes.length == 2
                && descriptionAndTimes[0].isBlank())) {
            throw new FoxException("☹ OOPS!!! The description of an event cannot be empty.");
        }
        if (descriptionAndTimes.length != 2 || descriptionAndTimes[1].isBlank()) {
            throw new FoxException("☹ OOPS!!! The start time of an event cannot be empty.");
        }
        String[] times = descriptionAndTimes[1].split("(?i)\\s+/to(?:\\s+|$)", -1);
        if (times.length != 2 || times[0].isBlank()) {
            throw new FoxException("☹ OOPS!!! The start time of an event cannot be empty.");
        }
        if (times[1].isBlank()) {
            throw new FoxException("☹ OOPS!!! The end time of an event cannot be empty.");
        }
        String from = times[0].trim();
        String to = times[1].trim();
        LocalTime fromTime = parseEventTime(from);
        LocalTime toTime = parseEventTime(to);
        if (!fromTime.isBefore(toTime)) {
            throw new FoxException("☹ OOPS!!! An event must end after it starts.");
        }
        return new Event(normalizeWhitespace(descriptionAndTimes[0]), from, to);
    }

    /** Rejects a command parameter that appears more than once. */
    private void rejectRepeatedParameter(String details, String parameter) throws FoxException {
        long occurrences = List.of(details.split("\\s+"))
                .stream()
                .filter(token -> token.equalsIgnoreCase(parameter))
                .count();
        if (occurrences > 1) {
            throw new FoxException("☹ OOPS!!! Please specify " + parameter + " only once.");
        }
    }

    /** Parses an event time in one of Fox's supported user-facing formats. */
    private LocalTime parseEventTime(String value) throws FoxException {
        for (DateTimeFormatter format : EVENT_TIME_FORMATS) {
            try {
                return LocalTime.parse(value, format);
            } catch (DateTimeParseException ignored) {
                // Try the next supported format.
            }
        }
        throw new FoxException("☹ OOPS!!! Please use a valid time such as 10am, 10:30am, or 22:30.");
    }

    /** Creates a case-insensitive event-time formatter. */
    private static DateTimeFormatter createTimeFormat(String pattern) {
        return new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern(pattern)
                .toFormatter(Locale.ENGLISH);
    }

    /** Collapses accidental whitespace in a task description. */
    private String normalizeWhitespace(String value) {
        return value.trim().replaceAll("\\s+", " ");
    }
}
