package mono.exception;

/**
 * Base class for exceptions that can be reported directly to Mono's users.
 */
public class MonoException extends Exception {
    /**
     * Creates an application exception with a user-facing message.
     *
     * @param message user-facing explanation of the error
     */
    public MonoException(String message) {
        super(message);
    }

    /**
     * Creates an application exception while preserving its underlying cause.
     *
     * @param message user-facing explanation of the error
     * @param cause underlying exception
     */
    public MonoException(String message, Throwable cause) {
        super(message, cause);
    }
}
