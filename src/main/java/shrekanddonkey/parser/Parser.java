package shrekanddonkey.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

/**
 * Interprets user commands and parses their date and time values.
 */
public class Parser {
    /**
     * Creates a parser.
     */
    public Parser() {
    }

    /**
     * Lists the command types understood by the chatbot.
     */
    public enum CommandType {
        /** Lists all tasks. */
        LIST,
        /** Marks a task as done. */
        MARK,
        /** Marks a task as not done. */
        UNMARK,
        /** Adds a to-do task. */
        TODO,
        /** Adds a deadline task. */
        DEADLINE,
        /** Adds an event task. */
        EVENT,
        /** Deletes a task. */
        DELETE,
        /** Finds tasks matching a keyword. */
        FIND,
        /** Sorts tasks by their relevant date. */
        SORT,
        /** Exits the chatbot. */
        EXIT,
        /** Represents an unsupported command. */
        UNKNOWN
    }

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm")
                    .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("uuuu-MM-dd")
                    .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter SHORT_DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("d/M/uuuu HHmm")
                    .withResolverStyle(ResolverStyle.STRICT);

    /**
     * Determines the type of a user command while preserving its original input rules.
     *
     * @param input command entered by the user.
     * @return the command type, or {@code UNKNOWN} when unsupported.
     */
    public static CommandType parseCommandType(String input) {
        if (input.equals("list")) {
            return CommandType.LIST;
        } else if (hasCommandWord(input, "mark")) {
            return CommandType.MARK;
        } else if (hasCommandWord(input, "unmark")) {
            return CommandType.UNMARK;
        } else if (hasCommandWord(input, "todo")) {
            return CommandType.TODO;
        } else if (hasCommandWord(input, "deadline")) {
            return CommandType.DEADLINE;
        } else if (hasCommandWord(input, "event")) {
            return CommandType.EVENT;
        } else if (hasCommandWord(input, "delete")) {
            return CommandType.DELETE;
        } else if (hasCommandWord(input, "find")) {
            return CommandType.FIND;
        } else if (input.equals("sort")) {
            return CommandType.SORT;
        } else if (input.equals("bye")) {
            return CommandType.EXIT;
        }
        return CommandType.UNKNOWN;
    }

    /**
     * Returns the trimmed text after a command word.
     *
     * @param input complete user input.
     * @param command command word at the beginning of the input.
     * @return command arguments, or an empty string when none are provided.
     */
    public static String getArguments(String input, String command) {
        assert input != null : "Input cannot be null";
        assert command != null : "Command word cannot be null";
        assert input.length() >= command.length() : "Input length must be at least command length";
        return input.substring(command.length()).trim();
    }

    /**
     * Parses a date or date/time entered by the user.
     *
     * @param text date or date/time text.
     * @return parsed date/time, using midnight for date-only input.
     * @throws DateTimeParseException if the text is not a supported date format.
     */
    public static LocalDateTime parseDateTime(String text) {
        assert text != null : "Date text to parse cannot be null";
        assert !text.trim().isEmpty() : "Date text cannot be empty";
        try {
            return LocalDateTime.parse(text, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (DateTimeParseException ignoredIsoDateTime) {
            try {
                return LocalDateTime.parse(text, DATE_TIME_FORMATTER);
            } catch (DateTimeParseException ignoredSpaceDateTime) {
                try {
                    return LocalDate.parse(text, DATE_FORMATTER).atStartOfDay();
                } catch (DateTimeParseException ignoredDateOnly) {
                    return LocalDateTime.parse(text, SHORT_DATE_TIME_FORMATTER);
                }
            }
        }
    }

    /**
     * Checks whether input is exactly a command or starts with that command and a space.
     *
     * @param input complete user input.
     * @param command command word to check.
     * @return {@code true} when the input begins with the complete command word.
     */
    private static boolean hasCommandWord(String input, String command) {
        return input.equals(command) || input.startsWith(command + " ");
    }
}
