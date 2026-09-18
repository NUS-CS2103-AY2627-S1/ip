package plana.command;

/**
 * Represents an action that selects and changes a task.
 */
public enum TaskAction {
    /** Removes a task from the task list. */
    DELETE("delete"),

    /** Marks a task as completed. */
    MARK("mark"),

    /** Marks a task as not completed. */
    UNMARK("unmark");

    private final String commandText;

    /**
     * Creates a task action with its command-line representation.
     *
     * @param commandText the command text used for this action.
     */
    TaskAction(String commandText) {
        this.commandText = commandText;
    }

    /**
     * Returns the command-line representation of this action.
     *
     * @return the command text
     */
    public String getCommandText() {
        return commandText;
    }
}
