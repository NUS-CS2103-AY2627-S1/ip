package bob;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/** Converts the first word of a user command into a command type. */
public class Parser {
    private static final List<DateTimeFormatter> INPUT_FORMATS = List.of(
            DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm"),
            DateTimeFormatter.ofPattern("d/M/uuuu HHmm")
    );

    /**
     * Determines which command the user entered from its first word.
     *
     * @param command Complete command entered by the user.
     * @return Command type represented by the command keyword.
     */
    public CommandType parse(String command) {
        if (command == null || command.trim().isEmpty()) {
            return CommandType.UNKNOWN;
        }
        String keyword = command.trim().split("\\s+", 2)[0];

        switch (keyword) {
            case "bye":
                return CommandType.BYE;
            case "list":
                return CommandType.LIST;
            case "find":
                return CommandType.FIND;
            case "edit":
                return CommandType.EDIT;
            case "delete":
                return CommandType.DELETE;
            case "mark":
                return CommandType.MARK;
            case "unmark":
                return CommandType.UNMARK;
            case "todo":
                return CommandType.TODO;
            case "deadline":
                return CommandType.DEADLINE;
            case "event":
                return CommandType.EVENT;
            default:
                return CommandType.UNKNOWN;
        }
    }

    /**
     * Parses a supported date-time value supplied as part of a command.
     *
     * @param input Date and time entered by the user.
     * @return Parsed date and time.
     * @throws BobException If the input has an unsupported or invalid format.
     */
    public static LocalDateTime parseDateTime(String input) throws BobException {
        if (input == null || input.trim().isEmpty()) {
            throw new BobException("Please enter a date and time.");
        }
        for (DateTimeFormatter formatter : INPUT_FORMATS) {
            try {
                return LocalDateTime.parse(input.trim(), formatter);
            } catch (DateTimeParseException ignored) {
                // Try the next supported format.
            }
        }

        throw new BobException(
                "Please enter the date as yyyy-MM-dd HHmm, "
                        + "for example 2019-12-02 1800.");
    }
}
