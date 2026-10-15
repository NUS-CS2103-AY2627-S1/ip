package bubu.parser;

import java.time.LocalDateTime;
import java.time.LocalTime;

import bubu.command.Command;
import bubu.command.CommandType;
import bubu.command.DeadlineCommand;
import bubu.command.EventCommand;
import bubu.command.ExitCommand;
import bubu.command.FindCommand;
import bubu.command.ListCommand;
import bubu.command.RemindCommand;
import bubu.command.TodoCommand;
import bubu.exception.BubuException;
import bubu.exception.DuplicateArgumentException;
import bubu.exception.EmptyDescriptionException;
import bubu.exception.InvalidDateRangeException;
import bubu.exception.InvalidDateTimeException;
import bubu.exception.InvalidDescriptionException;
import bubu.exception.MissingArgumentException;
import bubu.exception.UnexpectedArgumentException;
import bubu.exception.UnknownCommandException;
import bubu.util.DateTimeParser;

/**
 * Converts user input into command types, command objects, and date-time values.
 */
public class Parser {
    // Command syntax.
    private static final String REGEX_WHITESPACE = "\\s+";
    private static final String ARGUMENT_MARKER_BY = "/by";
    private static final String ARGUMENT_MARKER_FROM = "/from";
    private static final String ARGUMENT_MARKER_TO = "/to";
    private static final int ARGUMENT_MARKER_PREFIX_LENGTH = 1;
    private static final String DELIMITER_BY = REGEX_WHITESPACE + ARGUMENT_MARKER_BY
            + REGEX_WHITESPACE;
    private static final String DELIMITER_FROM = REGEX_WHITESPACE + ARGUMENT_MARKER_FROM
            + REGEX_WHITESPACE;
    private static final String DELIMITER_TO = REGEX_WHITESPACE + ARGUMENT_MARKER_TO
            + REGEX_WHITESPACE;

    // Command names.
    private static final String COMMAND_NAME_LIST = "list";
    private static final String COMMAND_NAME_BYE = "bye";
    private static final String COMMAND_NAME_REMIND = "remind";
    private static final String COMMAND_NAME_DEADLINE = "deadline";
    private static final String COMMAND_NAME_EVENT = "event";

    // Input validation.
    private static final String UNEXTRACTED_COMMAND_TYPE_MESSAGE =
            "Command type has not yet been extracted: ";
    private static final String TASK_FIELD_DELIMITER = "|";
    private static final String LINE_FEED = "\n";
    private static final String CARRIAGE_RETURN = "\r";

    // Split and result-array positions.
    private static final int ARGUMENT_SPLIT_LIMIT = 2;
    private static final int COMMAND_ONLY_PART_COUNT = 1;
    private static final int INDEX_COMMAND_WORD = 0;
    private static final int INDEX_ARGUMENT_BODY = 1;
    private static final int INDEX_FIRST_VALUE = 0;
    private static final int INDEX_SECOND_VALUE = 1;
    private static final int INDEX_THIRD_VALUE = 2;
    private static final int DEADLINE_VALUE_COUNT = 2;
    private static final int EVENT_VALUE_COUNT = 3;

    // Date-only defaults.
    private static final LocalTime DEFAULT_END_TIME = LocalTime.of(23, 59);
    private static final LocalTime DEFAULT_START_TIME = LocalTime.MIDNIGHT;

