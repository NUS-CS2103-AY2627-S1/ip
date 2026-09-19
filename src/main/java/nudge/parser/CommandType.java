package nudge.parser;

/**
 * Represents the command types recognized by Nudge.
 */
public enum CommandType {
    /** Exits the application. */
    BYE,

    /** Displays all tasks. */
    LIST,

    /** Finds tasks whose descriptions contain a keyword. */
    FIND,

    /** Sorts deadlines chronologically. */
    SORT,

    /** Marks a task as done. */
    MARK,

    /** Marks a task as not done. */
    UNMARK,

    /** Deletes a task. */
    DELETE,

    /** Adds a todo task. */
    TODO,

    /** Adds a deadline task. */
    DEADLINE,

    /** Adds an event task. */
    EVENT,

    /** Represents a command that Nudge does not recognize. */
    UNKNOWN
}
