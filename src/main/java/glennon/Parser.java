package glennon;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import glennon.command.AddCommand;
import glennon.command.Command;
import glennon.command.DeleteCommand;
import glennon.command.ExitCommand;
import glennon.command.FindCommand;
import glennon.command.ListCommand;
import glennon.command.MarkCommand;
import glennon.command.OnCommand;
import glennon.command.SortCommand;
import glennon.exception.GlennonException;
import glennon.task.Deadline;
import glennon.task.Event;
import glennon.task.Todo;
import glennon.util.Text;

/**
 * Recognizes Glennon commands and converts their arguments into values used by
 * the application.
 */
public final class Parser {
    /**
     * Identifies the command represented by a line of user input.
     */
    public enum CommandType {
        /** Ends the current application session. */
        BYE("bye", false),

        /** Displays every mission in the log. */
        LIST("list", false),

        /** Sorts scheduled missions chronologically. */
        SORT("sort", false),

        /** Finds missions whose descriptions contain a keyword. */
        FIND("find", true),

        /** Displays scheduled missions occurring on a date. */
        ON("on", true),

        /** Marks a mission as complete. */
        MARK("mark", true),

        /** Marks a mission as incomplete. */
        UNMARK("unmark", true),

        /** Removes a mission from the log. */
        DELETE("delete", true),

        /** Adds a mission without a date or time. */
        TODO("todo", true),

        /** Adds a mission with a completion deadline. */
        DEADLINE("deadline", true),

        /** Adds a mission with a start and end time. */
        EVENT("event", true),

        /** Represents input that does not match a supported command. */
        UNKNOWN("", false);

        /** Keyword entered by the user to invoke this command. */
        private final String keyword;

        /** Whether the keyword may be followed by command arguments. */
        private final boolean canAcceptArguments;

        /**
         * Creates a command type with its keyword and argument policy.
         *
         * @param keyword command keyword.
         * @param canAcceptArguments whether text may follow the keyword.
         */
        CommandType(String keyword, boolean canAcceptArguments) {
            this.keyword = keyword;
            this.canAcceptArguments = canAcceptArguments;
        }

        /**
         * Checks whether the input represents this command type.
         *
         * @param input complete user input.
         * @return true when the input starts with this command correctly.
         */
        private boolean matches(String input) {
            return input.equals(keyword)
                    || input.startsWith(keyword + " ")
                    || input.startsWith(keyword + "\t");
        }
    }

    /** Separates a deadline's description from its date text. */
    private static final String PARAMETER_BY = "/by";

    /** Separates an event's description from its start time. */
    private static final String PARAMETER_FROM = "/from";

    /** Separates an event's start time from its end time. */
    private static final String PARAMETER_TO = "/to";

    /** Matches complete schedule flags without treating ordinary slashes as flags. */
    private static final Pattern SCHEDULE_PARAMETER = Pattern.compile("(?<!\\S)/(?:by|from|to)(?!\\S)");

    /** Format accepted for deadline and event date-times. */
    private static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("d/M/uuuu HHmm")
                    .withResolverStyle(ResolverStyle.STRICT);

    /** Format accepted by the date-filter command. */
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("d/M/uuuu")
                    .withResolverStyle(ResolverStyle.STRICT);

    /** Guidance shown when a scheduled date or date-time value is invalid. */
    private static final String DATE_TIME_USAGE =
            "Please enter a valid calendar date as d/M/yyyy with an optional HHmm time, "
                    + "for example 2/12/2019 or 2/12/2019 1800.";

    /** Guidance shown when a date-filter value is invalid. */
    private static final String DATE_USAGE =
            "Please enter a valid calendar date as d/M/yyyy, for example 2/12/2019.";

    /** Error shown when an event does not end after it starts. */
    private static final String EVENT_ORDER_ERROR =
            "The event end must be after its start.";

    /** Guidance shown when a deadline command cannot be parsed. */
    private static final String DEADLINE_USAGE =
            "Use: deadline <mission> /by <d/M/yyyy [HHmm]>.";

    /** Guidance shown when an event command cannot be parsed. */
    private static final String EVENT_USAGE =
            "Use: event <mission> /from <d/M/yyyy [HHmm]> /to <d/M/yyyy [HHmm]>.";

    /** Prevents creation of this stateless utility class. */
    private Parser() {
    }

