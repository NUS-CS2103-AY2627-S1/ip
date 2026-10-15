package bos;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Interprets user input and converts it into values that Bos can execute.
 */
public final class Parser {
    private static final String DEADLINE_COMMAND_FORMAT = "deadline DESCRIPTION /by DATE";
    private static final String EVENT_COMMAND_FORMAT = "event DESCRIPTION /from START /to END";
    private static final String DATE_TIME_PATTERN = "uuuu-MM-dd HHmm";
    private static final String DISPLAY_DATE_TIME_PATTERN = "MMM d uuuu, h:mm a";
    private static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)
                    .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter DISPLAY_DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern(DISPLAY_DATE_TIME_PATTERN, Locale.ENGLISH);
    private static final Pattern MARK_PATTERN = Pattern.compile("^mark\\s+(\\d+)$");
    private static final Pattern UNMARK_PATTERN = Pattern.compile("^unmark\\s+(\\d+)$");
    private static final Pattern DELETE_PATTERN = Pattern.compile("^delete\\s+(\\d+)$");
    private static final Pattern FIND_PATTERN = Pattern.compile("^find\\s+(.*)$");
    private static final Pattern TODO_PATTERN = Pattern.compile("^todo\\s+(.*)$");
    private static final Pattern DEADLINE_PARAMETER_PATTERN = Pattern.compile(
            "(?i)(?<!\\S)/by(?=\\s|$)");
    private static final Pattern EVENT_FROM_PARAMETER_PATTERN = Pattern.compile(
            "(?i)(?<!\\S)/from(?=\\s|$)");
    private static final Pattern EVENT_TO_PARAMETER_PATTERN = Pattern.compile(
            "(?i)(?<!\\S)/to(?=\\s|$)");
    private static final Pattern DEADLINE_PATTERN = Pattern.compile(
            "^deadline\\s*(?<description>.*?)\\s+/by\\s*(?<date>.*)$",
            Pattern.CASE_INSENSITIVE);
    private static final Pattern EVENT_PATTERN = Pattern.compile(
            "^event\\s*(?<description>.*?)\\s+/from\\s*(?<from>.*?)\\s+/to\\s*(?<to>.*)$",
            Pattern.CASE_INSENSITIVE);

    private Parser() {
    }

    /**
     * Identifies the command keyword at the beginning of an input line.
     *
     * @param input complete line entered by the user.
     * @return matching command type, or {@link CommandType#UNKNOWN} when there is no match.
     */
    public static CommandType parseCommandType(String input) {
        if (input == null || input.isBlank()) {
            return CommandType.UNKNOWN;
        }

        String normalizedInput = input.strip();
        String commandWord = normalizedInput.split("\\s+", 2)[0];
        for (CommandType commandType : CommandType.values()) {
            if (commandType != CommandType.UNKNOWN
                    && commandWord.equals(commandType.getKeyword())) {
                return commandType;
            }
        }
        return CommandType.UNKNOWN;
    }

    /**
     * Checks whether the input contains exactly the specified no-argument command.
     * Leading and trailing whitespace is ignored.
     *
     * @param input complete line entered by the user.
     * @param commandType no-argument command to match.
     * @return true when the command matches and has no arguments.
     */
    public static boolean isExactCommand(String input, CommandType commandType) {
        return input != null && input.strip().equals(commandType.getKeyword());
    }

    /**
     * Ensures that a command which accepts no arguments has no extra text.
     *
     * @param input complete line entered by the user.
     * @param commandType no-argument command being validated.
     * @throws BosException if the command contains extra text.
     */
    public static void validateNoArguments(String input, CommandType commandType)
            throws BosException {
        if (!isExactCommand(input, commandType)) {
            throw BosException.createInvalidFormatException(commandType.getKeyword());
        }
    }

    /**
     * Reads a one-based task number and converts it to a valid list index.
     *
     * @param input complete mark, unmark, or delete command.
     * @param commandType type of task-number command being parsed.
     * @param taskCount number of tasks currently available.
     * @return zero-based index of the selected task.
     * @throws BosException if the number is invalid or does not identify a task.
     */
    public static int parseTaskIndex(String input, CommandType commandType, int taskCount)
            throws BosException {
        Pattern pattern = getTaskIndexPattern(commandType);
        Matcher matcher = pattern.matcher(input.strip());
        if (!matcher.matches()) {
            throw BosException.createInvalidTaskNumberException();
        }

        int taskIndex;
        try {
            taskIndex = Integer.parseInt(matcher.group(1)) - 1;
        } catch (NumberFormatException exception) {
            throw BosException.createInvalidTaskNumberException();
        }

        if (taskIndex < 0 || taskIndex >= taskCount) {
            throw BosException.createTaskNotFoundException();
        }

        assert taskIndex >= 0 && taskIndex < taskCount
                : "Parsed task index must identify an existing task";
        return taskIndex;
    }

    /**
     * Extracts the keyword from a find command.
     *
     * @param input complete find command.
     * @return trimmed keyword to search for.
     * @throws BosException if no keyword was provided.
     */
    public static String parseFindKeyword(String input) throws BosException {
        Matcher matcher = FIND_PATTERN.matcher(input.strip());
        if (!matcher.matches() || matcher.group(1).isBlank()) {
            throw BosException.createInvalidFormatException("find KEYWORD");
        }

        return normalizeTaskField(matcher.group(1));
    }

    /**
     * Converts a task-creation command into the corresponding task.
     *
     * @param input complete todo, deadline, or event command.
     * @param commandType type of task to create.
     * @return task described by the command.
     * @throws BosException if the command is incomplete or malformed.
     */
    public static Task parseTask(String input, CommandType commandType) throws BosException {
        return switch (commandType) {
            case TODO -> parseTodo(input);
            case DEADLINE -> parseDeadline(input);
            case EVENT -> parseEvent(input);
            default -> throw new IllegalArgumentException(commandType + " is not a task-creation command");
        };
    }

    /**
     * Formats a date-time written as {@code yyyy-MM-dd HHmm} for display.
     * Text in any other format is retained unchanged.
     *
     * @param text user-entered or stored date-time text.
     * @return readable date-time text.
     */
    public static String formatDateTimeForDisplay(String text) {
        try {
            LocalDateTime dateTime = LocalDateTime.parse(text, DATE_TIME_FORMAT);
            return dateTime.format(DISPLAY_DATE_TIME_FORMAT);
        } catch (DateTimeParseException exception) {
            return text;
        }
    }

    /**
     * Selects the format used by a command that refers to a task number.
     */
    private static Pattern getTaskIndexPattern(CommandType commandType) {
        return switch (commandType) {
            case MARK -> MARK_PATTERN;
            case UNMARK -> UNMARK_PATTERN;
            case DELETE -> DELETE_PATTERN;
            default -> throw new IllegalArgumentException(commandType + " does not use a task number");
        };
    }

    /**
     * Parses a todo command after its command type has been identified.
     */
    private static Task parseTodo(String input) throws BosException {
        Matcher matcher = TODO_PATTERN.matcher(input.strip());
        if (!matcher.matches() || matcher.group(1).isBlank()) {
            throw BosException.createEmptyDescriptionException(CommandType.TODO);
        }

        String description = normalizeTaskField(matcher.group(1));
        return new TodoTask(description);
    }

    /**
     * Parses a deadline command after its command type has been identified.
     */
    private static Task parseDeadline(String input) throws BosException {
        String normalizedInput = input.strip();
        if (normalizedInput.equals(CommandType.DEADLINE.getKeyword())) {
            throw BosException.createEmptyDescriptionException(CommandType.DEADLINE);
        }
        validateParameterCount(normalizedInput, DEADLINE_PARAMETER_PATTERN, "/by");

        Matcher matcher = DEADLINE_PATTERN.matcher(normalizedInput);
        if (!matcher.matches()) {
            throw BosException.createInvalidFormatException(DEADLINE_COMMAND_FORMAT);
        }
        if (matcher.group("description").isBlank()) {
            throw BosException.createEmptyDescriptionException(CommandType.DEADLINE);
        }
        if (matcher.group("date").isBlank()) {
            throw BosException.createInvalidFormatException(DEADLINE_COMMAND_FORMAT);
        }

        String description = normalizeTaskField(matcher.group("description"));
        String deadline = normalizeTaskField(matcher.group("date"));
        validateDateTime(deadline, "deadline");
        return new Deadline(description, deadline);
    }

    /**
     * Parses an event command after its command type has been identified.
     */
    private static Task parseEvent(String input) throws BosException {
        String normalizedInput = input.strip();
        if (normalizedInput.equals(CommandType.EVENT.getKeyword())) {
            throw BosException.createEmptyDescriptionException(CommandType.EVENT);
        }
        validateParameterCount(normalizedInput, EVENT_FROM_PARAMETER_PATTERN, "/from");
        validateParameterCount(normalizedInput, EVENT_TO_PARAMETER_PATTERN, "/to");

        Matcher matcher = EVENT_PATTERN.matcher(normalizedInput);
        if (!matcher.matches()) {
            throw BosException.createInvalidFormatException(EVENT_COMMAND_FORMAT);
        }
        if (matcher.group("description").isBlank()) {
            throw BosException.createEmptyDescriptionException(CommandType.EVENT);
        }
        if (matcher.group("from").isBlank() || matcher.group("to").isBlank()) {
            throw BosException.createInvalidFormatException(EVENT_COMMAND_FORMAT);
        }

        String description = normalizeTaskField(matcher.group("description"));
        String startTime = normalizeTaskField(matcher.group("from"));
        String endTime = normalizeTaskField(matcher.group("to"));
        validateEventTimes(startTime, endTime);
        return new Event(description, startTime, endTime);
    }

    /**
     * Validates one deadline value read from storage.
     *
     * @param deadline stored deadline value.
     * @param fieldName user-facing name of the date-time field.
     * @throws BosException if a date-like value is invalid.
     */
    static void validateDateTime(String deadline, String fieldName) throws BosException {
        parseDateTime(deadline, fieldName);
    }

    /**
     * Validates an event range read from a command or storage.
     *
     * @param startTime event start value.
     * @param endTime event end value.
     * @throws BosException if either value is invalid or the range is not increasing.
     */
    static void validateEventTimes(String startTime, String endTime) throws BosException {
        LocalDateTime parsedStart = parseDateTime(startTime, "event start");
        LocalDateTime parsedEnd = parseDateTime(endTime, "event end");

        if (!parsedStart.isBefore(parsedEnd)) {
            throw BosException.createInvalidEventRangeException();
        }
    }

    /**
     * Trims a task field, collapses repeated spaces, and rejects characters unsafe for storage.
     *
     * @param field task field to normalize.
     * @return normalized field.
     * @throws BosException if the field contains unsupported characters.
     */
    static String normalizeTaskField(String field) throws BosException {
        boolean hasControlCharacter = field.chars().anyMatch(Character::isISOControl);
        if (field.contains("|") || hasControlCharacter) {
            throw BosException.createInvalidTaskCharacterException();
        }
        return field.strip().replaceAll(" +", " ");
    }

    /**
     * Rejects a repeated command parameter while leaving missing-parameter reporting to the full format check.
     */
    private static void validateParameterCount(String input, Pattern parameterPattern, String parameter)
            throws BosException {
        long parameterCount = parameterPattern.matcher(input).results().count();
        if (parameterCount > 1) {
            throw BosException.createRepeatedParameterException(parameter);
        }
    }

    /**
     * Parses standardized date-time text using strict calendar validation.
     */
    private static LocalDateTime parseDateTime(String text, String fieldName)
            throws BosException {
        try {
            return LocalDateTime.parse(text, DATE_TIME_FORMAT);
        } catch (DateTimeParseException exception) {
            throw BosException.createInvalidDateTimeException(fieldName);
        }
    }
}
