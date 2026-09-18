package student.project.bob.storage;

/**
 * Represents a problem while reading or writing Bob's task data.
 */
public class StorageException extends Exception {

    /**
     * Creates a storage exception with a user-facing message and underlying cause.
     *
     * @param message explanation of the storage problem
     * @param cause underlying file or data-format problem
     */
    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
