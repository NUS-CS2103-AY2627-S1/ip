package bos;

/**
 * Represents a command that Bos can recognize.
 */
public enum CommandType {
    BYE("bye"),
    LIST("list"),
    FIND("find"),
    MARK("mark"),
    UNMARK("unmark"),
    DELETE("delete"),
    TODO("todo"),
    DEADLINE("deadline"),
    EVENT("event"),
    UNKNOWN("");

    private final String keyword;

    /**
     * Creates a command type associated with the keyword entered by users.
     *
     * @param keyword word that identifies the command.
     */
    CommandType(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Returns the word used to enter this command.
     *
     * @return command keyword.
     */
    public String getKeyword() {
        return keyword;
    }
}
