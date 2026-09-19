package yuno.exception;

/**
 * Represents a command that does not follow its required format.
 */
public class InvalidCommandFormatException extends YunoException {
    /**
     * Creates an exception with the specified error message.
     *
     * @param message Error message to display.
     */
    public InvalidCommandFormatException(String message) {
        super(message);
    }
}
