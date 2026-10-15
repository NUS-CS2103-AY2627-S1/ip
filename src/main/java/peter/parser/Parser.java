package peter.parser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import peter.exception.PeterException;
import peter.task.Deadline;
import peter.task.Event;
import peter.task.Task;
import peter.task.Todo;

/**
 * Parses user input strings into corresponding command tasks or parameters.
 */
public class Parser {
    private static final String STORAGE_DELIMITER = "|";

    /**
     * Parses the user input for creating a Todo task.
     *
     * @param input Raw user input string.
     * @return A new Todo object.
     * @throws PeterException If the description is empty or contains reserved characters.
     */
    public static Task parseTodo(String input) throws PeterException {
        String description = normalizeDescription(getArguments(input, "todo"),
                "That waypoint needs a description. Try: todo read book");
        return new Todo(description);
    }

    /**
     * Parses the command for creating a Deadline task.
     *
     * @param input Raw command string e.g., "deadline return book /by 2019-12-02 1800"
     * @return A new Deadline object.
     * @throws PeterException If the command format or date is invalid.
     */
    public static Task parseDeadline(String input) throws PeterException {
        String body = getArguments(input, "deadline");
        String[] parts = body.split("(?i)\\s+/by\\s+", -1);
        if (parts.length != 2) {
            throw new PeterException("Chart exactly one /by time. "
                    + "Try: deadline return book /by 2026-09-10 1800");
        }

        String description = normalizeDescription(parts[0], "A deadline needs a task before /by.");
        String dateTime = normalizeWhitespace(parts[1]);
        rejectStorageDelimiter(dateTime);
        return new Deadline(description, dateTime);
    }

    /**
     * Parses the user input for creating an Event task.
     *
     * @param input Raw user input string.
     * @return A new Event object.
     * @throws PeterException If the command format or time range is invalid.
     */
    public static Task parseEvent(String input) throws PeterException {
        String body = getArguments(input, "event");
        String[] fromParts = body.split("(?i)\\s+/from\\s+", -1);
        if (fromParts.length != 2) {
            throw new PeterException("Chart exactly one /from time. "
                    + "Try: event meeting /from 1400 /to 1600");
        }
        String[] toParts = fromParts[1].split("(?i)\\s+/to\\s+", -1);
        if (toParts.length != 2) {
            throw new PeterException("Chart exactly one /to time. "
                    + "Try: event meeting /from 1400 /to 1600");
        }

        String description = normalizeDescription(fromParts[0],
                "That event needs a description before /from.");
        String from = normalizeWhitespace(toParts[0]);
        String to = normalizeWhitespace(toParts[1]);
        rejectStorageDelimiter(from);
        rejectStorageDelimiter(to);
        return new Event(description, from, to);
    }

    /**
     * Parses the zero-based index from a command containing one task number.
     *
     * @param input Raw user input string containing a task index.
     * @return Zero-based task index.
     * @throws PeterException If the index is invalid, missing, or followed by extra parameters.
     */
    public static int parseIndex(String input) throws PeterException {
        String arguments = getArguments(input, firstWord(input));
        String[] parts = arguments.isEmpty() ? new String[0] : arguments.split("\\s+");
        if (parts.length == 0) {
            throw new PeterException("Which waypoint? Please include one task number.");
        }
        if (parts.length > 1) {
            throw new PeterException("Use exactly one task number, without extra parameters.");
        }
        if (!parts[0].matches("[1-9]\\d*")) {
            throw new PeterException("That waypoint number isn't valid. Use a positive whole number.");
        }
        try {
            return Math.subtractExact(Integer.parseInt(parts[0]), 1);
        } catch (ArithmeticException | NumberFormatException e) {
            throw new PeterException("That waypoint number is too large to navigate.");
        }
    }

    /**
     * Parses the date string from the view command.
     *
     * @param input Full user command string e.g., "view 2019-12-02"
     * @return Parsed date.
     * @throws PeterException If the date is missing, duplicated, or invalid.
     */
    public static LocalDate parseViewDate(String input) throws PeterException {
        String arguments = getArguments(input, "view");
        String[] parts = arguments.isEmpty() ? new String[0] : arguments.split("\\s+");
        if (parts.length == 0) {
            throw new PeterException("Which date should I scout? Try: view 2026-09-10");
        }
        if (parts.length > 1) {
            throw new PeterException("Scout one date at a time. Use yyyy-MM-dd, such as 2026-09-10.");
        }
        try {
            return LocalDate.parse(parts[0]);
        } catch (DateTimeParseException e) {
            throw new PeterException("I couldn't read that date. Use a real yyyy-MM-dd date, such as 2026-09-10.");
        }
    }

    /**
     * Parses the search keyword from the find command.
     *
     * @param input Full command input string (e.g., "find book").
     * @return The normalized keyword to search for.
     * @throws PeterException If the keyword is missing or contains reserved characters.
     */
    public static String parseFindKeyword(String input) throws PeterException {
        return normalizeDescription(getArguments(input, "find"),
                "What should I scout for? Try: find book");
    }

    /**
     * Ensures that a command which takes no parameters has no trailing content.
     *
     * @param input Full command input.
     * @param command Expected command word.
     * @throws PeterException If extra parameters are present.
     */
    public static void requireNoArguments(String input, String command) throws PeterException {
        if (!getArguments(input, command).isEmpty()) {
            throw new PeterException("The " + command + " command doesn't take extra parameters.");
        }
    }

    private static String getArguments(String input, String expectedCommand) throws PeterException {
        if (input == null) {
            throw new PeterException("I received an empty route. Please enter a command.");
        }
        String normalized = input.trim();
        if (normalized.isEmpty()) {
            return "";
        }
        String[] parts = normalized.split("\\s+", 2);
        if (!parts[0].equalsIgnoreCase(expectedCommand)) {
            throw new PeterException("I couldn't read that command format. Please try again.");
        }
        return parts.length == 2 ? normalizeWhitespace(parts[1]) : "";
    }

    private static String firstWord(String input) throws PeterException {
        if (input == null || input.trim().isEmpty()) {
            throw new PeterException("I received an empty route. Please enter a command.");
        }
        return input.trim().split("\\s+", 2)[0];
    }

    private static String normalizeDescription(String description, String emptyMessage) throws PeterException {
        String normalized = normalizeWhitespace(description);
        if (normalized.isEmpty()) {
            throw new PeterException(emptyMessage);
        }
        rejectStorageDelimiter(normalized);
        return normalized;
    }

    private static String normalizeWhitespace(String value) {
        return value == null ? "" : value.trim().replaceAll("\\s+", " ");
    }

    private static void rejectStorageDelimiter(String value) throws PeterException {
        if (value.contains(STORAGE_DELIMITER)) {
            throw new PeterException("The | character is reserved for Peter's log. Please leave it out.");
        }
        if (value.chars().anyMatch(Character::isISOControl)) {
            throw new PeterException("That text contains an unsupported control character.");
        }
    }
}
