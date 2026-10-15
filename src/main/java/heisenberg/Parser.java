package heisenberg;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

/**
 * Parses a user command and validates its arguments.
 */
public class Parser {
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter
            .ofPattern("uuuu-MM-dd HHmm", Locale.ENGLISH)
            .withResolverStyle(ResolverStyle.STRICT);

    private final String[] parts;
    private final CommandType command;

    /**
     * Creates a parser for the given raw user input.
     *
     * @param input Raw line entered by the user.
     * @throws InvalidCommandException If the input is empty or does not start with a known command.
     */
    public Parser(String input) {
        String trimmedInput = input.trim();
        if (trimmedInput.isEmpty()) {
            throw new InvalidCommandException("Unknown command was given.");
        }

        parts = trimmedInput.split("\\s+");
        try {
            command = CommandType.valueOf(parts[0].toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidCommandException("Unknown command was given.");
        }
    }

    public CommandType getCommand() {
        return command;
    }

    /**
     * Checks that the command has no arguments.
     *
     * @throws InvalidFormatException If any arguments follow the command word.
     */
    public void requireNoArguments() {
        if (parts.length != 1) {
            throw new InvalidFormatException("Command is formatted incorrectly.");
        }
    }

    /**
     * Returns the task number given as the only argument of the command.
     *
     * @return One-based task number entered by the user.
     * @throws InvalidFormatException If the argument is missing, extra, or not an integer.
     */
    public int getTaskNumber() {
        if (parts.length != 2) {
            throw new InvalidFormatException("Command is formatted incorrectly.");
        }

        try {
            return Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            throw new InvalidFormatException("Command is formatted incorrectly.");
        }
    }

    /**
     * Returns the task description, which spans from after the command word
     * up to the first date marker if the command expects one.
     *
     * @return Description text of the task.
     * @throws InvalidFormatException If the description is empty.
     */
    public String getDescription() {
        int descriptionEndIndex = switch (command) {
            case DEADLINE -> findMarker("/by");
            case EVENT -> findMarker("/from");
            default -> parts.length;
        };

        if (descriptionEndIndex == -1) {
            descriptionEndIndex = parts.length;
        }
        return joinParts(1, descriptionEndIndex);
    }

    public String getKeyword() {
        if (parts.length != 2) {
            throw new InvalidFormatException("Command is formatted incorrectly.");
        }
        return parts[1];
    }

    /**
     * Parses the deadline after the {@code /by} marker.
     *
     * @return Deadline date and time entered by the user.
     * @throws InvalidFormatException If the marker is missing or the date and time are invalid.
     */
    public LocalDateTime getDeadlineDateTime() {
        int deadlineMarkerIndex = requireMarker("/by");
        return parseDateTime(joinParts(deadlineMarkerIndex + 1, parts.length));
    }

    public LocalDateTime getEventFromDateTime() {
        int startMarkerIndex = requireMarker("/from");
        int endMarkerIndex = requireMarker("/to");
        if (endMarkerIndex < startMarkerIndex) {
            throw new InvalidFormatException("Task is formatted incorrectly.");
        }
        return parseDateTime(joinParts(startMarkerIndex + 1, endMarkerIndex));
    }

    public LocalDateTime getEventToDateTime() {
        int startMarkerIndex = requireMarker("/from");
        int endMarkerIndex = requireMarker("/to");
        if (endMarkerIndex < startMarkerIndex) {
            throw new InvalidFormatException("Task is formatted incorrectly.");
        }
        return parseDateTime(joinParts(endMarkerIndex + 1, parts.length));
    }

    private int requireMarker(String marker) {
        int index = findMarker(marker);
        if (index == -1) {
            throw new InvalidFormatException("Task is formatted incorrectly.");
        }
        return index;
    }

    private int findMarker(String marker) {
        for (int i = 1; i < parts.length; i++) {
            if (parts[i].equals(marker)) {
                return i;
            }
        }
        return -1;
    }

    private String joinParts(int startIndex, int endIndex) {
        StringBuilder value = new StringBuilder();
        for (int i = startIndex; i < endIndex; i++) {
            value.append(parts[i]).append(" ");
        }

        String result = value.toString().trim();
        if (result.isEmpty()) {
            throw new InvalidFormatException("Task is formatted incorrectly.");
        }
        return result;
    }

    private LocalDateTime parseDateTime(String value) {
        try {
            return LocalDateTime.parse(value, DATE_TIME_FORMAT);
        } catch (DateTimeParseException e) {
            throw new InvalidFormatException("Date/time must use yyyy-MM-dd HHmm format.");
        }
    }
}
