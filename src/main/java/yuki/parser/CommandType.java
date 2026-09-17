package yuki.parser;

/**
 * Represents a command supported by Yuki.
 */
public enum CommandType {
    TODO("todo"),
    DEADLINE("deadline"),
    EVENT("event"),
    MARK("mark"),
    UNMARK("unmark"),
    DELETE("delete"),
    FIND("find"),
    REMINDERS("reminders"),
    LIST("list"),
    BYE("bye");

    /** The word the user enters to select this command. */
    private final String keyword;

    /**
     * Creates a command type with its user-facing keyword.
     *
     * @param keyword The word used to enter this command.
     */
    CommandType(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Returns the word used to enter this command.
     *
     * @return This command's keyword.
     */
    public String getKeyword() {
        return keyword;
    }

}
