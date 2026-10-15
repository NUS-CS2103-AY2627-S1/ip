package gnaix;

import java.util.Locale;

/**
 * Represents the commands supported by Gnaix.
 */
public enum Command {
    BYE,
    LIST,
    MARK,
    UNMARK,
    DELETE,
    TODO,
    DEADLINE,
    EVENT,
    DATE,
    FIND,
    TAG,
    UNKNOWN;

    /**
     * Converts a user input string into its corresponding command.
     *
     * @param input User input representing a command.
     * @return The matching command, or UNKNOWN if the input is invalid.
     */
    public static Command fromString(String input) {
        try {
            return Command.valueOf(input.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }
}
