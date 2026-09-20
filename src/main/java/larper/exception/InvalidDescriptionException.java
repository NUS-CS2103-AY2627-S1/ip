package larper.exception;

/**
 * Signals that a task description contains text that cannot be safely saved.
 */
public class InvalidDescriptionException extends LarperException {
    /**
     * Creates an exception for descriptions that conflict with Larper's data file format.
     */
    public InvalidDescriptionException() {
        super(" The task description cannot contain `|` because Larper uses it to save data safely.");
    }
}
