package ollie;

/**
 * Represents the commands understood by Ollie.
 */
public enum CommandType {
    /** Shows the available commands. */
    HELP,
    /** Lists all stored tasks. */
    LIST,
    /** Finds tasks whose descriptions contain a keyword. */
    FIND,
    /** Creates a todo. */
    TODO,
    /** Creates a deadline. */
    DEADLINE,
    /** Creates an event. */
    EVENT,
    /** Marks a task as completed. */
    MARK,
    /** Marks a task as incomplete. */
    UNMARK,
    /** Deletes a task. */
    DELETE,
    /** Exits Ollie. */
    BYE,
    /** Represents an unrecognized command. */
    UNKNOWN
}
