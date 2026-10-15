package avon.exception;

/**
 * Signals that the first word of a command is not recognised by Avon.
 */
public class UnknownCommandException extends AvonException {
    /**
     * Creates an unknown-command error with a list of valid first words.
     *
     * @param supportedKeywords the comma-separated command keywords Avon understands.
     */
    public UnknownCommandException(String supportedKeywords) {
        super("I know not that command.\n"
                + "Do start with one of: " + supportedKeywords + ".");
    }
}
