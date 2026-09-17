package megatron.exception;

/**
 * Reports that a command does not follow its required task format.
 */
public final class InvalidTaskFormatException extends MegatronException {
    /**
     * Creates an error that shows the required command format.
     *
     * @param usage The required command format.
     */
    public InvalidTaskFormatException(String usage) {
        super("Use: " + usage);
    }
}
