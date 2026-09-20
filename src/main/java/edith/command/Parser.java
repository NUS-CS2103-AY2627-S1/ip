package edith.command;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.regex.Pattern;

import edith.EdithException;
import edith.task.DateTimeParser;
import edith.task.Deadline;
import edith.task.Event;
import edith.task.SortOrder;
import edith.task.ToDo;

/** Converts raw user commands into executable command objects. */
public class Parser {
    private static final String UNKNOWN_COMMAND = "OOPS!!! I'm sorry, but I don't know what that means :-(";

    /**
     * Parses a command and its arguments.
     *
     * @param command raw command entered by the user
     * @return executable command represented by the input
     * @throws EdithException if the input is not a recognized or valid command
     */
    public static Command parse(String command) throws EdithException {
        validateCommandSpacing(command);
        if (command.equals("bye")) {
            return new ExitCommand();
        }
        if (command.equals("list")) {
            return new ListCommand();
        }
        if (command.equals("help")) {
            return new HelpCommand();
        }
        if (command.equals("find") || command.startsWith("find ")) {
            return parseFindCommand(command);
        }
        if (command.equals("mark") || command.startsWith("mark ")) {
            return new MarkCommand(parseTaskNumber(command.substring(4), "mark"), true);
        }
        if (command.equals("unmark") || command.startsWith("unmark ")) {
            return new MarkCommand(parseTaskNumber(command.substring(6), "unmark"), false);
        }
        if (command.equals("delete") || command.startsWith("delete ")) {
            return new DeleteCommand(parseTaskNumber(command.substring(6), "delete"));
        }
        if (command.equals("sort") || command.startsWith("sort ")) {
            return parseSortCommand(command.substring(4));
        }
        if (command.equals("todo") || command.startsWith("todo ")) {
            return parseToDoCommand(command.substring(4));
        }
        if (command.equals("deadline") || command.startsWith("deadline ")) {
            return parseDeadlineCommand(command.substring(8));
        }
        if (command.equals("event") || command.startsWith("event ")) {
            return parseEventCommand(command.substring(5));
        }
        throw new EdithException(UNKNOWN_COMMAND);
    }

    /** Rejects whitespace and file-delimiter characters that make commands ambiguous. */
    private static void validateCommandSpacing(String command) throws EdithException {
        if (command == null || command.isEmpty()) {
            throw new EdithException("OOPS!!! Please enter a command.");
        }
        if (!command.equals(command.strip())) {
            throw new EdithException("OOPS!!! Commands must not begin or end with spaces.");
        }
        if (command.contains("\t") || command.contains("  ")) {
            throw new EdithException("OOPS!!! Use exactly one space between command parts.");
        }
        if (command.contains("|")) {
            throw new EdithException("OOPS!!! The character '|' cannot be used in a task.");
        }
    }

    /** Parses a find command with a required keyword. */
    private static FindCommand parseFindCommand(String command) throws EdithException {
        String keyword = command.substring(4);
        if (keyword.isEmpty()) {
            throw new EdithException("OOPS!!! The keyword cannot be empty.");
        }
        return new FindCommand(keyword.substring(1));
    }

    /** Parses a positive task number used by a mark, unmark, or delete command. */
    private static int parseTaskNumber(String arguments, String commandName) throws EdithException {
        if (!arguments.matches(" [1-9]\\d*")) {
            throw new EdithException("OOPS!!! The " + commandName + " command needs a positive task number.");
        }
        try {
            return Integer.parseInt(arguments.substring(1));
        } catch (NumberFormatException error) {
            throw new EdithException("OOPS!!! That task number is too large.");
        }
    }

    /** Parses a to-do command with a required description. */
    private static AddCommand parseToDoCommand(String arguments) throws EdithException {
        if (arguments.isEmpty()) {
            throw new EdithException("OOPS!!! The description of a todo cannot be empty.");
        }
        return new AddCommand(new ToDo(arguments.substring(1)));
    }

