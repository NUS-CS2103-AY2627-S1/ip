package peanutbuttercat;

/**
 * Represents a command understood by the chatbot.
 */
public enum CommandType {
    BYE("bye", false),
    LIST("list", false),
    FIND("find", true),
    MARK("mark", true),
    UNMARK("unmark", true),
    DELETE("delete", true),
    TODO("todo", true),
    DEADLINE("deadline", true),
    EVENT("event", true),
    ON("on", true),
    STATISTICS("stats", true),
    UNKNOWN("", false);

    private final String commandWord;
    private final boolean acceptsArguments;

    /**
     * Creates a command type with its input word and argument behavior.
     *
     * @param commandWord Word users enter to invoke the command.
     * @param acceptsArguments Whether arguments may follow the command word.
     */
    CommandType(String commandWord, boolean acceptsArguments) {
        this.commandWord = commandWord;
        this.acceptsArguments = acceptsArguments;
    }

    /**
     * Identifies the command at the start of the given input.
     *
     * @param input Full input entered by the user.
     * @return The matching command type, or {@link #UNKNOWN} if there is no match.
     */
    public static CommandType fromInput(String input) {
        if (input == null) {
            return UNKNOWN;
        }
        for (CommandType commandType : values()) {
            if (commandType.matches(input)) {
                return commandType;
            }
        }
        return UNKNOWN;
    }

    /**
     * Returns the word users enter to invoke this command.
     *
     * @return The command word.
     */
    public String getCommandWord() {
        return commandWord;
    }

    private boolean matches(String input) {
        if (this == UNKNOWN) {
            return false;
        }
        String trimmedInput = input.trim();
        boolean isExactMatch = trimmedInput.equals(commandWord);
        boolean hasArgument = acceptsArguments
                && trimmedInput.startsWith(commandWord)
                && trimmedInput.length() > commandWord.length()
                && Character.isWhitespace(trimmedInput.charAt(commandWord.length()));
        return isExactMatch || hasArgument;
    }
}
