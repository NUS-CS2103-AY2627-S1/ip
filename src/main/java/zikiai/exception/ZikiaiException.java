package zikiai.exception;

/**
 * Represents an error that Zikiai can explain to the user.
 */
public class ZikiaiException extends Exception {

    /**
     * Creates an exception with a message that explains the input error.
     *
     * @param message explanation shown to the user.
     */
    public ZikiaiException(String message) {
        super(message);
    }

    /**
     * Creates an exception with a user-facing message and its original cause.
     *
     * @param message explanation shown to the user.
     * @param cause lower-level error that caused this exception.
     */
    public ZikiaiException(String message, Throwable cause) {
        super(message, cause);
    }
}
