package jassabot.parser;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import jassabot.exception.JassaBotException;
import jassabot.util.DateTimeFormats;

/**
 * Interprets user input as commands understood by JassaBot.
 */
public final class Parser {
    private Parser() {
    }

    /**
     * Determines the type of a user-entered command.
     *
     * <p>The matching rules preserve the application's existing behavior. For example,
     * {@code deadline} is recognised so JassaBot can report its missing description, while
     * {@code mark} without a task number is recognised so it can report an invalid number.</p>
     *
     * @param command Complete line entered by the user.
     * @return Matching command type, or {@link CommandType#UNKNOWN} if none matches.
     */
    public static CommandType parseCommandType(String command) {
        if (command.equals("help")) {
            return CommandType.HELP;
        } else if (command.equals("bye")) {
            return CommandType.BYE;
        } else if (command.equals("list")) {
            return CommandType.LIST;
        } else if (command.equals("mark") || command.startsWith("mark ")) {
            return CommandType.MARK;
        } else if (command.equals("unmark") || command.startsWith("unmark ")) {
            return CommandType.UNMARK;
        } else if (command.equals("delete") || command.startsWith("delete ")) {
            return CommandType.DELETE;
        } else if (command.equals("find") || command.startsWith("find ")) {
            return CommandType.FIND;
        } else if (command.equals("deadline") || command.startsWith("deadline ")) {
            return CommandType.DEADLINE;
        } else if (command.equals("event") || command.startsWith("event ")) {
            return CommandType.EVENT;
        } else if (command.equals("todo") || command.startsWith("todo ")) {
            return CommandType.TODO;
        }
        return CommandType.UNKNOWN;
    }

    /**
     * Parses a task date and optional time from a supported user-facing format.
     *
     * <p>Supported formats are {@code yyyy-MM-dd}, {@code yyyy-MM-dd HHmm},
     * {@code d/M/yyyy}, and {@code d/M/yyyy HHmm}. A date without a time is
     * represented as midnight.</p>
     *
     * @param value Date and optional time entered after a command marker.
     * @return Parsed date and time.
     * @throws JassaBotException If the value is not a real date in a supported format.
     */
    public static LocalDateTime parseDateTime(String value) throws JassaBotException {
        try {
            return DateTimeFormats.parseUserDateTime(value);
        } catch (DateTimeParseException e) {
            throw new JassaBotException(
                    "Please enter a valid date as yyyy-MM-dd or d/M/yyyy, "
                            + "optionally followed by a time in HHmm format.");
        }
    }
}
