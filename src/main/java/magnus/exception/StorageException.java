package magnus.exception;

/**
 * Indicates that task data could not be loaded, parsed, or saved.
 */
public class StorageException extends MagnusException {
    /**
     * Creates a storage exception with an explanatory message and underlying cause.
     *
     * @param message The detail message describing the storage error.
     * @param cause The underlying cause of the storage error.
     */
    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
