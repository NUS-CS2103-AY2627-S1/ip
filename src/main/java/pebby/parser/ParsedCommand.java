package pebby.parser;

/**
 * Holds the command type and argument extracted from one line of user input.
 *
 * <p>Keeping these values together prevents other classes from repeatedly
 * splitting the original command text.</p>
 */
public class ParsedCommand {
    private final CommandType type;
    private final String argument;
    private final String errorMessage;

    /**
     * Creates a parsed command with its recognized type and remaining text.
     */
    public ParsedCommand(CommandType type, String argument) {
        this(type, argument, "");
    }

    /** Creates a parsed command, optionally recording why its format is invalid. */
    public ParsedCommand(CommandType type, String argument, String errorMessage) {
        this.type = type;
        this.argument = argument;
        this.errorMessage = errorMessage;
    }

    /**
     * Returns the recognized command type.
     */
    public CommandType getType() {
        return type;
    }

    /**
     * Returns the text after the command word, without surrounding spaces.
     */
    public String getArgument() {
        return argument;
    }

    /** Returns whether the command format was invalid. */
    public boolean hasError() {
        return !errorMessage.isEmpty();
    }

    /** Returns the user-facing explanation of the command-format error. */
    public String getErrorMessage() {
        return errorMessage;
    }
}
