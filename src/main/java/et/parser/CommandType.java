package et.parser;

/**
 * Represents a command ET can recognize from the first word of user input.
 */
public enum CommandType {
    TODO("todo"),
    DEADLINE("deadline"),
    EVENT("event"),
    LIST("list"),
    SORT("sort"),
    FIND("find"),
    MARK("mark"),
    UNMARK("unmark"),
    DELETE("delete"),
    BYE("bye"),
    UNKNOWN("");

    /** The word used to invoke this command. */
    private final String keyword;

    /**
     * Creates a command type with its input keyword.
     *
     * @param keyword the first word that identifies this command
     */
    CommandType(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Returns the word that invokes this command.
     *
     * @return the command keyword
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * Identifies the command from the first word of an input line.
     *
     * @param input the trimmed command line entered by the user
     * @return the matching command type, or {@link #UNKNOWN} when there is no match
     */
    public static CommandType fromInput(String input) {
        String firstWord = input.split("\\s+", 2)[0];
        for (CommandType commandType : values()) {
            if (commandType.keyword.equals(firstWord)) {
                return commandType;
            }
        }
        return UNKNOWN;
    }
}
