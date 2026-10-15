package koara.exception;

/**
 * Represents an error caused by an invalid command entered into Koara.
 */
public class KoaraException extends Exception {
    /**
     * Creates an exception containing a user-facing error message.
     *
     * @param message Error message to display.
     */
    public KoaraException(String message) {
        super(message);
    }
}
