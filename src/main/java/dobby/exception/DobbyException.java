package dobby.exception;

/**
 * Signals an error caused by invalid Dobby command data or task data.
 */
public class DobbyException extends Exception {
    /** Creates an exception with a user-facing Dobby error message. */
    public DobbyException(String message) {
        super("> " + message);
    }

    /** Creates an exception with a user-facing message and its underlying cause. */
    public DobbyException(String message, Throwable cause) {
        super("> " + message, cause);
    }
}
