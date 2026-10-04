package kelore.exception;

import java.io.IOException;

/** Represents a failure to load or save Kelore's persistent task data. */
public class StorageException extends IOException {
    /**
     * Creates a storage exception with its user-readable explanation.
     *
     * @param message Explanation of the storage failure.
     */
    public StorageException(String message) {
        super(message);
    }

    /**
     * Creates a storage exception that retains the underlying I/O failure.
     *
     * @param message Explanation of the storage failure.
     * @param cause Underlying failure.
     */
    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
