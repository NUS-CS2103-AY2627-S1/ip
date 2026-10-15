package dude.exception;

/**
 * Indicates that a command could not complete because its state could not be persisted.
 */
public final class CommandExecutionException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Creates a command execution exception with the underlying failure.
     *
     * @param cause Persistence or infrastructure failure.
     */
    public CommandExecutionException(Throwable cause) {
        super("Command state could not be persisted", cause);
    }
}
