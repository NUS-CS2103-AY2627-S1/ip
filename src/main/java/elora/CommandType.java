package elora;

/**
 * Represents the type of command entered by the user, as determined by
 * {@link Parser#parseCommandType(String)}.
 */
public enum CommandType {
    /** Exits the application. */
    BYE,
    /** Lists all tasks currently stored. */
    LIST,
    /** Marks a task as done. */
    MARK,
    /** Marks a task as not done. */
    UNMARK,
    /** Deletes a task from the list. */
    DELETE,
    /** Adds a todo task. */
    TODO,
    /** Adds a deadline task. */
    DEADLINE,
    /** Adds an event task. */
    EVENT,
    /** Lists tasks occurring on a specific date. */
    ON,
    /** Finds tasks whose description contains a keyword. */
    FIND,
    /** Sorts tasks by date, soonest-due first. */
    SORT,
    /** Represents any command word that isn't recognized. */
    UNKNOWN
}
