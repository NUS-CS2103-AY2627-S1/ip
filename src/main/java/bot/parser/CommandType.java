package bot.parser;

/**
 * Identifies the operation requested by a user command.
 */
public enum CommandType {
    /** Displays all stored tasks. */
    LIST,
    /** Marks a task as done. */
    MARK,
    /** Marks a task as not done. */
    UNMARK,
    /** Removes a task. */
    DELETE,
    /** Adds a task without a date or time. */
    TODO,
    /** Adds a task with a due date. */
    DEADLINE,
    /** Adds a task with a start and end time. */
    EVENT,
    /** Finds tasks whose descriptions contain a keyword. */
    FIND,
    /** Displays dated tasks occurring on a specified date. */
    ON,
    /** Sorts dated tasks chronologically and places todos afterward. */
    SORT,
    /** Ends the chatbot session. */
    BYE,
    /** Represents an unrecognized command word. */
    UNKNOWN;

    /**
     * Converts a command word into its corresponding command type.
     *
     * @param command command word entered by the user.
     * @return matching command type, or {@link #UNKNOWN} if the word is not recognized
     */
    public static CommandType from(String command) {
        try {
            return CommandType.valueOf(command.toUpperCase());
        } catch (IllegalArgumentException error) {
            return CommandType.UNKNOWN;
        }
    }
}
