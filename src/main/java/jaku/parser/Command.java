package jaku.parser;

/**
 * Identifies a command that Jaku can carry out.
 */
public enum Command {
    /** Ends the current Jaku session. */
    BYE("bye"),
    /** Shows every saved task. */
    LIST("list"),
    /** Marks one task as complete. */
    MARK("mark"),
    /** Restores one task to incomplete. */
    UNMARK("unmark"),
    /** Adds an undated task. */
    TODO("todo"),
    /** Adds a task with a due date. */
    DEADLINE("deadline"),
    /** Adds an event with start and end text. */
    EVENT("event"),
    /** Removes one task. */
    DELETE("delete"),
    /** Finds tasks by description. */
    FIND("find"),
    /** Adds a daily or weekly task. */
    REPEAT("repeat"),
    /** Represents input that is not a supported command. */
    UNKNOWN("");

    /** Text entered by the user to invoke this command. */
    private final String keyword;

    /**
     * Creates a command with its user-facing keyword.
     *
     * @param keyword text used to invoke this command
     */
    Command(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Returns the text used to invoke this command.
     *
     * @return the command keyword
     */
    public String getKeyword() {
        return keyword;
    }
}
