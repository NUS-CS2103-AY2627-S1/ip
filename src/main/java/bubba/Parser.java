package bubba;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Interprets user commands without changing the task list or interacting with the console.
 */
public class Parser {
    private static final DateTimeFormatter INPUT_FORMATTER =
            DateTimeFormatter.ofPattern("d/M/yyyy HHmm");

    /**
     * Creates a parser; commands can also be parsed directly through {@link #parse(String)}.
     */
    public Parser() {
    }

    /**
     * Defines the operations supported by the command language.
     */
    public enum Type {
        /** Ends the command loop. */
        EXIT,
        /** Displays all tasks. */
        LIST,
        /** Displays guidance for all supported commands. */
        HELP,
        /** Marks a task as completed. */
        MARK,
        /** Marks a task as incomplete. */
        UNMARK,
        /** Removes a task. */
        DELETE,
        /** Appends a new task. */
        ADD,
        /** Searches task descriptions for a keyword. */
        FIND
    }

    /**
     * Carries an interpreted command and the arguments needed to execute it.
     *
     * @param type Operation to execute.
     * @param task Task for an ADD command, or {@code null} for other operations.
     * @param taskNumber One-based number for MARK, UNMARK or DELETE, or zero for other operations.
     * @param keyword Search text for FIND, or {@code null} for other operations.
     */
    public record ParsedCommand(Type type, Task task, int taskNumber, String keyword) {
    }

    /**
     * Parses a command, including task descriptions, numbers and dates where applicable.
     * Dates use {@code d/M/yyyy HHmm}. Task-number bounds are checked by {@link TaskList}, not here.
     *
     * @param input Non-null command line entered by the user.
     * @return Command and arguments ready for execution.
     * @throws BubbaException If the command is unknown or required arguments cannot be parsed.
     */
    public static ParsedCommand parse(String input) throws BubbaException {
        input = input.trim();
        if (input.equals("bye")) {
            return new ParsedCommand(Type.EXIT, null, 0, null);
        }
        if (input.equals("list")) {
            return new ParsedCommand(Type.LIST, null, 0, null);
        }
        if (input.equals("help")) {
            return new ParsedCommand(Type.HELP, null, 0, null);
        }
        if (input.equals("find") || input.startsWith("find ")) {
            String keyword = input.substring(4).trim();
            if (keyword.isEmpty()) {
                throw new BubbaException("Search keyword cannot be empty!");
            }
            return new ParsedCommand(Type.FIND, null, 0, keyword);
        }
        for (Type type : new Type[] {Type.MARK, Type.UNMARK, Type.DELETE}) {
            String keyword = type.name().toLowerCase(java.util.Locale.ROOT);
            if (input.equals(keyword) || input.startsWith(keyword + " ")) {
                try {
                    int number = Integer.parseInt(input.substring(keyword.length()).trim());
                    return new ParsedCommand(type, null, number, null);
                } catch (NumberFormatException e) {
                    throw new BubbaException("Please enter valid task number.");
                }
            }
        }
        if (input.equals("todo") || input.startsWith("todo ")) {
            String description = input.substring(4).stripLeading();
            if (description.isBlank()) {
                throw new BubbaException("Todo description cannot be empty!");
            }
            return new ParsedCommand(Type.ADD, new Todo(description), 0, null);
        }
        if (input.equals("deadline") || input.startsWith("deadline ")) {
            return new ParsedCommand(
                    Type.ADD, parseDeadline(input.substring(8).stripLeading()), 0, null);
        }
        if (input.equals("event") || input.startsWith("event ")) {
            return new ParsedCommand(
                    Type.ADD, parseEvent(input.substring(5).stripLeading()), 0, null);
        }
        throw new BubbaException("Sorry, unsure what you mean by that.");
    }

    private static Task parseDeadline(String input) throws BubbaException {
        String[] parts = input.split(" /by ", 2);
        if (parts.length != 2) {
            throw new BubbaException("Use deadline <description> /by <d/M/yyyy HHmm>.");
        }
        if (parts[0].isBlank()) {
            throw new BubbaException("Description is empty!");
        }
        if (parts[1].isBlank()) {
            throw new BubbaException("The date of a deadline can't be empty!");
        }
        try {
            return new Deadline(parts[0], LocalDateTime.parse(parts[1].trim(), INPUT_FORMATTER));
        } catch (DateTimeParseException e) {
            throw new BubbaException("Please enter deadline in d/M/yyyy HHmm format!");
        }
    }

    private static Task parseEvent(String input) throws BubbaException {
        String[] parts = input.split(" /from ", 2);
        if (parts.length != 2) {
            throw new BubbaException("Use event <description> /from <d/M/yyyy HHmm> /to <d/M/yyyy HHmm>.");
        }
        String[] times = parts[1].split(" /to ", 2);
        if (times.length != 2) {
            throw new BubbaException("Use event <description> /from <d/M/yyyy HHmm> /to <d/M/yyyy HHmm>.");
        }
        if (parts[0].isBlank()) {
            throw new BubbaException("Description cannot be empty!");
        }
        if (times[0].isBlank()) {
            throw new BubbaException("The from time cannot be empty!");
        }
        if (times[1].isBlank()) {
            throw new BubbaException("The to time cannot be empty!");
        }
        try {
            LocalDateTime from = LocalDateTime.parse(times[0].trim(), INPUT_FORMATTER);
            LocalDateTime to = LocalDateTime.parse(times[1].trim(), INPUT_FORMATTER);
            if (!from.isBefore(to)) {
                throw new BubbaException("The event must end after it starts!");
            }
            return new Event(parts[0], from, to);
        } catch (DateTimeParseException e) {
            throw new BubbaException("Enter the event time in the correct format d/M/yyyy HHmm!");
        }
    }
}
