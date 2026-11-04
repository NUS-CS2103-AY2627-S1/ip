package soar.parser;

/**
 * Identifies a command that Soar accepts from the user.
 */
public enum CommandType {
    BYE("bye", false),
    LIST("list", false),
    FIND("find", true),
    DATE("date", true),
    MARK("mark", TaskAction.COMPLETE_FLIGHT),
    UNMARK("unmark", TaskAction.RETURN_TO_FLIGHT_PATH),
    DELETE("delete", TaskAction.SHOT_DOWN),
    EDIT("edit", TaskAction.UPDATED),
    TODO("todo", true),
    DEADLINE("deadline", true),
    EVENT("event", true);

    /** Word that identifies this command at the start of an input line. */
    private final String commandWord;

    /** Whether text may follow the command word. */
    private final boolean allowsArguments;

    /** Action used when a task-number command is missing its number. */
    private final TaskAction missingTaskNumberAction;

    /**
     * Creates a command that does not operate on a numbered task.
     *
     * @param commandWord Word entered by the user for this command.
     * @param allowsArguments Whether text may follow the command word.
     */
    CommandType(String commandWord, boolean allowsArguments) {
        this(commandWord, allowsArguments, null);
    }

    /**
     * Creates a command that operates on a numbered task.
     *
     * @param commandWord Word entered by the user for this command.
     * @param missingTaskNumberAction Action described when its task number is missing.
     */
    CommandType(String commandWord, TaskAction missingTaskNumberAction) {
        this(commandWord, true, missingTaskNumberAction);
    }

    /**
     * Creates a command with its complete parsing and error-message data.
     *
     * @param commandWord Word entered by the user for this command.
     * @param allowsArguments Whether text may follow the command word.
     * @param missingTaskNumberAction Action described when its task number is missing.
     */
    CommandType(String commandWord, boolean allowsArguments, TaskAction missingTaskNumberAction) {
        this.commandWord = commandWord;
        this.allowsArguments = allowsArguments;
        this.missingTaskNumberAction = missingTaskNumberAction;
    }

    /**
     * Checks whether an input line uses this command's accepted shape.
     *
     * @param input Complete line entered by the user.
     * @return {@code true} if this command accepts the input
     */
    public boolean matches(String input) {
        return input.equals(commandWord)
                || (allowsArguments && input.startsWith(commandWord + " "));
    }

    /**
     * Returns the word entered by the user for this command.
     *
     * @return command word
     */
    public String getCommandWord() {
        return commandWord;
    }

    /**
     * Returns the action phrase for a missing task number.
     *
     * @return missing-number action, or {@code null} for other commands
     */
    public TaskAction getMissingTaskNumberAction() {
        return missingTaskNumberAction;
    }
}
