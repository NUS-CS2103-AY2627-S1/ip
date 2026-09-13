package odysseus;

/**
 * A command that targets an existing task by its one-based number.
 */
public enum TaskAction {
    MARK("mark"),
    UNMARK("unmark"),
    DELETE("delete");

    private final String commandWord;

    TaskAction(String commandWord) {
        this.commandWord = commandWord;
    }

    /**
     * Returns whether the input begins this action command.
     */
    public boolean matches(String command) {
        return command.equals(commandWord) || command.startsWith(commandWord + " ");
    }

    /**
     * Returns the command word entered by the user.
     */
    public String getCommandWord() {
        return commandWord;
    }
}
