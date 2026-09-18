package benjamin.parser;

/**
 * The kinds of command the chatbot understands.
 *
 * <p>Each constant is named after the keyword that starts the command line,
 * which is what lets {@link #from(String)} look them up by name.
 */
public enum CommandType {
    /** Add a task with only a description. */
    TODO,

    /** Add a task due by a given date. */
    DEADLINE,

    /** Add a task running between two given dates. */
    EVENT,

    /** Show every task in the list. */
    LIST,

    /** Show the tasks whose description contains a given keyword. */
    FIND,

    /** Show the tasks falling on a given date. */
    ON,

    /** Mark a task as done. */
    MARK,

    /** Mark a task as not done yet. */
    UNMARK,

    /** Attach a label to a task. */
    TAG,

    /** Remove a label from a task. */
    UNTAG,

    /** Remove a task from the list. */
    DELETE,

    /** End the conversation. */
    BYE,

    /** Anything that does not name a command. */
    UNKNOWN;

    /**
     * Returns the kind of command named by the first word of the input.
     *
     * <p>Matching ignores capitalization, so {@code BYE} and {@code bye} are
     * treated the same.
     *
     * @param input the full line typed by the user.
     * @return the matching kind, or {@link #UNKNOWN} if the first word is not
     *     a command or the line is blank.
     */
    public static CommandType from(String input) {
        String trimmedInput = input.trim();

        if (trimmedInput.isEmpty()) {
            return UNKNOWN;
        }

        String keyword = trimmedInput.split("\\s+", 2)[0].toUpperCase();

        try {
            return valueOf(keyword);
        } catch (IllegalArgumentException exception) {
            return UNKNOWN;
        }
    }
}
