package rambo.exception;

import rambo.ui.Constants;

/**
 * Represents an error caused by invalid input to Rambo.
 */
public class RamboException extends RuntimeException {

    /**
     * Creates an exception with a user-friendly explanation of the error.
     *
     * @param message explanation shown to the user
     */
    public RamboException(String message) {
        super(Constants.BOT_NAME + ": " + message);
    }

    /**
     * Creates an exception that retains the underlying cause for debugging.
     *
     * @param message explanation shown to the user
     * @param cause underlying exception that caused the failure
     */
    public RamboException(String message, Throwable cause) {
        super("\n" + Constants.BOT_NAME + ": " + message, cause);
    }
}
