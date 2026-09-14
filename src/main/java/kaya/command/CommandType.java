package kaya.command;

/**
 * Represents the commands understood by Kaya.
 */
public enum CommandType {
    /** Ends the chatbot session. */
    BYE("bye"),

    /** Displays all stored tasks. */
    LIST("list"),

    /** Marks a task as completed. */
    MARK("mark"),

    /** Marks a task as not completed. */
    UNMARK("unmark"),

    /** Removes a task from the list. */
    DELETE("delete"),

    /** Updates one field of an existing task. */
    UPDATE("update"),

    /** Finds tasks containing a keyword. */
    FIND("find"),

    /** Adds a task without a date. */
    TODO("todo"),

    /** Adds a task with a due date. */
    DEADLINE("deadline"),

    /** Adds a task with starting and ending dates. */
    EVENT("event"),

    /** Represents input that does not match a supported command. */
    UNKNOWN("");

    private final String commandWord;

    /**
     * Creates a command type associated with its command word.
     *
     * @param commandWord the word used to invoke the command
     */
    CommandType(String commandWord) {
        this.commandWord = commandWord;
    }

    /**
     * Identifies the command type at the start of the input.
     *
     * @param input the full command entered by the user
     * @return the matching command type, or {@link #UNKNOWN}
     */
    public static CommandType fromInput(String input) {
        String commandWord = input.trim().split("\\s+", 2)[0];
        for (CommandType commandType : values()) {
            if (commandType.commandWord.equals(commandWord)) {
                return commandType;
            }
        }
        return UNKNOWN;
    }
}
