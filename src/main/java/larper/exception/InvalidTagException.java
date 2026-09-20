package larper.exception;

/**
 * Reports that a task tag does not contain one valid word.
 */
public class InvalidTagException extends LarperException {
    private static final String MESSAGE = " Tags are identity labels, not essays. Use one word like #school.";

    /**
     * Creates an exception for an invalid task tag.
     */
    public InvalidTagException() {
        super(MESSAGE);
    }
}
