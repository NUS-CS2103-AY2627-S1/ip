package duck.parser;

/**
 * Represents the commands understood by Duck and their command words.
 */
public enum CommandType {
    /** Command that exits Duck. */
    BYE("bye", false),

    /** Command that displays the current task list. */
    LIST("list", false),

    /** Command that displays guidance for every supported command. */
    HELP("help", false),

    /** Command that finds tasks containing a keyword. */
    FIND("find", true),

    /** Command that marks a task as done. */
    MARK("mark", true),

    /** Command that marks a task as not done. */
    UNMARK("unmark", true),

    /** Command that creates a todo. */
    TODO("todo", true),

    /** Command that creates an event. */
    EVENT("event", true),

    /** Command that creates a deadline. */
    DEADLINE("deadline", true),

    /** Command that deletes a task. */
    DELETE("delete", true);

    /** Word that identifies this command. */
    private final String commandWord;

    /** Whether text may follow the command word. */
    private final boolean canAcceptArguments;

    /**
     * Creates a command type with its input syntax.
     *
     * @param commandWord Word that identifies the command.
     * @param canAcceptArguments Whether the command accepts trailing arguments.
     */
    CommandType(String commandWord, boolean canAcceptArguments) {
        this.commandWord = commandWord;
        this.canAcceptArguments = canAcceptArguments;
    }

    /**
     * Returns the word that identifies this command.
     *
     * @return command word
     */
    public String getCommandWord() {
        return this.commandWord;
    }

    /**
     * Returns whether text may follow this command word.
     *
     * @return true when the command accepts arguments
     */
    public boolean canAcceptArguments() {
        return this.canAcceptArguments;
    }
}