    /**
     * Converts one complete line of user input into an executable command.
     *
     * @param input complete user input.
     * @return command containing all parsed arguments.
     * @throws GlennonException if the command or any argument is invalid.
     */
    public static Command parse(String input) throws GlennonException {
        String normalizedInput = normalizeInput(input);
        CommandType commandType = parseCommandType(normalizedInput);
        if (commandType != CommandType.UNKNOWN && !commandType.canAcceptArguments
                && !normalizedInput.equals(commandType.keyword)) {
            throw new GlennonException("The " + commandType.keyword
                    + " command does not take arguments. Use: " + commandType.keyword + ".");
        }
        return switch (commandType) {
            case BYE -> new ExitCommand();
            case LIST -> new ListCommand();
            case SORT -> new SortCommand();
            case FIND -> new FindCommand(parseKeyword(normalizedInput));
            case ON -> new OnCommand(parseDate(normalizedInput));
            case MARK -> new MarkCommand(parseMissionIndex(normalizedInput, commandType), true);
            case UNMARK -> new MarkCommand(parseMissionIndex(normalizedInput, commandType), false);
            case DELETE -> new DeleteCommand(parseMissionIndex(normalizedInput, commandType));
            case TODO -> new AddCommand(parseTodo(normalizedInput));
            case DEADLINE -> new AddCommand(parseDeadline(normalizedInput));
            case EVENT -> new AddCommand(parseEvent(normalizedInput));
            case UNKNOWN -> throw new GlennonException(
                    "Glennon doesn't recognize that command.\n"
                            + "Try: todo, deadline, event, list, sort, find, on, mark, "
                            + "unmark, delete, or bye.");
        };
    }

    /**
     * Identifies the command type while preserving Glennon's case-sensitive
     * command syntax.
     *
     * @param input complete user input.
     * @return matching command type, or {@link CommandType#UNKNOWN}.
     */
    public static CommandType parseCommandType(String input) {
        if (input == null || hasInvalidControlCharacters(input)) {
            return CommandType.UNKNOWN;
        }
        String normalizedInput = input.strip();
        for (CommandType commandType : CommandType.values()) {
            if (commandType != CommandType.UNKNOWN && commandType.matches(normalizedInput)) {
                return commandType;
            }
        }
        return CommandType.UNKNOWN;
    }

    /**
     * Parses the keyword supplied to a {@code find} command.
     *
     * @param input complete find command.
     * @return trimmed search keyword.
     * @throws GlennonException if the keyword is missing.
     */
    public static String parseKeyword(String input) throws GlennonException {
        String keyword = parseArguments(input, CommandType.FIND);
        if (keyword.isEmpty()) {
            throw new GlennonException("Please enter a keyword after find.");
        }
        return keyword;
    }

    /**
     * Parses a to-do command into a pending to-do mission.
     *
     * @param input complete to-do command.
     * @return parsed to-do mission.
     * @throws GlennonException if the description is missing.
     */
    public static Todo parseTodo(String input) throws GlennonException {
        String description = Text.stripWhitespace(parseArguments(input, CommandType.TODO));
        if (description.isEmpty()) {
            throw new GlennonException("Please enter a mission after todo.");
        }
        return new Todo(description);
    }

    /**
     * Parses a deadline command into a pending deadline mission.
     *
     * @param input complete deadline command.
     * @return parsed deadline mission.
     * @throws GlennonException if the description or deadline is missing.
     */
    public static Deadline parseDeadline(String input) throws GlennonException {
        String details = parseArguments(input, CommandType.DEADLINE);
        Map<String, Integer> positions = parseParameterPositions(details, List.of(PARAMETER_BY), DEADLINE_USAGE);
        int bySeparatorIndex = positions.getOrDefault(PARAMETER_BY, -1);
        if (bySeparatorIndex <= 0) {
            throw new GlennonException(DEADLINE_USAGE);
        }
        String description = Text.stripWhitespace(details.substring(0, bySeparatorIndex));
        String by = details.substring(bySeparatorIndex + PARAMETER_BY.length()).strip();
        if (description.isEmpty() || by.isEmpty()) {
            throw new GlennonException(DEADLINE_USAGE);
        }
        return new Deadline(description, parseScheduledDateTime(by, LocalTime.of(23, 59)));
    }

    /**
     * Parses an event command into a pending event mission.
     *
     * @param input complete event command.
     * @return parsed event mission.
     * @throws GlennonException if its description, start, or end is missing.
     */
    public static Event parseEvent(String input) throws GlennonException {
        String details = parseArguments(input, CommandType.EVENT);
        Map<String, Integer> positions = parseParameterPositions(
                details, List.of(PARAMETER_FROM, PARAMETER_TO), EVENT_USAGE);
        int fromSeparatorIndex = positions.getOrDefault(PARAMETER_FROM, -1);
        int fromValueIndex = fromSeparatorIndex + PARAMETER_FROM.length();
        int toSeparatorIndex = positions.getOrDefault(PARAMETER_TO, -1);
        if (fromSeparatorIndex <= 0 || toSeparatorIndex <= fromValueIndex) {
            throw new GlennonException(EVENT_USAGE);
        }
        String description = Text.stripWhitespace(details.substring(0, fromSeparatorIndex));
        String from = details.substring(fromValueIndex, toSeparatorIndex).strip();
        String to = details.substring(toSeparatorIndex + PARAMETER_TO.length()).strip();
        if (description.isEmpty() || from.isEmpty() || to.isEmpty()) {
            throw new GlennonException(EVENT_USAGE);
        }
        LocalDateTime start = parseScheduledDateTime(from, LocalTime.MIN);
        LocalDateTime end = parseScheduledDateTime(to, LocalTime.MAX);
        if (!end.isAfter(start)) {
            throw new GlennonException(EVENT_ORDER_ERROR);
        }
        return new Event(description, start, end);
    }

