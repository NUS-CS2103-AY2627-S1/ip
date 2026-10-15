package swell.exception;

/**
 * Represents an error caused by invalid user input in Swell.
 */
public class SwellException extends Exception {
    /**
     * Creates a Swell exception with the given message.
     *
     * @param message error message.
     */
    public SwellException(String message) {
        super(message);
    }
}
