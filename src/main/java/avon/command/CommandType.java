package avon.command;

import java.util.StringJoiner;

import avon.exception.UnknownCommandException;

/**
 * Represents a command that Avon understands.
 */
public enum CommandType {
    TODO("todo", true),
    DEADLINE("deadline", true),
    EVENT("event", true),
    LIST("list", false),
    FIND("find", true),
    MARK("mark", true),
    UNMARK("unmark", true),
    DELETE("delete", true),
    HELP("help", false),
    BYE("bye", false);

    private final String keyword;
    private final boolean canHaveArguments;

    /**
     * Creates a command type with its parsing properties.
     *
     * @param keyword the first word that identifies the command.
     * @param canHaveArguments whether text may follow the command keyword.
     */
    CommandType(String keyword, boolean canHaveArguments) {
        this.keyword = keyword;
        this.canHaveArguments = canHaveArguments;
    }

    /**
     * Returns the word used to invoke this command.
     *
     * @return the command keyword.
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * Identifies the type of a complete command.
     *
     * @param command the complete command entered by the user.
     * @return the matching command type.
     * @throws UnknownCommandException if no supported command matches the input.
     */
    public static CommandType parse(String command) throws UnknownCommandException {
        for (CommandType commandType : values()) {
            if (commandType.matches(command)) {
                return commandType;
            }
        }
        throw new UnknownCommandException(getSupportedKeywords());
    }

    /**
     * Returns all supported command keywords in display order.
     *
     * @return a comma-separated list of command keywords.
     */
    public static String getSupportedKeywords() {
        StringJoiner keywords = new StringJoiner(", ");
        for (CommandType commandType : values()) {
            keywords.add(commandType.keyword);
        }
        return keywords.toString();
    }

    /**
     * Checks whether a complete command matches this command type.
     *
     * @param command the complete command entered by the user.
     * @return true if the command has this type.
     */
    private boolean matches(String command) {
        if (command.equals(keyword)) {
            return true;
        }
        return canHaveArguments
                && command.startsWith(keyword)
                && command.length() > keyword.length()
                && Character.isWhitespace(command.charAt(keyword.length()));
    }
}
