package ducky;

/**
 * Represents an error caused by invalid user input.
 */
public class DuckyException extends Exception {
    /**
     * Creates an exception with the given user-facing error message.
     *
     * @param message the error message.
     */
    public DuckyException(String message) {
        super(message);
    }
}
