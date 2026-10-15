package peanutbuttercat;

/**
 * Represents a storage failure that the application can explain without terminating.
 */
public class StorageException extends Exception {

    /**
     * Creates a storage exception with a user-friendly message and its technical cause.
     *
     * @param message Explanation suitable for display to the user.
     * @param cause Underlying file-system failure.
     */
    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
