package charliek.exception;

/**
 * Indicates that CharlieK could not read or write its task file.
 */
public class TaskStorageException extends CharlieKException {
    /**
     * Creates a storage exception with a user-facing message.
     *
     * @param message the message shown to the user.
     */
    public TaskStorageException(String message) {
        super(message);
    }

    /**
     * Creates a storage exception with a user-facing message and original cause.
     *
     * @param message the message shown to the user.
     * @param cause the underlying file-system failure.
     */
    public TaskStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
