package jassabot.storage;

/**
 * Represents a recoverable problem while parsing or saving task data.
 */
public class StorageException extends Exception {
    /**
     * Creates a storage exception with a user-safe explanation.
     *
     * @param message Explanation of the storage problem.
     */
    public StorageException(String message) {
        super(message);
    }

    /**
     * Creates a storage exception while preserving the underlying technical cause.
     *
     * @param message Explanation of the storage problem.
     * @param cause Underlying file-system error.
     */
    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
