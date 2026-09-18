package rene.command;

/**
 * Represents a command accepted by the chatbot and its input keyword.
 */
public enum CommandType {
    /** Exits the application. */
    BYE("bye"),
    /** Displays all tasks. */
    LIST("list"),
    /** Marks a task as completed. */
    MARK("mark"),
    /** Marks a task as incomplete. */
    UNMARK("unmark"),
    /** Removes a task. */
    DELETE("delete"),
    /** Finds tasks whose descriptions contain a keyword. */
    FIND("find"),
    /** Displays guidance for all supported commands. */
    HELP("help"),
    /** Adds a task without a date or time. */
    TODO("todo"),
    /** Adds a task with a due date. */
    DEADLINE("deadline"),
    /** Adds a task with a start and end value. */
    EVENT("event");

    private final String keyword;

    /**
     * Creates a command type with its input keyword.
     *
     * @param keyword the word that begins this command.
     */
    CommandType(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Returns the command word entered by the user.
     *
     * @return the command keyword.
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * Returns whether this command occupies the whole line or is followed by arguments.
     *
     * @param input the line entered by the user.
     * @return whether the input begins with this complete command keyword.
     */
    public boolean matches(String input) {
        return input.equals(keyword) || input.startsWith(keyword + " ");
    }

    /**
     * Finds the command type represented by an input line.
     *
     * @param input the line entered by the user.
     * @return the matching command type, or {@code null} when none matches.
     */
    public static CommandType fromInput(String input) {
        for (CommandType commandType : values()) {
            if (commandType.matches(input)) {
                return commandType;
            }
        }
        return null;
    }
}
