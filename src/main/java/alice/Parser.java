package alice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/**
 * Handles parsing of raw user input strings into structured data or task
 * objects, including validation and date/time parsing.
 */
public class Parser {
    private static final DateTimeFormatter INPUT_FORMAT =
            DateTimeFormatter.ofPattern("d/M/yyyy HHmm", Locale.ENGLISH);
    private static final String FROM_MARKER = "/from ";
    private static final String TO_MARKER = "/to ";
    private static final int EVENT_PREFIX_LENGTH = "event".length();

    /**
     * Extracts and validates the description from a "todo" command.
     *
     * @param input the full raw user input, e.g. "todo read book".
     * @return the extracted, trimmed description.
     * @throws AliceException if the description is empty.
     */
    public static String parseTodoDescription(String input) throws AliceException {
        String description = input.length() > 4 ? input.substring(5).trim() : "";
        if (description.isEmpty()) {
            throw new AliceException("The description of a todo cannot be empty.");
        }
        return description;
    }

    /**
     * Parses a "deadline" command into a Deadline task, extracting the
     * description and the /by date/time.
     *
     * @param input the full raw user input, e.g. "deadline return book /by 2/12/2019 1800".
     * @return a new Deadline task built from the parsed input.
     * @throws AliceException if the input is missing required parts or the
     *         date/time cannot be parsed.
     */
    public static Deadline parseDeadline(String input) throws AliceException {
        if (!input.contains(" /by ")) {
            throw new AliceException("A deadline needs a description and a /by date.");
        }
        String rest = input.substring(9);
        String[] parts = rest.split(" /by ");
        if (parts.length < 2 || parts[0].trim().isEmpty() || parts[1].trim().isEmpty()) {
            throw new AliceException("A deadline needs both a description and a /by date.");
        }
        LocalDateTime by = parseDateTime(parts[1]);
        return new Deadline(parts[0].trim(), by);
    }

    /**
     * Parses an "event" command into an Event task, extracting the
     * description and the /from and /to date/times.
     *
     * @param input the full raw user input, e.g.
     *        "event project meeting /from 2/12/2019 1400 /to 2/12/2019 1600".
     * @return a new Event task built from the parsed input.
     * @throws AliceException if the input is missing required parts or the
     *         date/times cannot be parsed.
     */
    public static Event parseEvent(String input) throws AliceException {
        String rest = input.length() > EVENT_PREFIX_LENGTH ? input.substring(EVENT_PREFIX_LENGTH).trim() : "";

        // Locating the markers by index, rather than splitting on them, keeps
        // a malformed command (a missing marker, or /to written before /from)
        // from indexing past the end of a split array, which used to throw an
        // ArrayIndexOutOfBoundsException and kill the program outright.
        int fromIndex = rest.indexOf(FROM_MARKER);
        if (fromIndex < 0) {
            throw new AliceException("An event needs a /from date, e.g. "
                    + "event project meeting /from 2/12/2019 1400 /to 2/12/2019 1600.");
        }
        int toIndex = rest.indexOf(TO_MARKER, fromIndex);
        if (toIndex < 0) {
            if (rest.contains(TO_MARKER)) {
                throw new AliceException("An event's /from must come before its /to.");
            }
            throw new AliceException("An event needs a /to date, e.g. "
                    + "event project meeting /from 2/12/2019 1400 /to 2/12/2019 1600.");
        }

        String description = rest.substring(0, fromIndex).trim();
        if (description.isEmpty()) {
            throw new AliceException("The description of an event cannot be empty.");
        }
        String fromText = rest.substring(fromIndex + FROM_MARKER.length(), toIndex);
        String toText = rest.substring(toIndex + TO_MARKER.length());
        if (fromText.isBlank() || toText.isBlank()) {
            throw new AliceException("An event needs both a /from and a /to date.");
        }

        LocalDateTime from = parseDateTime(fromText);
        LocalDateTime to = parseDateTime(toText);
        if (to.isBefore(from)) {
            throw new AliceException("An event cannot end before it starts.");
        }
        return new Event(description, from, to);
    }

    /**
     * Parses a single date/time string in the expected input format
     * (d/M/yyyy HHmm), shared by both {@link #parseDeadline} and
     * {@link #parseEvent} so the format and its error message are defined
     * in exactly one place.
     *
     * @param text the raw date/time text, e.g. "2/12/2019 1800".
     * @return the parsed date/time.
     * @throws AliceException if the text cannot be parsed in the expected format.
     */
    private static LocalDateTime parseDateTime(String text) throws AliceException {
        try {
            return LocalDateTime.parse(text.trim(), INPUT_FORMAT);
        } catch (DateTimeParseException e) {
            throw new AliceException("Please use the date format d/M/yyyy HHmm, e.g. 2/12/2019 1800.");
        }
    }

    /**
     * Parses the task number given to a command such as "mark 3", returning
     * it as a zero-based index.
     *
     * <p>Both a missing number and a non-numeric one are reported as an
     * {@link AliceException} naming the command, so that every caller
     * reports these two mistakes the same way instead of letting an
     * unchecked exception escape.
     *
     * @param input the full raw user input, e.g. "mark 3".
     * @param commandWord the command the input starts with, e.g. "mark".
     * @return the zero-based index.
     * @throws AliceException if the task number is missing or not a number.
     */
    public static int parseIndexArgument(String input, String commandWord) throws AliceException {
        assert input.startsWith(commandWord) : "input must start with the command word";
        String indexText = input.substring(commandWord.length()).trim();
        if (indexText.isEmpty()) {
            throw new AliceException(commandWord + " needs a task number, e.g. " + commandWord + " 2.");
        }
        try {
            return Integer.parseInt(indexText) - 1;
        } catch (NumberFormatException e) {
            throw new AliceException("'" + indexText + "' is not a task number. Try " + commandWord + " 2.");
        }
    }

    /**
     * Parses a "snooze" command into the task index and number of days to
     * push its deadline back by.
     *
     * @param input the full raw user input, e.g. "snooze 2 3".
     * @return a two-element array: {zero-based task index, number of days}.
     * @throws AliceException if the input doesn't have exactly a task number
     *         and a day count, either part is not a number, or the day count
     *         isn't positive.
     */
    public static int[] parseSnooze(String input) throws AliceException {
        String rest = input.length() > 6 ? input.substring(7).trim() : "";
        String[] parts = rest.split("\\s+");
        if (parts.length != 2 || parts[0].isEmpty() || parts[1].isEmpty()) {
            throw new AliceException("A snooze needs a task number and number of days, e.g. snooze 2 3.");
        }
        int zeroBasedIndex;
        int days;
        try {
            zeroBasedIndex = Integer.parseInt(parts[0]) - 1;
            days = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            throw new AliceException("A snooze needs two numbers, e.g. snooze 2 3.");
        }
        if (days <= 0) {
            throw new AliceException("The number of days to snooze must be positive.");
        }
        return new int[]{zeroBasedIndex, days};
    }

    /**
     * Extracts and validates one or more keywords from a "find" command.
     * Multiple keywords are separated by whitespace, e.g.
     * "find book magazine" searches for tasks matching "book" or
     * "magazine".
     *
     * @param input the full raw user input, e.g. "find book" or
     *        "find book magazine".
     * @return the extracted keywords.
     * @throws AliceException if no keyword is given.
     */
    public static String[] parseFindKeywords(String input) throws AliceException {
        String keywordText = input.length() > 4 ? input.substring(5).trim() : "";
        if (keywordText.isEmpty()) {
            throw new AliceException("The keyword to find cannot be empty.");
        }
        return keywordText.split("\\s+");
    }
}
