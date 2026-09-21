package echo.exception;

/**
 * An exception class for all Exceptions unique to Echo.
 */
public class EchoException extends Exception {
    /**
     * Creates an {@link EchoException} with the given message, prefixed for display.
     * @param message detail of what went wrong, shown to the user
     */
    public EchoException(String message) {
        super("Signal scrambled.\n" + message);
    }
}
