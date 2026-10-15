package lumi.exception;

/**
 * Represents an input error that Lumi can explain to the user and recover from.
 */
public class LumiException extends Exception {
    /** Prefix that identifies Lumi's recoverable, user-facing errors. */
    public static final String ERROR_PREFIX = "Signal unclear: ";

    /**
     * Creates an exception with a user-friendly explanation of the input error.
     *
     * @param message Explanation shown to the user.
     */
    public LumiException(String message) {
        super(ERROR_PREFIX + message);
    }
}
