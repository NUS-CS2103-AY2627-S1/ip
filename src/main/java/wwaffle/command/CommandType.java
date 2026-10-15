package wwaffle.command;

/**
 * Represents the supported categories of user commands.
 */
public enum CommandType {
    /** Displays all tasks. */
    LIST,
    /** Marks a task as complete. */
    MARK,
    /** Marks a task as incomplete. */
    UNMARK,
    /** Deletes a task. */
    DELETE,
    /** Adds a todo. */
    TODO,
    /** Adds a deadline. */
    DEADLINE,
    /** Adds an event. */
    EVENT,
    /** Finds tasks containing a keyword. */
    FIND,
    /** Sorts tasks by description or completion status. */
    SORT,
    /** Removes every task from the list. */
    CLEAR,
    /** Responds to a friendly greeting. */
    GREETING,
    /** Responds to appreciation. */
    THANKS,
    /** Exits the application. */
    BYE,
    /** Represents an unsupported command. */
    UNKNOWN
}
