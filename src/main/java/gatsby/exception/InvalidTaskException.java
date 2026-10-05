package gatsby.exception;

/**
 * Signals that a command referred to a task number that does not exist.
 */
public class InvalidTaskException extends GatsbyException {
    /**
     * Creates an exception with a user-facing explanation.
     *
     * @param message the explanation shown by Gatsby
     */
    public InvalidTaskException(String message) {
        super(message);
    }
}
