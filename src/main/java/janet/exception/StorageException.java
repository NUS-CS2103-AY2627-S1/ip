package janet.exception;

/** Indicates that Janet cannot safely load or save task data. */
public class StorageException extends JanetException {
    /**
     * Creates a storage exception with a user-facing message.
     *
     * @param message message to display to the user.
     */
    public StorageException(String message) {
        super(message);
    }

    /**
     * Creates a storage exception caused by an input/output failure.
     *
     * @param message message to display to the user.
     * @param cause underlying input/output failure.
     */
    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
