package yuno.exception;

/**
 * Represents a command that Yuno does not recognize.
 */
public class UnknownCommandException extends YunoException {
    /**
     * Creates an exception with the specified error message.
     *
     * @param message Error message to display.
     */
    public UnknownCommandException(String message) {
        super(message);
    }
}
