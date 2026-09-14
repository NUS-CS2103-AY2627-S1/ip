package kaya.parser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import kaya.command.CommandType;
import kaya.command.UpdateCommand;
import kaya.exception.KayaException;
import kaya.task.Deadline;
import kaya.task.Event;
import kaya.task.Todo;

/**
 * Interprets user input and converts it into commands and task data Kaya can use.
 */
public class Parser {
    /** Recognises date-field tokens separated by whitespace, including tabs. */
    private static final Pattern DATE_FIELD = Pattern.compile("(?<!\\S)/(?:by|from|to)(?=\\s|$)");

    /**
     * Creates a parser for interpreting Kaya commands.
     */
    public Parser() {
        // Parser has no state to initialize.
    }

    /**
     * Identifies the command at the start of the input.
     *
     * @param input the full command entered by the user
     * @return the matching command type, or {@link CommandType#UNKNOWN}
     */
    public CommandType parseCommandType(String input) {
        return CommandType.fromInput(input);
    }

    /**
     * Parses a {@code todo DESCRIPTION} command.
     *
     * @param input the full todo command
     * @return the parsed todo task
     * @throws KayaException if the description is missing
     */
    public Todo parseTodo(String input) throws KayaException {
        String description = input.trim().substring("todo".length()).trim();
        if (description.isEmpty()) {
            throw new KayaException("A todo needs a description.");
        }
        return new Todo(description);
    }

    /**
     * Extracts the search keyword from a find command.
     *
     * @param input the full find command
     * @return the non-empty search keyword
     * @throws KayaException if the keyword is missing
     */
    public String parseFindKeyword(String input) throws KayaException {
        String keyword = input.trim().substring("find".length()).trim();
        if (keyword.isEmpty()) {
            throw new KayaException("Tell me what keyword to find.");
        }
        return keyword;
    }

    /**
     * Parses a {@code deadline DESCRIPTION /by yyyy-MM-dd} command.
     *
     * @param input the full deadline command
     * @return the parsed deadline task
     * @throws KayaException if the description, separator, or date is invalid
     */
    public Deadline parseDeadline(String input) throws KayaException {
        String details = input.trim().substring("deadline".length()).trim();
        String[] fields = splitDateFields(details, "/by");
        String description = fields[0];
        String byText = fields[1];
        if (description.isEmpty()) {
            throw new KayaException("A deadline needs a description.");
        }
        if (byText.isEmpty()) {
            throw new KayaException("A deadline needs a date after /by.");
        }
        return new Deadline(description, parseDate(byText));
    }

    /**
     * Parses an {@code event DESCRIPTION /from yyyy-MM-dd /to yyyy-MM-dd} command.
     *
     * @param input the full event command
     * @return the parsed event task
     * @throws KayaException if required details are missing or invalid
     */
    public Event parseEvent(String input) throws KayaException {
        String details = input.trim().substring("event".length()).trim();
        String[] fields = splitDateFields(details, "/from", "/to");
        String description = fields[0];
        String fromText = fields[1];
        String toText = fields[2];
        if (description.isEmpty()) {
            throw new KayaException("An event needs a description.");
        }
        if (fromText.isEmpty()) {
            throw new KayaException("An event needs a starting date after /from.");
        }
        if (toText.isEmpty()) {
            throw new KayaException("An event needs an ending date after /to.");
        }

        LocalDate from = parseDate(fromText);
        LocalDate to = parseDate(toText);
        if (to.isBefore(from)) {
            throw new KayaException("An event's ending date cannot be before its starting date.");
        }
        return new Event(description, from, to);
    }

    /**
     * Separates a description from date values without changing its internal spacing.
     *
     * @param details the command arguments after the command word
     * @param expectedFields the required date fields in their expected order
     * @return the description followed by the trimmed date values
     * @throws KayaException if a field is missing, repeated, unexpected, or out of order
     */
    private String[] splitDateFields(String details, String... expectedFields) throws KayaException {
        List<MatchResult> separators = DATE_FIELD.matcher(details).results().toList();
        String guidance = "Use these date fields exactly once and in this order: "
                + String.join(" ", expectedFields) + ". Put a yyyy-MM-dd date after each field.";
        if (separators.size() != expectedFields.length) {
            throw new KayaException(guidance);
        }
        String[] values = new String[expectedFields.length + 1];
        int previousEnd = 0;
        for (int i = 0; i < expectedFields.length; i++) {
            MatchResult separator = separators.get(i);
            if (!separator.group().equals(expectedFields[i])) {
                throw new KayaException(guidance);
            }
            values[i] = details.substring(previousEnd, separator.start()).trim();
            previousEnd = separator.end();
        }
        values[expectedFields.length] = details.substring(previousEnd).trim();
        return values;
    }

    /**
     * Parses and validates a one-based task number, returning a zero-based index.
     *
     * @param input the full task-index command
     * @param command the command word preceding the task number
     * @param taskCount the number of tasks that can be selected
     * @return the corresponding zero-based task index
     * @throws KayaException if the number is missing, invalid, or out of range
     */
    public int parseTaskIndex(String input, String command, int taskCount) throws KayaException {
        String taskNumber = input.trim().substring(command.length()).trim();
        if (taskNumber.isEmpty()) {
            throw new KayaException("Tell me which task number to " + command + ".");
        }
        int index;
        try {
            index = Integer.parseInt(taskNumber) - 1;
        } catch (NumberFormatException exception) {
            throw new KayaException("The task number must be a whole number.");
        }
        if (index < 0 || index >= taskCount) {
            throw new KayaException("That task number is not in your list. Type list to check the task numbers.");
        }
        return index;
    }

    /**
     * Parses a command that updates one field of an existing task.
     *
     * @param input the full update command
     * @param taskCount the number of selectable tasks
     * @return the parsed update
     * @throws KayaException if the index, field, or value is invalid
     */
    public UpdateCommand parseUpdate(String input, int taskCount) throws KayaException {
        String[] parts = input.trim().split("\\s+", 4);
        if (parts.length != 4 || parts[3].isBlank()) {
            throw new KayaException("Use updates like: update NUMBER /description TEXT, "
                    + "or update NUMBER /by|/from|/to yyyy-MM-dd.");
        }
        int index = parseTaskIndex("update " + parts[1], "update", taskCount);
        String field = parts[2];
        if (!field.equals("/description") && !field.equals("/by")
                && !field.equals("/from") && !field.equals("/to")) {
            throw new KayaException("Update fields are /description, /by, /from, and /to.");
        }
        return new UpdateCommand(index, field, parts[3].trim());
    }

    /**
     * Parses a date in ISO {@code yyyy-MM-dd} format.
     *
     * @param dateText the date text to parse
     * @return the parsed date
     * @throws KayaException if the date is invalid or uses another format
     */
    private LocalDate parseDate(String dateText) throws KayaException {
        try {
            return LocalDate.parse(dateText);
        } catch (DateTimeParseException exception) {
            throw new KayaException("Use dates in yyyy-MM-dd format, for example 2019-10-15.");
        }
    }
}
