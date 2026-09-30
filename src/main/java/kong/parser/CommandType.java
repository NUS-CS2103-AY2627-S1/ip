package kong.parser;

/**
 * Identifies the command words understood by Kong.
 */
public enum CommandType {
    /** Adds a task without a date. */
    TODO,
    /** Adds a task with a due date. */
    DEADLINE,
    /** Adds a task spanning a date range. */
    EVENT,
    /** Lists all tasks. */
    LIST,
    /** Finds tasks containing a keyword. */
    FIND,
    /** Lists tasks occurring on a date. */
    ON,
    /** Marks a task as complete. */
    MARK,
    /** Marks a task as incomplete. */
    UNMARK,
    /** Removes a task. */
    DELETE,
    /** Ends the application. */
    BYE,
    /** Represents an unrecognized command word. */
    UNKNOWN;

    /**
     * Resolves a command word without regard to letter case.
     *
     * @param command command word to resolve
     * @return matching command type, or {@link #UNKNOWN} if none matches
     */
    public static CommandType fromString(String command) {
        try {
            return CommandType.valueOf(command.toUpperCase());
        } catch (IllegalArgumentException e) {
            return CommandType.UNKNOWN;
        }
    }
}
