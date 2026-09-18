package chris.parser;

/**
 * Represents a command supported by the Chris chatbot.
 */
public enum CommandType {
    /** Ends the chatbot session. */
    BYE("bye"),

    /** Displays all stored tasks. */
    LIST("list"),

    /** Finds tasks containing a keyword. */
    FIND("find"),

    /** Marks a task as completed. */
    MARK("mark"),

    /** Marks a task as incomplete. */
    UNMARK("unmark"),

    /** Removes a task from the task list. */
    DELETE("delete"),

    /** Adds a todo task. */
    TODO("todo"),

    /** Adds a deadline task. */
    DEADLINE("deadline"),

    /** Adds an event task. */
    EVENT("event"),

    /** Represents an unsupported command. */
    UNKNOWN("");

    private final String commandWord;

    CommandType(String commandWord) {
        this.commandWord = commandWord;
    }

    /**
     * Returns the word users enter for this command.
     *
     * @return Command word.
     */
    public String getCommandWord() {
        return this.commandWord;
    }

    /**
     * Converts a command word into its corresponding command type.
     *
     * @param commandWord Command word entered by the user.
     * @return Matching command type, or {@code UNKNOWN} if unsupported.
     */
    public static CommandType parseCommandWord(String commandWord) {
        for (CommandType commandType : values()) {
            if (commandType.commandWord.equals(commandWord)) {
                return commandType;
            }
        }
        return UNKNOWN;
    }
}
