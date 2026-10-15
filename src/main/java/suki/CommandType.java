package suki;

/**
 * Represents the type of command a user can enter.
 *
 * <p>{@link #UNKNOWN} stands for anything Suki does not recognise, so that
 * unrecognised input is handled as an ordinary case rather than by throwing
 * during parsing.
 */
public enum CommandType {
    /** Show every task in the list. */
    LIST,
    /** Mark a task as done. */
    MARK,
    /** Mark a task as not done. */
    UNMARK,
    /** Remove a task from the list. */
    DELETE,
    /** Add a task with no date attached. */
    TODO,
    /** Add a task due by a given date. */
    DEADLINE,
    /** Add a task spanning a start and end date. */
    EVENT,
    /** Search for tasks whose description contains some text. */
    FIND,
    /** Reorder the list so the earliest scheduled task comes first. */
    SORT,
    /** Anything Suki does not recognise. */
    UNKNOWN;

    /**
     * Returns the command matching the given word.
     *
     * @param commandWord the first word of a line typed by the user
     * @return the matching command type, or {@link #UNKNOWN} if there is none
     */
    public static CommandType fromWord(String commandWord) {
        switch (commandWord) {
        case "list":
            return LIST;
        case "mark":
            return MARK;
        case "unmark":
            return UNMARK;
        case "delete":
            return DELETE;
        case "todo":
            return TODO;
        case "deadline":
            return DEADLINE;
        case "event":
            return EVENT;
        case "find":
            return FIND;
        case "sort":
            return SORT;
        default:
            return UNKNOWN;
        }
    }
}
