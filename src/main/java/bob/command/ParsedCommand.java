package bob.command;

/**
 * Stores a recognized command together with the text following its command word.
 */
public class ParsedCommand {
    private final Command command;
    private final String arguments;

    /**
     * Creates a parsed command.
     *
     * @param command Recognized command type.
     * @param arguments Text following the command word.
     */
    public ParsedCommand(Command command, String arguments) {
        this.command = command;
        this.arguments = arguments;
    }

    /** Returns the recognized command type. */
    public Command getCommand() {
        return command;
    }

    /** Returns the text following the command word. */
    public String getArguments() {
        return arguments;
    }
}
