package stewie.parser;

import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses command words and validates fields before tasks are changed.
 */
public class Parser {
    private static final Pattern CREATE_MARKER = Pattern.compile("(?<!\\S)/[^\\s/]+");
    private static final Pattern UPDATE_MARKER = Pattern.compile("(?<!\\S)([a-zA-Z]+/|/[^\\s/]+)");

    /**
     * Normalizes command casing and whitespace consistently in both interfaces.
     *
     * @param input User input, which may be null.
     * @return Normalized input, or an empty string for null input.
     */
    public static String normalize(String input) {
        return input == null ? "" : input.strip().replaceAll("(?U)\\s+", " ").toLowerCase(Locale.ROOT);
    }

    /**
     * Returns the command type, including commands whose required arguments are missing.
     *
     * @param input User input.
     * @return Recognized command or ERROR.
     */
    public static Command getCommand(String input) {
        String[] parts = normalize(input).split(" ", 2);
        try {
            Command command = Command.valueOf(parts[0].toUpperCase(Locale.ROOT));
            if ((command == Command.LIST || command == Command.BYE) && parts.length != 1) {
                return Command.ERROR;
            }
            return command;
        } catch (IllegalArgumentException exception) {
            return Command.ERROR;
        }
    }

    /**
     * Returns a zero-based index for a command with exactly one positive task number.
     *
     * @param input User command.
     * @return Task index, or -1 for missing, extra, nonnumeric, or overflowing arguments.
     */
    public static int getTaskIndex(String input) {
        String[] parts = normalize(input).split(" ");
        return parts.length == 2 ? parseIndex(parts[1]) : -1;
    }

    private static int parseIndex(String number) {
        if (!number.matches("[0-9]+")) {
            return -1;
        }
        try {
            int value = Integer.parseInt(number);
            return value > 0 ? value - 1 : -1;
        } catch (NumberFormatException exception) {
            return -1;
        }
    }

    /**
     * Parses a deadline with exactly one /by field.
     *
     * @param input User command.
     * @return Description and deadline.
     * @throws IllegalArgumentException If a field is missing, repeated, or unsupported.
     */
    public static String[] parseDeadline(String input) {
        return parseCreation(input, new String[] {"/by"},
                "Use: deadline <description> /by <date>. Supply each field once.");
    }

    /**
     * Parses an event with one /from field followed by one /to field.
     *
     * @param input User command.
     * @return Description, start date, and end date.
     * @throws IllegalArgumentException If fields are missing, repeated, unsupported, or out of order.
     */
    public static String[] parseEvent(String input) {
        return parseCreation(input, new String[] {"/from", "/to"},
                "Use: event <description> /from <date> /to <date>. Supply each field once in this order.");
    }

    /**
     * Validates the complete marker sequence so no supplied field is silently discarded.
     */
    private static String[] parseCreation(String input, String[] markers, String guidance) {
        String body = body(input);
        Matcher matcher = CREATE_MARKER.matcher(body);
        String[] values = new String[markers.length + 1];
        int count = 0;
        int start = 0;
        while (matcher.find()) {
            if (count >= markers.length || !matcher.group().equals(markers[count])) {
                throw new IllegalArgumentException(guidance);
            }
            values[count++] = body.substring(start, matcher.start()).strip();
            start = matcher.end();
        }
        if (count != markers.length) {
            throw new IllegalArgumentException(guidance);
        }
        values[count] = body.substring(start).strip();
        if (Arrays.stream(values).anyMatch(String::isEmpty)) {
            throw new IllegalArgumentException(guidance);
        }
        return values;
    }

    /**
     * Returns a required todo description.
     *
     * @param input User command.
     * @return Task description.
     * @throws IllegalArgumentException If the description is missing.
     */
    public static String parseTodo(String input) {
        String description = body(input);
        if (description.isEmpty()) {
            throw new IllegalArgumentException("Use: todo <description>.");
        }
        return description;
    }

    private static String body(String input) {
        String[] parts = input == null ? new String[0] : input.strip().split("(?U)\\s+", 2);
        return parts.length < 2 ? "" : parts[1].strip();
    }

    /**
     * Returns the task index from an update with at least one replacement field.
     *
     * @param input User command.
     * @return Task index or -1 for invalid input.
     */
    public static int getUpdateTaskIndex(String input) {
        String[] parts = normalize(input).split(" ", 3);
        return parts.length == 3 ? parseIndex(parts[1]) : -1;
    }

    /**
     * Returns an update description, or an empty string if omitted.
     *
     * @param input User command.
     * @return Replacement description.
     */
    public static String parseUpdateDescription(String input) {
        String description = parseUpdate(input)[0];
        return description == null ? "" : description;
    }

    /**
     * Parses optional update fields without accepting repeated or unknown markers.
     *
     * @param input User command.
     * @return Description, deadline, start, and end; null entries preserve existing fields.
     * @throws IllegalArgumentException If a marker is unsupported, repeated, or has no value.
     */
    public static String[] parseUpdate(String input) {
        String fields = body(body(input));
        String[] values = new String[4];
        Matcher matcher = UPDATE_MARKER.matcher(fields);
        int field = 0;
        int start = 0;
        while (matcher.find()) {
            storeUpdateValue(values, field, fields.substring(start, matcher.start()));
            field = getUpdateFieldIndex(matcher.group());
            if (values[field] != null) {
                throw new IllegalArgumentException("Supply each update field only once; d/ and by/ are aliases.");
            }
            start = matcher.end();
        }
        storeUpdateValue(values, field, fields.substring(start));
        return values;
    }

    /**
     * Returns the destination index for a supported update marker, treating deadline aliases equally.
     */
    private static int getUpdateFieldIndex(String marker) {
        return switch (marker.toLowerCase(Locale.ROOT)) {
            case "d/", "by/" -> 1;
            case "from/" -> 2;
            case "to/" -> 3;
            default -> throw new IllegalArgumentException("Use update fields: d/ (or by/), from/, to/.");
        };
    }

    /**
     * Stores a trimmed field value, allowing only the optional description to be empty.
     */
    private static void storeUpdateValue(String[] values, int field, String rawValue) {
        String value = rawValue.strip();
        if (field != 0 && value.isEmpty()) {
            throw new IllegalArgumentException("Update fields cannot be empty.");
        }
        values[field] = value.isEmpty() ? null : value;
    }

    /**
     * Returns at least one search keyword.
     *
     * @param input User command.
     * @return Search keywords.
     * @throws IllegalArgumentException If no keyword is supplied.
     */
    public static String[] parseFindKeywords(String input) {
        String keywords = body(input);
        if (keywords.isEmpty()) {
            throw new IllegalArgumentException("Use: find <keyword> [more keywords].");
        }
        return keywords.split("(?U)\\s+");
    }
}
