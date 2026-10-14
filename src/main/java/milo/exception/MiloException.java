package milo.exception;

/**
 * Represents an input error that Milo can explain to the user and recover from.
 */
public class MiloException extends Exception {
    /** Identifies this exception's serialized form across application versions. */
    private static final long serialVersionUID = 1L;

    /**
     * Creates an exception carrying a user-friendly explanation of the input error.
     *
     * @param message explanation to display to the user.
     */
    public MiloException(String message) {
        super(message);
    }

    /**
     * Creates a user-friendly error while retaining its underlying cause for diagnosis.
     *
     * @param message explanation to display to the user.
     * @param cause original error that prevented the operation.
     */
    public MiloException(String message, Throwable cause) {
        super(message, cause);
    }
}
