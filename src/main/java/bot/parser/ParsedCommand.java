package bot.parser;

/**
 * Contains the command type and arguments extracted from one line of user input.
 */
public final class ParsedCommand {
    private final CommandType type;
    private final String commandWord;
    private final String arguments;

    /**
     * Creates a parsed command.
     *
     * @param type recognized command type.
     * @param commandWord first word entered by the user.
     * @param arguments text following the command word.
     */
    public ParsedCommand(CommandType type, String commandWord, String arguments) {
        this.type = type;
        this.commandWord = commandWord;
        this.arguments = arguments;
    }

    /**
     * Returns the recognized command type.
     *
     * @return command type
     */
    public CommandType getType() {
        return type;
    }

    /**
     * Returns the original command word for use in error messages.
     *
     * @return command word entered by the user
     */
    public String getCommandWord() {
        return commandWord;
    }

    /**
     * Returns the text following the command word.
     *
     * @return command arguments, or an empty string if none were supplied
     */
    public String getArguments() {
        return arguments;
    }
}
