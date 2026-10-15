package chione.command;

/**
 * The words Chione understands at the start of a line.
 *
 * <p>Each constant owns the keyword the user types to invoke it, so the list of
 * valid commands exists in exactly one place: adding a command here is enough
 * for {@link chione.Parser Parser} to recognize it and quote it in error messages, and for the
 * compiler to demand a branch for it in the switch that carries commands out.
 *
 * <p>Recognizing which keyword was typed is {@link chione.Parser Parser}'s job, not this
 * enum's. What is left here is the vocabulary itself.
 *
 * <p>An enum is used rather than string constants because a {@code CommandType} can
 * only ever hold one of these ten values. A typo like {@code CommandType.LITS}
 * fails to compile, whereas the string {@code "lits"} would compile happily and
 * simply never match anything at runtime.
 */
public enum CommandType {
    /** Adds a task with no date attached. */
    TODO("todo"),

    /** Adds a task due by a given date. */
    DEADLINE("deadline"),

    /** Adds a task that runs between two given dates. */
    EVENT("event"),

    /** Shows every stored task. */
    LIST("list"),

    /** Shows the tasks falling on one particular day. */
    ON("on"),

    /** Shows the tasks whose description contains a given word. */
    FIND("find"),

    /** Marks a task as done. */
    MARK("mark"),

    /** Marks a task as not done. */
    UNMARK("unmark"),

    /** Removes a task from the list. */
    DELETE("delete"),

    /** Ends the conversation. */
    BYE("bye");

    /** The word the user types to invoke this command. */
    private final String keyword;

    /**
     * Associates a keyword with a constant.
     *
     * <p>Enum constructors are implicitly private: constants are created once,
     * when the enum is first loaded, and never anywhere else.
     *
     * @param keyword the word the user types
     */
    CommandType(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Returns the word the user types to invoke this command.
     *
     * @return the keyword, e.g. {@code "delete"}
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * Returns everything after this command's keyword, e.g. {@code "2"} from
     * {@code "delete 2"} or the empty string from {@code "delete"}.
     *
     * @param input one line of user input, already trimmed and known to invoke this command
     * @return the arguments, trimmed
     */
    public String argumentsOf(String input) {
        // Parser recognized the keyword before handing the line over, so the
        // substring below can never run off the end of the input.
        assert input.startsWith(keyword)
                : "argumentsOf needs a line starting with \"" + keyword + "\"";
        return input.substring(keyword.length()).trim();
    }
}
