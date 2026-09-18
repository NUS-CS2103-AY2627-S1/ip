package blud.exception;

/** Signals that task storage could not be created, read, written, or validated. */
public class StorageException extends RuntimeException {
    /** Creates a storage exception with a user-facing explanation and cause. */
    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
