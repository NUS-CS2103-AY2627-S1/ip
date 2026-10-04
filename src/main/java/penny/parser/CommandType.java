package penny.parser;

/**
 * Enumeration representing valid command keywords supported by Penny.
 */
public enum CommandType {
    TODO,
    DEADLINE,
    EVENT,
    LIST,
    MARK,
    UNMARK,
    DELETE,
    ON,
    FIND,
    BYE,
    UNKNOWN;

    /**
     * Parses a string input into its corresponding CommandType.
     *
     * @param command The string keyword to match.
     * @return The matched CommandType, or UNKNOWN if not found.
     */
    public static CommandType fromString(String command) {
        if (command == null || command.trim().isEmpty()) {
            return UNKNOWN;
        }
        try {
            return CommandType.valueOf(command.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }
}
