package windy.command;

import java.util.Locale;

/**
 * Represents a command supported by the Windy chatbot.
 */
public enum CommandType {
    /** Exits the application. */
    BYE,
    /** Displays all saved tasks. */
    LIST,
    /** Marks a task as completed. */
    MARK,
    /** Marks a task as not completed. */
    UNMARK,
    /** Removes a task. */
    DELETE,
    /** Creates a task without a date. */
    TODO,
    /** Creates a task with a due date. */
    DEADLINE,
    /** Creates a task that spans a date range. */
    EVENT,
    /** Finds incomplete tasks relevant on a date. */
    DATE,
    /** Finds tasks whose descriptions contain a keyword. */
    FIND,
    /** Reverts the latest task change. */
    UNDO,
    /** Restores the reverted task change. */
    REDO,
    /** Represents an unsupported command word. */
    UNKNOWN;

    /**
     * Converts the first word of a user's input into a command type.
     *
     * @param commandWord the first word entered by the user.
     * @return the corresponding command type, or {@link #UNKNOWN} if it is unsupported.
     */
    public static CommandType from(String commandWord) {
        try {
            return CommandType.valueOf(commandWord.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            return UNKNOWN;
        }
    }
}
