package pebby.parser;

/** Identifies supported command words and extracts their user-supplied arguments. */
public enum CommandType {
    LIST("list"),
    MARK("mark"),
    UNMARK("unmark"),
    TODO("todo"),
    DEADLINE("deadline"),
    EVENT("event"),
    FIND("find"),
    SCHEDULE("schedule"),
    DELETE("delete"),
    HELP("help"),
    BYE("bye"),
    UNKNOWN("");

    private final String keyword;

    CommandType(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Returns the command type recognized at the start of the supplied command text.
     */
    public static CommandType from(String command) {
        if (command == null) {
            return UNKNOWN;
        }

        String trimmedCommand = command.trim();
        for (CommandType type : values()) {
            if (type == UNKNOWN) {
                continue;
            }
            if (trimmedCommand.equals(type.keyword)
                    || trimmedCommand.startsWith(type.keyword + " ")) {
                return type;
            }
        }
        return UNKNOWN;
    }

    /**
     * Returns the text after this recognized command's keyword.
     *
     * @param command a command beginning with this command type's keyword
     * @return the command argument without surrounding whitespace
     */
    public String argumentFrom(String command) {
        String trimmedCommand = command.trim();
        assert this != UNKNOWN : "Only recognized command types have arguments.";
        assert trimmedCommand.startsWith(keyword) : "A command argument must follow its recognized keyword.";
        return trimmedCommand.substring(keyword.length()).trim();
    }
}
