package lloyd.command;

/**
 * Represents the command type and argument text recognized from one user input.
 */
public final class ParsedCommand {
    private final CommandType commandType;
    private final String arguments;

    /**
     * Creates a parsed command.
     *
     * @param commandType Recognized command type.
     * @param arguments Text following the command word, or an empty string.
     */
    public ParsedCommand(CommandType commandType, String arguments) {
        if (commandType == null || arguments == null) {
            throw new IllegalArgumentException(
                    "Command type and arguments cannot be null");
        }
        this.commandType = commandType;
        this.arguments = arguments;
    }

    /**
     * Returns the recognized command type.
     *
     * @return Command type, including {@link CommandType#UNKNOWN}.
     */
    public CommandType getCommandType() {
        return commandType;
    }

    /**
     * Reports whether argument text follows the command word.
     *
     * @return {@code true} when the command contains arguments.
     */
    public boolean hasArguments() {
        return !arguments.isBlank();
    }

    /**
     * Returns the text following the command word.
     *
     * @return Argument text, or an empty string when none was supplied.
     */
    public String getArguments() {
        return arguments;
    }
}
