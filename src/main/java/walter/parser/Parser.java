package walter.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.Locale;

import walter.DukeException;
import walter.command.AddCommand;
import walter.command.AddPlaceCommand;
import walter.command.Command;
import walter.command.DeleteCommand;
import walter.command.DeletePlaceCommand;
import walter.command.ExitCommand;
import walter.command.FindCommand;
import walter.command.ListCommand;
import walter.command.ListPlacesCommand;
import walter.command.MarkCommand;
import walter.command.OnCommand;
import walter.command.UnmarkCommand;
import walter.place.Place;
import walter.task.Deadline;
import walter.task.Event;
import walter.task.Todo;

/**
 * Translates raw user input into validated, executable {@link Command} objects.
 * Parsing constructs command arguments such as tasks, indexes, and dates without performing
 * input/output, mutating the task list, or saving data.
 */
public class Parser {
    private static final String UNKNOWN_COMMAND_HELP = "Unknown command. Try: todo DESCRIPTION; "
            + "deadline DESCRIPTION /by yyyy-MM-dd; event DESCRIPTION /at TIME; list; "
            + "find KEYWORD; mark INDEX; unmark INDEX; delete INDEX; on yyyy-MM-dd; or bye.";
    private static final List<DateTimeFormatter> EVENT_DATE_TIME_FORMATTERS = List.of(
            eventDateTimeFormatter("uuuu-MM-dd HH:mm"),
            eventDateTimeFormatter("uuuu-MM-dd h:mma"),
            eventDateTimeFormatter("uuuu-MM-dd ha"),
            eventDateTimeFormatter("uuuu-MM-dd h:mm a"),
            eventDateTimeFormatter("uuuu-MM-dd h a"));
    private static final String DEADLINE_DELIMITER = "/by";
    private static final String EVENT_AT_DELIMITER = "/at";
    private static final String EVENT_FROM_DELIMITER = "/from";
    private static final String EVENT_TO_DELIMITER = "/to";
    private static final String PLACE_ADDRESS_DELIMITER = "/at";

    /**
     * Parses one input line into the corresponding executable command.
     *
     * @param input Raw command line entered by the user.
     * @return Command representing the validated input.
     * @throws DukeException If the input is blank, unknown, or has invalid command syntax.
     */
    public static Command parse(String input) throws DukeException {
        if (input == null) {
            throw new DukeException("Command cannot be null.");
        }
        String command = normalize(input);
        String commandWord = getCommandWord(command);
        return switch (commandWord) {
            case "list" -> parseExactCommand(command, "list", new ListCommand());
            case "places" -> parseExactCommand(command, "places", new ListPlacesCommand());
            case "bye" -> parseExactCommand(command, "bye", new ExitCommand());
            case "on" -> new OnCommand(parseOnDate(command));
            case "find" -> new FindCommand(parseFindKeyword(command));
            case "done", "mark" -> new MarkCommand(parseTaskIndex(command));
            case "unmark" -> new UnmarkCommand(parseTaskIndex(command));
            case "delete" -> new DeleteCommand(parseTaskIndex(command));
            case "deleteplace" -> new DeletePlaceCommand(parsePlaceIndex(command));
            case "todo" -> new AddCommand(parseTodo(command));
            case "deadline" -> new AddCommand(parseDeadline(command));
            case "event" -> new AddCommand(parseEvent(command));
            case "place" -> new AddPlaceCommand(parsePlace(command));
            default -> throw new DukeException(UNKNOWN_COMMAND_HELP);
        };
    }

    /**
     * Removes leading and trailing whitespace from one input line.
     *
     * @param input Input text to normalize.
     * @return Input with leading and trailing whitespace removed.
     */
    private static String normalize(String input) {
        return input.strip();
    }

    /**
     * Returns a no-argument command after confirming that no trailing argument was supplied.
     *
     * @param input Input text to validate.
     * @param commandWord Expected command word.
     * @param command Command to return after successful validation.
     * @return Validated command.
     * @throws DukeException If input contains anything after the command word.
     */
    private static Command parseExactCommand(String input, String commandWord, Command command)
            throws DukeException {
        if (!normalize(input).equals(commandWord)) {
            throw new DukeException(commandWord + " does not take arguments.");
        }
        return command;
    }

    /**
     * Returns the first command word after rejecting blank input.
     *
     * @param input Input from which to extract the command word.
     * @return First whitespace-delimited word in the normalized input.
     * @throws DukeException If the input is blank.
     */
    private static String getCommandWord(String input) throws DukeException {
        String command = normalize(input);
        if (command.isBlank()) {
            throw new DukeException("Command cannot be blank.");
        }

        int commandWordEnd = 0;
        while (commandWordEnd < command.length()
                && !Character.isWhitespace(command.charAt(commandWordEnd))) {
            commandWordEnd++;
        }
        return command.substring(0, commandWordEnd);
    }

