package gatsby.exception;

/**
 * Signals that the user typed something Gatsby does not recognise.
 */
public class UnknownCommandException extends GatsbyException {
    /**
     * Creates an exception with a user-facing explanation.
     *
     * @param message the explanation shown by Gatsby
     */
    public UnknownCommandException(String message) {
        super(message);
    }
}
