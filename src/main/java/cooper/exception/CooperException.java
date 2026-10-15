package cooper.exception;

/**
 * Represents an error caused by invalid user input or unavailable task storage.
 */
public class CooperException extends RuntimeException {
    /**
     * Creates an exception with a message suitable for displaying to the user.
     *
     * @param message Explanation of the error.
     */
    public CooperException(String message) {
        super(message);
    }
}
