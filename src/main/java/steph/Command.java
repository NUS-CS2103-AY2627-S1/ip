package steph;

/**
 * The fixed set of command keywords Steph understands. Each constant carries
 * the exact keyword text (e.g. "todo") a user command must start with, so
 * parsing stays in one place instead of the keyword and the enum name
 * (which follows a different naming convention) having to be kept in sync.
 */
public enum Command {
    LIST("list"),
    MARK("mark"),
    UNMARK("unmark"),
    TODO("todo"),
    DEADLINE("deadline"),
    EVENT("event"),
    DELETE("delete"),
    FIND("find");

    private final String keyword;

    Command(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Returns the Command whose keyword matches exactly, or null if the given
     * keyword doesn't match any known command.
     */
    public static Command fromKeyword(String keyword) {
        for (Command command : values()) {
            if (command.keyword.equals(keyword)) {
                return command;
            }
        }
        return null;
    }
}
