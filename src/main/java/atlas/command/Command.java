package atlas.command;

/**
 * The commands Atlas understands.
 */
public enum Command {
    /** Exits Atlas. */
    BYE("bye"),
    /** Lists all tasks. */
    LIST("list"),
    /** Marks a task as complete. */
    MARK("mark"),
    /** Marks a task as incomplete. */
    UNMARK("unmark"),
    /** Removes a task. */
    DELETE("delete"),
    /** Finds tasks containing a keyword. */
    FIND("find"),
    /** Adds a todo task. */
    TODO("todo"),
    /** Adds a deadline task. */
    DEADLINE("deadline"),
    /** Adds an event task. */
    EVENT("event"),
    /** Ranks a task as high, medium or low priority. */
    PRIORITY("priority"),
    /** Manages clients. */
    CLIENT("client");

    /** The text that invokes this command. */
    private final String word;

    /**
     * Creates a command represented by the given input word.
     *
     * @param word text users type to invoke the command.
     */
    Command(String word) {
        this.word = word;
    }

    /**
     * Returns the text users type to invoke this command.
     *
     * @return command word.
     */
    public String getWord() {
        return word;
    }
}
