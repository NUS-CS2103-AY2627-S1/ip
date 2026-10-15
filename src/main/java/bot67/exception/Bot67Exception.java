package bot67.exception;

/**
 * Represents an expected error while processing a Bot67 command.
 */
public class Bot67Exception extends Exception {
    /**
     * Creates an exception with the specified user-facing message.
     *
     * @param message the error message
     */
    public Bot67Exception(String message) {
        super(message);
    }
}
