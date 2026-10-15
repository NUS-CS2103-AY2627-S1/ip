package bubu.exception;

/**
 * Indicates that the user entered a command the chatbot does not recognize.
 */
public class UnknownCommandException extends BubuException {
    private static final String UNKNOWN_COMMAND_MESSAGE = "I'm sorry, but I don't know what '%s' means. ^.^";
    private static final String UNKNOWN_COMMAND_GENERIC_MESSAGE = "I'm sorry, but I don't know what that means. ^.^";

    /**
     * Creates an error that names the unrecognized command.
     *
     * @param command unrecognized command input.
     */
    public UnknownCommandException(String command) {
        super(String.format(UNKNOWN_COMMAND_MESSAGE, command));
    }

    /**
     * Creates an error for empty or whitespace-only input.
     */
    public UnknownCommandException() {
        super(UNKNOWN_COMMAND_GENERIC_MESSAGE);
    }
}
