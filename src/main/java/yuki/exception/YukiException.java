package yuki.exception;

/**
 * Represents an invalid command or other user-input error in Yuki.
 */
public class YukiException extends RuntimeException {
    /**
     * Creates an exception with a message that can be shown to the user.
     *
     * @param message Explanation of the invalid input.
     */
    public YukiException(String message) {
        super(message);
    }

    /**
     * Creates an exception with a user-facing message and its underlying cause.
     *
     * @param message Explanation of the error.
     * @param cause Lower-level failure that caused the error.
     */
    public YukiException(String message, Throwable cause) {
        super(message, cause);
    }
}
