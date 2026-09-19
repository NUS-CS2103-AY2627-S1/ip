package daddy.parser;

/**
 * Identifies the commands that Daddy understands.
 */
public enum CommandType {
    /** Ends the application. */
    BYE,
    /** Displays every task. */
    LIST,
    /** Displays dated tasks that occur on one date. */
    LIST_ON,
    /** Creates a todo task. */
    TODO,
    /** Creates a deadline task. */
    DEADLINE,
    /** Creates an event task. */
    EVENT,
    /** Marks one task as done. */
    MARK,
    /** Marks one task as not done. */
    UNMARK,
    /** Removes one task. */
    DELETE,
    /** Displays tasks whose descriptions contain a keyword. */
    FIND,
    /** Finds the earliest working-hours slot of a requested duration. */
    FREE,
    /** Represents a mark command without a task number. */
    MARK_MISSING_ARGUMENT,
    /** Represents an unmark command without a task number. */
    UNMARK_MISSING_ARGUMENT,
    /** Represents a delete command without a task number. */
    DELETE_MISSING_ARGUMENT,
    /** Represents a find command without a keyword. */
    FIND_MISSING_ARGUMENT,
    /** Represents unrecognized input. */
    UNKNOWN
}
