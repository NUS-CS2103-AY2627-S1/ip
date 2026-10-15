package alex.storage;

/**
 * Signals that task data could not be loaded from or saved to disk.
 */
public class StorageException extends Exception {
    /**
     * Creates an exception with an explanatory message.
     *
     * @param message explanation of the storage error.
     */
    public StorageException(String message) {
        super(message);
    }

    /**
     * Creates an exception with an explanatory message and cause.
     *
     * @param message explanation of the storage error.
     * @param cause underlying cause of the error.
     */
    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
