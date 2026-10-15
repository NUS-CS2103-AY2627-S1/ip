package hse.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

import hse.command.ByeCommand;
import hse.command.Command;
import hse.command.DeadlineCommand;
import hse.command.DeleteCommand;
import hse.command.EventCommand;
import hse.command.FindCommand;
import hse.command.FindDateCommand;
import hse.command.InvalidCommand;
import hse.command.ListCommand;
import hse.command.MarkCommand;
import hse.command.SortCommand;
import hse.command.TodoCommand;
import hse.command.UnmarkCommand;

/** Interprets user commands, task numbers, and dates. */
public class Parser {
    /** Supported command categories. */
    public enum CommandType {
        BYE, LIST, FIND, FIND_DATE, MARK, UNMARK, DELETE, TODO, DEADLINE, EVENT, SORT, INVALID
    }

    private static final DateTimeFormatter ISO_DATE_FORMAT = strictFormatter("uuuu-M-d");
    private static final DateTimeFormatter SLASH_DATE_FORMAT = strictFormatter("d/M/uuuu");
    private static final DateTimeFormatter DASH_DATE_FORMAT = strictFormatter("d-M-uuuu");
    private static final DateTimeFormatter ISO_DATE_TIME_FORMAT = strictFormatter("uuuu-M-d HHmm");
    private static final DateTimeFormatter SLASH_DATE_TIME_FORMAT = strictFormatter("d/M/uuuu HHmm");
    private static final DateTimeFormatter DASH_DATE_TIME_FORMAT = strictFormatter("d-M-uuuu HHmm");

    /** Returns the category of a command without executing it. */
    public static CommandType parseCommandType(String command) {
        if (command.equals("bye")) {
            return CommandType.BYE;
        }
        if (command.equals("list")) {
            return CommandType.LIST;
        }
        if (command.equals("sort")) {
            return CommandType.SORT;
        }
        if (command.equals("finddate") || command.startsWith("finddate ")) {
            return CommandType.FIND_DATE;
        }
        if (command.equals("find") || command.startsWith("find ")) {
            return CommandType.FIND;
        }
        if (command.startsWith("mark ")) {
            return CommandType.MARK;
        }
        if (command.startsWith("unmark ")) {
            return CommandType.UNMARK;
        }
        if (command.startsWith("delete ")) {
            return CommandType.DELETE;
        }
        if (command.equals("todo") || command.startsWith("todo ")) {
            return CommandType.TODO;
        }
        if (command.startsWith("deadline ")) {
            return CommandType.DEADLINE;
        }
        if (command.startsWith("event ")) {
            return CommandType.EVENT;
        }
        return CommandType.INVALID;
    }

    /** Converts a full user command into the object that performs its behavior. */
    public static Command parse(String command) {
        switch (parseCommandType(command)) {
            case BYE:
                return new ByeCommand();
            case LIST:
                return new ListCommand();
            case SORT:
                return new SortCommand();
            case FIND:
                return new FindCommand(command.length() > 4 ? command.substring(5).trim() : "");
            case FIND_DATE:
                return new FindDateCommand(command.length() > 8 ? command.substring(9).trim() : "");
            case MARK:
                return new MarkCommand(command);
            case UNMARK:
                return new UnmarkCommand(command);
            case DELETE:
                return new DeleteCommand(command);
            case TODO:
                return new TodoCommand(command.length() > 4 ? command.substring(5).trim() : "");
            case DEADLINE:
                return new DeadlineCommand(command.substring(9));
            case EVENT:
                return new EventCommand(command.substring(6));
            default:
                return new InvalidCommand();
        }
    }

    /** Converts the task number in a command into a validated zero-based index. */
    public static int getIndex(String command, int taskCount) {
        String[] parts = command.trim().split("\\s+");
        int index = Integer.parseInt(parts[1]) - 1;
        if (index < 0 || index >= taskCount) {
            throw new IndexOutOfBoundsException();
        }
        return index;
    }

    /** Parses a supported date format. */
    public static LocalDate parseDate(String date) {
        try {
            return LocalDate.parse(date, ISO_DATE_FORMAT);
        } catch (DateTimeParseException e) {
            try {
                return LocalDate.parse(date, SLASH_DATE_FORMAT);
            } catch (DateTimeParseException e2) {
                return LocalDate.parse(date, DASH_DATE_FORMAT);
            }
        }
    }

    /** Parses a supported date-time format, or treats a date as midnight. */
    public static LocalDateTime parseDateTime(String dateTime) {
        try {
            return LocalDateTime.parse(dateTime, ISO_DATE_TIME_FORMAT);
        } catch (DateTimeParseException e) {
            try {
                return LocalDateTime.parse(dateTime, SLASH_DATE_TIME_FORMAT);
            } catch (DateTimeParseException e2) {
                try {
                    return LocalDateTime.parse(dateTime, DASH_DATE_TIME_FORMAT);
                } catch (DateTimeParseException e3) {
                    return parseDate(dateTime).atStartOfDay();
                }
            }
        }
    }

    /** Returns a formatter that rejects impossible dates instead of adjusting them. */
    private static DateTimeFormatter strictFormatter(String pattern) {
        return DateTimeFormatter.ofPattern(pattern).withResolverStyle(ResolverStyle.STRICT);
    }
}
