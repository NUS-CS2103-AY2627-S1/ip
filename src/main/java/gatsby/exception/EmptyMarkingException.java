package gatsby.exception;

/**
 * Signals that a mark or unmark command was given without a task number.
 */
public class EmptyMarkingException extends GatsbyException {
    /**
     * Creates an exception with a user-facing explanation.
     *
     * @param message the explanation shown by Gatsby
     */
    public EmptyMarkingException(String message) {
        super(message);
    }
}
