package habpyduck.parser;

/**
 * Represents the commands that HabpyDuck understands.
 */
public enum CommandType {
    LIST("list"),
    MARK("mark"),
    UNMARK("unmark"),
    DELETE("delete"),
    FIND("find"),
    FINDTAG("findtag"),
    TAG("tag"),
    UNTAG("untag"),
    TODO("todo"),
    DEADLINE("deadline"),
    EVENT("event"),
    BYE("bye"),
    UNKNOWN("");

    private final String commandWord;

    /**
     * Creates a command type with the exact word the user types.
     *
     * @param commandWord the first word of the command.
     */
    CommandType(String commandWord) {
        this.commandWord = commandWord;
    }

    /**
     * Returns the command type represented by a command word.
     *
     * @param commandWord the first word entered by the user.
     * @return the matching command type, or UNKNOWN if there is no match.
     */
    public static CommandType fromCommandWord(String commandWord) {
        for (CommandType commandType : values()) {
            if (commandType.commandWord.equals(commandWord)) {
                return commandType;
            }
        }
        return UNKNOWN;
    }
}
