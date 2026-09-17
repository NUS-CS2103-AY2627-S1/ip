package kia.command;

/**
 * Command categories understood by Kia.
 */
public enum CommandType {
    /** Adds a to-do task. */
    TODO,
    /** Adds a deadline task. */
    DEADLINE,
    /** Adds an event task. */
    EVENT,
    /** Lists all tasks. */
    LIST,
    /** Marks a task as done. */
    MARK,
    /** Marks a task as not done. */
    UNMARK,
    /** Deletes a task. */
    DELETE,
    /** Searches task descriptions for a keyword. */
    FIND,
    /** Updates one or more details of an existing task. */
    UPDATE,
    /** Exits the chatbot. */
    BYE,
    /** Represents an unrecognised command. */
    UNKNOWN
}
