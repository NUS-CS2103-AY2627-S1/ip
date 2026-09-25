package goat;

/**
 * Identifies the supported command words entered by the user.
 */
public enum CommandType {
    /** Exit command. */
    BYE("bye"),
    /** Delete-task command. */
    DELETE("delete"),
    /** Add-deadline command. */
    DEADLINE("deadline"),
    /** Add-event command. */
    EVENT("event"),
    /** Find-tasks command. */
    FIND("find"),
    /** List-tasks command. */
    LIST("list"),
    /** Mark-task command. */
    MARK("mark"),
    /** Sort-tasks command. */
    SORT("sort"),
    /** Add-todo command. */
    TODO("todo"),
    /** Unmark-task command. */
    UNMARK("unmark"),
    /** Unrecognized command. */
    UNKNOWN("");

    private final String commandWord;

    CommandType(String commandWord) {
        this.commandWord = commandWord;
    }

    /**
     * Finds the command type represented by a command word.
     *
     * @param commandWord first word of the user's input
     * @return matching command type, or {@link #UNKNOWN} if unsupported
     */
    public static CommandType from(String commandWord) {
        for (CommandType type : values()) {
            if (type.commandWord.equals(commandWord)) {
                return type;
            }
        }
        return UNKNOWN;
    }
}
