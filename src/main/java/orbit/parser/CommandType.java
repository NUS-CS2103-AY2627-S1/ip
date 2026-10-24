package orbit.parser;

import orbit.exception.OrbitException;

/**
 * Identifies the commands understood by Orbit.
 */
public enum CommandType {
    /** Ends the current Orbit session. */
    BYE("bye"),

    /** Displays every stored task. */
    LIST("list"),

    /** Sorts dated tasks chronologically and places undated tasks last. */
    SORT("sort"),

    /** Marks one task as completed. */
    MARK("mark"),

    /** Marks one task as not completed. */
    UNMARK("unmark"),

    /** Adds a task without a date. */
    TODO("todo"),

    /** Adds a task with a due date. */
    DEADLINE("deadline"),

    /** Adds a task spanning two dates. */
    EVENT("event"),

    /** Finds tasks whose descriptions contain a keyword. */
    FIND("find"),

    /** Removes one stored task. */
    DELETE("delete");

    private final String keyword;

    /**
     * Creates a command type for its console keyword.
     *
     * @param keyword command word entered by the user
     */
    CommandType(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Returns the command word entered by the user.
     *
     * @return command keyword
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * Checks whether an input starts with this exact command word.
     *
     * @param input trimmed user input
     * @return true if this command word matches exactly
     */
    private boolean matches(String input) {
        return input.equals(keyword)
                || (input.startsWith(keyword)
                && input.length() > keyword.length()
                && Character.isWhitespace(input.charAt(keyword.length())));
    }

    /**
     * Resolves the command type represented by an input line.
     *
     * @param input trimmed user input
     * @return matching command type
     * @throws OrbitException if the command word is unknown
     */
    public static CommandType fromInput(String input) throws OrbitException {
        assert input != null : "Command input should not be null";
        assert input.equals(input.trim()) && !input.isEmpty()
                : "Command input should be trimmed and non-empty";
        for (CommandType commandType : values()) {
            if (commandType.matches(input)) {
                return commandType;
            }
        }
        throw new OrbitException("I don't know that command.");
    }
}
