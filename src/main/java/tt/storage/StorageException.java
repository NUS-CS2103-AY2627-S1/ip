package tt.storage;

/**
 * Represents a failure to read or write the application's task data.
 */
public class StorageException extends RuntimeException {

    /**
     * Creates a storage exception with a user-facing explanation.
     *
     * @param message explanation of the storage failure.
     * @param cause low-level failure that prevented the storage operation.
     */
    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a storage exception for invalid save-file contents.
     *
     * @param message explanation of the invalid data.
     */
    public StorageException(String message) {
        super(message);
    }
}
