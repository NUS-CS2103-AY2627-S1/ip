package crystal.parser;

/**
 * Represents the commands understood by the Crystal chatbot.
 */
public enum CommandType {
    /** Adds a task without a date or time. */
    TODO("todo"),
    /** Adds a task with a deadline. */
    DEADLINE("deadline"),
    /** Adds an event with a start and end. */
    EVENT("event"),
    /** Adds a task that can be completed within a date range. */
    WITHIN("within"),
    /** Lists stored tasks. */
    LIST("list"),
    /** Finds tasks whose descriptions contain a keyword. */
    FIND("find"),
    /** Marks a task as completed. */
    MARK("mark"),
    /** Marks a task as incomplete. */
    UNMARK("unmark"),
    /** Deletes a task. */
    DELETE("delete"),
    /** Displays the command guide. */
    HELP("help"),
    /** Exits the chatbot. */
    EXIT("bye"),
    /** Represents input that does not match a supported command. */
    UNKNOWN("");

    /** Keyword that identifies this command at the start of user input. */
    private final String keyword;

    /**
     * Creates a command type with its identifying keyword.
     *
     * @param keyword text that identifies the command.
     */
    CommandType(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Returns the keyword used to identify this command.
     *
     * @return command keyword.
     */
    public String getKeyword() {
        return keyword;
    }
}
