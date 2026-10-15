package magnus.exception;

/**
 * Indicates that user input does not identify a supported command.
 */
public class CommandNotFoundException extends MagnusException {
    /**
     * Creates an exception describing the unrecognized command.
     *
     * @param message The detail message describing the error.
     */
    public CommandNotFoundException(String message) {
        super(message);
    }
}
