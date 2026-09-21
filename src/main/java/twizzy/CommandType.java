package twizzy;

/**
 * Identifies the supported user command keywords.
 */
public enum CommandType {
    TODO("todo", true),
    DEADLINE("deadline", true),
    EVENT("event", true),
    FIND("find", true),
    SNOOZE("snooze", true),
    UNSNOOZE("unsnooze", true),
    HELP("help", false),
    LIST("list", false),
    MARK("mark", true),
    UNMARK("unmark", true),
    DELETE("delete", true),
    BYE("bye", false),
    UNKNOWN("", false);

    private final String keyword;

    private final boolean acceptsArguments;

    /**
     * Creates a command type for a user-facing keyword.
     *
     * @param keyword text that begins this type of command
     * @param acceptsArguments whether text may follow the keyword
     */
    CommandType(String keyword, boolean acceptsArguments) {
        this.keyword = keyword;
        this.acceptsArguments = acceptsArguments;
    }

    /**
     * Returns the command's user-facing keyword.
     *
     * @return command keyword
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * Determines the type of a complete command without matching keyword prefixes.
     *
     * @param command complete user command
     * @return matching command type, or {@link #UNKNOWN} when no keyword matches
     */
    public static CommandType from(String command) {
        for (CommandType type : values()) {
            if (type == UNKNOWN) {
                continue;
            }
            if (type == LIST && command.equals("list snoozed")) {
                return type;
            }
            if (command.equals(type.keyword)
                    || type.acceptsArguments && command.startsWith(type.keyword + " ")) {
                return type;
            }
        }
        return UNKNOWN;
    }
}
