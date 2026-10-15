package atlas.command;

/**
 * The commands that follow the {@code client} command word.
 */
public enum ClientCommand {
    /** Adds a client. */
    ADD("add"),
    /** Lists all clients. */
    LIST("list"),
    /** Finds clients whose details contain a keyword. */
    FIND("find"),
    /** Removes a client. */
    DELETE("delete");

    /** The text that invokes this client command. */
    private final String word;

    /**
     * Creates a client command represented by the given input word.
     *
     * @param word text users type to invoke the command.
     */
    ClientCommand(String word) {
        this.word = word;
    }

    /**
     * Returns the text users type to invoke this client command.
     *
     * @return command word.
     */
    public String getWord() {
        return word;
    }
}
