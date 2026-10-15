package bubba;

/**
 * Represents an application error with a message suitable for displaying to the user.
 */
public class BubbaException extends Exception {
    /**
     * Creates an error with the supplied explanation.
     *
     * @param message Explanation of the error.
     */
    public BubbaException(String message) {
        super(message);
    }
}