    /**
     * Converts a command's one-based mission number into a zero-based index.
     * The caller checks the index against the stored missions.
     *
     * @param input complete command containing the mission number.
     * @param commandType command whose arguments contain the number.
     * @return zero-based mission index.
     * @throws GlennonException if the number is missing or not a positive ASCII integer.
     */
    public static int parseMissionIndex(
            String input, CommandType commandType) throws GlennonException {
        String missionNumber = parseArguments(input, commandType);
        if (!missionNumber.matches("[0-9]+")) {
            throw new GlennonException("Please enter a valid mission number.");
        }
        try {
            int number = Integer.parseInt(missionNumber);
            if (number <= 0) {
                throw new GlennonException("Please enter a valid mission number.");
            }
            return number - 1;
        } catch (NumberFormatException e) {
            throw new GlennonException("Please enter a valid mission number.", e);
        }
    }

    /**
     * Parses the date supplied to an {@code on} command.
     *
     * @param input complete date-filter command.
     * @return parsed calendar date.
     * @throws GlennonException if the date is missing, malformed, or impossible.
     */
    public static LocalDate parseDate(String input) throws GlennonException {
        String value = parseArguments(input, CommandType.ON);
        try {
            return LocalDate.parse(value, DATE_FORMAT);
        } catch (DateTimeParseException e) {
            throw new GlennonException(DATE_USAGE, e);
        }
    }

    /**
     * Removes a recognized command's keyword and surrounding argument whitespace.
     *
     * @param input complete user input.
     * @param commandType recognized command type.
     * @return trimmed command arguments.
     * @throws GlennonException if the input is blank or contains invalid control characters.
     */
    private static String parseArguments(String input, CommandType commandType) throws GlennonException {
        String normalizedInput = normalizeInput(input);
        assert commandType.canAcceptArguments
                : "Argument parsing requires a command type that accepts arguments";
        assert commandType.matches(normalizedInput)
                : "Input must match the command type used to parse its arguments";
        return normalizedInput.substring(commandType.keyword.length()).strip();
    }

    /**
     * Validates a command before removing surrounding spaces and tabs.
     *
     * @param input complete user input.
     * @return input with surrounding whitespace removed.
     * @throws GlennonException if the input is empty or contains control characters other than tabs.
     */
    private static String normalizeInput(String input) throws GlennonException {
        if (input == null) {
            throw new GlennonException("Please enter a command.");
        }
        if (hasInvalidControlCharacters(input)) {
            throw new GlennonException("Please enter one command without control characters.");
        }
        String normalizedInput = input.strip();
        if (normalizedInput.isEmpty()) {
            throw new GlennonException("Please enter a command.");
        }
        return normalizedInput;
    }

    /**
     * Checks for characters that could split a command or interfere with its display.
     *
     * @param input complete user input.
     * @return true when the input contains a forbidden control character or Unicode line separator.
     */
    private static boolean hasInvalidControlCharacters(String input) {
        for (int i = 0; i < input.length(); i++) {
            char character = input.charAt(i);
            if (Character.isISOControl(character) && character != '\t'
                    || character == '\u2028' || character == '\u2029') {
                return true;
            }
        }
        return false;
    }

    /**
     * Locates complete schedule flags and rejects unexpected or repeated parameters.
     *
     * @param details scheduled command arguments.
     * @param expectedParameters parameters supported by the command.
     * @param usage guidance for the scheduled command.
     * @return each supplied parameter's starting position.
     * @throws GlennonException if a known parameter is repeated or unsupported by the command.
     */
    private static Map<String, Integer> parseParameterPositions(
            String details, List<String> expectedParameters, String usage) throws GlennonException {
        Map<String, Integer> positions = new HashMap<>();
        Matcher matcher = SCHEDULE_PARAMETER.matcher(details);
        while (matcher.find()) {
            String parameter = matcher.group();
            if (!expectedParameters.contains(parameter)) {
                throw new GlennonException("Unexpected parameter " + parameter + ". " + usage);
            }
            if (positions.putIfAbsent(parameter, matcher.start()) != null) {
                throw new GlennonException("Please specify " + parameter + " only once.");
            }
        }
        return positions;
    }

    /**
     * Converts a user-entered date or date-time into a strongly typed value.
     *
     * @param value date text with an optional time in {@code d/M/yyyy [HHmm]} format.
     * @param defaultTime time used when the input contains only a date.
     * @return parsed date and time.
     * @throws GlennonException if the value is malformed or is not a real date.
     */
    private static LocalDateTime parseScheduledDateTime(
            String value, LocalTime defaultTime) throws GlennonException {
        String normalizedValue = value.replaceAll("[ \\t]+", " ");
        try {
            return LocalDateTime.parse(normalizedValue, DATE_TIME_FORMAT);
        } catch (DateTimeParseException dateTimeException) {
            try {
                return LocalDate.parse(normalizedValue, DATE_FORMAT).atTime(defaultTime);
            } catch (DateTimeParseException dateException) {
                throw new GlennonException(DATE_TIME_USAGE, dateException);
            }
        }
    }
}
