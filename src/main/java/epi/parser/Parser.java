package epi.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import epi.exception.EpiException;
import epi.task.Deadline;
import epi.task.Event;
import epi.task.TaskList;
import epi.task.Todo;

/** Converts raw user commands into task data and validated indexes. */
public class Parser {
    private static final Pattern PARAMETER = Pattern.compile("(?i)(?<!\\S)/(by|from|to)(?=\\s|$)");

    /**
     * Trims surrounding whitespace and splits at the first run of whitespace after the command word.
     *
     * @param input Non-null user input; command and argument case are preserved.
     * @return One element containing the command, or two containing the command and remaining argument.
     *     Blank input produces one empty element.
     */
    public String[] parseInput(String input) {
        return input.trim().split("\\s+", 2);
    }

    /**
     * Converts a one-based user task number into a validated zero-based index.
     *
     * @param argument Non-null task number text, optionally surrounded by whitespace.
     * @param tasks Non-null full task list against which to validate the number.
     * @return The zero-based index of an existing task.
     * @throws EpiException If the argument is blank, the list is empty, or the number is outside the list.
     * @throws NumberFormatException If the nonblank argument is not a valid {@code int}.
     */
    public int parseTaskIndex(String argument, TaskList tasks) throws EpiException {
        argument = argument.trim();
        if (argument.isEmpty()) {
            throw new EpiException("You need to give me a task number, human.");
        }
        int taskNumber = Integer.parseInt(argument);
        if (tasks.size() == 0) {
            throw new EpiException("Cannot! You don't even have a single task");
        }
        if (taskNumber <= 0 || taskNumber > tasks.size()) {
            throw new EpiException("That task number doesn't exist in my memory!");
        }
        return taskNumber - 1;
    }

    /**
     * Creates an incomplete todo from its user-provided description.
     *
     * @param argument Non-null description text following the command word.
     * @return A todo with surrounding whitespace removed from its description.
     * @throws EpiException If the description is blank.
     * @throws IllegalArgumentException If the description contains a pipe character, newline, or carriage return.
     */
    public Todo parseTodo(String argument) throws EpiException {
        if (argument.isBlank()) {
            throw new EpiException("The description of a todo cannot be empty");
        }
        return new Todo(argument.trim());
    }

    /**
     * Creates an incomplete deadline from its description and {@code /by} date/time text.
     *
     * @param argument Non-null text in the form {@code description /by yyyy-MM-dd HHmm}.
     * @return A deadline with trimmed fields and normalized date/time spacing.
     * @throws EpiException If a field is missing or a reserved parameter is repeated, misplaced, or inappropriate.
     * @throws java.time.format.DateTimeParseException If the due date/time has an invalid format or value.
     * @throws IllegalArgumentException If the description contains a pipe character, newline, or carriage return.
     */
    public Deadline parseDeadline(String argument) throws EpiException {
        if (argument.isBlank()) {
            throw new EpiException("The description of a deadline cannot be empty!");
        }
        List<String> parts = splitTaskFields(argument, List.of("by"),
                "Invalid format! Use: deadline <task> /by <time>");
        return new Deadline(parts.get(0), normalizeDate(parts.get(1)));
    }

    /**
     * Creates an incomplete event from its description, start, and end date/time text.
     *
     * @param argument Non-null text in the form
     *     {@code description /from yyyy-MM-dd HHmm /to yyyy-MM-dd HHmm}.
     * @return An event with trimmed fields and normalized date/time spacing.
     * @throws EpiException If a field is missing or a reserved parameter is repeated, misplaced, or inappropriate.
     * @throws java.time.format.DateTimeParseException If either date/time has an invalid format or value.
     * @throws IllegalArgumentException If the description contains a pipe character, newline, or carriage return,
     *     or the end is not strictly after the start.
     */
    public Event parseEvent(String argument) throws EpiException {
        if (argument.isBlank()) {
            throw new EpiException("The description of a event cannot be empty!");
        }
        List<String> parts = splitTaskFields(argument, List.of("from", "to"),
                "Invalid format! Use: event <task> /from <start> /to <end>");
        return new Event(parts.get(0), normalizeDate(parts.get(1)), normalizeDate(parts.get(2)));
    }

    /**
     * Splits task fields while rejecting missing, repeated, misplaced, and inappropriate reserved parameters.
     * Parameter names are matched case-insensitively.
     *
     * @param argument Non-null command arguments containing a description and parameter values.
     * @param expected Required parameter names without slashes, in their required order.
     * @param usage Message to report when the fields do not match the required syntax.
     * @return The trimmed description followed by the trimmed values in parameter order.
     * @throws EpiException If parameters do not match the expected sequence or any field is blank.
     */
    private List<String> splitTaskFields(String argument, List<String> expected, String usage) throws EpiException {
        List<String> fields = new ArrayList<>();
        Matcher matcher = PARAMETER.matcher(argument);
        int previousEnd = 0;
        int parameterIndex = 0;
        while (matcher.find()) {
            if (parameterIndex >= expected.size()
                    || !matcher.group(1).equalsIgnoreCase(expected.get(parameterIndex))) {
                throw new EpiException(usage);
            }
            fields.add(argument.substring(previousEnd, matcher.start()).trim());
            previousEnd = matcher.end();
            parameterIndex++;
        }
        fields.add(argument.substring(previousEnd).trim());
        if (parameterIndex != expected.size() || fields.stream().anyMatch(String::isBlank)) {
            throw new EpiException(usage);
        }
        return fields;
    }

    /**
     * Normalizes spacing only within a date/time field, without parsing or validating the date.
     *
     * @param value Non-null date/time field with surrounding whitespace already removed.
     * @return The field with each run of whitespace replaced by one space.
     */
    private String normalizeDate(String value) {
        return value.replaceAll("\\s+", " ");
    }
}
