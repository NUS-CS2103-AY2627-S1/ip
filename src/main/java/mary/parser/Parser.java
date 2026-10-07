package mary.parser;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mary.command.AddCommand;
import mary.command.Command;
import mary.command.DeleteCommand;
import mary.command.ErrorCommand;
import mary.command.ExitCommand;
import mary.command.FindCommand;
import mary.command.ListCommand;
import mary.command.MarkCommand;
import mary.command.OnCommand;
import mary.command.SortCommand;
import mary.command.UnknownCommand;
import mary.exception.MaryException;
import mary.task.Deadline;
import mary.task.Event;
import mary.task.Task;
import mary.task.Todo;

/**
 * Interprets task-creation commands and validates their arguments.
 */
public class Parser {
    private static final String TODO_PREFIX = "todo ";
    private static final String DEADLINE_PREFIX = "deadline ";
    private static final String EVENT_PREFIX = "event ";
    private static final String BY_MARKER = " /by ";
    private static final String FROM_MARKER = " /from ";
    private static final String TO_MARKER = " /to ";

    /**
     * Creates the command object matching the input command.
     */
    public static Command parse(String command) {
        if (command == null) {
            return new UnknownCommand("");
        }
        if (command.chars().anyMatch(value -> Character.isISOControl(value) && value != '\t')) {
            return new ErrorCommand("enter one command per line; remove control characters.");
        }
        String[] parts = command.strip().split("\\s+", 2);
        String word = parts[0];
        String arguments = parts.length == 2 ? parts[1].strip() : "";
        String normalized = word + " " + arguments;
        return switch (word) {
            case "sort" -> new SortCommand(arguments);
            case "find" -> new FindCommand(arguments);
            case "bye", "list" -> arguments.isEmpty()
                    ? (word.equals("bye") ? new ExitCommand() : new ListCommand())
                    : new ErrorCommand("use '" + word + "' without arguments.");
            case "delete" -> new DeleteCommand(normalized);
            case "mark", "unmark" -> new MarkCommand(normalized);
            case "on" -> new OnCommand(normalized);
            case "todo", "deadline", "event" -> new AddCommand(normalized);
            default -> new UnknownCommand(command);
        };
    }

    /**
     * Converts a todo, deadline, or event command into a task.
     */
    public static Task parseTask(String command) throws MaryException {
        if (command == null || command.chars().anyMatch(value -> Character.isISOControl(value) && value != '\t')) {
            throw new MaryException("enter one command per line; remove control characters.");
        }
        if (command.contains("|")) {
            throw new MaryException("task descriptions cannot contain '|'; replace it with another character.");
        }
        command = command.replace('\t', ' ').strip().replaceFirst("\\s+", " ");
        if (!command.contains(" ")) {
            command += " ";
        }
        validateMarkers(command);
        if (command.startsWith(TODO_PREFIX)) {
            return parseTodo(command.substring(TODO_PREFIX.length()).trim());
        }
        if (command.startsWith(DEADLINE_PREFIX)) {
            return parseDeadline(command.substring(DEADLINE_PREFIX.length()).trim());
        }
        if (command.startsWith(EVENT_PREFIX)) {
            return parseEvent(command.substring(EVENT_PREFIX.length()).trim());
        }
        throw new MaryException("use 'todo description' to add a task without a date.");
    }

    /**
     * Rejects repeated named date parameters before splitting their values.
     */
    private static void validateMarkers(String command) throws MaryException {
        if (command.startsWith(TODO_PREFIX)) {
            return;
        }
        Matcher markers = Pattern.compile("(?:^|\\s)/(by|from|to)(?=\\s|$)").matcher(command);
        Set<String> seen = new HashSet<>();
        while (markers.find()) {
            if (!seen.add(markers.group(1))) {
                throw new MaryException("parameter /" + markers.group(1) + " is repeated; specify it only once.");
            }
            boolean isDeadlineParameter = command.startsWith(DEADLINE_PREFIX) && markers.group(1).equals("by");
            boolean isEventParameter = command.startsWith(EVENT_PREFIX) && !markers.group(1).equals("by");
            if (!isDeadlineParameter && !isEventParameter) {
                throw new MaryException("parameter /" + markers.group(1)
                        + " is not supported here; deadlines use /by, events use /from and /to.");
            }
        }
    }

    /**
     * Validates the trimmed todo description before creating a task.
     */
    private static Todo parseTodo(String description) throws MaryException {
        if (description.isEmpty()) {
            throw new MaryException("please add a task description after 'todo'.");
        }
        return new Todo(description);
    }

    /**
     * Separates deadline arguments and translates invalid dates into usage guidance.
     */
    private static Deadline parseDeadline(String content) throws MaryException {
        content = content.replaceAll("\\s+/(by|from|to)\\s+", " /$1 ");
        int marker = content.indexOf(BY_MARKER);
        if (marker < 0 || content.substring(0, marker).trim().isEmpty()
                || content.substring(marker + BY_MARKER.length()).trim().isEmpty()) {
            throw new MaryException("use 'deadline description /by date or time'.");
        }
        String description = content.substring(0, marker).trim();
        String by = content.substring(marker + BY_MARKER.length()).trim().replaceAll("\\s+", " ");
        try {
            return new Deadline(description, Task.parseDateTime(by));
        } catch (DateTimeParseException exception) {
            throw new MaryException("use date/time format d/M/yyyy HHmm, for example 2/12/2019 1800.");
        }
    }

    /**
     * Separates event arguments, preserving marker order and date validation rules.
     */
    private static Event parseEvent(String content) throws MaryException {
        content = content.replaceAll("\\s+/(by|from|to)\\s+", " /$1 ");
        int fromMarker = content.indexOf(FROM_MARKER);
        int toMarker = content.indexOf(TO_MARKER);
        if (fromMarker < 0 || toMarker <= fromMarker) {
            throw new MaryException("use 'event description /from start /to end'.");
        }
        String description = content.substring(0, fromMarker).trim();
        String from = content.substring(fromMarker + FROM_MARKER.length(), toMarker).trim().replaceAll("\\s+", " ");
        String to = content.substring(toMarker + TO_MARKER.length()).trim().replaceAll("\\s+", " ");
        if (description.isEmpty() || from.isEmpty() || to.isEmpty()) {
            throw new MaryException("event description, start time, and end time cannot be empty.");
        }
        try {
            LocalDateTime start = Task.parseDateTime(from);
            LocalDateTime end = Task.parseDateTime(to);
            if (!start.isBefore(end)) {
                throw new MaryException("event end must be after its start; correct /from or /to.");
            }
            return new Event(description, start, end);
        } catch (DateTimeParseException exception) {
            throw new MaryException("use event date/time format d/M/yyyy HHmm for both /from and /to.");
        }
    }
}