    /**
     * Parses a Todo command.
     *
     * @param input Complete Todo command.
     * @return Todo containing the parsed description.
     * @throws DukeException If the Todo description is empty.
     */
    private static Todo parseTodo(String input) throws DukeException {
        String description = argumentAfter(input, "todo");
        if (description.isEmpty()) {
            throw new DukeException("Todo description cannot be empty.");
        }
        return new Todo(description);
    }

    /**
     * Parses a Deadline command with an ISO date.
     *
     * @param input Complete Deadline command.
     * @return Deadline containing the parsed description and date.
     * @throws DukeException If the description or date is missing, the {@code /by} delimiter is
     *         absent, or the date is not a valid ISO date.
     */
    private static Deadline parseDeadline(String input) throws DukeException {
        String taskDetails = argumentAfter(input, "deadline");
        if (taskDetails.isEmpty()) {
            throw new DukeException("Deadline description cannot be empty.");
        }

        int delimiterIndex = findDelimiter(taskDetails, DEADLINE_DELIMITER, 0);
        if (delimiterIndex < 0) {
            throw new DukeException("Deadline requires /by.");
        }
        if (findDelimiter(taskDetails, DEADLINE_DELIMITER,
                delimiterIndex + DEADLINE_DELIMITER.length()) >= 0) {
            throw new DukeException("Deadline requires exactly one /by.");
        }

        String description = taskDetails.substring(0, delimiterIndex).strip();
        String byText = taskDetails.substring(
                delimiterIndex + DEADLINE_DELIMITER.length()).strip();
        if (description.isEmpty()) {
            throw new DukeException("Deadline description cannot be empty.");
        }
        if (byText.isEmpty()) {
            throw new DukeException("Deadline date/time cannot be empty.");
        }
        return new Deadline(description, parseDeadlineDate(byText));
    }

    /**
     * Parses either supported Event command representation.
     *
     * @param input Complete Event command using either {@code /at} or {@code /from} and
     *         {@code /to}.
     * @return Event containing the parsed description and time details.
     * @throws DukeException If required delimiters or event details are missing.
     */
    private static Event parseEvent(String input) throws DukeException {
        String taskDetails = argumentAfter(input, "event");
        if (taskDetails.isEmpty()) {
            throw new DukeException("Event description cannot be empty.");
        }

        int atDelimiterIndex = findDelimiter(taskDetails, EVENT_AT_DELIMITER, 0);
        if (atDelimiterIndex >= 0) {
            if (findDelimiter(taskDetails, EVENT_AT_DELIMITER,
                    atDelimiterIndex + EVENT_AT_DELIMITER.length()) >= 0
                    || findDelimiter(taskDetails, EVENT_FROM_DELIMITER, 0) >= 0
                    || findDelimiter(taskDetails, EVENT_TO_DELIMITER, 0) >= 0) {
                throw new DukeException(
                        "Event must use either one /at or one /from and one /to.");
            }
            return parseAtEvent(taskDetails, atDelimiterIndex);
        }
        return parseFromToEvent(taskDetails);
    }

    /**
     * Parses event details that use the {@code /at} format.
     *
     * @param taskDetails Event details following the command word.
     * @param atDelimiterIndex Index of the validated {@code /at} delimiter.
     * @return Event containing one time description.
     * @throws DukeException If the event description or time is empty.
     */
    private static Event parseAtEvent(String taskDetails, int atDelimiterIndex)
            throws DukeException {
        String description = taskDetails.substring(0, atDelimiterIndex).strip();
        String atTime = taskDetails.substring(
                atDelimiterIndex + EVENT_AT_DELIMITER.length()).strip();
        if (description.isEmpty()) {
            throw new DukeException("Event description cannot be empty.");
        }
        if (atTime.isEmpty()) {
            throw new DukeException("Event date/time cannot be empty.");
        }
        return new Event(description, atTime);
    }

