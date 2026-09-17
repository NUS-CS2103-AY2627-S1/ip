package nova.parser;

/**
 * Identifies the commands that Nova can execute.
 */
public enum CommandType {
    /** Adds a task without a date or time. */
    TODO("todo", true),
    /** Adds a task with a due date or time. */
    DEADLINE("deadline", true),
    /** Adds a task with a start and end date or time. */
    EVENT("event", true),
    /** Finds tasks whose descriptions contain a keyword. */
    FIND("find", true),
    /** Finds scheduled tasks occurring on a date. */
    ON("on", true),
    /** Displays all tasks. */
    LIST("list", false),
    /** Marks a task as completed. */
    MARK("mark", true),
    /** Marks a task as not completed. */
    UNMARK("unmark", true),
    /** Removes a task. */
    DELETE("delete", true),
    /** Changes one detail of a task. */
    EDIT("edit", true),
    /** Exits Nova. */
    BYE("bye", false);

    private final String keyword;
    private final boolean acceptsArguments;

    /**
     * Creates a command type with its user-facing keyword and argument policy.
     *
     * @param keyword word that starts this command.
     * @param acceptsArguments whether text may follow the keyword.
     */
    CommandType(String keyword, boolean acceptsArguments) {
        this.keyword = keyword;
        this.acceptsArguments = acceptsArguments;
    }

    /**
     * Returns whether a complete command uses this command type.
     *
     * @param command trimmed command entered by the user.
     * @return {@code true} if the keyword and argument form match.
     */
    boolean matches(String command) {
        boolean isExactMatch = command.equals(keyword);
        boolean isCommandWithArguments = acceptsArguments
                && command.startsWith(keyword + " ");
        return isExactMatch || isCommandWithArguments;
    }

    /**
     * Returns this command's user-facing keyword.
     *
     * @return word that identifies this command.
     */
    String getKeyword() {
        return keyword;
    }

    /**
     * Extracts the argument text from a command of this type.
     *
     * @param command matching complete command.
     * @return trimmed text following the command keyword.
     */
    String extractArguments(String command) {
        return command.substring(keyword.length()).trim();
    }
}
