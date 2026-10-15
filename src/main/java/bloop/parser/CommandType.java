package bloop.parser;

/**
 * Represents the supported commands and their input keywords.
 */
public enum CommandType {
    TODO("todo"),
    DEADLINE("deadline"),
    EVENT("event"),
    LIST("list"),
    MARK("mark"),
    UNMARK("unmark"),
    DELETE("delete"),
    FIND("find"),
    BYE("bye"),
    UNKNOWN("");

    private final String keyword;

    CommandType(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Returns the text keyword that identifies this command.
     *
     * @return command keyword
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * Identifies the command represented by the user's full input.
     *
     * @param input complete user input
     * @return matching command type, or {@link #UNKNOWN} when no command matches
     */
    public static CommandType fromInput(String input) {
        for (CommandType commandType : values()) {
            if (!commandType.keyword.isEmpty()
                    && (input.equals(commandType.keyword) || input.startsWith(commandType.keyword + " "))) {
                return commandType;
            }
        }
        return UNKNOWN;
    }
}
