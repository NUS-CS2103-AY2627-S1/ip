package cookie.command;

/** Represents the valid commands for the Cookie application. */
public enum Command {
    /** Terminates the Cookie application. */
    BYE,
    /** Displays all stored tasks. */
    LIST,
    /** Marks a task as done. */
    MARK,
    /** Marks a task as not done. */
    UNMARK,
    /** Deletes a task. */
    DELETE,
    /** Displays tasks occurring on a date. */
    ON,
    /** Finds tasks whose descriptions contain a specified keyword. */
    FIND,
    /** Displays tasks in a temporary sorted view. */
    SORT,
    /** Creates a todo task. */
    TODO,
    /** Creates a deadline task. */
    DEADLINE,
    /** Creates an event task. */
    EVENT;

    /**
     * Converts a string action into a {@code Command} enum.
     *
     * @param action The command word to convert.
     * @return The matching command.
     * @throws CookieException If the command is unrecognized.
     */
    public static Command fromString(String action) throws CookieException {
        try {
            return Command.valueOf(action.toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new CookieException("What is that command!?");
        }
    }
}
