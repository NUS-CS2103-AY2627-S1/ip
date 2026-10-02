package clsl;

/**
 * Represents an error caused by an invalid command or task data.
 */
public class ClslException extends Exception {
    /**
     * Creates an exception with the specified user-facing message.
     *
     * @param message Explanation of the error.
     */
    public ClslException(String message) {
        super(message);
    }
}
