package peter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

/**
 * Makes sense of raw user input: splitting it into a command and arguments,
 * and parsing arguments into task data. Contains no console I/O and does
 * not mutate the task list directly.
 */
public class Parser {
    // STRICT (rather than the default SMART) so an impossible calendar date like
    // 2019-02-30 is rejected outright instead of silently being resolved to 2019-02-28.
    // Uses uuuu (proleptic year) rather than yyyy (year-of-era): under STRICT, yyyy needs
    // an explicit era field to resolve, which this format doesn't have, and parsing fails
    // even for a valid date.
    private static final DateTimeFormatter INPUT_DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm").withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter INPUT_DATE_FORMAT =
            DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT);
    private static final String MISSING_ARGUMENTS_MESSAGE =
            "Whoa, my spidey-sense needs more than that to work with, pal.";
    private static final String INVALID_DATE_MESSAGE =
            "That date's got my spidey-sense all tangled - try yyyy-MM-dd, with a real calendar date.";
    private static final String INVALID_DATE_TIME_MESSAGE =
            "That date's got my spidey-sense all tangled - try yyyy-MM-dd or yyyy-MM-dd HHmm, "
                    + "with a real calendar date.";
    private static final String INVALID_DESCRIPTION_MESSAGE =
            "Can't have a '|' or a line break in there, pal - that'll tear a hole in my web (the save file).";
    private static final String DUPLICATE_MARKER_MESSAGE_FORMAT =
            "One %s is plenty, pal - you've webbed that marker in there twice.";
    private static final String EVENT_END_NOT_AFTER_START_MESSAGE =
            "An event's gotta end after it starts, pal - check your /from and /to.";

    /**
     * Represents a raw line of input split into its command keyword and the
     * remaining arguments.
     */
    public static class ParsedInput {
        private final Command command;
        private final String arguments;

        /**
         * Creates a parsed input.
         *
         * @param command the recognised command
         * @param arguments the remaining text after the command keyword
         */
        public ParsedInput(Command command, String arguments) {
            this.command = command;
            this.arguments = arguments;
        }

        /**
         * Returns the recognised command.
         *
         * @return the command
         */
        public Command getCommand() {
            return command;
        }

        /**
         * Returns the arguments that followed the command keyword.
         *
         * @return the trimmed argument text
         */
        public String getArguments() {
            return arguments;
        }
    }

    /**
     * Splits a raw line of user input into a command and its arguments.
     *
     * @param rawInput the full line entered by the user
     * @return the parsed input
     * @throws PeterException if the input is empty
     */
    public static ParsedInput parseInput(String rawInput) throws PeterException {
        assert rawInput != null : "raw input should not be null";
        String trimmedInput = rawInput.trim();
        if (trimmedInput.isEmpty()) {
            throw new PeterException(MISSING_ARGUMENTS_MESSAGE);
        }
        String[] keywordAndRest = trimmedInput.split("\\s+", 2);
        String keyword = keywordAndRest[0];
        String arguments = keywordAndRest.length > 1 ? keywordAndRest[1] : "";
        return new ParsedInput(Command.fromKeyword(keyword), arguments);
    }

    /**
     * Parses arguments for a todo command.
     *
     * @param arguments the todo description entered by the user
     * @return the new todo task
     * @throws PeterException if the description is empty
     */
    public static Todo parseTodo(String arguments) throws PeterException {
        if (arguments.isEmpty()) {
            throw new PeterException(MISSING_ARGUMENTS_MESSAGE);
        }
        validateDescription(arguments);
        return new Todo(arguments);
    }

    /**
     * Parses the keyword used by the {@code find} command.
     *
     * @param arguments the search keyword entered by the user
     * @return the trimmed keyword
     * @throws PeterException if the keyword is empty
     */
    public static String parseFindKeyword(String arguments) throws PeterException {
        if (arguments.isEmpty()) {
            throw new PeterException(MISSING_ARGUMENTS_MESSAGE);
        }
        return arguments;
    }

    /**
     * Parses arguments for a deadline command in the form
     * {@code <description> /by <date/time>}.
     *
     * @param arguments the deadline details entered by the user
     * @return the new deadline task
     * @throws PeterException if the details are missing or the date is invalid
     */
    public static Deadline parseDeadline(String arguments) throws PeterException {
        if (arguments.isEmpty() || !arguments.contains("/by")) {
            throw new PeterException(MISSING_ARGUMENTS_MESSAGE);
        }
        if (countOccurrences(arguments, "/by") > 1) {
            throw new PeterException(String.format(DUPLICATE_MARKER_MESSAGE_FORMAT, "/by"));
        }
        String[] parts = arguments.split("/by", 2);
        String description = parts[0].trim();
        String byText = normalizeWhitespace(parts[1]);
        if (description.isEmpty() || byText.isEmpty()) {
            throw new PeterException(MISSING_ARGUMENTS_MESSAGE);
        }
        validateDescription(description);
        LocalDateTime by = parseUserDateTime(byText);
        return new Deadline(description, by);
    }

    /**
     * Parses arguments for an event command in the form
     * {@code <description> /from <date/time> /to <date/time>}.
     *
     * @param arguments the event details entered by the user
     * @return the new event task
     * @throws PeterException if the details are missing or a date is invalid
     */
    public static Event parseEvent(String arguments) throws PeterException {
        if (arguments.isEmpty() || !arguments.contains("/from") || !arguments.contains("/to")) {
            throw new PeterException(MISSING_ARGUMENTS_MESSAGE);
        }
        if (countOccurrences(arguments, "/from") > 1) {
            throw new PeterException(String.format(DUPLICATE_MARKER_MESSAGE_FORMAT, "/from"));
        }
        if (countOccurrences(arguments, "/to") > 1) {
            throw new PeterException(String.format(DUPLICATE_MARKER_MESSAGE_FORMAT, "/to"));
        }
        String[] fromParts = arguments.split("/from", 2);
        String description = fromParts[0].trim();
        String[] toParts = fromParts[1].split("/to", 2);
        String fromText = normalizeWhitespace(toParts[0]);
        String toText = toParts.length > 1 ? normalizeWhitespace(toParts[1]) : "";
        if (description.isEmpty() || fromText.isEmpty() || toText.isEmpty()) {
            throw new PeterException(MISSING_ARGUMENTS_MESSAGE);
        }
        validateDescription(description);
        LocalDateTime from = parseUserDateTime(fromText);
        LocalDateTime to = parseUserDateTime(toText);
        if (!to.isAfter(from)) {
            throw new PeterException(EVENT_END_NOT_AFTER_START_MESSAGE);
        }
        return new Event(description, from, to);
    }

    /**
     * Parses the task number for mark/unmark/delete commands into a
     * zero-based index. Does not check the index against the task list size.
     *
     * @param arguments the task number entered by the user
     * @return the zero-based task index
     * @throws PeterException if the argument is missing or not a number
     */
    public static int parseTaskIndex(String arguments) throws PeterException {
        if (arguments.isEmpty()) {
            throw new PeterException(MISSING_ARGUMENTS_MESSAGE);
        }
        try {
            return Integer.parseInt(arguments) - 1;
        } catch (NumberFormatException exception) {
            throw new PeterException("That's not a number, bud - try again?");
        }
    }

    /**
     * Parses the date used by the {@code schedule} command.
     *
     * @param arguments the date entered by the user
     * @return the parsed date
     * @throws PeterException if the date is invalid
     */
    public static LocalDate parseDate(String arguments) throws PeterException {
        try {
            return LocalDate.parse(normalizeWhitespace(arguments), INPUT_DATE_FORMAT);
        } catch (DateTimeParseException exception) {
            throw new PeterException(INVALID_DATE_MESSAGE);
        }
    }

    /**
     * Parses a user date and time, defaulting date-only values to midnight.
     *
     * @param dateTimeText the user-entered date and optional time
     * @return the parsed date and time
     * @throws PeterException if the text does not match a recognised format
     */
    private static LocalDateTime parseUserDateTime(String dateTimeText) throws PeterException {
        try {
            if (dateTimeText.matches("\\d{4}-\\d{2}-\\d{2}")) {
                return LocalDate.parse(dateTimeText, INPUT_DATE_FORMAT).atStartOfDay();
            }
            return LocalDateTime.parse(dateTimeText, INPUT_DATE_TIME_FORMAT);
        } catch (DateTimeParseException exception) {
            throw new PeterException(INVALID_DATE_TIME_MESSAGE);
        }
    }

    /**
     * Collapses runs of whitespace within the text to a single space, and
     * trims the ends, so a stray extra space (e.g. two spaces between a date
     * and a time) does not cause an otherwise-valid date/time to fail to
     * parse.
     *
     * @param text the text to normalise
     * @return the trimmed text with internal whitespace runs collapsed
     */
    private static String normalizeWhitespace(String text) {
        return text.trim().replaceAll("\\s+", " ");
    }

    /**
     * Rejects a description that would corrupt Peter's pipe-delimited
     * storage format or break its one-line-per-task layout.
     *
     * @param description the task description to validate
     * @throws PeterException if the description contains a storage delimiter or a line break
     */
    private static void validateDescription(String description) throws PeterException {
        if (description.contains("|") || description.contains("\n") || description.contains("\r")) {
            throw new PeterException(INVALID_DESCRIPTION_MESSAGE);
        }
    }

    /**
     * Counts non-overlapping occurrences of a literal marker within text.
     *
     * @param text the text to search
     * @param marker the literal marker to count
     * @return the number of times the marker occurs
     */
    private static int countOccurrences(String text, String marker) {
        int count = 0;
        int index = text.indexOf(marker);
        while (index != -1) {
            count++;
            index = text.indexOf(marker, index + marker.length());
        }
        return count;
    }
}
