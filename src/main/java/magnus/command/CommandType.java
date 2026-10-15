package magnus.command;

/**
 * Identifies the commands supported by Magnus and their user-facing keywords.
 */
public enum CommandType {
    /** Exits the application. */
    BYE("bye", false),
    /** Displays all tasks. */
    LIST("list", false),
    /** Displays deadline tasks on a specified date. */
    LIST_DEADLINE("list_deadline", false),
    /** Displays event tasks within a specified date range. */
    LIST_EVENT("list_event", false),
    /** Displays tasks whose descriptions contain a specified query. */
    FIND("find", false),
    /** Marks a task as completed. */
    MARK("mark", true),
    /** Marks a task as incomplete. */
    UNMARK("unmark", true),
    /** Adds a to-do task. */
    TODO("todo", true),
    /** Adds a deadline task. */
    DEADLINE("deadline", true),
    /** Adds an event task. */
    EVENT("event", true),
    /** Starts a two-step update of an existing task. */
    UPDATE("update", false),
    /** Deletes a task. */
    DELETE("delete", true);

    private final String keyword;
    private final boolean canChangeTaskList;

    /**
     * Creates a command type with the keyword entered by users.
     *
     * @param keyword The command's user-facing keyword.
     * @param canChangeTaskList Whether executing the command can modify the task list.
     */
    CommandType(String keyword, boolean canChangeTaskList) {
        this.keyword = keyword;
        this.canChangeTaskList = canChangeTaskList;
    }

    /**
     * Returns whether this command type can modify the task list.
     *
     * @return {@code true} if the command can modify tasks; {@code false} otherwise.
     */
    public boolean canChangeTaskList() {
        return this.canChangeTaskList;
    }

    /**
     * Parses the command type represented by the supplied keyword.
     *
     * @param keyword The command keyword entered by the user.
     * @return The matching command type.
     * @throws IllegalArgumentException If the keyword does not identify a supported command.
     */
    public static CommandType parseKeyword(String keyword) {
        for (CommandType type : values()) {
            if (type.keyword.equalsIgnoreCase(keyword)) {
                return type;
            }
        }

        throw new IllegalArgumentException("Unknown command: " + keyword);
    }
}
