package choo.parser;

/**
 * Identifies the action represented by a parsed user command.
 */
public enum CommandType {
    /** Ends the current CHOO session. */
    BYE,
    /** Displays every task. */
    LIST,
    /** Sorts deadlines chronologically. */
    SORT,
    /** Marks a task as completed. */
    MARK,
    /** Marks a task as incomplete. */
    UNMARK,
    /** Removes a task. */
    DELETE,
    /** Finds tasks containing a keyword. */
    FIND,
    /** Adds a new task. */
    ADD
}
