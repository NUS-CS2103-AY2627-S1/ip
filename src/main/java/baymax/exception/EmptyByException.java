package baymax.exception;

/**
 * Represents a deadline command with no due-date description.
 */
public class EmptyByException extends BaymaxException {

    /**
     * Creates an exception for a missing deadline due date.
     */
    public EmptyByException() {
        super(" Sorry, a deadline needs a due date.");
    }
}
