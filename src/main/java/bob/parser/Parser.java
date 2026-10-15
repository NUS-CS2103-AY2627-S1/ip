package bob.parser;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import bob.command.AddCommand;
import bob.command.Command;
import bob.command.DeleteCommand;
import bob.command.ExitCommand;
import bob.command.FindCommand;
import bob.command.ListCommand;
import bob.command.MarkCommand;
import bob.command.UpdateCommand;
import bob.exception.BobException;
import bob.task.Deadline;
import bob.task.Event;
import bob.task.ToDo;
import bob.util.DatetimeHelper;

/**
 * Parses user input into executable Command objects.
 */
public class Parser {

    private static final String DELIMITER_BY = " /by ";
    private static final String DELIMITER_FROM = " /from ";
    private static final String DELIMITER_TO = " /to ";
    private static final String FLAG_DESC = "/desc";
    private static final String FLAG_BY = "/by";
    private static final String FLAG_FROM = "/from";
    private static final String FLAG_TO = "/to";
    private static final Set<String> VALID_UPDATE_FLAGS = Set.of(FLAG_DESC, FLAG_BY, FLAG_FROM, FLAG_TO);
    private static final String REGEX_FLAG_DELIMITER = "\\s+(?=/)";
    private static final String MESSAGE_INVALID_DATE_FORMAT = """
            Error: Cannot parse date
            Date Format: dd/MM/yy HH:mm
            """;

    /**
     * Prevents instantiation of this utility class.
     */
    private Parser() {
    }

    /**
     * Parses the full user command string into a specific Command.
     *
     * @param fullCommand the raw input entered by the user
     * @return the Command corresponding to the input
     * @throws BobException if the command is unrecognized or has invalid arguments
     */
    public static Command parse(String fullCommand) throws BobException {
        if (fullCommand == null || fullCommand.isBlank()) {
            throw new BobException("What's that?");
        }

        String[] parts = fullCommand.trim().split(" ", 2);
        assert parts.length > 0 : "Command parts array should never be empty after splitting";
        String commandWord = parts[0].toLowerCase(Locale.ROOT);
        assert commandWord != null && !commandWord.isEmpty() : "Command word should not be empty";
        String arguments = parts.length > 1 ? parts[1].trim() : "";

        switch (commandWord) {
            case "bye":
                return new ExitCommand();

            case "list":
                return new ListCommand();

            case "find":
                return parseFindCommand(arguments);

            case "mark":
                return parseMarkCommand(arguments, true);

            case "unmark":
                return parseMarkCommand(arguments, false);

            case "delete":
                return parseDeleteCommand(arguments);

            case "update":
                return parseUpdateCommand(arguments);

            case "todo":
                return parseTodoCommand(arguments);

            case "deadline":
                return parseDeadlineCommand(arguments);

            case "event":
                return parseEventCommand(arguments);

            default:
                throw new BobException("What's that?");
        }
    }

    /**
     * Parses the arguments for a mark or unmark command.
     *
     * @param args   the argument string containing the 1-based task index
     * @param isDone true to mark the task as done, false to unmark
     * @return a {@link MarkCommand} configured with the parsed task index and
     *         status
     * @throws BobException if the arguments are empty or not a valid integer
     */
    private static Command parseMarkCommand(String args, boolean isDone) throws BobException {
        int taskId = parseListIndex(args);
        return new MarkCommand(taskId, isDone);
    }

    /**
     * Parses the arguments for a delete command.
     *
     * @param args the argument string containing the 1-based task index
     * @return a {@link DeleteCommand} configured with the parsed task index
     * @throws BobException if the arguments are empty or not a valid integer
     */
    private static Command parseDeleteCommand(String args) throws BobException {
        int taskId = parseListIndex(args);
        return new DeleteCommand(taskId);
    }

    /**
     * Parses the arguments for creating a todo task.
     *
     * @param args the description of the todo task
     * @return an {@link AddCommand} configured with a new {@link ToDo} task
     * @throws BobException if the description is empty
     */
    private static Command parseTodoCommand(String args) throws BobException {
        if (args.isEmpty()) {
            throw new BobException("todo needs a description");
        }
        return new AddCommand(new ToDo(args));
    }

    /**
     * Parses the arguments for creating a deadline task.
     *
     * @param args the argument string containing task description and {@code /by}
     *             date-time
     * @return an {@link AddCommand} configured with a new {@link Deadline} task
     * @throws BobException if the description or date is missing, or date-time
     *                      format is invalid
     */
    private static Command parseDeadlineCommand(String args) throws BobException {
        if (args.isEmpty()) {
            throw new BobException("deadline needs a description");
        }

        String[] parts = splitOnDelimiter(args, DELIMITER_BY);
        if (parts.length < 2 || parts[0].isBlank() || parts[1].isBlank()) {
            throw new BobException("""
                    Error: No deadline set for deadline task
                    Usage: deadline ___ \\by ___
                    """);
        }

        LocalDateTime by = parseDateTime(parts[1]);
        return new AddCommand(new Deadline(parts[0], by));
    }

