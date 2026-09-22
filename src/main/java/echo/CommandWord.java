package echo;

/**
 * Represents the command words supported by Echo.
 */
public enum CommandWord {
    BYE("bye"),
    LIST("list"),
    MARK("mark"),
    UNMARK("unmark"),
    TODO("todo"),
    DEADLINE("deadline"),
    EVENT("event"),
    DELETE("delete"),
    FIND("find"),
    UNDO("undo"),
    INVALID("invalid");

    private final String cmd;

    CommandWord(String cmd) {
        this.cmd = cmd;
    }

    /**
     * Returns the command word that corresponds to the given input.
     *
     * @param input the command word entered by the user
     * @return the matching command word, or {@link #INVALID} when no command matches
     */
    public static CommandWord fromString(String input) {
        assert input != null;
        for (CommandWord cmdword : values()) {
            if (cmdword.cmd.equals(input)) {
                return cmdword;
            }
        }
        return INVALID;
    }
}
