package littledaisy.command;

/**
 * The commands littleDaisy understands, one constant per command word.
 *
 * <p>{@link #UNKNOWN} stands for "the user typed something that is no
 * command". Turning bad input into a value of the same type as good input
 * lets the command loop handle both through one switch, instead of checking
 * for null or catching a lookup failure separately.
 */
public enum Command {
    /** Ends the conversation. */
    BYE("bye"),

    /** Shows every stored task. */
    LIST("list"),

    /** Ticks a task off. */
    MARK("mark"),

    /** Undoes a {@link #MARK}. */
    UNMARK("unmark"),

    /** Adds a task with no date attached. */
    TODO("todo"),

    /** Adds a task due by some time. */
    DEADLINE("deadline"),

    /** Adds a task spanning two times. */
    EVENT("event"),

    /** Removes a task from the list. */
    DELETE("delete"),

    /** Finds tasks whose descriptions contain a keyword. */
    FIND("find"),

    /** Shows the command reference. */
    HELP("help"),

    /** Stands for any word that is none of the above. */
    UNKNOWN("");

    /** The word the user types to invoke this command. */
    private final String word;

    /**
     * Creates a command that answers to the given word.
     *
     * @param word the word the user types
     */
    Command(String word) {
        this.word = word;
    }

    /**
     * Returns the command invoked by the given word.
     *
     * @param word the first word of the user's line
     * @return the matching command, or {@link #UNKNOWN} if nothing matches
     */
    public static Command of(String word) {
        for (Command command : values()) {
            if (command != UNKNOWN && command.word.equals(word)) {
                return command;
            }
        }
        return UNKNOWN;
    }
}
