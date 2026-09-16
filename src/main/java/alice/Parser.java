package alice;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Parses user input commands and extracts relevant information such as
 * command type, description, dates, and indices.
 */
public class Parser {

    /**
     * Parses the full command string into a Command enum.
     *
     * @param fullCommand The raw user input.
     * @return The corresponding Command enum, or {@link Command#UNKNOWN} if the command is invalid.
     */
    public static Command parseCommand(String fullCommand) {
        if (fullCommand == null || fullCommand.trim().isEmpty()) {
            return Command.UNKNOWN;
        }
        String[] parts = fullCommand.trim().split(" ");
        return Command.parse(parts[0]);
    }

    /**
     * Extracts the description from a command string, removing the command prefix.
     *
     * @param fullCommand    The raw user input.
     * @param commandPrefix The prefix to remove (e.g., "todo").
     * @return The trimmed description, or an empty string if the prefix is not found or description is empty.
     */
    public static String extractDescription(String fullCommand, String commandPrefix) {
        String trimmed = fullCommand.trim();
        if (!trimmed.startsWith(commandPrefix)) {
            return "";
        }
        String description = trimmed.substring(commandPrefix.length()).trim();
        return description.isEmpty() ? "" : description;
    }

    /**
     * Parses a deadline command to extract the description and the date string.
     *
     * @param fullCommand The raw input string starting with "deadline ".
     * @return A String array where index 0 is the description and index 1 is the date string,
     *         or {@code null} if the format is invalid.
     */
    public static String[] parseDeadline(String fullCommand) {
        String trimmed = fullCommand.trim();
        if (!trimmed.startsWith("deadline ")) {
            return null;
        }
        String remaining = trimmed.substring("deadline ".length());
        if (!remaining.contains(" /by ")) {
            return null;
        }
        String[] parts = remaining.split(" /by ", -1);
        if (parts.length != 2 || parts[0].trim().isEmpty() || parts[1].trim().isEmpty()) {
            return null;
        }
        return new String[]{parts[0].trim(), parts[1].trim()};
    }

    /**
     * Parses an event command to extract the description, start date, and end date.
     *
     * @param fullCommand The raw input string starting with "event ".
     * @return A String array where indices are [description, fromDate, toDate],
     *         or {@code null} if the format is invalid.
     */
    public static String[] parseEvent(String fullCommand) {
        String trimmed = fullCommand.trim();
        if (!trimmed.startsWith("event ")) {
            return null;
        }
        String remaining = trimmed.substring("event ".length());
        if (!remaining.contains(" /from ") || !remaining.contains(" /to ")) {
            return null;
        }
        String[] fromParts = remaining.split(" /from ", -1);
        if (fromParts.length != 2 || fromParts[0].trim().isEmpty()) {
            return null;
        }
        String[] toParts = fromParts[1].split(" /to ", -1);
        if (toParts.length != 2 || toParts[0].trim().isEmpty() || toParts[1].trim().isEmpty()) {
            return null;
        }
        return new String[]{fromParts[0].trim(), toParts[0].trim(), toParts[1].trim()};
    }

    /**
     * Parses a task index from the command arguments.
     *
     * @param parts The split command array (e.g., ["mark", "2"]).
     * @return The 0-based index of the task, or {@code -1} if the index is missing, negative, or invalid.
     */
    public static int parseIndex(String[] parts) {
        if (parts.length != 2) {
            return -1;
        }
        try {
            int index = Integer.parseInt(parts[1]) - 1;
            if (index < 0) {
                return -1;
            }
            return index;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Parses a date string into a {@link LocalDate}.
     *
     * @param dateString Date string to parse.
     * @return Parsed date, or {@code null} if the date is invalid.
     */
    public static LocalDate parseDate(String dateString) {
        try {
            return LocalDate.parse(dateString.trim());
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
