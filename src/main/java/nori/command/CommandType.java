package nori.command;

/**
 * Identifies the words that begin Nori commands.
 */
public enum CommandType {
    /** Adds a task with no date or time. */
    TODO("todo"),
    /** Adds a task due by a single date. */
    DEADLINE("deadline"),
    /** Adds a task that runs from a start to an end. */
    EVENT("event"),
    /** Shows every task, or only those in a date range. */
    LIST("list"),
    /** Shows the commands Nori understands. */
    HELP("help"),
    /** Shows the deadlines and events falling on one date. */
    ON("on"),
    /** Shows one date's tasks laid out as a schedule. */
    SCHEDULE("schedule"),
    /** Shows the tasks whose descriptions contain a keyword. */
    FIND("find"),
    /** Marks a task as done. */
    MARK("mark"),
    /** Marks a task as not done. */
    UNMARK("unmark"),
    /** Removes a task from the list. */
    DELETE("delete"),
    /** Ends the session. */
    BYE("bye");

    /** The word the user types to select this command. */
    private final String keyword;

    /**
     * Creates a command type with its input keyword.
     *
     * @param keyword the command's input keyword.
     */
    CommandType(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Returns the keyword used to recognize this command.
     *
     * @return the command keyword.
     */
    public String getKeyword() {
        return keyword;
    }
}
