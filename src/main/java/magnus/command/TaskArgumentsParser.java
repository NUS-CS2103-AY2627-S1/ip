package magnus.command;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses the named fields used by commands that create or update dated tasks.
 * Delimiters are recognized only as standalone tokens so slashes in ordinary text
 * are not accidentally interpreted as task fields.
 */
final class TaskArgumentsParser {
    private static final Pattern PATTERN_FIELD_DELIMITER = Pattern.compile(
            "(?<!\\S)/(?:by|from|to)(?!\\S)");
    private static final Pattern PATTERN_DEADLINE_ARGUMENTS = Pattern.compile(
            "^(?<description>.+?)\\s+/by\\s+(?<deadline>.+)$");
    private static final Pattern PATTERN_EVENT_ARGUMENTS = Pattern.compile(
            "^(?<description>.+?)\\s+/from\\s+(?<start>.+?)\\s+/to\\s+(?<end>.+)$");

    private TaskArgumentsParser() {
    }

    /**
     * Parses a description followed by exactly one {@code /by} field.
     *
     * @param input The complete command arguments.
     * @return The separated description and deadline.
     * @throws IllegalArgumentException If the fields are missing, repeated, or misplaced.
     */
    static DeadlineArguments parseDeadline(String input) {
        String normalizedInput = requireSingleLineInput(input);
        Matcher matcher = PATTERN_DEADLINE_ARGUMENTS.matcher(normalizedInput);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("A deadline requires exactly one /by field");
        }

        String description = matcher.group("description").strip();
        String deadline = matcher.group("deadline").strip();
        if (containsFieldDelimiter(description) || containsFieldDelimiter(deadline)) {
            throw new IllegalArgumentException("A deadline accepts exactly one /by field");
        }
        return new DeadlineArguments(description, deadline);
    }

    /**
     * Parses a description followed by one {@code /from} field and then one {@code /to} field.
     *
     * @param input The complete command arguments.
     * @return The separated description, start time, and end time.
     * @throws IllegalArgumentException If the fields are missing, repeated, or misplaced.
     */
    static EventArguments parseEvent(String input) {
        String normalizedInput = requireSingleLineInput(input);
        Matcher matcher = PATTERN_EVENT_ARGUMENTS.matcher(normalizedInput);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("An event requires /from followed by /to");
        }

        String description = matcher.group("description").strip();
        String start = matcher.group("start").strip();
        String end = matcher.group("end").strip();
        if (containsFieldDelimiter(description)
                || containsFieldDelimiter(start)
                || containsFieldDelimiter(end)) {
            throw new IllegalArgumentException("An event accepts one /from and one /to field");
        }
        return new EventArguments(description, start, end);
    }

    /**
     * Returns whether text contains a reserved task-field delimiter.
     *
     * @param text The text to inspect.
     * @return {@code true} if a standalone reserved delimiter is present.
     */
    static boolean containsFieldDelimiter(String text) {
        return text != null && PATTERN_FIELD_DELIMITER.matcher(text).find();
    }

    /**
     * Strips surrounding whitespace after rejecting absent or multiline input.
     *
     * @param input The input to normalize.
     * @return The stripped, non-blank input.
     */
    private static String requireSingleLineInput(String input) {
        if (input == null || input.isBlank()
                || input.contains("\n") || input.contains("\r")) {
            throw new IllegalArgumentException("Task arguments must be non-blank and single-line");
        }
        return input.strip();
    }

    /**
     * Contains the parsed fields of a deadline command.
     *
     * @param description The task description.
     * @param deadline The deadline text.
     */
    record DeadlineArguments(String description, String deadline) {
    }

    /**
     * Contains the parsed fields of an event command.
     *
     * @param description The task description.
     * @param start The event start text.
     * @param end The event end text.
     */
    record EventArguments(String description, String start, String end) {
    }
}
