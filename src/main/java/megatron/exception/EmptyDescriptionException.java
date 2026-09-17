package megatron.exception;

/**
 * Reports that a task description was missing.
 */
public final class EmptyDescriptionException extends MegatronException {
    /**
     * Creates an error for a missing todo description.
     */
    public EmptyDescriptionException() {
        super("A todo description cannot be empty.");
    }
}
