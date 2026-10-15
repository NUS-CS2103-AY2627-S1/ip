package tardt.exception;

/**
 * Represents a user-facing error caused by an invalid TardT command or command format.
 */
public class TardTException extends Exception {
    /** Creates an exception with the message that should be shown to the user. */
    public TardTException(String message) {
        super(message);
    }
}