    /** Parses a deadline command with one description and one valid deadline. */
    private static AddCommand parseDeadlineCommand(String arguments) throws EdithException {
        String[] parts = splitRequiredArguments(arguments, "deadline", " /by ", 2);
        validateDate(parts[1], "deadline");
        return new AddCommand(new Deadline(parts[0], parts[1]));
    }

    /** Parses an event command with one description, start, and end. */
    private static AddCommand parseEventCommand(String arguments) throws EdithException {
        String[] fromParts = splitRequiredArguments(arguments, "event", " /from ", 2);
        String[] times = splitRequiredArguments(" " + fromParts[1], "event", " /to ", 2);
        LocalDateTime start = parseDateTime(times[0], "start date/time");
        LocalDateTime end = parseEventEnd(times[1], start);
        if (!end.isAfter(start)) {
            throw new EdithException("OOPS!!! An event's end date/time must be after its start date/time.");
        }
        return new AddCommand(new Event(fromParts[0], times[0], times[1]));
    }

    /** Splits arguments around exactly one required delimiter. */
    private static String[] splitRequiredArguments(String arguments, String commandName,
                                                   String delimiter, int expectedParts) throws EdithException {
        String[] parts = arguments.split(Pattern.quote(delimiter), -1);
        if (parts.length != expectedParts || parts[0].isEmpty() || parts[1].isEmpty()) {
            throw invalidFormat(commandName);
        }
        if (parts[0].charAt(0) != ' ') {
            throw invalidFormat(commandName);
        }
        parts[0] = parts[0].substring(1);
        if (parts[0].isEmpty()) {
            throw invalidFormat(commandName);
        }
        return parts;
    }

    /** Returns the error used for malformed commands with required parameters. */
    private static EdithException invalidFormat(String commandName) {
        return new EdithException("OOPS!!! Invalid " + commandName + " command. Enter help to see its format.");
    }

    /** Validates a complete user-supplied date or date-time. */
    private static void validateDate(String text, String fieldName) throws EdithException {
        parseDateTime(text, fieldName);
    }

    /** Parses a complete user-supplied date or date-time. */
    private static LocalDateTime parseDateTime(String text, String fieldName) throws EdithException {
        LocalDateTime dateTime = DateTimeParser.parseForSorting(text);
        if (dateTime == null) {
            throw new EdithException("OOPS!!! Please enter a valid " + fieldName + ".");
        }
        return dateTime;
    }

    /** Parses an event end date/time, allowing a time on the same day as the start. */
    private static LocalDateTime parseEventEnd(String text, LocalDateTime start) throws EdithException {
        LocalDateTime dateTime = DateTimeParser.parseForSorting(text);
        if (dateTime != null) {
            return dateTime;
        }
        LocalTime time = DateTimeParser.parseTimeOnly(text);
        if (time == null) {
            throw new EdithException("OOPS!!! Please enter a valid end date/time.");
        }
        return start.toLocalDate().atTime(time);
    }

    /** Parses one of the supported sort orders. */
    private static SortCommand parseSortCommand(String arguments) throws EdithException {
        SortOrder sortOrder = switch (arguments) {
            case " alph" -> SortOrder.ALPHABETICAL;
            case " alph desc" -> SortOrder.ALPHABETICAL_DESCENDING;
            case " date" -> SortOrder.DATE;
            case " date desc" -> SortOrder.DATE_DESCENDING;
            case " status" -> SortOrder.STATUS;
            case " added" -> SortOrder.ADDED;
            case " added desc" -> SortOrder.ADDED_DESCENDING;
            default -> throw invalidSortCommand();
        };
        return new SortCommand(sortOrder);
    }

    /** Returns the error used for malformed sort commands. */
    private static EdithException invalidSortCommand() {
        return new EdithException("OOPS!!! That sort order is not available. Enter help to see the options.");
    }
}
