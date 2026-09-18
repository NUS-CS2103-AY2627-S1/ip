package anby;

/**
 * Commands supported by Anby.
 */
public enum Command {
    /** Shows all tasks. */
    LIST,
    /** Marks a task as done. */
    MARK,
    /** Marks a task as not done. */
    UNMARK,
    /** Deletes a task. */
    DELETE,
    /** Exits Anby. */
    BYE,
    /** Adds a todo task. */
    TODO,
    /** Adds a deadline task. */
    DEADLINE,
    /** Adds an event task. */
    EVENT,
    /** Finds and shows tasks that contains the searched keyword. */
    FIND,
    /** Shows a random quote. */
    BURGER
}
