package jasper.command;

/**
 * Represents the various types of commands supported by the application.
 */
public enum CommandType {
    /** Represents a command to add a deadline task */
    DEADLINE,
    /** Represents a command to delete a task */
    DELETE,
    /** Represents an invalid or erroneous command */
    ERROR,
    /** Represents a command to add an event task */
    EVENT,
    /** Represents a command to find tasks */
    FIND,
    /** Represents a command to list all tasks */
    LIST,
    /** Represents a command to mark a task as completed */
    MARK,
    /** Represents a command to exit the application */
    QUIT,
    /** Represents a command to add a todo task */
    TODO,
    /** Represents a command to mark a task as not completed */
    UNMARK
}
