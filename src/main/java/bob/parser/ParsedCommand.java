package bob.parser;

/**
 * Represents a command word and its associated argument.
 */
public class ParsedCommand {
    private final String commandWord;
    private final String argument;

    /**
     * Creates a parsed command.
     *
     * @param commandWord Word identifying the command to execute.
     * @param argument Argument supplied with the command.
     */
    public ParsedCommand(String commandWord, String argument) {
        this.commandWord = commandWord;
        this.argument = argument;
    }

    /**
     * Returns the command word of the command statement.
     *
     * @return Command word of the command statement.
     */
    public String getCommandWord() {
        return commandWord;
    }

    /**
     * Returns the argument of the command statement.
     *
     * @return Argument of the command statement.
     */
    public String getArgument() {
        return argument;
    }
}
