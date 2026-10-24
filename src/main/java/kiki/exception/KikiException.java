package kiki.exception;

/**
 * Represents an input error that Kiki can explain to the user.
 */
public class KikiException extends Exception {
    /**
     * Creates a new exception with a user-facing message.
     *
     * @param message Message explaining the error to the user.
     */
    public KikiException(String message) {
        super(message);
    }
}
