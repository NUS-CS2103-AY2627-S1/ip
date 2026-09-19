package bro.command;

/**
 * Represents the valid command types supported by Bro.
 */
public enum Command {
    LIST,
    TASKS,
    FIND,
    MARK,
    UNMARK,
    DELETE,
    TODO,
    DEADLINE,
    EVENT,
    EDIT,
    BYE,
    UNKNOWN;

    /**
     * Converts a raw string command into its corresponding Command enum.
     *
     * @param text The command word entered by the user.
     * @return The corresponding Command enum constant, or UNKNOWN if the command
     *         word entered is invalid/null.
     */
    public static Command fromString(String text) {
        if (text == null || text.trim().isEmpty()) {
            return UNKNOWN;
        }
        try {
            return Command.valueOf(text.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }
}
