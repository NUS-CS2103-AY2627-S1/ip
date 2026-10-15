package samantha.exception;

/**
 * Base class for checked exceptions raised by Samantha.
 */
public abstract class SamanthaException extends Exception {
    /**
     * Creates an application exception with a user-facing message.
     *
     * @param message explanation of the failure
     */
    protected SamanthaException(String message) {
        super(message);
    }

    /**
     * Creates an application exception with a message and underlying cause.
     *
     * @param message explanation of the failure
     * @param cause lower-level failure that caused this exception
     */
    protected SamanthaException(String message, Throwable cause) {
        super(message, cause);
    }
}
