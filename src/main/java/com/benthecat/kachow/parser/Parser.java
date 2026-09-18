package com.benthecat.kachow.parser;

import static com.benthecat.kachow.parser.ParserSupport.DATE_FORMAT_GUIDANCE;
import static com.benthecat.kachow.parser.ParserSupport.USAGE_EDIT;
import static com.benthecat.kachow.parser.ParserSupport.USAGE_FIND;
import static com.benthecat.kachow.parser.ParserSupport.USAGE_ON;
import static com.benthecat.kachow.parser.ParserSupport.createInvalidTaskNumberException;
import static com.benthecat.kachow.parser.ParserSupport.createMissingEditFieldException;
import static com.benthecat.kachow.parser.ParserSupport.findFirstWhitespaceIndex;
import static com.benthecat.kachow.parser.ParserSupport.findNextEditFieldIndex;
import static com.benthecat.kachow.parser.ParserSupport.skipWhitespace;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.EnumMap;
import java.util.Map;

import com.benthecat.kachow.exception.KachowException;
import com.benthecat.kachow.task.Task;

/**
 * Converts raw user input into validated commands that the application can execute.
 */
public class Parser {
    private final TaskParser taskParser = new TaskParser();
    private final TaskEditor taskEditor = new TaskEditor();

    /** Creates a parser for Kachow's supported console commands. */
    public Parser() {
        // This parser has no mutable state to initialize.
    }

    /**
     * Separates one input line into its command and argument.
     *
     * @param input Raw input entered by the user.
     * @return Parsed command keyword and its remaining argument.
     * @throws KachowException If the command keyword is empty or unsupported.
     */
    public ParsedCommand parse(String input) throws KachowException {
        if (input == null) {
            throw new KachowException("Enter a command to keep racing.");
        }
        if (input.codePoints().anyMatch(character -> (
                Character.isISOControl(character) && character != '\t')
                        || character == 0x2028 || character == 0x2029)) {
            throw new KachowException("Enter one command without line breaks or control characters.");
        }

        String commandText = input.replaceAll("(?U)\\s+", " ").strip();
        int separatorIndex = findFirstWhitespaceIndex(commandText);
        String keyword = separatorIndex == -1 ? commandText : commandText.substring(0, separatorIndex);
        String argument = separatorIndex == -1 ? "" : commandText.substring(separatorIndex).strip();

        if (keyword.isEmpty()) {
            throw new KachowException(
                    "That command stalled on the starting line. Enter a command to keep racing.");
        }

        return new ParsedCommand(Command.fromKeyword(keyword), argument);
    }

    /**
     * Validates a command that must not have an argument.
     *
     * @param parsedCommand Command to validate.
     * @throws KachowException If an unexpected argument is present.
     */
    public void requireNoArgument(ParsedCommand parsedCommand) throws KachowException {
        assert parsedCommand.command() == Command.BYE || parsedCommand.command() == Command.LIST
                : "Only bye and list are argument-free commands";

        if (!parsedCommand.argument().isEmpty()) {
            Command command = parsedCommand.command();
            throw new KachowException(
                    "The " + command.getKeyword() + " command has extra cargo. Use: " + command.getKeyword());
        }
    }

    /**
     * Creates the task described by an add command.
     *
     * @param parsedCommand Todo, deadline, or event command to parse.
     * @return Parsed task.
     * @throws KachowException If a required task component is missing or invalid.
     */
    public Task parseTask(ParsedCommand parsedCommand) throws KachowException {
        return taskParser.parseTask(parsedCommand);
    }

    /**
     * Parses the date argument accepted by the {@code on} command.
     *
     * @param parsedCommand On command to parse.
     * @return Validated calendar date.
     * @throws KachowException If the date is missing, invalid, or includes a time.
     */
    public LocalDate parseDate(ParsedCommand parsedCommand) throws KachowException {
        assert parsedCommand.command() == Command.ON : "Only the on command has a date argument";

        String argument = parsedCommand.argument();
        if (argument.isEmpty()) {
            throw new KachowException("Tell me which race date to check. Use: " + USAGE_ON);
        }

        DateTimeParser.ParsedDateTime parsedDate;
        try {
            parsedDate = DateTimeParser.parse(argument);
        } catch (DateTimeParseException exception) {
            throw new KachowException("That date is invalid. " + DATE_FORMAT_GUIDANCE, exception);
        }
        if (parsedDate.time().isPresent()) {
            throw new KachowException("The on command needs a date without a time. Use: " + USAGE_ON);
        }
        return parsedDate.date();
    }

    /**
     * Returns the non-empty keyword accepted by the {@code find} command.
     *
     * @param parsedCommand Find command to parse.
     * @return Keyword to search for in task descriptions.
     * @throws KachowException If the keyword is missing.
     */
    public String parseSearchKeyword(ParsedCommand parsedCommand) throws KachowException {
        assert parsedCommand.command() == Command.FIND : "Only the find command has a search keyword";

        String keyword = parsedCommand.argument();
        if (keyword.isEmpty()) {
            throw new KachowException("Tell me which racer to search for. Use: " + USAGE_FIND);
        }
        return keyword;
    }

