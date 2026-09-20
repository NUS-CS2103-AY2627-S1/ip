package harold.command;

/**
 * Identifies the commands supported by Harold.
 */
public enum CommandType {
    /** Terminates the application. */
    BYE,
    /** Displays all tasks. */
    LIST,
    /** Marks a task as complete. */
    MARK,
    /** Marks a task as incomplete. */
    UNMARK,
    /** Removes a task. */
    DELETE,
    /** Adds a todo. */
    TODO,
    /** Adds a deadline. */
    DEADLINE,
    /** Adds an event. */
    EVENT,
    /** Finds tasks whose descriptions contain a keyword. */
    FIND,
    /** Represents an unsupported command word. */
    UNKNOWN;

    /**
     * Returns the command type represented by the first word of a command.
     *
     * @param command Full command entered by the user.
     * @return Matching command type, or {@link #UNKNOWN} when no type matches.
     */
    public static CommandType fromCommand(String command) {
        String commandWord = command.split("\\s+", 2)[0];
        return switch (commandWord) {
            case "list" -> LIST;
            case "mark" -> MARK;
            case "unmark" -> UNMARK;
            case "delete" -> DELETE;
            case "todo" -> TODO;
            case "deadline" -> DEADLINE;
            case "event" -> EVENT;
            case "find" -> FIND;
            default -> UNKNOWN;
        };
    }
}
