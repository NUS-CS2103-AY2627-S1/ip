package pixel.command;

/**
 * Represents the commands understood by Pixel.
 */
public enum CommandType {
    /** Ends the current session. */
    BYE,
    /** Lists all tasks. */
    LIST,
    /** Finds tasks occurring on a date. */
    DATE,
    /** Marks a task as done. */
    MARK,
    /** Marks a task as not done. */
    UNMARK,
    /** Adds a ToDo task. */
    TODO,
    /** Adds a Deadline task. */
    DEADLINE,
    /** Adds an Event task. */
    EVENT,
    /** Deletes a task. */
    DELETE,
    /** Updates one detail of an existing task. */
    UPDATE,
    /** Represents unrecognized input. */
    UNKNOWN,
    /** Finds a task. */
    FIND
}
