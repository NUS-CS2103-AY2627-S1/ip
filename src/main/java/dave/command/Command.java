package dave.command;

/**
 * Represents the set of commands recognized by the chatbot.
 */
public enum Command {

    /** Command to terminate the chatbot session. */
    BYE,
    /** Command to list all tracked tasks. */
    LIST,
    /** Command to search for tasks by keyword. */
    FIND,
    /** Command to mark a task as completed. */
    MARK,
    /** Command to mark a task as uncompleted. */
    UNMARK,
    /** Command to add a todo task. */
    TODO,
    /** Command to add a deadline task. */
    DEADLINE,
    /** Command to add an event task. */
    EVENT,
    /** Command to delete a task from the list. */
    DELETE,
    /** Command to snooze or reschedule a task. */
    SNOOZE,
    /** Unrecognized command. */
    UNKNOWN;

    /**
     * Converts a string representation of a command into the corresponding Command enum constant.
     *
     * @param text String representation of the command word.
     * @return Corresponding Command constant, or UNKNOWN if not matched.
     */
    public static Command from(String text) {
        if (text == null) {
            return UNKNOWN;
        }

        try {
            return Command.valueOf(text.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }
}
