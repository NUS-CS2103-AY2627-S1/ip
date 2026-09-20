package duchess;

/**
 * Represents an expected, user-correctable error in Duchess.
 */
public class DuchessException extends Exception {
    /**
     * Creates an exception with a message suitable for displaying to the user.
     *
     * @param message the explanation of the error
     */
    public DuchessException(String message) {
        super(message);
    }
}
