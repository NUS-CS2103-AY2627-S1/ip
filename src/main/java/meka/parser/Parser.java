package meka.parser;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

import meka.command.AddCommand;
import meka.command.ArchiveCommand;
import meka.command.Command;
import meka.command.DeleteCommand;
import meka.command.ExitCommand;
import meka.command.FindCommand;
import meka.command.ListCommand;
import meka.command.MarkCommand;
import meka.command.UnmarkCommand;
import meka.exception.MekaException;
import meka.task.Deadline;
import meka.task.Event;
import meka.task.Task;
import meka.task.Todo;

/**
 * Interprets user commands and converts their arguments into task data.
 */
public class Parser {
    /** Strict parser for user-entered date-times such as {@code 2/12/2019 1800}. */
    private static final DateTimeFormatter INPUT_DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("d/M/uuuu HHmm", Locale.ENGLISH)
                    .withResolverStyle(ResolverStyle.STRICT);
    /**
     * Message shown when a task-number command has no usable number.
     */
    private static final String NUMBER_REQUIRED_MESSAGE =
            "The following command requires a number to proceed.";
    /**
     * Message shown when a task creation command has no description.
     */
    private static final String DESCRIPTION_REQUIRED_MESSAGE =
            "The following command requires a task description to proceed.";
    /**
     * Message shown when a deadline or event has a missing date-time.
     */
    private static final String DATE_TIME_REQUIRED_MESSAGE =
            "The following command requires date/time details to proceed.";
    /**
     * Message shown when a date-time does not match the required format.
     */
    private static final String INVALID_DATE_TIME_MESSAGE =
            "Please enter date and time as d/M/yyyy HHmm "
                    + "(for example, 2/12/2019 1800).";
    /**
     * Message shown when task details contain the saved-data delimiter.
     */
    private static final String RESERVED_DELIMITER_MESSAGE =
            "Task details cannot contain \" | \" because it is reserved for saved data.";
    /**
     * Message shown when the input does not match a supported command.
     */
    private static final String UNKNOWN_COMMAND_MESSAGE =
            "I do not understand this command. Please input a valid command.";

    /**
     * Prevents construction because command parsing uses stateless methods.
     */
    private Parser() {
    }

    /**
     * Converts user input into the corresponding executable command.
     *
     * @param input complete user input.
     * @return parsed command object.
     * @throws MekaException if the command or its arguments are invalid.
     */
    public static Command parse(String input) throws MekaException {
        assert input != null : "Command input should come from a user interface";

        input = input.trim().replaceAll("[ \\t]+", " ");

        if (input.equals("bye")) {
            return new ExitCommand();
        }
        if (input.equals("list")) {
            return new ListCommand();
        }
        if (input.equals("archive all")) {
            return new ArchiveCommand();
        }
        if (isCommand(input, "find")) {
            return new FindCommand(parseDescription(input, "find"));
        }
        if (isCommand(input, "mark")) {
            return new MarkCommand(parseTaskNumber(input, "mark"));
        }
        if (isCommand(input, "unmark")) {
            return new UnmarkCommand(parseTaskNumber(input, "unmark"));
        }
        if (isCommand(input, "delete")) {
            return new DeleteCommand(parseTaskNumber(input, "delete"));
        }
        if (isCommand(input, "todo")) {
            return new AddCommand(parseTodo(input));
        }
        if (isCommand(input, "deadline")) {
            return new AddCommand(parseDeadline(input));
        }
        if (isCommand(input, "event")) {
            return new AddCommand(parseEvent(input));
        }
        throw new MekaException(UNKNOWN_COMMAND_MESSAGE);
    }

    /**
     * Extracts a task number from a mark, unmark, or delete command.
     *
     * @param input complete user input.
     * @param command command word at the start of the input.
     * @return the supplied task number.
     * @throws MekaException if the number is missing or is not numeric.
     */
    private static int parseTaskNumber(String input, String command) throws MekaException {
        String numberText = input.substring(command.length()).trim();
        if (numberText.isEmpty()) {
            throw new MekaException(NUMBER_REQUIRED_MESSAGE);
        }
        try {
            return Integer.parseInt(numberText);
        } catch (NumberFormatException exception) {
            throw new MekaException(NUMBER_REQUIRED_MESSAGE);
        }
    }

    /**
     * Creates a todo task from a user command.
     *
     * @param input complete todo command.
     * @return parsed todo task.
     * @throws MekaException if the description is missing or cannot be stored.
     */
    private static Task parseTodo(String input) throws MekaException {
        return new Todo(parseDescription(input, "todo"));
    }

    /**
     * Creates a deadline task from a user command.
     *
     * @param input complete deadline command.
     * @return parsed deadline task.
     * @throws MekaException if required task details are missing or invalid.
     */
    private static Task parseDeadline(String input) throws MekaException {
        requireSingleMarker(input, "by");
        int byIndex = findArgumentMarker(input, "by");
        String description = byIndex < 0
                ? parseDescription(input, "deadline")
                : input.substring("deadline".length(), byIndex).trim();
        requireDescription(description);
        if (byIndex < 0) {
            throw new MekaException(UNKNOWN_COMMAND_MESSAGE);
        }

        String by = input.substring(byIndex + " /by".length()).trim();
        requireDateTime(by);
        return new Deadline(description, parseDateTime(by));
    }

