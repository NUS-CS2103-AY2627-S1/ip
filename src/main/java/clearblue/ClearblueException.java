package clearblue;

/**
 * Signals a problem specific to Clearblue's own operation (e.g. a storage
 * failure) that the caller should report to the user instead of crashing on.
 */
public class ClearblueException extends Exception {
    /**
     * Creates an exception with the given message.
     *
     * @param message description of what went wrong
     */
    public ClearblueException(String message) {
        super(message);
    }

    /**
     * Creates an exception with the given message and underlying cause.
     *
     * @param message description of what went wrong
     * @param cause underlying exception that caused this failure
     */
    public ClearblueException(String message, Throwable cause) {
        super(message, cause);
    }
}
