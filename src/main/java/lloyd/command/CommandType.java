package lloyd.command;

/**
 * Represents the fixed set of commands understood by the Lloyd chatbot.
 */
public enum CommandType {
    /** Ends the current chatbot session. */
    BYE("bye"),
    /** Displays all tasks in their current order. */
    LIST("list"),
    /** Displays tasks that contain a keyword in their description. */
    FIND("find"),
    /** Displays deadlines and event endpoints on a requested date. */
    CHECK("check"),
    /** Displays incomplete dated tasks that are overdue or due soon. */
    REMINDER("reminder"),
    /** Displays a summary of the supported commands and their formats. */
    HELP("help"),
    /** Marks a task as complete. */
    MARK("mark"),
    /** Marks a task as incomplete. */
    UNMARK("unmark"),
    /** Removes a task from the list. */
    DELETE("delete"),
    /** Adds a task without a date or time. */
    TODO("todo"),
    /** Adds a task with a due date. */
    DEADLINE("deadline"),
    /** Adds a task with a start and end date-time. */
    EVENT("event"),
    /** Represents input that does not begin with a recognized command word. */
    UNKNOWN("");

    private final String commandWord;

    /**
     * Associates a command type with the word entered by the user.
     *
     * @param commandWord Text that selects this command type.
     */
    CommandType(String commandWord) {
        this.commandWord = commandWord;
    }

    /**
     * Finds the command type represented by a word entered by the user.
     *
     * @param commandWord First word of the user's input.
     * @return Matching command type, or {@link #UNKNOWN} when no command matches.
     */
    public static CommandType from(String commandWord) {
        for (CommandType commandType : values()) {
            if (commandType.commandWord.equals(commandWord)) {
                return commandType;
            }
        }
        return UNKNOWN;
    }
}
