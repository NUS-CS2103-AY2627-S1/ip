package meowmeow.parser;

/**
 * Represents the set of commands the chatbot understands.
 * Any input that does not match a known command resolves to {@code UNKNOWN}.
 */
public enum CommandType {
    BYE,
    LIST,
    TODO,
    DEADLINE,
    EVENT,
    MARK,
    UNMARK,
    DELETE,
    ON,
    FIND,
    UNKNOWN;

    /**
     * Returns the command matching the given input word, ignoring case.
     *
     * @param input Word typed by the user.
     * @return Matching command, or {@code UNKNOWN} if none matches.
     */
    public static CommandType fromString(String input) {
        try {
            return CommandType.valueOf(input.toUpperCase());
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }
}
