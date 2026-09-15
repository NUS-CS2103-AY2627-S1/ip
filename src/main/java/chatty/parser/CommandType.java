package chatty.parser;

/**
 * Identifies the commands understood by Chatty.
 */
public enum CommandType {
    /** Ends the current Chatty session. */
    BYE("bye", false),

    /** Displays all stored tasks. */
    LIST("list", false),

    /** Finds tasks whose descriptions contain a keyword. */
    FIND("find", true),

    /** Marks a selected task as done. */
    MARK("mark", true),

    /** Marks a selected task as not done. */
    UNMARK("unmark", true),

    /** Removes a selected task. */
    DELETE("delete", true),

    /** Adds a todo. */
    TODO("todo", true),

    /** Adds a deadline. */
    DEADLINE("deadline", true),

    /** Adds an event. */
    EVENT("event", true),

    /** Adds a recurring task. */
    RECURRING("recurring", true),

    /** Represents input that does not match a supported command. */
    UNKNOWN("", false);

    /** Keyword entered by the user to select this command. */
    private final String keyword;

    /** Whether the command can be followed by arguments. */
    private final boolean acceptsArguments;

    /**
     * Creates a command type with its keyword and argument policy.
     *
     * @param keyword keyword used to select the command.
     * @param acceptsArguments whether the command accepts trailing arguments.
     */
    CommandType(String keyword, boolean acceptsArguments) {
        this.keyword = keyword;
        this.acceptsArguments = acceptsArguments;
    }

    /**
     * Returns the keyword used to select this command.
     *
     * @return command keyword.
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * Returns whether this command accepts text after its keyword.
     *
     * @return {@code true} if command arguments are accepted.
     */
    public boolean acceptsArguments() {
        return acceptsArguments;
    }
}
