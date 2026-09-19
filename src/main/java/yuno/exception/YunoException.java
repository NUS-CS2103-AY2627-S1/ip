package yuno.exception;

/**
 * Represents an error caused by an invalid Yuno command or operation.
 */
public class YunoException extends Exception {
    /**
     * Creates an exception with the specified error message.
     *
     * @param message Error message to display.
     */
    public YunoException(String message) {
        super(message);
    }

    /**
     * Creates an exception with the specified error message and cause.
     *
     * @param message Error message to display.
     * @param cause Error that caused this exception.
     */
    public YunoException(String message, Throwable cause) {
        super(message, cause);
    }
}
