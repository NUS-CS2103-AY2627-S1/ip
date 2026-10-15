package tangent.exception;

/** Thrown when a command cannot be carried out for various reasons. */
public class TangentException extends Exception {
    /**
     * Creates an exception with a specific message.
     *
     * @param message The error message that should be shown to the user.
     */
    public TangentException(String message) {
        super(message);
    }
}
