package sace;

/**
 * Represents an invalid command or task operation entered by the user.
 */
public class SaceException extends Exception {
    /**
     * Creates an exception containing a user-friendly explanation.
     *
     * @param message explanation of the invalid input.
     */
    public SaceException(String message) {
        super(message);
    }
}
