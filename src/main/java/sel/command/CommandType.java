package sel.command;

/**
 * Represents the type of command a user has entered.
 * Used by {@link sel.parser.Parser} to classify raw input before dispatching
 * it to the appropriate handler in {@code Sel}.
 */
public enum CommandType {
    BYE,
    /** Lists all tasks currently in the task list. */
    LIST,
    /** Marks a task as done. */
    MARK,
    /** Marks a task as not done. */
    UNMARK,
    /** Deletes a task from the task list. */
    DELETE,
    /** Adds a new todo task. */
    TODO,
    /** Adds a new deadline task. */
    DEADLINE,
    /** Adds a new event task. */
    EVENT,
    /** Find */
    FIND,
    /** Represents any input that does not match a known command. */
    UNKNOWN
}
