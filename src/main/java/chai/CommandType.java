package chai;

/** The kinds of commands Chai understands. */
public enum CommandType {
    /** Exits Chai. */
    BYE,
    /** Displays all tasks. */
    LIST,
    /** Marks a task as completed. */
    MARK,
    /** Marks a task as incomplete. */
    UNMARK,
    /** Adds a todo. */
    TODO,
    /** Adds a deadline. */
    DEADLINE,
    /** Adds an event. */
    EVENT,
    /** Deletes a task. */
    DELETE,
    /** Finds tasks containing a keyword. */
    FIND,
    /** Sorts dated tasks chronologically and places todos afterward. */
    SORT,
    /** Represents a command Chai does not recognize. */
    UNKNOWN;

    /**
     * Maps the first word of user input to a command type.
     *
     * @param keyword First word of the user command.
     * @return Matching command type, or {@link #UNKNOWN} when no command matches.
     */
    public static CommandType fromKeyword(String keyword) {
        try {
            return CommandType.valueOf(keyword.toUpperCase());
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }
}
