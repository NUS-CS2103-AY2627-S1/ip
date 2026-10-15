package henry.parser;

/**
 * Represents a command that Henry can recognise.
 */
public enum CommandType {
    BYE("bye", false, "b"),
    LIST("list", false, "l"),
    MARK("mark", true, "m"),
    UNMARK("unmark", true, "u"),
    DELETE("delete", true, "del"),
    FIND("find", true, "f"),
    HELP("help", false),
    TODO("todo", true, "t"),
    DEADLINE("deadline", true, "d"),
    EVENT("event", true, "e"),
    UNKNOWN("", false);

    private final String commandWord;
    private final boolean acceptsArguments;
    private final String[] aliases;

    /**
     * Creates a command type with its user-facing command word.
     *
     * @param commandWord word that identifies the command.
     * @param acceptsArguments whether text may follow the command word.
     * @param aliases shorter words that also identify the command.
     */
    CommandType(String commandWord, boolean acceptsArguments, String... aliases) {
        this.commandWord = commandWord;
        this.acceptsArguments = acceptsArguments;
        this.aliases = aliases;
    }

    /**
     * Returns the word that identifies this command.
     *
     * @return command word entered by the user.
     */
    public String getCommandWord() {
        return commandWord;
    }

    /**
     * Finds the command type represented by the given input.
     *
     * @param input complete user input.
     * @return matching command type, or {@link #UNKNOWN} when none matches.
     */
    public static CommandType from(String input) {
        for (CommandType type : values()) {
            if (type == UNKNOWN) {
                continue;
            }

            if (type.matches(input, type.commandWord)) {
                return type;
            }
            for (String alias : type.aliases) {
                if (type.matches(input, alias)) {
                    return type;
                }
            }
        }
        return UNKNOWN;
    }

    private boolean matches(String input, String commandIdentifier) {
        boolean isExactMatch = input.equals(commandIdentifier);
        boolean hasArguments = acceptsArguments
                && input.startsWith(commandIdentifier + " ");
        return isExactMatch || hasArguments;
    }
}
