package sage.exception;

/** Represents a user-facing error reported by Sage. */
public class SageException extends Exception {
    /** Creates an exception with the supplied user-facing message.
     *
     * @param message user-facing error message
     */
    public SageException(String message) {
        super(message);
    }
}
