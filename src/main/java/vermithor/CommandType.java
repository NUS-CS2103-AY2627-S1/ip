package vermithor;

/**
 * Lists the commands Vermithor understands.
 */
public enum CommandType {
    LIST,
    MARK,
    UNMARK,
    TODO,
    DEADLINE,
    EVENT,
    DELETE,
    FIND,
    SORT,
    UNKNOWN;

    /**
     * Converts a command word into its matching command type.
     *
     * @param commandWord the first word entered by the user
     * @return the matching command type, or {@code UNKNOWN} when it is unsupported
     */
    public static CommandType fromCommandWord(String commandWord) {
        try {
            return CommandType.valueOf(commandWord.toUpperCase());
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }
}
