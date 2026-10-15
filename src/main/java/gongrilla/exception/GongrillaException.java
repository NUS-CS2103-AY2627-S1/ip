package gongrilla.exception;

/**
 * Represents an error caused by a command that gongrilla.Gongrilla cannot process.
 */
public class GongrillaException extends Exception {
    /**
     * Creates an exception with a user-friendly explanation of the error.
     *
     * @param message explanation displayed to the user.
     */
    public GongrillaException(String message) {
        super(message);
    }
}
