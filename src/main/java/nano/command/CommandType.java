package nano.command;

/**
 * Represents the types of commands that Nano can understand.
 */
public enum CommandType {
    BYE("bye", false), LIST("list", false),
    MARK("mark", true), UNMARK("unmark", true),
    DELETE("delete", true), TODO("todo", true),
    DEADLINE("deadline", true), EVENT("event", true),
    FIND("find", true), PRIORITY("priority", true),
    UNKNOWN("", false);

    private final String keyword;
    private final boolean acceptsArguments;

    /**
     * Creates a command type with its keyword and argument behavior.
     *
     * @param keyword text used to identify the command.
     * @param acceptsArguments whether additional text is allowed.
     */
    CommandType(String keyword, boolean acceptsArguments) {
        this.keyword = keyword;
        this.acceptsArguments = acceptsArguments;
    }

    /**
     * Checks whether an input belongs to this command type.
     *
     * @param input user input to check.
     * @return true if the input matches this command type.
     */
    public boolean matches(String input) {
        return input.equals(keyword)
                || acceptsArguments && input.startsWith(keyword + " ");
    }

    /**
     * Finds the command type represented by user input.
     *
     * @param input user input to classify.
     * @return the matching command type, or UNKNOWN if there is no match.
     */
    public static CommandType from(String input) {
        for (CommandType type : values()) {
            if (type != UNKNOWN && type.matches(input)) {
                return type;
            }
        }
        return UNKNOWN;
    }

    /**
     * Returns the keyword used to identify this command.
     *
     * @return the command keyword.
     */
    public String keyword() {
        return keyword;
    }
}