    /**
     * Parses the arguments for creating an event task.
     *
     * @param args the argument string containing description, {@code /from}, and
     *             {@code /to} date-times
     * @return an {@link AddCommand} configured with a new {@link Event} task
     * @throws BobException if any part is missing or date-time format is invalid
     */
    private static Command parseEventCommand(String args) throws BobException {
        if (args.isEmpty()) {
            throw new BobException("event needs a description");
        }

        String[] parts = splitOnDelimiter(args, DELIMITER_FROM);
        if (parts.length < 2 || parts[0].isBlank()) {
            throw new BobException("""
                    Error: Missing either /from or /to
                    Usage: event ___ /from ___ /to ___
                    """);
        }

        String[] dateParts = splitOnDelimiter(parts[1], DELIMITER_TO);
        if (dateParts.length < 2 || dateParts[0].isBlank() || dateParts[1].isBlank()) {
            throw new BobException("""
                    Error: Missing either /from or /to
                    Usage: event ___ /from ___ /to ___
                    """);
        }

        LocalDateTime from = parseDateTime(dateParts[0]);
        LocalDateTime to = parseDateTime(dateParts[1]);
        return new AddCommand(new Event(parts[0], from, to));
    }

    /**
     * Parses a date-time string in the standard input format into a
     * {@link LocalDateTime}.
     *
     * @param dateTimeStr the date-time string to parse
     * @return the parsed {@link LocalDateTime}
     * @throws BobException if the date-time string does not match the expected
     *                      format
     */
    private static LocalDateTime parseDateTime(String dateTimeStr) throws BobException {
        try {
            return LocalDateTime.parse(dateTimeStr, DatetimeHelper.INPUT_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new BobException(MESSAGE_INVALID_DATE_FORMAT);
        }
    }

    /**
     * Splits an argument string on a case-insensitive syntax delimiter while preserving user text.
     *
     * @param value the argument string to split
     * @param delimiter the lowercase syntax delimiter
     * @return the text before and after the first delimiter, or the original text if absent
     */
    private static String[] splitOnDelimiter(String value, String delimiter) {
        int delimiterIndex = value.toLowerCase(Locale.ROOT).indexOf(delimiter);
        if (delimiterIndex < 0) {
            return new String[] { value };
        }
        return new String[] {
            value.substring(0, delimiterIndex),
            value.substring(delimiterIndex + delimiter.length())
        };
    }

    /**
     * Parses the arguments for creating a find command.
     *
     * @param args the search keyword entered by the user
     * @return a {@link FindCommand} configured with the search keyword
     * @throws BobException if the search keyword is empty
     */
    private static Command parseFindCommand(String args) throws BobException {
        if (args.isEmpty()) {
            throw new BobException("find needs a keyword");
        }
        return new FindCommand(args);
    }

    /**
     * Parses the arguments for an update command.
     *
     * @param args the raw arguments string containing the 1-based task index and flags
     * @return an {@link UpdateCommand} configured with the parsed task index and updated field values
     * @throws BobException if arguments are invalid, index is missing/non-integer, flags are invalid,
     *                      duplicate flags are found, or date format cannot be parsed
     */
    private static Command parseUpdateCommand(String args) throws BobException {
        String[] parts = splitUpdateArguments(args);
        int taskId = parseListIndex(parts[0]);
        ParsedUpdateFields fields = parseUpdateFlags(parts[1]);
        return new UpdateCommand(taskId, fields.name(), fields.deadline(), fields.from(), fields.to());
    }

    /**
     * Splits and validates the raw update argument string into task index and flags segments.
     *
     * @param args the raw arguments string
     * @return a two-element array containing the task index string and flags string
     * @throws BobException if arguments are empty or no flags are specified
     */
    private static String[] splitUpdateArguments(String args) throws BobException {
        if (args == null || args.isBlank()) {
            throw new BobException("Error: Argument must be an integer");
        }

        String[] parts = args.trim().split("\\s+", 2);
        if (parts.length < 2 || parts[1].isBlank()) {
            throw new BobException("""
                    Error: No fields specified to update.
                    Usage: update <INDEX> [/desc <DESC>] [/by <BY>] [/from <FROM>] [/to <TO>]""");
        }
        return parts;
    }

    /**
     * Parses and validates the flags section of an update command into structured fields.
     *
     * @param flagsString the flags portion of the arguments
     * @return a {@link ParsedUpdateFields} containing the parsed update values
     * @throws BobException if flag format, values, or date formats are invalid
     */
    private static ParsedUpdateFields parseUpdateFlags(String flagsString) throws BobException {
        validateFlagsPrefix(flagsString);
        String[] flagSegments = splitFlagSegments(flagsString);
        return processFlagSegments(flagSegments);
    }

    /**
     * Validates that the flags argument starts with a flag identifier.
     *
     * @param flagsString the flags portion of the arguments
     * @throws BobException if leading preamble text precedes the flags
     */
    private static void validateFlagsPrefix(String flagsString) throws BobException {
        if (!flagsString.trim().startsWith("/")) {
            throw new BobException("Error: Invalid arguments before flags");
        }
    }

    /**
     * Splits the flags string into individual flag segments.
     *
     * @param flagsString the flags portion of the arguments
     * @return an array of raw flag segment strings
     */
    private static String[] splitFlagSegments(String flagsString) {
        return flagsString.trim().split(REGEX_FLAG_DELIMITER);
    }

    /**
     * Processes each flag segment and aggregates the parsed field values.
     *
     * @param segments the raw flag segments
     * @return a {@link ParsedUpdateFields} containing all specified updates
     * @throws BobException if duplicate, unrecognized, or invalid flag values are found
     */
    private static ParsedUpdateFields processFlagSegments(String[] segments) throws BobException {
        Set<String> seenFlags = new HashSet<>();
        String newName = null;
        LocalDateTime newDeadline = null;
        LocalDateTime newFrom = null;
        LocalDateTime newTo = null;

        for (String rawSegment : segments) {
            String segment = rawSegment.trim();
            if (segment.isEmpty()) {
                continue;
            }

            FlagEntry entry = parseFlagEntry(segment, seenFlags);
            switch (entry.flag()) {
                case FLAG_DESC:
                    newName = entry.value();
                    break;
                case FLAG_BY:
                    newDeadline = parseDateTime(entry.value());
                    break;
                case FLAG_FROM:
                    newFrom = parseDateTime(entry.value());
                    break;
                case FLAG_TO:
                    newTo = parseDateTime(entry.value());
                    break;
                default:
                    throw new BobException("Error: Unrecognized flag: " + entry.flag());
            }
        }

        if (seenFlags.isEmpty()) {
            throw new BobException("""
                    Error: No fields specified to update.
                    Usage: update <INDEX> [/desc <DESC>] [/by <BY>] [/from <FROM>] [/to <TO>]""");
        }

        return new ParsedUpdateFields(newName, newDeadline, newFrom, newTo);
    }

    /**
     * Parses a single flag segment into a validated {@link FlagEntry}.
     *
     * @param segment   the single flag segment string (e.g., "/desc buy milk")
     * @param seenFlags set of flags already parsed for duplicate detection
     * @return a validated {@link FlagEntry}
     * @throws BobException if the flag is unrecognized, duplicated, or has an empty value
     */
    private static FlagEntry parseFlagEntry(String segment, Set<String> seenFlags) throws BobException {
        String[] parts = segment.split("\\s+", 2);
        String flag = parts[0].toLowerCase(Locale.ROOT);
        String value = parts.length > 1 ? parts[1].trim() : "";

        if (!VALID_UPDATE_FLAGS.contains(flag)) {
            throw new BobException("Error: Unrecognized flag: " + flag);
        }

        if (seenFlags.contains(flag)) {
            throw new BobException("Error: Duplicate flag: " + flag);
        }
        seenFlags.add(flag);

        if (value.isEmpty()) {
            throw new BobException(flag.equals(FLAG_DESC)
                    ? "Error: Description cannot be empty"
                    : "Error: Date cannot be empty");
        }

        return new FlagEntry(flag, value);
    }

    /**
     * Parses a 1-based task list index string into a positive integer.
     *
     * @param idString the string representation of the index
     * @return the parsed positive integer
     * @throws BobException if the string is empty or not a positive integer
     */
    private static int parseListIndex(String idString) throws BobException {
        if (idString == null || idString.isBlank()) {
            throw new BobException("Error: Argument must be an integer");
        }
        try {
            int id = Integer.parseInt(idString.trim());
            if (id <= 0) {
                throw new BobException("Error: taskId out of bounds");
            }
            return id;
        } catch (NumberFormatException e) {
            throw new BobException("Error: Argument must be an integer");
        }
    }

    /**
     * Represents an individual flag and its string value.
     */
    private record FlagEntry(String flag, String value) {}

    /**
     * Represents the parsed field values for an update command.
     */
    private record ParsedUpdateFields(String name, LocalDateTime deadline, LocalDateTime from, LocalDateTime to) {}
}
