package ralph.parser;

import java.time.LocalDateTime;

import ralph.exception.RalphException;
import ralph.storage.Storage;

/**
 * Parses raw user input and returns the corresponding command object.
 */
public class Parser {
    /**
     * Parses a user's raw input line into a command token and argument string.
     *
     * @param line the raw user input
     * @return a Parsed instance containing the command and args
     * @throws RalphException when the input is empty or null
     */
    public Parsed parseCommand(String line) throws RalphException {
        String trimmed = line == null ? "" : line.trim();
        if (trimmed.isEmpty()) {
            throw new RalphException("You didn't say anything — try typing a command.");
        }
        String[] parts = trimmed.split("\\s+", 2);
        String command = parts[0].toLowerCase();
        String rest = parts.length > 1 ? parts[1].trim() : "";
        assert command != null && !command.isEmpty() : "Parsed command must not be empty";
        return new Parsed(command, rest);
    }

    /**
     * Parses a 1-based task index used by commands such as mark/delete into a zero-based index.
     *
     * @param value   the string value to parse
     * @param command the command that requested the index (used for error messages)
     * @return zero-based task index
     * @throws RalphException when the supplied value is empty or not a number
     */
    public static int parseTaskIndex(String value, String command) throws RalphException {
        if (value == null || value.trim().isEmpty()) {
            throw new RalphException("Which task number? Say e.g. 'mark 2'.");
        }

        int parsedIndex;
        try {
            parsedIndex = Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new RalphException("That's not a number — give me a task index like '2'.");
        }

        if (parsedIndex <= 0) {
            throw new RalphException("Task number must be positive — give me a task index like '2'.");
        }

        int zeroBasedIndex = parsedIndex - 1;
        assert zeroBasedIndex >= 0 : "Parsed task index must be zero-based and non-negative";
        return zeroBasedIndex;
    }

    /**
     * Ensures a command that requires a description has one and returns the trimmed value.
     *
     * @param value   the raw description portion of the input
     * @param command the command name (used to tailor the error message)
     * @return the trimmed description
     * @throws RalphException when the description is missing or empty
     */
    public static String getRequiredDescription(String value, String command) throws RalphException {
        String trimmed = value == null ? "" : value.trim();
        if (!trimmed.isEmpty()) {
            return trimmed;
        }

        switch (command) {
            case "todo":
                throw new RalphException("Give me a description for your todo, please.");
            case "deadline":
                throw new RalphException("Give me what the deadline is for.");
            case "event":
                throw new RalphException("Give me a description for the event, please.");
            case "find":
                throw new RalphException("Please tell me what to search for.");
            default:
                return "";
        }
    }

    /**
     * Parse a user-provided date/time string into LocalDateTime.
     *
     * @param input the string to parse
     * @return the parsed LocalDateTime
     * @throws RalphException if parsing fails
     */
    public static LocalDateTime parseDateTime(String input) throws RalphException {
        LocalDateTime dt = tryParseDateTime(input);
        if (dt == null) {
            String in = input == null ? "" : input.trim();
            throw new RalphException(
                "Could not parse date/time: '" + in + "'. Use yyyy-MM-dd or yyyy-MM-dd HH:mm "
                    + "(e.g. 2019-10-15 or 2019-10-15 18:00). Check the date is valid (e.g., Feb 30 is invalid)."
            );
        }
        return dt;
    }

    private static LocalDateTime tryParseDateTime(String input) {
        return Storage.tryParseDateTimeForReuse(input);
    }

    /**
     * Simple DTO representing a parsed user line: the command token and its remaining args.
     */
    public record Parsed(String command, String args) {
        /**
         * Creates a Parsed value.
         *
         * @param command the command token (lowercased)
         * @param args    the remaining arguments (may be empty)
         */
        public Parsed {
        }
    }
}
