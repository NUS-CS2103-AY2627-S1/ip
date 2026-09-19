package daddy.exception;

/**
 * Represents an error that can be explained directly to a Daddy user.
 */
public class DaddyException extends Exception {
    /** Keeps this serializable exception compatible across application versions. */
    private static final long serialVersionUID = 1L;

    /**
     * Creates an error with a user-friendly correction message.
     *
     * @param message the explanation and suggested correction to show the user
     */
    public DaddyException(String message) {
        super(message);
    }
}
