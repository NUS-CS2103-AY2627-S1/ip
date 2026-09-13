package alfred.parser;

/**
 * Defines the command keywords Alfred recognizes in user input.
 */
public enum CommandType {
    BYE("bye"),
    LIST("list"),
    FIND("find"),
    SORT("sort"),
    MARK("mark"),
    UNMARK("unmark"),
    DELETE("delete"),
    TODO("todo"),
    DEADLINE("deadline"),
    EVENT("event"),
    UNKNOWN("");

    private final String keyword;

    /**
     * Creates a command type with the keyword users enter in the console.
     *
     * @param keyword command word recognized by Alfred
     */
    CommandType(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Finds the command type at the beginning of a user input line.
     *
     * @param input full line entered by the user
     * @return the matching command type, or {@link #UNKNOWN} when no command type matches
     */
    public static CommandType fromInput(String input) {
        for (CommandType commandType : values()) {
            if (commandType == UNKNOWN) {
                continue;
            }
            if (input.equals(commandType.keyword) || input.startsWith(commandType.keyword + " ")) {
                return commandType;
            }
        }
        return UNKNOWN;
    }

    /**
     * Returns the command word used in user input and error messages.
     *
     * @return the command keyword
     */
    public String getKeyword() {
        return keyword;
    }
}
