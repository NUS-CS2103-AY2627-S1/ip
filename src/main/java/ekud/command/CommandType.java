package ekud.command;

import ekud.EkudException;

/**
 * The fixed set of command words the chatbot understands.
 * Each value maps a keyword to that command class's {@code parse} method.
 * Behavior stays in the {@link Command} classes; this enum is only the vocabulary.
 */
public enum CommandType {
    TODO("todo", TodoCommand::parse),
    DEADLINE("deadline", DeadlineCommand::parse),
    EVENT("event", EventCommand::parse),
    LIST("list", ListCommand::parse),
    ON("on", OnCommand::parse),
    FIND("find", FindCommand::parse),
    MARK("mark", MarkCommand::parse),
    UNMARK("unmark", UnmarkCommand::parse),
    DELETE("delete", DeleteCommand::parse),
    HELP("help", HelpCommand::parse),
    BYE("bye", ByeCommand::parse);

    /**
     * Parses the argument text that follows a command word.
     */
    @FunctionalInterface
    private interface ArgumentParser {
        Command parse(String arguments) throws EkudException;
    }

    private final String keyword;
    private final ArgumentParser parser;

    CommandType(String keyword, ArgumentParser parser) {
        this.keyword = keyword;
        this.parser = parser;
    }

    /**
     * Returns the user-facing command word, for example {@code todo}.
     *
     * @return the keyword
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * Parses the arguments for this command type.
     *
     * @param arguments text after the command word
     * @return the command to execute
     * @throws EkudException if the arguments are invalid
     */
    public Command parse(String arguments) throws EkudException {
        return parser.parse(arguments);
    }

    /**
     * Returns the command type for a keyword, or {@code null} if it is unknown.
     *
     * @param word the first token of user input
     * @return the matching type, or {@code null}
     */
    public static CommandType fromKeyword(String word) {
        for (CommandType type : values()) {
            if (type.keyword.equals(word)) {
                return type;
            }
        }
        return null;
    }

    /**
     * Returns the keywords as a readable list for help and error messages,
     * for example {@code todo, deadline, ..., or bye}.
     *
     * @return comma-separated keywords, with {@code or} before the last
     */
    public static String getHelpList() {
        CommandType[] types = values();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < types.length; i++) {
            if (i > 0) {
                builder.append(i == types.length - 1 ? ", or " : ", ");
            }
            builder.append(types[i].getKeyword());
        }
        return builder.toString();
    }
}
