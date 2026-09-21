package janet.logic;

import java.util.Locale;

/**
 * The commands Janet understands at the start of user input.
 */
public enum CommandName {
    LIST,
    TODO,
    EVENT,
    DEADLINE,
    DELETE,
    MARK,
    UNMARK,
    FIND,
    BYE,
    UNKNOWN;

    /**
     * Parses the command name from a complete user input.
     *
     * @param command the complete user input.
     * @return the matching command name, or {@link #UNKNOWN}
     */
    public static CommandName from(String command) {
        if (command.equals("list")) {
            return LIST;
        }
        if (command.equals("bye")) {
            return BYE;
        }

        int firstWhitespace = -1;
        for (int index = 0; index < command.length(); index++) {
            if (Character.isWhitespace(command.charAt(index))) {
                firstWhitespace = index;
                break;
            }
        }
        String name = firstWhitespace < 0 ? command : command.substring(0, firstWhitespace);
        try {
            return CommandName.valueOf(name.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            return UNKNOWN;
        }
    }
}
