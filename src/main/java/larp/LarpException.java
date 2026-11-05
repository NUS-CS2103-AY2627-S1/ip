package larp;

/**
 * Represents an error caused by an invalid command given to Larp.
 */
public class LarpException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Creates an exception with a message suitable for displaying to the user.
     *
     * @param message Explanation of the error.
     */
    public LarpException(String message) {
        super(message);
    }
}
