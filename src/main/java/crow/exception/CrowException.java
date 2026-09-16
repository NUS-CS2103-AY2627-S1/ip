package crow.exception;

/**
 * Represents an error caused by an invalid command given to Crow.
 */
public class CrowException extends Exception {
    /**
     * Creates an exception with a user-friendly error message.
     *
     * @param message Explanation of the invalid command.
     */
    public CrowException(String message) {
        super(message);
    }
}
