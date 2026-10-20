package yapper;

/**
 * Represents the commands understood by Yapper.
 *
 * <p>An enum is suitable because the command vocabulary is a small, fixed set.
 * Each constant stores the word users type, keeping command recognition in one
 * place instead of scattering string comparisons throughout the application.</p>
 */
public enum CommandType {
    /** Displays all tasks. */
    LIST("list", false),
    /** Summarizes task completion counts. */
    STATS("stats", false),
    /** Displays supported commands. */
    HELP("help", false),
    /** Marks a numbered task completed. */
    MARK("mark", true),
    /** Marks a numbered task incomplete. */
    UNMARK("unmark", true),
    /** Removes a numbered task. */
    DELETE("delete", true),
    /** Adds a task without a date. */
    TODO("todo", true),
    /** Adds a task with a due date or time. */
    DEADLINE("deadline", true),
    /** Adds a task spanning a start and end time. */
    EVENT("event", true),
    /** Lists tasks scheduled on a calendar date. */
    ON("on", true),
    /** Searches for a keyword in task descriptions. */
    FIND("find", true),
    /** Ends the command loop. */
    BYE("bye", false),
    /** Represents input that does not match a supported command. */
    UNKNOWN("", false);

    /** Exact lowercase word used to recognize this command. */
    private final String keyword;
    /** Whether a space and arguments may follow the command word. */
    private final boolean acceptsArguments;

    /**
     * Defines a command keyword and its argument policy.
     *
     * @param keyword Exact word typed by the user.
     * @param acceptsArguments Whether arguments may follow a space after the word.
     */
    CommandType(String keyword, boolean acceptsArguments) {
        this.keyword = keyword;
        this.acceptsArguments = acceptsArguments;
    }

    /**
     * Identifies the command word at the start of the user's input.
     *
     * @param input Trimmed user input.
     * @return Matching command, or {@link #UNKNOWN} if no command matches.
     */
    public static CommandType from(String input) {
        for (CommandType command : values()) {
            if (command != UNKNOWN && command.matches(input)) {
                return command;
            }
        }
        return UNKNOWN;
    }

    /**
     * Returns whether the input begins with this complete command word.
     *
     * @param input Trimmed user input.
     * @return Whether the keyword matches exactly or is followed by a space when arguments are accepted.
     */
    private boolean matches(String input) {
        return input.equals(keyword) || acceptsArguments && input.startsWith(keyword + " ");
    }
}
