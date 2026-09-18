package rem.exception;

/**
 * Indicates that a command does not identify an existing task.
 */
public class InvalidTaskNumberException extends RemException {
    private static final String MESSAGE = "Please give me a task number I can work with..."
            + "\nUse list to see task numbers, then use a positive number from that list."
            + "\nExample: mark 1";

    /**
     * Creates an exception for a missing, malformed, or out-of-range task number.
     */
    public InvalidTaskNumberException() {
        super(MESSAGE);
    }
}