    /**
     * Parses event details that use the {@code /from} and {@code /to} format.
     *
     * @param taskDetails Event details following the command word.
     * @return Event containing start and end time descriptions.
     * @throws DukeException If either delimiter or any event detail is missing.
     */
    private static Event parseFromToEvent(String taskDetails) throws DukeException {
        int fromDelimiterIndex = findDelimiter(taskDetails, EVENT_FROM_DELIMITER, 0);
        if (fromDelimiterIndex < 0) {
            if (findDelimiter(taskDetails, EVENT_TO_DELIMITER, 0) >= 0) {
                throw new DukeException("Event requires /from command when given /to command.");
            }
            throw new DukeException("Event requires /at or /from and /to.");
        }
        if (findDelimiter(taskDetails, EVENT_FROM_DELIMITER,
                fromDelimiterIndex + EVENT_FROM_DELIMITER.length()) >= 0) {
            throw new DukeException("Event requires exactly one /from and one /to.");
        }
        int firstToDelimiterIndex = findDelimiter(taskDetails, EVENT_TO_DELIMITER, 0);
        if (firstToDelimiterIndex >= 0 && firstToDelimiterIndex < fromDelimiterIndex) {
            throw new DukeException("Event /from must appear before /to.");
        }
        int toDelimiterIndex = findDelimiter(
                taskDetails,
                EVENT_TO_DELIMITER,
                fromDelimiterIndex + EVENT_FROM_DELIMITER.length());
        if (toDelimiterIndex < 0) {
            throw new DukeException("Event requires /to command when given /from command.");
        }
        if (findDelimiter(taskDetails, EVENT_TO_DELIMITER,
                toDelimiterIndex + EVENT_TO_DELIMITER.length()) >= 0) {
            throw new DukeException("Event requires exactly one /from and one /to.");
        }

        String description = taskDetails.substring(0, fromDelimiterIndex).strip();
        String startTime = taskDetails.substring(
                fromDelimiterIndex + EVENT_FROM_DELIMITER.length(), toDelimiterIndex).strip();
        String endTime = taskDetails.substring(
                toDelimiterIndex + EVENT_TO_DELIMITER.length()).strip();
        if (description.isEmpty()) {
            throw new DukeException("Event description cannot be empty.");
        }
        if (startTime.isEmpty()) {
            throw new DukeException("Event start cannot be empty.");
        }
        if (endTime.isEmpty()) {
            throw new DukeException("Event end cannot be empty.");
        }
        validateComparableEventRange(startTime, endTime);
        return new Event(description, startTime, endTime);
    }

    /**
     * Rejects a reversed event range when both endpoints are comparable ISO dates or dated
     * times. Time-only and other free-text descriptions remain accepted because an earlier clock
     * time can legitimately mean an overnight event.
     *
     * @param startTime Event start text.
     * @param endTime Event end text.
     * @throws DukeException If both values are comparable dates and the start is after the end.
     */
    private static void validateComparableEventRange(String startTime, String endTime)
            throws DukeException {
        LocalDateTime startDateTime = parseComparableEventDateTime(startTime);
        LocalDateTime endDateTime = parseComparableEventDateTime(endTime);
        if (startDateTime != null && endDateTime != null && startDateTime.isAfter(endDateTime)) {
            throw new DukeException("Event start must not be after its end.");
        }
    }

    /**
     * Parses one supported event endpoint, returning {@code null} for intentionally free text.
     */
    private static LocalDateTime parseComparableEventDateTime(String text) {
        if (hasSupportedDateFormat(text)) {
            try {
                return LocalDate.parse(text).atStartOfDay();
            } catch (DateTimeParseException exception) {
                return null;
            }
        }
        for (DateTimeFormatter formatter : EVENT_DATE_TIME_FORMATTERS) {
            try {
                return LocalDateTime.parse(text.strip().replaceAll("\\s+", " "), formatter);
            } catch (DateTimeParseException exception) {
                // Try the next explicitly supported dated-time representation.
            }
        }
        return null;
    }

    /**
     * Creates a strict, case-insensitive formatter for a documented event dated-time pattern.
     */
    private static DateTimeFormatter eventDateTimeFormatter(String pattern) {
        return new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern(pattern)
                .toFormatter(Locale.ENGLISH)
                .withResolverStyle(ResolverStyle.STRICT);
    }

    /**
     * Parses a place name and address separated by exactly one {@code /at} token.
     *
     * @param input Complete place command.
     * @return Place containing the parsed name and address.
     * @throws DukeException If the name, delimiter, or address is invalid.
     */
    private static Place parsePlace(String input) throws DukeException {
        String placeDetails = argumentAfter(input, "place");
        if (placeDetails.isEmpty()) {
            throw new DukeException("Place name cannot be empty.");
        }

        int delimiterIndex = findDelimiter(placeDetails, PLACE_ADDRESS_DELIMITER, 0);
        int secondDelimiterIndex = delimiterIndex < 0
                ? -1
                : findDelimiter(placeDetails, PLACE_ADDRESS_DELIMITER,
                        delimiterIndex + PLACE_ADDRESS_DELIMITER.length());
        if (delimiterIndex < 0 || secondDelimiterIndex >= 0) {
            throw new DukeException("Place requires exactly one /at.");
        }

        String name = placeDetails.substring(0, delimiterIndex).strip();
        String address = placeDetails.substring(
                delimiterIndex + PLACE_ADDRESS_DELIMITER.length()).strip();
        if (name.isEmpty()) {
            throw new DukeException("Place name cannot be empty.");
        }
        if (address.isEmpty()) {
            throw new DukeException("Place address cannot be empty.");
        }
        return new Place(name, address);
    }

