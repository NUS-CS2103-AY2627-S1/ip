package sheppy;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;

import sheppy.task.Deadline;
import sheppy.task.Event;
import sheppy.task.Task;
import sheppy.task.Todo;

/** Interprets user commands and creates tasks from them. */
public class Parser {
    /** Prevents instantiation of this utility class. */
    private Parser() {
    }

    /**
     * Identifies the command represented by the user's input.
     *
     * @param command the complete command entered by the user
     * @return the matching command type, or {@link CommandType#UNKNOWN}
     */
    public static CommandType parseCommand(String command) {
        assert command != null : "Command to parse must not be null";
        command = normalize(command);

        String[] parts = command.split(" ", 2);
        boolean hasArguments = parts.length > 1;
        return switch (parts[0]) {
            case "bye" -> hasArguments ? CommandType.UNKNOWN : CommandType.BYE;
            case "list" -> hasArguments ? CommandType.UNKNOWN : CommandType.LIST;
            case "sort" -> hasArguments ? CommandType.UNKNOWN : CommandType.SORT;
            case "find" -> CommandType.FIND;
            case "mark" -> CommandType.MARK;
            case "unmark" -> CommandType.UNMARK;
            case "delete" -> CommandType.DELETE;
            case "todo" -> CommandType.TODO;
            case "deadline" -> CommandType.DEADLINE;
            case "event" -> CommandType.EVENT;
            default -> CommandType.UNKNOWN;
        };
    }

    /**
     * Creates a task from an add command.
     *
     * @param command the complete user command
     * @return the task described by the command
     * @throws SheppyException if the command is malformed
     */
    public static Task parseTask(String command) throws SheppyException {
        command = normalize(command);
        if (command.contains("|")) {
            throw new SheppyException("please remove | from task details; it is reserved for saved data.");
        }
        return switch (parseCommand(command)) {
            case TODO -> new Todo(command.substring(4).trim());
            case DEADLINE -> parseDeadline(command);
            case EVENT -> parseEvent(command);
            default -> throw new SheppyException("that is not a task command.");
        };
    }

    /** Parses a deadline command. */
    private static Deadline parseDeadline(String command) throws SheppyException {
        String details = command.substring("deadline".length()).trim();
        int separator = details.indexOf(" /by ");
        if (separator < 0) {
            throw new SheppyException("a deadline needs a description and a /by date or time.");
        }
        requireSingleMarker(details, "/by");
        String description = details.substring(0, separator).trim();
        String by = details.substring(separator + " /by ".length()).trim();
        return new Deadline(description, parseDate(by));
    }

    /** Parses an event command. */
    private static Event parseEvent(String command) throws SheppyException {
        String details = command.substring("event".length()).trim();
        int fromSeparator = details.indexOf(" /from ");
        int toSeparator = details.indexOf(" /to ");
        if (fromSeparator < 0 || toSeparator < 0 || toSeparator < fromSeparator) {
            throw new SheppyException("an event needs a description, /from time, and /to time.");
        }
        requireSingleMarker(details, "/from");
        requireSingleMarker(details, "/to");
        String description = details.substring(0, fromSeparator).trim();
        String from = details.substring(fromSeparator + " /from ".length(), toSeparator).trim();
        String to = details.substring(toSeparator + " /to ".length()).trim();
        return new Event(description, from, to);
    }

    /**
     * Parses a task number from a mark, unmark, or delete command.
     *
     * @param command the complete command entered by the user
     * @return the one-based task number
     * @throws SheppyException if the command does not contain a valid number
     */
    public static int parseTaskNumber(String command) throws SheppyException {
        String[] parts = command.trim().split("\\s+");
        String commandName = parts[0];
        if (parts.length != 2) {
            throw new SheppyException("use " + commandName
                    + " followed by a task number, such as " + commandName + " 2.");
        }
        try {
            return Integer.parseInt(parts[1]);
        } catch (NumberFormatException exception) {
            throw new SheppyException("the task number must be a whole number.");
        }
    }

    /**
     * Returns the keyword from a find command.
     *
     * @param command the complete find command
     * @return the keyword to search for
     * @throws SheppyException if the keyword is empty
     */
    public static String parseFindKeyword(String command) throws SheppyException {
        command = normalize(command);
        String keyword = command.substring("find".length()).trim();
        if (keyword.isEmpty()) {
            throw new SheppyException("find needs a keyword to search for.");
        }
        return keyword;
    }

    /**
     * Creates the error used for an unrecognized command.
     *
     * @return an exception with Sheppy's unknown-command message
     */
    public static SheppyException unknownCommand() {
        return new SheppyException(
                "I don't recognize that command. Try todo, deadline, event, list, find, sort, "
                        + "mark, unmark, or delete.");
    }

    /** Parses a date entered in the Level 8 ISO format. */
    private static LocalDate parseDate(String value) throws SheppyException {
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException exception) {
            throw new SheppyException("please use dates in yyyy-MM-dd format, such as 2019-10-15.");
        }
    }

    /**
     * Normalizes incidental whitespace in a command.
     *
     * @param command the raw command
     * @return the trimmed command with whitespace runs replaced by spaces
     */
    public static String normalize(String command) {
        return command.trim().replaceAll("\\s+", " ");
    }

    /** Rejects repeated date/time markers rather than treating them as task text. */
    private static void requireSingleMarker(String details, String marker) throws SheppyException {
        long count = Arrays.stream(details.split(" "))
                .filter(marker::equals).count();
        if (count != 1) {
            throw new SheppyException("use " + marker + " exactly once.");
        }
    }
}