    /**
     * Extracts the command word after ignoring leading and repeated whitespace.
     *
     * @param input Command entered by the user.
     * @return The matching command type.
     * @throws BubuException If the command is unknown or input is empty.
     */
    public static CommandType parseCommandType(String input) throws BubuException {
        assert input != null : "input string cannot be null";

        String trimmedInput = input.trim();
        if (trimmedInput.isEmpty()) {
            throw new UnknownCommandException();
        }

        String commandWord = trimmedInput.split(REGEX_WHITESPACE, ARGUMENT_SPLIT_LIMIT)[INDEX_COMMAND_WORD];

        try {
            return CommandType.valueOf(commandWord.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new UnknownCommandException(trimmedInput);
        }
    }

    /**
     * Creates an executable command object from the parsed command type.
     *
     * @param commandType Parsed command type.
     * @param input Full user input.
     * @return Executable command object.
     * @throws BubuException If command arguments are invalid.
     */
    public static Command createCommand(CommandType commandType, String input) throws BubuException {
        assert commandType != null : "CommandType cannot be null";
        assert input != null : "Input string cannot be null";

        return switch (commandType) {
            case LIST -> {
                requireNoArguments(input, COMMAND_NAME_LIST);
                yield new ListCommand();
            }
            case BYE -> {
                requireNoArguments(input, COMMAND_NAME_BYE);
                yield new ExitCommand();
            }
            case TODO -> new TodoCommand(parseArg(input));
            case FIND -> new FindCommand(parseArg(input));
            case REMIND -> {
                requireNoArguments(input, COMMAND_NAME_REMIND);
                yield new RemindCommand();
            }
            case DEADLINE -> createDeadlineCommand(input);
            case EVENT -> createEventCommand(input);
            default -> throw new IllegalArgumentException(UNEXTRACTED_COMMAND_TYPE_MESSAGE + commandType);
        };
    }

    /**
     * Extracts the text following a command word.
     *
     * @param input Full command entered by the user.
     * @return Trimmed command argument.
     * @throws BubuException If the argument is missing.
     */
    public static String parseArg(String input) throws BubuException {
        String[] args = input.trim().split(REGEX_WHITESPACE, ARGUMENT_SPLIT_LIMIT);
        if (args.length < ARGUMENT_SPLIT_LIMIT || args[INDEX_ARGUMENT_BODY].trim().isEmpty()) {
            throw new EmptyDescriptionException(args[INDEX_COMMAND_WORD]);
        }

        String argument = args[INDEX_ARGUMENT_BODY].trim();
        validateDescription(argument);
        return argument;
    }

    /**
     * Extracts a deadline description and its {@code /by} value.
     *
     * @param input Full deadline command.
     * @return Description at index 0 and date-time text at index 1.
     * @throws BubuException If required deadline arguments are missing.
     */
    public static String[] parseDeadline(String input) throws BubuException {
        String args = parseArg(input);
        ensureArgumentAppearsOnce(args, ARGUMENT_MARKER_BY);
        String[] parts = args.split(DELIMITER_BY, ARGUMENT_SPLIT_LIMIT);

        if (hasMissingParts(parts)) {
            throw new MissingArgumentException(COMMAND_NAME_DEADLINE);
        }

        String[] result = new String[] {parts[INDEX_FIRST_VALUE].trim(), parts[INDEX_SECOND_VALUE].trim()};
        assert result.length == DEADLINE_VALUE_COUNT : "Parsed deadline must yield description and date/time";
        return result;
    }

    /**
     * Extracts an event description plus its {@code /from} and {@code /to} values.
     *
     * @param input Full event command.
     * @return Description, start date-time text, and end date-time text.
     * @throws BubuException If required event arguments are missing.
     */
    public static String[] parseEvent(String input) throws BubuException {
        String args = parseArg(input);
        ensureArgumentAppearsOnce(args, ARGUMENT_MARKER_FROM);
        ensureArgumentAppearsOnce(args, ARGUMENT_MARKER_TO);

        String[] parts = args.split(DELIMITER_FROM, ARGUMENT_SPLIT_LIMIT);
        if (parts.length < ARGUMENT_SPLIT_LIMIT || parts[INDEX_FIRST_VALUE].trim().isEmpty()) {
            throw new EmptyDescriptionException(COMMAND_NAME_EVENT);
        }

        String[] timeLine = parts[INDEX_SECOND_VALUE].split(DELIMITER_TO, ARGUMENT_SPLIT_LIMIT);
        if (hasMissingParts(timeLine)) {
            throw new MissingArgumentException(COMMAND_NAME_EVENT);
        }

        String[] output = new String[] {parts[INDEX_FIRST_VALUE].trim(),
                timeLine[INDEX_FIRST_VALUE].trim(),
                timeLine[INDEX_SECOND_VALUE].trim()};
        assert output.length == EVENT_VALUE_COUNT : "Parsed event must yield description, start, and end";
        return output;
    }

    /**
     * Parses a date-time or date-only value into a {@code LocalDateTime}.
     *
     * @param input Date in yyyy-MM-dd or date-time in yyyy-MM-dd HHmm format.
     * @param defaultTime Time to use when the input contains only a date.
     * @return The parsed date and time.
     * @throws InvalidDateTimeException If neither accepted format matches.
     */
    public static LocalDateTime parseDateTime(String input, LocalTime defaultTime)
            throws InvalidDateTimeException {
        return DateTimeParser.parse(input, defaultTime);
    }

    /**
     * Creates a deadline command from the user input.
     *
     * @param input The user input string.
     * @return The created deadline command.
     * @throws BubuException If the input is invalid.
     */
    private static DeadlineCommand createDeadlineCommand(String input) throws BubuException {
        String[] info = parseDeadline(input);
        LocalDateTime dueTime = parseDateTime(info[INDEX_SECOND_VALUE], DEFAULT_END_TIME);
        return new DeadlineCommand(info[INDEX_FIRST_VALUE], dueTime);
    }

    /**
     * Creates an event command from the user input.
     *
     * @param input The user input string.
     * @return The created event command.
     * @throws BubuException If the input is invalid.
     */
    private static EventCommand createEventCommand(String input) throws BubuException {
        String[] info = parseEvent(input);
        LocalDateTime start = parseDateTime(info[INDEX_SECOND_VALUE], DEFAULT_START_TIME);
        LocalDateTime end = parseDateTime(info[INDEX_THIRD_VALUE], DEFAULT_END_TIME);
        if (!end.isAfter(start)) {
            throw new InvalidDateRangeException();
        }
        return new EventCommand(info[INDEX_FIRST_VALUE], start, end);
    }

    /**
     * Checks if the provided parts array has missing or empty elements.
     *
     * @param parts The array of strings to check.
     * @return True if any part is missing or empty, false otherwise.
     */
    private static boolean hasMissingParts(String[] parts) {
        return parts.length < ARGUMENT_SPLIT_LIMIT
                || parts[INDEX_FIRST_VALUE].trim().isEmpty()
                || parts[INDEX_SECOND_VALUE].trim().isEmpty();
    }

    /**
     * Rejects descriptions that would break the task file format.
     *
     * @param description text to validate.
     * @throws InvalidDescriptionException if the text contains an unsupported character.
     */
    private static void validateDescription(String description) throws InvalidDescriptionException {
        if (description.contains(TASK_FIELD_DELIMITER)
                || description.contains(LINE_FEED)
                || description.contains(CARRIAGE_RETURN)) {
            throw new InvalidDescriptionException();
        }
    }

    /**
     * Ensures that a named command argument appears at most once.
     *
     * @param input command argument text.
     * @param argument argument marker to count.
     * @throws DuplicateArgumentException if the argument appears multiple times.
     */
    private static void ensureArgumentAppearsOnce(String input, String argument)
            throws DuplicateArgumentException {
        int firstIndex = input.indexOf(argument);
        if (firstIndex >= 0 && input.indexOf(argument, firstIndex + argument.length()) >= 0) {
            throw new DuplicateArgumentException(
                    argument.substring(ARGUMENT_MARKER_PREFIX_LENGTH));
        }
    }

    /**
     * Rejects extra text for commands that do not accept arguments.
     *
     * @param input complete command input.
     * @param commandName command that should not receive an argument.
     * @throws UnexpectedArgumentException if extra text is present.
     */
    private static void requireNoArguments(String input, String commandName)
            throws UnexpectedArgumentException {
        String[] parts = input.trim().split(REGEX_WHITESPACE, ARGUMENT_SPLIT_LIMIT);
        if (parts.length > COMMAND_ONLY_PART_COUNT) {
            throw new UnexpectedArgumentException(commandName);
        }
    }
}