    /**
     * Parses a positive, one-based task number for a task-list command.
     *
     * @param parsedCommand Mark, unmark, or delete command to parse.
     * @return Validated task number.
     * @throws KachowException If the task number is missing or malformed.
     */
    public int parseTaskNumber(ParsedCommand parsedCommand) throws KachowException {
        assert parsedCommand.command() == Command.MARK
                || parsedCommand.command() == Command.UNMARK
                || parsedCommand.command() == Command.DELETE
                : "Only task-list commands have a task number";

        String argument = parsedCommand.argument();
        Command action = parsedCommand.command();
        if (argument.isEmpty()) {
            throw new KachowException(
                    "Tell me which racer to " + action.getKeyword()
                            + ". Use: " + action.getKeyword() + " TASK_NUMBER");
        }

        return parsePositiveTaskNumber(argument, action);
    }

    /**
     * Parses the task number and replacement field/value pairs of an edit command.
     *
     * @param parsedCommand Edit command to parse.
     * @return Validated edit request.
     * @throws KachowException If the task number, a field, or a replacement value is missing or malformed.
     */
    public EditCommand parseEditCommand(ParsedCommand parsedCommand) throws KachowException {
        assert parsedCommand.command() == Command.EDIT : "Only the edit command can be parsed as an edit";

        String argument = parsedCommand.argument();
        if (argument.isEmpty()) {
            throw new KachowException("Tell me which racer to edit. Use: " + USAGE_EDIT);
        }

        int numberEndIndex = findFirstWhitespaceIndex(argument);
        String taskNumberText = numberEndIndex == -1 ? argument : argument.substring(0, numberEndIndex);
        int taskNumber = parsePositiveTaskNumber(taskNumberText, Command.EDIT);
        if (numberEndIndex == -1) {
            throw createMissingEditFieldException();
        }

        String fieldArguments = argument.substring(numberEndIndex).strip();
        return new EditCommand(taskNumber, parseEditChanges(fieldArguments));
    }

    /**
     * Creates an updated copy of a task according to one parsed edit request.
     *
     * @param task Existing task to update.
     * @param editCommand Parsed edit request.
     * @return Updated task with its unedited details and completion status preserved.
     * @throws KachowException If the selected field does not apply or its new value is invalid.
     */
    public Task applyEdit(Task task, EditCommand editCommand) throws KachowException {
        return taskEditor.applyEdit(task, editCommand);
    }

    /** Parses every field/value pair while rejecting duplicate or incomplete fields. */
    private Map<EditField, String> parseEditChanges(String fieldArguments) throws KachowException {
        Map<EditField, String> changes = new EnumMap<>(EditField.class);
        int fieldIndex = 0;
        while (fieldIndex < fieldArguments.length()) {
            int fieldEndIndex = findFirstWhitespaceIndex(fieldArguments.substring(fieldIndex));
            int absoluteFieldEndIndex = fieldEndIndex == -1
                    ? fieldArguments.length()
                    : fieldIndex + fieldEndIndex;
            String fieldText = fieldArguments.substring(fieldIndex, absoluteFieldEndIndex);
            EditField field = EditField.fromKeyword(fieldText);
            if (changes.containsKey(field)) {
                throw new KachowException(
                        "That edit repeats " + field.getKeyword() + ". Specify each detail once.");
            }

            int valueStartIndex = skipWhitespace(fieldArguments, absoluteFieldEndIndex);
            int nextFieldIndex = findNextEditFieldIndex(fieldArguments, valueStartIndex);
            int valueEndIndex = nextFieldIndex == -1 ? fieldArguments.length() : nextFieldIndex;
            String value = fieldArguments.substring(valueStartIndex, valueEndIndex).strip();
            if (value.isEmpty()) {
                throw new KachowException(
                        "That " + field.getKeyword() + " detail needs a new value. Use: " + USAGE_EDIT);
            }
            changes.put(field, value);
            fieldIndex = valueEndIndex;
        }
        return Map.copyOf(changes);
    }

    /** Parses a positive task number from a complete numeric token. */
    private int parsePositiveTaskNumber(String taskNumberText, Command action) throws KachowException {
        if (!taskNumberText.matches("[0-9]+")) {
            throw createInvalidTaskNumberException(action, null);
        }
        int taskNumber;
        try {
            taskNumber = Integer.parseInt(taskNumberText);
        } catch (NumberFormatException exception) {
            throw createInvalidTaskNumberException(action, exception);
        }
        if (taskNumber <= 0) {
            throw createInvalidTaskNumberException(action, null);
        }
        return taskNumber;
    }

    /** Holds a recognized command and the unprocessed text following its keyword. */
    public record ParsedCommand(Command command, String argument) { }

    /** Holds a task number and every replacement field/value pair of an edit command. */
    public record EditCommand(int taskNumber, Map<EditField, String> changes) {
        /** Creates an edit command with an immutable snapshot of its changes. */
        public EditCommand {
            changes = Map.copyOf(changes);
        }
    }

    /** Identifies a task detail that the edit command can replace. */
    public enum EditField {
        DESCRIPTION("/description"),
        BY("/by"),
        FROM("/from"),
        TO("/to");

        private final String keyword;

        EditField(String keyword) {
            this.keyword = keyword;
        }

        /** Returns the slash-prefixed keyword for this editable field. */
        public String getKeyword() {
            return keyword;
        }

        /** Converts a slash-prefixed field keyword into its corresponding editable field. */
        private static EditField fromKeyword(String keyword) throws KachowException {
            for (EditField field : values()) {
                if (field.keyword.equals(keyword)) {
                    return field;
                }
            }
            throw new KachowException(
                    "That detail cannot be edited. Use /description, /by, /from, or /to.");
        }
    }
}
