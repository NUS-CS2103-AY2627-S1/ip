package beemo.parser;

/**
 * Identifies the command represented by a line of user input.
 */
public enum CommandType {
    /** Exits Beemo. */
    BYE("bye", false),
    /** Displays guidance for supported commands. */
    HELP("help", false),
    /** Displays all tasks. */
    LIST("list", false),
    /** Marks a task as completed. */
    MARK("mark", true),
    /** Marks a task as incomplete. */
    UNMARK("unmark", true),
    /** Deletes a task. */
    DELETE("delete", true),
    /** Finds tasks containing a keyword in their descriptions. */
    FIND("find", true),
    /** Adds a todo. */
    TODO("todo", true),
    /** Adds a deadline. */
    DEADLINE("deadline", true),
    /** Adds an event. */
    EVENT("event", true),
    /** Represents unrecognized input. */
    UNKNOWN("", false);

    private final String keyword;
    private final boolean canAcceptArguments;

    /**
     * Creates a command type with its keyword and argument behavior.
     *
     * @param keyword Word that identifies the command.
     * @param canAcceptArguments Whether text may follow the keyword.
     */
    CommandType(String keyword, boolean canAcceptArguments) {
        this.keyword = keyword;
        this.canAcceptArguments = canAcceptArguments;
    }

    /**
     * Returns the word that identifies this command type.
     *
     * @return Command keyword.
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * Returns the command type matching the input's first keyword.
     *
     * @param input Full command entered by the user.
     * @return Matching command type, or {@link #UNKNOWN} if none matches.
     */
    public static CommandType from(String input) {
        for (CommandType type : values()) {
            if (input.equals(type.keyword)
                    || (type.canAcceptArguments && input.startsWith(type.keyword + " "))) {
                return type;
            }
        }
        return UNKNOWN;
    }
}
