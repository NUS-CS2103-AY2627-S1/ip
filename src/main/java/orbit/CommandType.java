package orbit;

/**
 * Commands understood by Orbit.
 */
public enum CommandType {
    BYE,
    LIST,
    MARK,
    UNMARK,
    TODO,
    DEADLINE,
    EVENT,
    DELETE,
    FIND,
    UNKNOWN;

    /**
     * Identifies the command word at the start of a user input.
     *
     * @param input Complete user input.
     * @return Matching command type, or {@link #UNKNOWN}.
     */
    public static CommandType from(String input) {
        String commandWord = input.strip().split("\\s+", 2)[0];
        for (CommandType command : values()) {
            if (command.name().equalsIgnoreCase(commandWord)) {
                return command;
            }
        }
        return UNKNOWN;
    }
}
