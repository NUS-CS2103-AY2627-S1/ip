package shan.exception;

/**
 * Represents a command that Shan does not recognize or cannot process.
 */
public class InvalidCommandException extends ShanException {
    private static final long serialVersionUID = 1L;

    /**
     * Constructs an invalid-command exception.
     *
     * @param message Explanation of the invalid command.
     */
    public InvalidCommandException(String message) {
        super(message);
    }
}
