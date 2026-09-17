package nori;

/**
 * Contains a parsed command and its remaining arguments.
 */
public class ParsedCommand {
    private final String arguments;
    private final Command command;

    /**
     * Creates a parsed command.
     *
     * @param command Recognized command word.
     * @param arguments Text following the command word.
     */
    public ParsedCommand(Command command, String arguments) {
        this.command = command;
        this.arguments = arguments;
    }

    /**
     * Returns the recognized command.
     *
     * @return Recognized command.
     */
    public Command getCommand() {
        return command;
    }

    /**
     * Returns the text following the command word.
     *
     * @return Command arguments, or an empty string if none were given.
     */
    public String getArguments() {
        return arguments;
    }
}
