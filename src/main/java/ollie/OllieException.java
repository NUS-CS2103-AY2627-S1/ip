package ollie;

/**
 * Represents an error that Ollie can explain to the user.
 */
public class OllieException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Creates an Ollie-specific exception with the given message.
     *
     * @param message Explanation of the error.
     */
    public OllieException(String message) {
        super(message);
    }
}
