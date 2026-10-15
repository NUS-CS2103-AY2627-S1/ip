package cheecken;

/**
 * Commands understood by the Cheecken command-line interface.
 */
public enum CommandType {
    BYE("bye"),
    LIST("list"),
    MARK("mark"),
    UNMARK("unmark"),
    DEADLINE("deadline"),
    EVENT("event"),
    TODO("todo"),
    DELETE("delete"),
    FIND("find");

    private final String keyword;

    /**
     * Creates a command with its recognized keyword.
     * @param keyword command keyword recognized by this enum value
     */
    CommandType(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Returns whether the input starts with this command keyword.
     * Commands without arguments require an exact match.
     * @param input command input to process
     * @return true if the input matches the command keyword or its argument prefix
     */
    public boolean matches(String input) {
        return input.equals(keyword) || input.startsWith(keyword + " ");
    }
}
