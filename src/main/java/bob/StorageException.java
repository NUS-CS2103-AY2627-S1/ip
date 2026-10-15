package bob;

/**
 * Indicates that saved task data could not be read or was not valid.
 */
public class StorageException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Creates an exception with a user-facing explanation.
     *
     * @param message explanation of the storage problem
     */
    public StorageException(String message) {
        super(message);
    }

    /**
     * Creates an exception with an explanation and underlying I/O cause.
     *
     * @param message explanation of the storage problem
     * @param cause underlying cause
     */
    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
