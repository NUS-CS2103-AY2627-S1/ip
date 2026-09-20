package fifi.exception;

/**
 * Represents an error caused by adding too many tasks.
 */
public class ExcessiveTaskException extends FifiException {
    /**
     * Creates an exception with the message to show to the user.
     *
     * @param message the error message
     */
    public ExcessiveTaskException(String message) {
        super(message);
    }
}
