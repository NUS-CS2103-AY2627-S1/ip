package penguin.enums;

/**
 * Defines the commands supported by Penguin and their user-facing keywords.
 */
public enum CommandType {
    /** Adds a to-do task. */
    TODO("todo"),
    /** Adds a deadline task. */
    DEADLINE("deadline"),
    /** Adds an event task. */
    EVENT("event"),
    /** Lists all tasks. */
    LIST("list"),
    /** Marks a task as completed. */
    MARK("mark"),
    /** Marks a task as incomplete. */
    UNMARK("unmark"),
    /** Deletes a task. */
    DELETE("delete"),
    /** Searches tasks occurring on a date. */
    ON("on"),
    /** Searches task descriptions. */
    FIND("find"),
    /** Reverts the most recent change. */
    UNDO("undo"),
    /** Exits the application. */
    BYE("bye");

    /** User-facing keyword that selects this command. */
    private final String keyword;

    /**
     * Creates a command type with its user-facing keyword.
     *
     * @param commandKeyword keyword that selects the command.
     */
    CommandType(String commandKeyword) {
        this.keyword = commandKeyword;
    }

    /**
     * Returns the user-facing keyword for this command.
     *
     * @return command keyword.
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * Finds the command represented by a user-facing keyword.
     *
     * @param keyword lowercase command keyword.
     * @return matching command type, or {@code null} if the keyword is unknown.
     */
    public static CommandType fromKeyword(String keyword) {
        for (CommandType commandType : values()) {
            if (commandType.keyword.equals(keyword)) {
                return commandType;
            }
        }
        return null;
    }
}
