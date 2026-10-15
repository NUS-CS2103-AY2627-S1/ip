package min.exception;

/**
 * Represents a user-facing error caused by invalid input or saved data.
 */
public class MinException extends Exception {

    /** Creates an error with the message that should be shown to the user. */
    public MinException(String message) {
        super(message);
    }
}
