package stewie.storage;

/**
 * Represents a recoverable storage failure that must be shown without confirming a task change.
 */
public class StorageException extends RuntimeException {
    /**
     * Creates an error with recovery guidance for the user.
     *
     * @param message Explanation of the failure and next steps.
     */
    public StorageException(String message) {
        super(message);
    }
}