    /**
     * Creates an event task from a user command.
     *
     * @param input complete event command.
     * @return parsed event task.
     * @throws MekaException if required task details are missing or invalid.
     */
    private static Task parseEvent(String input) throws MekaException {
        requireSingleMarker(input, "from");
        requireSingleMarker(input, "to");
        int fromIndex = findArgumentMarker(input, "from");
        int toIndex = fromIndex < 0
                ? -1
                : findArgumentMarker(input, "to", fromIndex + " /from".length());
        String description = fromIndex < 0
                ? parseDescription(input, "event")
                : input.substring("event".length(), fromIndex).trim();
        requireDescription(description);
        if (fromIndex < 0 || toIndex < 0) {
            throw new MekaException(UNKNOWN_COMMAND_MESSAGE);
        }

        String from = input.substring(fromIndex + " /from".length(), toIndex).trim();
        String to = input.substring(toIndex + " /to".length()).trim();
        requireDateTime(from);
        requireDateTime(to);
        LocalDateTime start = parseDateTime(from);
        LocalDateTime end = parseDateTime(to);
        if (!end.isAfter(start)) {
            throw new MekaException("The event end must be after its start.");
        }
        return new Event(description, start, end);
    }

    /**
     * Rejects repeated date parameters so no supplied value is silently ignored.
     */
    private static void requireSingleMarker(String input, String marker) throws MekaException {
        int firstIndex = findArgumentMarker(input, marker);
        if (firstIndex >= 0 && findArgumentMarker(input, marker, firstIndex + 1) >= 0) {
            throw new MekaException("Specify /" + marker + " only once.");
        }
    }

    /**
     * Returns whether the input contains the given command word, optionally
     * followed by arguments.
     *
     * @param input complete user input.
     * @param command command word to match.
     * @return true if the input represents the command.
     */
    private static boolean isCommand(String input, String command) {
        return input.equals(command) || input.startsWith(command + " ");
    }

    /**
     * Finds a slash-prefixed argument marker such as {@code /by} or
     * {@code /from}.
     *
     * @param input complete user input.
     * @param marker marker name without the slash.
     * @return the marker's starting index, or -1 if it is not present.
     */
    private static int findArgumentMarker(String input, String marker) {
        return findArgumentMarker(input, marker, 0);
    }

    /**
     * Finds a slash-prefixed argument marker at or after a given index.
     *
     * A marker is accepted only when it ends the input or is followed by
     * whitespace, preventing text such as {@code /bye} from matching
     * {@code /by}.
     *
     * @param input complete user input.
     * @param marker marker name without the slash.
     * @param startIndex index at which to begin searching.
     * @return the marker's starting index, or -1 if it is not present.
     */
    private static int findArgumentMarker(String input, String marker, int startIndex) {
        String markerText = " /" + marker;
        int markerIndex = input.indexOf(markerText, startIndex);

        while (markerIndex >= 0) {
            int afterMarker = markerIndex + markerText.length();
            if (afterMarker == input.length()
                    || Character.isWhitespace(input.charAt(afterMarker))) {
                return markerIndex;
            }
            markerIndex = input.indexOf(markerText, markerIndex + 1);
        }
        return -1;
    }

    /**
     * Extracts and validates a task description from a task creation command.
     *
     * @param input complete user input.
     * @param command command word at the start of the input.
     * @return the non-empty task description.
     * @throws MekaException if the description is empty or cannot be stored safely.
     */
    private static String parseDescription(String input, String command) throws MekaException {
        String description = input.substring(command.length()).trim();
        requireDescription(description);
        return description;
    }

    /**
     * Ensures a task description contains visible characters and can be stored.
     *
     * @param description task description to validate.
     * @throws MekaException if the description is empty or contains the file delimiter.
     */
    private static void requireDescription(String description) throws MekaException {
        requireStorableText(description, DESCRIPTION_REQUIRED_MESSAGE);
    }

    /**
     * Ensures a date-time argument is present and can be stored safely.
     *
     * @param dateTime date and time text to validate.
     * @throws MekaException if the value is empty or contains the file delimiter.
     */
    private static void requireDateTime(String dateTime) throws MekaException {
        requireStorableText(dateTime, DATE_TIME_REQUIRED_MESSAGE);
    }

    /**
     * Parses a user-entered date and time in the {@code d/M/yyyy HHmm} format.
     *
     * @param dateTime date and time text supplied by the user.
     * @return the parsed date and time.
     * @throws MekaException if the text is not a valid date and time.
     */
    private static LocalDateTime parseDateTime(String dateTime) throws MekaException {
        try {
            return LocalDateTime.parse(dateTime, INPUT_DATE_TIME_FORMAT);
        } catch (DateTimeParseException exception) {
            throw new MekaException(INVALID_DATE_TIME_MESSAGE);
        }
    }

    /**
     * Validates user-entered text before placing it in the pipe-separated file.
     *
     * @param text text to validate.
     * @param emptyMessage message to use when the text is empty.
     * @throws MekaException if the text is empty or contains the file delimiter.
     */
    private static void requireStorableText(String text, String emptyMessage)
            throws MekaException {
        if (text.isBlank()) {
            throw new MekaException(emptyMessage);
        }
        if (text.contains(" | ")) {
            throw new MekaException(RESERVED_DELIMITER_MESSAGE);
        }
    }
}
