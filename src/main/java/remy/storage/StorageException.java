package remy.storage;

import java.io.IOException;

/**
 * Represents a failure to read or write Remy's task file.
 */
public class StorageException extends IOException {

    /**
     * Creates a storage exception with a user-facing explanation and its original cause.
     *
     * @param message explanation of the storage failure
     * @param cause original failure reported by the environment
     */
    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
