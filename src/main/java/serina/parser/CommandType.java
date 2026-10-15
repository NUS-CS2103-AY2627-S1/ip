package serina.parser;

/**
 * Represents the commands understood by Serina.
 */
public enum CommandType {
    /** Shows command help. */
    HELP,
    /** Shows every task. */
    LIST,
    /** Marks a task as complete. */
    MARK,
    /** Marks a task as incomplete. */
    UNMARK,
    /** Deletes a task. */
    DELETE,
    /** Finds tasks. */
    FIND,
    /** Adds a todo. */
    TODO,
    /** Adds a deadline. */
    DEADLINE,
    /** Adds an event. */
    EVENT,
    /** Exits Serina. */
    BYE
}
