package sozius.parser;

/**
 * Command Enum class for all commands
 */
public enum Command {
    LIST("list", "ls"),
    MARK("mark", "m"),
    UNMARK("unmark", "um"),
    TODO("todo", "td"),
    DEADLINE("deadline", "dl"),
    EVENT("event", "e"),
    DELETE("delete", "del"),
    FIND("find", "f"),
    HELP("help", "h");

    private final String[] words;

    Command(String... words) {
        this.words = words;
    }

    /**
     * Returns the command of the string
     * @param s the input string
     * @return a Command, or null if the string does not match any
     */
    public static Command getCommand(String s) {
        for (Command c : values()) {
            for (String word : c.words) {
                if (word.equals(s)) {
                    return c;
                }
            }
        }
        return null;
    }
}
