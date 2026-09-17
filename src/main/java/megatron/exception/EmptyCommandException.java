package megatron.exception;

/**
 * Reports that the user entered a blank command.
 */
public final class EmptyCommandException extends MegatronException {
    /**
     * Creates an error for a blank command.
     */
    public EmptyCommandException() {
        super("Please enter a command.");
    }
}
