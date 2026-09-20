package larper.exception;

/**
 * Signals that a delete command is missing a numeric task number.
 */
public class NonNumberDeleteException extends LarperException {
    /**
     * Creates an exception for delete commands that do not provide a number.
     */
    public NonNumberDeleteException() {
        super(" Deletion needs a number. Reputation management requires specifics.");
    }
}
