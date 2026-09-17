package della.command;

/**
 * Represents a command recognised by Della.
 */
public enum Command {
    /** Ends the application. */
    BYE,
    /** Lists all tasks. */
    LIST,
    /** Marks a task as complete. */
    MARK,
    /** Marks a task as incomplete. */
    UNMARK,
    /** Adds a todo task. */
    TODO,
    /** Adds a deadline task. */
    DEADLINE,
    /** Adds an event task. */
    EVENT,
    /** Deletes a task. */
    DELETE,
    /** Finds a task. */
    FIND,
    /** Represents an unrecognised command. */
    UNKNOWN
}
