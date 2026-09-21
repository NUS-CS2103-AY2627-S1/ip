package yqr.exception;

/**
 * Represents a recoverable command, data, or storage error that can be shown to the user.
 */
public class YqrException extends Exception {
    /**
     * Creates an exception containing an explanation for the user.
     *
     * @param message explanation of the invalid input.
     */
    public YqrException(String message) {
        super(message);
    }
}
