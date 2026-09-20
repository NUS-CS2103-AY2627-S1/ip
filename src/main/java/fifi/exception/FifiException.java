package fifi.exception;

/**
 * Represents an error caused by invalid Fifi usage or data.
 */
public class FifiException extends Exception {
    /**
     * Creates an exception with the message to show to the user.
     *
     * @param message the error message
     */
    public FifiException(String message) {
        super(message);
    }
}
