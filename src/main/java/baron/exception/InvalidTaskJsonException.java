package baron.exception;

/**
 * Exception thrown when a task's JSON format is invalid.
 */
public class InvalidTaskJsonException extends BaronException {
    public InvalidTaskJsonException() {
        super("A ledger entry is corrupted: its JSON task format is not recognised.");
    }
}
