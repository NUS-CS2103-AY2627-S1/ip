package nubish.utils;

/**
 * Represents supported user commands and their text keywords.
 */
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
    UNKNOWN("");

    private final String keyword;

    Command(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Converts a user-entered keyword into a command.
     *
     * @param keyword command keyword entered by the user
     * @return matching command, or {@link #UNKNOWN} if no command matches
     */
    public static Command fromKeyword(String keyword) {
        for (Command command : values()) {
            if (command.keyword.equals(keyword)) {
                return command;
            }
        }
        return UNKNOWN;
    }
}
