package coco;

/**
 * The type of action a user command represents, identified by its first word.
 */
public enum Command {
    LIST,
    MARK,
    UNMARK,
    DELETE,
    TODO,
    DEADLINE,
    EVENT,
    FIND,
    COMMANDS,
    BYE,
    UNKNOWN;

    /**
     * Returns the Command whose name matches the first word of the given
     * input.
     *
     * @param input Raw user input line.
     * @return The matching Command, or UNKNOWN if the first word doesn't
     *         match any known command.
     */
    public static Command fromInput(String input) {
        String keyword = input.split(" ", 2)[0];
        for (Command command : values()) {
            if (command != UNKNOWN && command.name().toLowerCase().equals(keyword)) {
                return command;
            }
        }
        return UNKNOWN;
    }
}
