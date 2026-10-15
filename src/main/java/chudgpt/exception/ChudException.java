package chudgpt.exception;

/** Represents a recoverable error raised by the ChudGPT application. */
public class ChudException extends Exception {

    /**
     * Creates an exception with the specified message.
     *
     * @param message description of the application error.
     */
    public ChudException(String message) {
        super(message);
    }

    /**
     * Creates an exception with the specified message and underlying cause.
     *
     * @param message description of the application error.
     * @param cause underlying failure that caused the application error.
     */
    public ChudException(String message, Throwable cause) {
        super(message, cause);
    }
}
