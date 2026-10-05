package gatsby.exception;

/**
 * Signals that a task with the same details already exists.
 */
public class DuplicateTaskException extends GatsbyException {
    /**
     * Creates an exception with a user-facing explanation.
     *
     * @param message the explanation shown by Gatsby
     */
    public DuplicateTaskException(String message) {
        super(message);
    }
}
