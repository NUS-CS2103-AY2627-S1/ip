package alex.parser;

import java.util.Arrays;

/**
 * Represents a command supported by Alex.
 */
public enum CommandType {
    BYE("bye", false),
    LIST("list", false),
    UPCOMING("upcoming", false),
    MARK("mark", true),
    UNMARK("unmark", true),
    DELETE("delete", true),
    FIND("find", true),
    TODO("todo", true),
    DEADLINE("deadline", true),
    EVENT("event", true),
    UNKNOWN("", false);

    private final String keyword;
    private final boolean canAcceptArguments;

    /**
     * Creates a command type with its keyword and argument policy.
     *
     * @param keyword text that identifies the command.
     * @param canAcceptArguments whether the command accepts arguments.
     */
    CommandType(String keyword, boolean canAcceptArguments) {
        this.keyword = keyword;
        this.canAcceptArguments = canAcceptArguments;
    }

    /**
     * Returns the keyword that identifies this command type.
     *
     * @return the command keyword.
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * Returns whether this command type accepts arguments.
     *
     * @return whether arguments are allowed after the command keyword.
     */
    public boolean canAcceptArguments() {
        return canAcceptArguments;
    }

    /**
     * Identifies the type of a user command.
     *
     * @param command full user command.
     * @return the matching command type, or {@link #UNKNOWN}.
     */
    public static CommandType parse(String command) {
        return Arrays.stream(values())
                .filter(type -> type != UNKNOWN)
                .filter(type -> command.equals(type.keyword)
                        || command.startsWith(type.keyword + " "))
                .findFirst()
                .orElse(UNKNOWN);
    }
}
