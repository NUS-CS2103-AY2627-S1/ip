package baron.core.exception;

/**
 * Represents an error caused by an invalid Baron command.
 */
public class BaronException extends Exception {
    /**
     * Creates an exception with the specified error message.
     *
     * @param message The explanation of the error.
     */
    public BaronException(String message) {
        super(message);
    }
}
