package lizzy.exception;

/**
 * Represents an input error that Lizzy can explain to the user without ending the session.
 */
public class LizzyException extends Exception {
    /**
     * Version identifier for Java exception serialization.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Creates an exception containing a user-facing explanation and correction.
     *
     * @param message the error message to show the user
     */
    public LizzyException(String message) {
        super(message);
    }
}
