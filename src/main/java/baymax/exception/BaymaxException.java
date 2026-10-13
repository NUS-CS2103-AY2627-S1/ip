package baymax.exception;

/**
 * Represents an application-level error that can be shown to the user.
 */
public class BaymaxException extends RuntimeException {

    /**
     * Creates a Baymax exception with the given user-facing message.
     *
     * @param message the message to show to the user
     */
    public BaymaxException(String message) {
        super(message);
    }
}
