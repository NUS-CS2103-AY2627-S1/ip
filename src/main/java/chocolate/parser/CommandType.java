package chocolate.parser;

/**
 * Identifies an action that Chocolate can perform.
 */
public enum CommandType {
    /** Exits the application. */
    BYE,
    /** Lists all tasks. */
    LIST,
    /** Marks a task as complete. */
    MARK,
    /** Marks a task as incomplete. */
    UNMARK,
    /** Adds a todo. */
    TODO,
    /** Adds a deadline. */
    DEADLINE,
    /** Adds an event. */
    EVENT,
    /** Deletes a task. */
    DELETE,
    /** Finds tasks whose descriptions contain a keyword. */
    FIND,
    /** Archives all active tasks. */
    ARCHIVE,
    /** Lists every archived task. */
    ARCHIVE_LIST
}
