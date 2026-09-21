package sheppy;

/** The commands understood by Sheppy. */
public enum CommandType {
    /** Exit the application. */
    BYE,

    /** Display all tasks. */
    LIST,

    /** Find tasks whose descriptions contain a keyword. */
    FIND,

    /** Sort tasks alphabetically by description. */
    SORT,

    /** Add a todo task. */
    TODO,

    /** Add a deadline task. */
    DEADLINE,

    /** Add an event task. */
    EVENT,

    /** Mark a task as done. */
    MARK,

    /** Mark a task as not done. */
    UNMARK,

    /** Delete a task. */
    DELETE,

    /** An unrecognized command. */
    UNKNOWN;

    /**
     * Identifies commands that need a successful save before they take effect.
     *
     * @return whether the command changes the task list
     */
    public boolean changesTasks() {
        return switch (this) {
            case TODO, DEADLINE, EVENT, MARK, UNMARK, DELETE, SORT -> true;
            default -> false;
        };
    }
}
