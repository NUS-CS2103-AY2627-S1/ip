package dennis;

/**
 * Represents an error caused by invalid input entered by the user.
 */
public class DennisException extends Exception {
    /**
     * Creates an exception with a user-friendly error message.
     *
     * @param message explanation of the invalid input
     */
    public DennisException(String message) {
        super(message);
    }
}
