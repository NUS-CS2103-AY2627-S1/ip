package alice;

/**
 * Represents all possible commands that the user can input.
 * Provides utility methods to parse strings into Command enums
 * and to check the nature of the command (e.g., if it adds a task or requires an index).
 */
public enum Command {
    BYE,
    LIST,
    MARK,
    UNMARK,
    DELETE,
    TODO,
    DEADLINE,
    EVENT,
    UNKNOWN,
    FIND,
    VIEW,
    UNDO;

    /**
     * Parses a string input into a Command enum.
     *
     * @param input The user's raw command string.
     * @return The corresponding Command enum, or {@link #UNKNOWN} if the command is not recognized.
     */
    public static Command parse(String input) {
        if (input == null || input.trim().isEmpty()) {
            return UNKNOWN;
        }
        String lowerInput = input.trim().toLowerCase().split(" ")[0];
        switch (lowerInput) {
            case "bye":
                return BYE;
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
            case "view":
                return VIEW;
            case "find":
                return FIND;
            case "undo":
                return UNDO;
            default:
                return UNKNOWN;
        }
    }

    /**
     * Checks if this command is used to create a new task.
     *
     * @return true if the command is TODO, DEADLINE, or EVENT.
     */
    public boolean isAddCommand() {
        return this == TODO || this == DEADLINE || this == EVENT;
    }

    /**
     * Checks if this command requires a valid task index (e.g., for mark, unmark, or delete).
     *
     * @return true if the command is MARK, UNMARK, or DELETE.
     */
    public boolean requiresIndex() {
        return this == MARK || this == UNMARK || this == DELETE;
    }
}
