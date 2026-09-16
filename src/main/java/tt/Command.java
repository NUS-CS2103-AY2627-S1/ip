package tt;

import java.util.Locale;

/**
 * Represents the commands understood by the task manager.
 */
public enum Command {
    /** Exits the application. */
    BYE,
    /** Lists all tasks. */
    LIST,
    /** Finds tasks containing a keyword. */
    FIND,
    /** Sorts tasks alphabetically. */
    SORT,
    /** Marks a task as complete. */
    MARK,
    /** Marks a task as incomplete. */
    UNMARK,
    /** Deletes a task. */
    DELETE,
    /** Clears tasks of a selected type. */
    CLEAR,
    /** Adds a todo. */
    TODO,
    /** Adds a deadline. */
    DEADLINE,
    /** Adds an event. */
    EVENT,
    /** Represents an unsupported command word. */
    UNKNOWN;

    /**
     * Converts user input into a command, or {@link #UNKNOWN} if it is unsupported.
     *
     * @param commandWord command word entered by the user.
     * @return matching command, or {@link #UNKNOWN} when no command matches.
     */
    public static Command fromString(String commandWord) {
        if (commandWord == null) {
            return UNKNOWN;
        }
        try {
            return Command.valueOf(commandWord.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }
}
