package larper.exception;

/**
 * Represents an expected user-facing error in Larper.
 */
public class LarperException extends Exception {
    /**
     * Creates a Larper exception with a message that can be shown to the user.
     *
     * @param message User-facing error message.
     */
    public LarperException(String message) {
        super(message);
    }
}
