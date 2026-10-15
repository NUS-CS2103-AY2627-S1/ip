package lumi.command;

/**
 * Represents the fixed set of commands understood by Lumi.
 */
public enum CommandType {
    /** Adds a task without a date or time. */
    TODO("todo", true),

    /** Adds a task that must be completed by a specific date or time. */
    DEADLINE("deadline", true),

    /** Adds a task that occurs between a start and end date or time. */
    EVENT("event", true),

    /** Displays all stored tasks. */
    LIST("list", false),

    /** Finds tasks whose descriptions contain a keyword. */
    FIND("find", true),

    /** Marks a stored task as complete. */
    MARK("mark", true),

    /** Marks a stored task as incomplete. */
    UNMARK("unmark", true),

    /** Removes a stored task. */
    DELETE("delete", true),

    /** Reschedules a deadline or event. */
    SNOOZE("snooze", true),

    /** Ends the application. */
    BYE("bye", false),

    /** Represents input that does not match a supported command. */
    UNKNOWN(null, false);

    /** The word used to enter this command, or null for an unknown command. */
    private final String keyword;

    /** Whether text is allowed after the command keyword. */
    private final boolean acceptsArgument;

    /**
     * Creates a command type with its user-facing keyword.
     *
     * @param keyword Word used to enter the command.
     * @param acceptsArgument Whether text is allowed after the command keyword.
     */
    CommandType(String keyword, boolean acceptsArgument) {
        this.keyword = keyword;
        this.acceptsArgument = acceptsArgument;
    }

    /**
     * Returns the word used to enter this command.
     *
     * @return The command keyword, or null for {@link #UNKNOWN}.
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * Reports whether a complete input line represents this command type.
     *
     * @param input Complete user input.
     * @return True if this command type matches the input.
     */
    public boolean matches(String input) {
        if (keyword == null) {
            return false;
        }
        boolean isExactMatch = input.equals(keyword);
        boolean hasArgument = acceptsArgument && input.startsWith(keyword + " ");
        return isExactMatch || hasArgument;
    }
}
