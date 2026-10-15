package zen;

/** Represents an application-level error that can be shown to the user. */
public class ZenException extends Exception {
    /**
     * Creates an exception containing a user-facing error message.
     *
     * @param message the explanation of the error.
     */
    public ZenException(String message) {
        super(message);
    }
}
