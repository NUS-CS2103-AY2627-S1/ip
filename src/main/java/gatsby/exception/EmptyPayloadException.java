package gatsby.exception;

/**
 * Signals that a command was given without the details it needs, such as a description or a date.
 */
public class EmptyPayloadException extends GatsbyException {
    /**
     * Creates an exception with a user-facing explanation.
     *
     * @param message the explanation shown by Gatsby
     */
    public EmptyPayloadException(String message) {
        super(message);
    }
}
