package kibo.exception;

/**
 * Represents a command that is unknown or does not follow the required syntax.
 */
public class InvalidCommandException extends KiboException {
    private static final long serialVersionUID = 1L;

    /**
     * Creates an invalid-command exception with a user-facing explanation.
     *
     * @param message explanation of the command error.
     */
    public InvalidCommandException(String message) {
        super(message);
    }
}
