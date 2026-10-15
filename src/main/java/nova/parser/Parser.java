package nova.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import nova.exception.NovaException;
import nova.task.Event;
import nova.task.Task;

/** Interprets user input as a validated Nova command. */
public class Parser {
    /** The command categories understood by Nova. */
    public enum CommandType {
        EXIT, LIST, FIND, ON, MARK, UNMARK, DELETE, TAG, UNTAG, TODO, DEADLINE, EVENT
    }

    /** The validated result of parsing one user command. */
    public record ParsedCommand(CommandType type, int taskNumber, String description,
                                LocalDate date, String from, String to, String tag) {
        /** Creates a command without a tag argument. */
        public ParsedCommand(CommandType type, int taskNumber, String description, LocalDate date,
                             String from, String to) {
            this(type, taskNumber, description, date, from, to, "");
        }
    }

    /** Parses one complete command and extracts its arguments. */
    public ParsedCommand parse(String command) throws NovaException {
        if (command == null) {
            throw new NovaException("Please enter a command.");
        }
        if (command.equals("bye")) {
            return new ParsedCommand(CommandType.EXIT, 0, "", null, "", "");
        }
        if (command.equals("list")) {
            return new ParsedCommand(CommandType.LIST, 0, "", null, "", "");
        }
        if (isCommandWithOptionalArgument(command, "find")) {
            return parseFindCommand(command);
        }
        if (command.startsWith("on ")) {
            return new ParsedCommand(CommandType.ON, 0, "", parseDate(command.substring(3).trim()), "", "");
        }
        if (isCommandWithOptionalArgument(command, "mark")) {
            return createTaskCommand(CommandType.MARK, command, "mark");
        }
        if (isCommandWithOptionalArgument(command, "unmark")) {
            return createTaskCommand(CommandType.UNMARK, command, "unmark");
        }
        if (isCommandWithOptionalArgument(command, "delete")) {
            return createTaskCommand(CommandType.DELETE, command, "delete");
        }
        if (isCommandWithOptionalArgument(command, "tag")) {
            return parseTagCommand(CommandType.TAG, command, "tag");
        }
        if (isCommandWithOptionalArgument(command, "untag")) {
            return parseTagCommand(CommandType.UNTAG, command, "untag");
        }
        if (isCommandWithOptionalArgument(command, "todo")) {
            return parseTodoCommand(command);
        }
        if (command.startsWith("deadline ")) {
            return parseDeadlineCommand(command);
        }
        if (command.startsWith("event ")) {
            return parseEventCommand(command);
        }
        throw new NovaException("I don't recognize that command.");
    }

    /** Returns whether a command is either complete or followed by an argument. */
    private static boolean isCommandWithOptionalArgument(String command, String commandWord) {
        return command.trim().equals(commandWord) || command.startsWith(commandWord + " ");
    }

    /** Parses a find command and validates its keyword. */
    private static ParsedCommand parseFindCommand(String command) throws NovaException {
        String keyword = command.trim().equals("find") ? "" : command.substring(5).trim();
        if (keyword.isEmpty()) {
            throw new NovaException("Please add a keyword after 'find'.");
        }
        return new ParsedCommand(CommandType.FIND, 0, keyword, null, "", "");
    }

    /** Parses a todo command and validates its description. */
    private static ParsedCommand parseTodoCommand(String command) throws NovaException {
        String description = command.trim().equals("todo") ? "" : command.substring(5).trim();
        if (description.isEmpty()) {
            throw new NovaException("Please add a description after 'todo'.");
        }
        rejectStorageDelimiter(description, "task descriptions");
        return new ParsedCommand(CommandType.TODO, 0, description, null, "", "");
    }

    /** Parses a deadline command and validates its format and date. */
    private static ParsedCommand parseDeadlineCommand(String command) throws NovaException {
        int byIndex = command.indexOf(" /by ");
        if (byIndex <= 9 || byIndex + 5 >= command.length()) {
            throw new NovaException("Please use: deadline DESCRIPTION /by DATE.");
        }
        String description = command.substring(9, byIndex);
        rejectStorageDelimiter(description, "task descriptions");
        return new ParsedCommand(CommandType.DEADLINE, 0, description,
                parseDate(command.substring(byIndex + 5)), "", "");
    }

    /** Parses an event command and validates its time range. */
    private static ParsedCommand parseEventCommand(String command) throws NovaException {
        int fromIndex = command.indexOf(" /from ");
        int toIndex = command.indexOf(" /to ");
        if (fromIndex <= 6 || toIndex <= fromIndex + 7 || toIndex + 5 >= command.length()) {
            throw new NovaException("Please use: event DESCRIPTION /from START /to END.");
        }
        String description = command.substring(6, fromIndex);
        String from = command.substring(fromIndex + 7, toIndex);
        String to = command.substring(toIndex + 5);
        rejectStorageDelimiter(description, "task descriptions");
        rejectStorageDelimiter(from, "event date-times");
        rejectStorageDelimiter(to, "event date-times");
        validateEventDateTimeRange(from, to);
        return new ParsedCommand(CommandType.EVENT, 0, description, null, from, to);
    }

    /** Parses a tag or untag command and validates its task number and label. */
    private static ParsedCommand parseTagCommand(CommandType type, String command, String commandWord)
            throws NovaException {
        String[] arguments = command.substring(commandWord.length()).trim().split("\\s+", -1);
        if (arguments.length != 2) {
            throw new NovaException("Please use: " + commandWord + " INDEX #label.");
        }
        int taskNumber;
        try {
            taskNumber = Integer.parseInt(arguments[0]);
        } catch (NumberFormatException exception) {
            throw new NovaException("Please provide a valid task number.");
        }
        if (!Task.isValidTag(arguments[1])) {
            throw new NovaException("Please provide a valid tag in the format #label.");
        }
        return new ParsedCommand(type, taskNumber, "", null, "", "", arguments[1]);
    }

    /** Verifies that an event uses real date-times in chronological order. */
    private static void validateEventDateTimeRange(String from, String to) throws NovaException {
        LocalDateTime start;
        LocalDateTime end;
        try {
            start = Event.parseDateTime(from);
            end = Event.parseDateTime(to);
        } catch (DateTimeParseException exception) {
            throw new NovaException("Please use event date-times in yyyy-MM-dd HH:mm format.");
        }
        if (!start.isBefore(end)) {
            throw new NovaException("Event start must be before its end.");
        }
    }

    /** Rejects characters that would be interpreted as storage separators. */
    private static void rejectStorageDelimiter(String value, String valueDescription) throws NovaException {
        if (value.contains("|")) {
            throw new NovaException("The character '|' is not allowed in " + valueDescription + ".");
        }
    }

    /** Creates a parsed command whose only argument is a task number. */
    private static ParsedCommand createTaskCommand(CommandType type, String command, String commandWord)
            throws NovaException {
        try {
            int taskNumber = Integer.parseInt(command.substring(commandWord.length()).trim());
            return new ParsedCommand(type, taskNumber, "", null, "", "");
        } catch (NumberFormatException exception) {
            throw new NovaException("Please provide a valid task number.");
        }
    }

    /** Parses a date and reports malformed values consistently. */
    private static LocalDate parseDate(String value) throws NovaException {
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException exception) {
            throw new NovaException("Please use a valid date in yyyy-MM-dd format.");
        }
    }
}