    /**
     * Parses a one-based task number and returns its zero-based index.
     *
     * @param input Complete command containing a task number.
     * @return Zero-based task index corresponding to the entered number.
     * @throws DukeException If the task number is missing or is not an integer.
     */
    private static int parseTaskIndex(String input) throws DukeException {
        String command = normalize(input);
        int commandWordEnd = getCommandWord(command).length();
        if (commandWordEnd == command.length()
                || command.substring(commandWordEnd).isBlank()) {
            throw new DukeException("Task number is required.");
        }

        String taskNumberText = command.substring(commandWordEnd).strip();
        int taskNumber;
        try {
            taskNumber = Integer.parseInt(taskNumberText);
        } catch (NumberFormatException exception) {
            throw new DukeException("Task number must be an integer.", exception);
        }
        return taskNumber - 1;
    }

    /**
     * Parses a one-based place number and returns its zero-based index.
     *
     * @param input Complete deleteplace command.
     * @return Zero-based place index.
     * @throws DukeException If the place number is missing or is not an integer.
     */
    private static int parsePlaceIndex(String input) throws DukeException {
        String placeNumberText = argumentAfter(input, "deleteplace");
        if (placeNumberText.isEmpty()) {
            throw new DukeException("Place number is required.");
        }
        try {
            return Integer.parseInt(placeNumberText) - 1;
        } catch (NumberFormatException exception) {
            throw new DukeException("Place number must be an integer.", exception);
        }
    }

    /**
     * Parses the ISO date supplied to an {@code on} command.
     *
     * @param input Complete {@code on} command.
     * @return Date requested by the command.
     * @throws DukeException If the date is missing or is not a valid ISO date.
     */
    private static LocalDate parseOnDate(String input) throws DukeException {
        String dateText = argumentAfter(input, "on");
        if (dateText.isEmpty()) {
            throw new DukeException("Date is required for the on command.");
        }
        if (!hasSupportedDateFormat(dateText)) {
            throw new DukeException("Date must be in yyyy-MM-dd format.");
        }
        try {
            return LocalDate.parse(dateText);
        } catch (DateTimeParseException exception) {
            throw new DukeException("Date is not a valid calendar date.", exception);
        }
    }

    /**
     * Parses the keyword supplied to a {@code find} command.
     */
    private static String parseFindKeyword(String input) throws DukeException {
        String keyword = argumentAfter(input, "find");
        if (keyword.isEmpty()) {
            throw new DukeException("Keyword is required for the find command.");
        }
        return keyword;
    }

    /**
     * Returns the stripped text following a known command word.
     *
     * @param input Complete command input.
     * @param commandWord Command word at the start of the input.
     * @return Remaining input after removing the command word and surrounding whitespace.
     */
    private static String argumentAfter(String input, String commandWord) {
        return normalize(input).substring(commandWord.length()).strip();
    }

    /**
     * Parses a Deadline date and converts failures into the existing user-facing error.
     *
     * @param dateText Text expected to contain an ISO date.
     * @return Parsed date.
     * @throws DukeException If the text is not a valid ISO date.
     */
    private static LocalDate parseDeadlineDate(String dateText) throws DukeException {
        if (!hasSupportedDateFormat(dateText)) {
            throw new DukeException("Deadline date must be in yyyy-MM-dd format.");
        }
        try {
            return LocalDate.parse(dateText);
        } catch (DateTimeParseException exception) {
            throw new DukeException("Deadline date is not a valid calendar date.", exception);
        }
    }

    /**
     * Checks that a date uses Walter's documented four-digit positive-year representation.
     *
     * @param dateText Date text to validate before calendar parsing.
     * @return {@code true} when the text has the supported {@code yyyy-MM-dd} shape and year.
     */
    private static boolean hasSupportedDateFormat(String dateText) {
        return dateText.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}")
                && !dateText.startsWith("0000");
    }

    /**
     * Finds a delimiter that appears as a complete whitespace-separated token.
     *
     * @param text Text to search.
     * @param delimiter Delimiter token to locate.
     * @param fromIndex Index at which to begin searching.
     * @return Index of the first complete delimiter token, or {@code -1} if none exists.
     */
    private static int findDelimiter(String text, String delimiter, int fromIndex) {
        int delimiterIndex = text.indexOf(delimiter, fromIndex);
        while (delimiterIndex >= 0) {
            int delimiterEnd = delimiterIndex + delimiter.length();
            boolean hasValidStart = delimiterIndex == 0
                    || Character.isWhitespace(text.charAt(delimiterIndex - 1));
            boolean hasValidEnd = delimiterEnd == text.length()
                    || Character.isWhitespace(text.charAt(delimiterEnd));
            if (hasValidStart && hasValidEnd) {
                return delimiterIndex;
            }
            delimiterIndex = text.indexOf(delimiter, delimiterEnd);
        }
        return -1;
    }
}
