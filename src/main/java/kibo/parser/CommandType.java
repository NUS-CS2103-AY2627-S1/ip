package kibo.parser;

/**
 * Identifies the commands understood by the Kibo chatbot.
 */
public enum CommandType {
    /** Creates a task without a date or time. */
    TODO("todo"),
    /** Creates a task that must be completed by a date. */
    DEADLINE("deadline"),
    /** Creates a task occurring between a start and end. */
    EVENT("event"),
    /** Displays all stored tasks. */
    LIST("list"),
    /** Finds tasks whose descriptions contain a keyword. */
    FIND("find"),
    /** Displays deadlines and dated events scheduled on a date. */
    SCHEDULE("schedule"),
    /** Marks a task as completed. */
    MARK("mark"),
    /** Marks a task as not completed. */
    UNMARK("unmark"),
    /** Removes a task from the list. */
    DELETE("delete"),
    /** Exits Kibo. */
    BYE("bye"),
    /** Represents input that does not match a supported command. */
    UNKNOWN("");

    private final String keyword;

    /**
     * Creates a command type associated with its user-facing keyword.
     *
     * @param keyword word entered to invoke the command.
     */
    CommandType(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Returns the word used to invoke this command.
     *
     * @return command keyword.
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * Determines the command represented by the given input.
     *
     * @param input full user input.
     * @return matching command type, or {@link #UNKNOWN} if none matches.
     */
    public static CommandType fromInput(String input) {
        for (CommandType commandType : values()) {
            if (commandType == UNKNOWN) {
                continue;
            }
            if (input.equals(commandType.keyword)
                    || (input.startsWith(commandType.keyword)
                    && input.length() > commandType.keyword.length()
                    && Character.isWhitespace(input.charAt(commandType.keyword.length())))) {
                return commandType;
            }
        }
        return UNKNOWN;
    }
}
