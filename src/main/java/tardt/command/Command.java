package tardt.command;

/** Represents the supported command keywords and an unrecognised input. */
public enum Command {
    BYE("bye"),
    LIST("list"),
    MARK("mark"),
    UNMARK("unmark"),
    TODO("todo"),
    DEADLINE("deadline"),
    EVENT("event"),
    DELETE("delete"),
    FIND("find"),
    INVALID(null);

    private final String keyword;

    Command(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Returns the command that matches the input keyword, if one exists.
     *
     * @param keyword A string representing the input command from the user.
     * @return A Command object corresponding to the action about to be taken.
     *      INVALID if input command is unrecognisable.
     * */
    public static Command fromKeyword(String keyword) {
        for (Command command : values()) {
            if (keyword.equals(command.keyword)) {
                return command;
            }
        }
        return INVALID;
    }
}
