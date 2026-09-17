package megatron.exception;

/**
 * Reports that a task command does not contain a valid number.
 */
public final class InvalidTaskNumberException extends MegatronException {
    /**
     * Creates an error for an invalid task number.
     */
    public InvalidTaskNumberException() {
        super("Please provide a valid task number.");
    }
}
