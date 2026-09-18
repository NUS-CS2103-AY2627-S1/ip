package plana.command;

/**
 * Represents an action available under the {@code client} command.
 */
public enum ClientAction {
    /** Adds a client. */
    ADD("add"),

    /** Lists all clients. */
    LIST("list"),

    /** Displays one client's details. */
    VIEW("view"),

    /** Finds clients by keyword. */
    FIND("find"),

    /** Edits one client. */
    EDIT("edit"),

    /** Deletes one client. */
    DELETE("delete");

    private final String commandText;

    ClientAction(String commandText) {
        this.commandText = commandText;
    }

    /**
     * Returns the action's command-line representation.
     *
     * @return the action text.
     */
    public String getCommandText() {
        return commandText;
    }
}
