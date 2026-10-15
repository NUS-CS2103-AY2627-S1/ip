package labubu;

/**
 * Signals a task command with missing or malformed task details.
 */
public class InvalidTaskInputException extends Exception {
    /**
     * Creates an exception with the message shown for invalid task input.
     */
    public InvalidTaskInputException() {
        super("Oops! Invalid task details.");
    }
}
