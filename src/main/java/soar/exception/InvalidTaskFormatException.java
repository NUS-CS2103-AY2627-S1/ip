package soar.exception;

/**
 * Signals that a deadline or event command is missing required scheduling details.
 */
public class InvalidTaskFormatException extends SoarException {
    /**
     * Creates an exception with guidance for correcting the command format.
     *
     * @param message Explanation of the missing or invalid task detail.
     */
    public InvalidTaskFormatException(String message) {
        super(message);
    }
}
